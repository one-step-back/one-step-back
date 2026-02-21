package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.ArtistBankAccountDTO;
import com.app.onestepback.domain.dto.artist.ArtistDTO;
import com.app.onestepback.domain.dto.artist.ArtistHomeDTO;
import com.app.onestepback.domain.dto.artist.ArtistPageDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.model.ArtistVO;
import com.app.onestepback.domain.type.artist.ArtistSortType;
import com.app.onestepback.global.exception.BusinessException;
import com.app.onestepback.global.exception.ErrorCode;
import com.app.onestepback.repository.ArtistMapper;
import com.app.onestepback.service.artist.cmd.ArtistRegisterCMD;
import com.app.onestepback.service.artist.cmd.ArtistUpdateCmd;
import com.app.onestepback.service.file.FileService;
import com.app.onestepback.service.file.cmd.SaveFileCmd;
import com.app.onestepback.service.member.MemberService;
import com.app.onestepback.service.member.cmd.MemberUpdateCmd;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 아티스트 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 * <p>
 * 클래스 레벨의 트랜잭션을 제거하고, 각 메서드의 성격(읽기/쓰기)에 맞춰
 * 세밀한 트랜잭션 관리를 수행하도록 최적화되었습니다.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final FileService fileService;
    private final MemberService memberService;
    private final ArtistMapper artistMapper;

    @Override
    @Transactional
    public Long saveArtist(ArtistRegisterCMD cmd) {
        String blogImageId = uploadBlogImage(cmd.memberId(), cmd.blogImage());

        ArtistVO newArtist = ArtistVO.builder()
                .artistId(cmd.memberId())
                .artistBlogName(cmd.blogName())
                .artistDescription(cmd.blogDescription())
                .artistBlogImageId(blogImageId)
                .build();

        artistMapper.insert(newArtist);

        return newArtist.getArtistId();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "artistDetail", key = "#artistId + '_' + (#viewerId != null ? #viewerId : 'anonymous')")
    public ArtistPageDTO getArtistDetail(Long artistId, Long viewerId) {
        return artistMapper.selectArtistPageInfo(artistId, viewerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ARTIST_NOT_FOUND));
    }

    @Override
    @Transactional
    @CacheEvict(value = "artistDetail", allEntries = true)
    public SessionUser updateArtist(ArtistUpdateCmd cmd) {
        String fileId = uploadBlogImage(cmd.memberId(), cmd.blogImage());

        ArtistVO updatedArtist = ArtistVO.builder()
                .artistId(cmd.memberId())
                .artistBlogName(cmd.blogName())
                .artistDescription(cmd.description())
                .artistBlogImageId(fileId)
                .build();

        artistMapper.updateArtist(updatedArtist);

        MemberUpdateCmd memberUpdateCmd = new MemberUpdateCmd(
                cmd.memberId(),
                cmd.memberNickname(),
                null,
                cmd.profileImage()
        );

        return memberService.updateProfile(memberUpdateCmd);
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistBankAccountDTO getBankAccount(Long artistId) {
        return artistMapper.selectBankAccount(artistId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistDTO.MyArtists getMyArtists(Long memberId) {
        return artistMapper.selectMyArtists(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArtistDTO.ListInfo> getArtistList(Long memberId, ArtistSortType sort) {
        ArtistSortType activeSort = (sort == null) ? ArtistSortType.RANDOM : sort;
        return artistMapper.selectArtistList(memberId, activeSort.name());
    }

    @Override
    @Transactional(readOnly = true)
    public ArtistHomeDTO.Info getArtistHomeInfo(Long artistId) {
        ArtistHomeDTO.FundingHighlight funding = artistMapper.selectHomeFunding(artistId).orElse(null);
        List<ArtistHomeDTO.RecentFeed> feeds = artistMapper.selectHomeFeeds(artistId);
        List<ArtistHomeDTO.MembershipInfo> memberships = artistMapper.selectHomeMemberships(artistId);

        return ArtistHomeDTO.Info.builder()
                .funding(funding)
                .feeds(feeds)
                .memberships(memberships)
                .build();
    }

    @Override
    @Transactional
    public void updateAccount(Long artistId, ArtistBankAccountDTO bankInfo) {
        artistMapper.updateAccount(artistId, bankInfo);
    }

    /**
     * 아티스트의 프로필 또는 블로그 이미지를 파일 스토리지에 업로드하고 식별자를 반환합니다.
     *
     * @param targetId  파일과 연관될 대상 식별자 (아티스트 ID)
     * @param imageFile 클라이언트로부터 전달받은 멀티파트 파일 객체
     * @return 업로드된 파일의 고유 식별자(ULID). 파일이 없을 경우 null 반환
     */
    private String uploadBlogImage(long targetId, MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        SaveFileCmd saveCmd = new SaveFileCmd(
                targetId,
                targetId,
                "ARTIST",
                imageFile.getOriginalFilename(),
                imageFile.getContentType(),
                imageFile.getSize(),
                () -> {
                    try {
                        return imageFile.getInputStream();
                    } catch (IOException e) {
                        throw new IllegalStateException("이미지 스트림을 읽는 중 I/O 오류가 발생하였습니다.", e);
                    }
                }
        );

        return fileService.store(saveCmd).id();
    }
}