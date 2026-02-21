package com.app.onestepback.service.membership;

import com.app.onestepback.domain.dto.membership.MembershipDetailDTO;
import com.app.onestepback.domain.dto.membership.MembershipDTO;
import com.app.onestepback.domain.dto.membership.MembershipManageDTO;
import com.app.onestepback.service.membership.cmd.MembershipRegisterCmd;
import com.app.onestepback.service.membership.cmd.MembershipUpdateCmd;

import java.util.List;

/**
 * 아티스트의 멤버십(후원) 상품과 관련된 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * <p>
 * 멤버십 상품의 생성, 조회, 수정 및 활성 상태 관리 기능을 제공합니다.
 * </p>
 */
public interface MembershipService {

    /**
     * 신규 멤버십 상품을 시스템에 등록합니다.
     *
     * @param cmd 멤버십 등록에 필요한 메타데이터 및 이미지 정보가 포함된 커맨드 객체
     */
    void registerMembership(MembershipRegisterCmd cmd);

    /**
     * 아티스트의 멤버십 관리 대시보드 구성을 위한 종합 정보를 조회합니다.
     *
     * @param artistId 조회를 요청한 아티스트의 고유 식별자
     * @return 정산 계좌 등록 여부 및 보유 멤버십 목록이 통합된 DTO
     */
    MembershipManageDTO getMembershipManageInfo(Long artistId);

    /**
     * 특정 아티스트가 운영 중인 멤버십 목록을 조회합니다.
     *
     * @param artistId 조회 대상 아티스트의 고유 식별자
     * @param viewerId 현재 페이지를 열람 중인 회원의 고유 식별자 (구독 상태 확인용)
     * @return 멤버십 기본 정보가 담긴 DTO 리스트
     */
    List<MembershipDTO> getMemberships(Long artistId, Long viewerId);

    /**
     * 단일 멤버십 상품의 상세 정보를 조회합니다.
     *
     * @param membershipId 조회할 멤버십의 고유 식별자
     * @return 멤버십 상세 정보 DTO
     */
    MembershipDetailDTO getMembershipInfo(Long membershipId);

    /**
     * 등록된 멤버십의 내용 및 첨부 이미지를 수정합니다.
     *
     * @param cmd 멤버십 수정에 필요한 데이터가 포함된 커맨드 객체
     */
    void update(MembershipUpdateCmd cmd);

    /**
     * 멤버십 상품의 노출(활성화) 여부를 변경합니다.
     *
     * @param artistId     상태 변경을 요청한 아티스트의 고유 식별자 (권한 검증용)
     * @param membershipId 상태를 변경할 멤버십의 고유 식별자
     * @param active       활성화 여부 (true: 노출, false: 숨김)
     */
    void updateStatus(Long artistId, Long membershipId, boolean active);

    /**
     * 멤버십 상품을 영구적으로 삭제합니다.
     * <p>
     * 현재 해당 멤버십을 이용 중인 활성 구독자가 존재할 경우 삭제가 제한됩니다.
     * </p>
     *
     * @param artistId     삭제를 요청한 아티스트의 고유 식별자 (권한 검증용)
     * @param membershipId 삭제할 멤버십의 고유 식별자
     */
    void delete(Long artistId, Long membershipId);
}