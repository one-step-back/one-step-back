package com.app.onestepback.service.membership;

import com.app.onestepback.domain.dto.membership.MembershipDTO;
import com.app.onestepback.domain.dto.membership.MembershipDetailDTO;
import com.app.onestepback.domain.dto.membership.MembershipManageDTO;
import com.app.onestepback.domain.model.MembershipVO;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.repository.ArtistMapper;
import com.app.onestepback.repository.MembershipMapper;
import com.app.onestepback.repository.SubscriptionMapper;
import com.app.onestepback.service.file.FileService;
import com.app.onestepback.service.membership.cmd.MembershipRegisterCmd;
import com.app.onestepback.service.membership.cmd.MembershipUpdateCmd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 멤버십 상품 관리를 위한 비즈니스 로직 구현체입니다.
 * <p>
 * 멤버십 가격 위계(Hierarchy) 준수 여부 및 활성 구독자 존재 여부를 검증하여
 * 서비스의 데이터 정합성을 엄격하게 유지합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final FileService fileService;
    private final MembershipMapper membershipMapper;
    private final SubscriptionMapper subscriptionMapper;
    private final ArtistMapper artistMapper;

    @Override
    @Transactional
    @CacheEvict(value = "artistDetail", allEntries = true)
    public void registerMembership(MembershipRegisterCmd cmd) {
        if (cmd.levelOrder() < 1 || cmd.levelOrder() > 3) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if (membershipMapper.existsByArtistAndLevel(cmd.artistId(), cmd.levelOrder())) {
            throw new BusinessException(ErrorCode.MEMBERSHIP_LEVEL_DUPLICATED);
        }

        validatePriceHierarchy(cmd.artistId(), cmd.levelOrder(), cmd.price());

        MembershipVO vo = MembershipVO.builder()
                .artistId(cmd.artistId())
                .name(cmd.name())
                .description(cmd.description())
                .imageId(cmd.imageId())
                .price(cmd.price())
                .levelOrder(cmd.levelOrder())
                .build();

        membershipMapper.insert(vo);

        if (cmd.imageId() != null) {
            fileService.connectFiles(vo.getId(), "MEMBERSHIP_IMAGE", List.of(cmd.imageId()), cmd.artistId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipManageDTO getMembershipManageInfo(Long artistId) {
        MembershipManageDTO dto = new MembershipManageDTO();
        boolean hasBankInfo = artistMapper.checkBankInfoExistence(artistId);
        dto.setHasAppliedBankAccount(hasBankInfo);

        if (hasBankInfo) {
            dto.setMemberships(membershipMapper.selectAllDtoByArtistId(artistId, null));
        } else {
            dto.setMemberships(List.of());
        }
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MembershipDTO> getMemberships(Long artistId, Long viewerId) {
        return membershipMapper.selectAllDtoByArtistId(artistId, viewerId);
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipDetailDTO getMembershipInfo(Long membershipId) {
        return membershipMapper.selectDetailDtoById(membershipId).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBERSHIP_NOT_FOUND)
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = "artistDetail", allEntries = true)
    public void update(MembershipUpdateCmd cmd) {
        validateZeroSubscribers(cmd.membershipId());

        if (cmd.levelOrder() < 1 || cmd.levelOrder() > 3) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        validatePriceHierarchyForUpdate(cmd.artistId(), cmd.membershipId(), cmd.levelOrder(), cmd.price());

        String finalImageId = cmd.currentImageId();

        if (cmd.deleteImage()) {
            finalImageId = null;
            if (cmd.currentImageId() != null) {
                fileService.disconnectFiles(List.of(cmd.currentImageId()), cmd.artistId());
            }
        } else if (cmd.newImageId() != null && !cmd.newImageId().isBlank()) {
            finalImageId = cmd.newImageId();
            fileService.connectFiles(cmd.membershipId(), "MEMBERSHIP_IMAGE", List.of(cmd.newImageId()), cmd.artistId());
            if (cmd.currentImageId() != null) {
                fileService.disconnectFiles(List.of(cmd.currentImageId()), cmd.artistId());
            }
        }

        MembershipVO vo = MembershipVO.builder()
                .id(cmd.membershipId())
                .artistId(cmd.artistId())
                .name(cmd.name())
                .description(cmd.description())
                .price(cmd.price())
                .levelOrder(cmd.levelOrder())
                .imageId(finalImageId)
                .build();

        if (membershipMapper.update(vo) == 0) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "artistDetail", allEntries = true)
    public void updateStatus(Long artistId, Long membershipId, boolean active) {
        String status = active ? "Y" : "N";
        if (membershipMapper.updateStatus(membershipId, artistId, status) == 0) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "artistDetail", allEntries = true)
    public void delete(Long artistId, Long membershipId) {
        MembershipVO existing = membershipMapper.selectById(membershipId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        if (!existing.getArtistId().equals(artistId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        validateZeroSubscribers(membershipId);

        if (membershipMapper.delete(membershipId, artistId) > 0) {
            if (existing.getImageId() != null) {
                fileService.disconnectFiles(List.of(existing.getImageId()), artistId);
            }
        } else {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    // ===================================================================================
    // [PRIVATE HELPERS]
    // ===================================================================================

    /**
     * 멤버십 생성 시 가격 위계(Hierarchy)를 검증합니다.
     */
    private void validatePriceHierarchy(Long artistId, Integer newLevel, Long newPrice) {
        List<MembershipVO> existingMemberships = membershipMapper.selectAllByArtistId(artistId);
        for (MembershipVO existing : existingMemberships) {
            checkHierarchy(existing, newLevel, newPrice);
        }
    }

    /**
     * 멤버십 수정 시 자신을 제외한 다른 멤버십들과의 가격 위계를 검증합니다.
     */
    private void validatePriceHierarchyForUpdate(Long artistId, Long currentId, Integer newLevel, Long newPrice) {
        List<MembershipVO> existingMemberships = membershipMapper.selectAllByArtistId(artistId);
        for (MembershipVO existing : existingMemberships) {
            if (existing.getId().equals(currentId)) continue;
            checkHierarchy(existing, newLevel, newPrice);
        }
    }

    /**
     * 등급 순서에 따른 가격 적절성을 확인하고, 규칙 위반 시 예외를 발생시킵니다.
     */
    private void checkHierarchy(MembershipVO existing, Integer newLevel, Long newPrice) {
        if (existing.getLevelOrder() < newLevel && existing.getPrice() >= newPrice) {
            log.warn("[Membership] 가격 위계 오류 발생. 신규 등급({})의 가격({})이 기존 하위 등급({})의 가격({})보다 같거나 낮습니다.",
                    newLevel, newPrice, existing.getLevelOrder(), existing.getPrice());
            throw new BusinessException(ErrorCode.MEMBERSHIP_PRICE_HIERARCHY_VIOLATION);
        }
        if (existing.getLevelOrder() > newLevel && existing.getPrice() <= newPrice) {
            log.warn("[Membership] 가격 위계 오류 발생. 신규 등급({})의 가격({})이 기존 상위 등급({})의 가격({})보다 같거나 높습니다.",
                    newLevel, newPrice, existing.getLevelOrder(), existing.getPrice());
            throw new BusinessException(ErrorCode.MEMBERSHIP_PRICE_HIERARCHY_VIOLATION);
        }
    }

    /**
     * 해당 멤버십의 활성 구독자가 존재하는지 확인하고, 존재할 경우 수정을 제한합니다.
     */
    private void validateZeroSubscribers(Long membershipId) {
        if (subscriptionMapper.countActiveSubscribers(membershipId) > 0) {
            throw new BusinessException(ErrorCode.MEMBERSHIP_HAS_ACTIVE_SUBSCRIBERS);
        }
    }
}