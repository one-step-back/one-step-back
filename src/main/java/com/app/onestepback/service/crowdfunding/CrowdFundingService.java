package com.app.onestepback.service.crowdfunding;

import com.app.onestepback.domain.dto.crowdfunding.CrowdFundingDTO;
import com.app.onestepback.domain.dto.payment.FundingPaymentDTO;
import com.app.onestepback.domain.model.CrowdFundingVO;
import com.app.onestepback.global.common.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 크라우드 펀딩과 관련된 전반적인 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * <p>
 * 펀딩 프로젝트의 제안 및 생성, 상태 관리(승낙, 반려, 성공, 실패),
 * 후원 결제 처리 및 조회 기능 등을 총괄합니다.
 * </p>
 */
public interface CrowdFundingService {

    /**
     * 새로운 크라우드 펀딩 프로젝트를 제안하거나 생성합니다.
     *
     * @param cmd     크라우드 펀딩 생성에 필요한 데이터가 담긴 커맨드 객체
     * @param mainImg 펀딩 대표 이미지 파일 (선택 사항)
     * @return 생성된 펀딩 프로젝트의 고유 식별자(ID)
     */
    Long create(CrowdFundingServiceCommand.Create cmd, MultipartFile mainImg);

    /**
     * 특정 펀딩 프로젝트에 대한 후원금을 결제하고 내역을 생성합니다.
     *
     * @param fundingId       후원 대상 펀딩 프로젝트 식별자
     * @param memberId        결제를 수행하는 회원의 식별자
     * @param paymentMethodId 사용될 결제 수단의 고유 식별자
     * @param amount          최종 후원 결제 금액
     * @return 생성된 펀딩 결제 내역의 고유 식별자(ID)
     */
    Long fund(Long fundingId, Long memberId, Long paymentMethodId, Long amount);

    /**
     * 특정 아티스트에게 속한 크라우드 펀딩 목록을 전체 조회합니다. (아티스트 대시보드용)
     *
     * @param artistId 대상을 식별할 아티스트의 고유 식별자
     * @return 펀딩 목록 뷰 DTO 리스트
     */
    List<CrowdFundingDTO.ListView> getList(Long artistId);

    /**
     * 전체 공개된 크라우드 펀딩 목록을 상태 조건에 따라 필터링하여 조회합니다. (메인 리스트용)
     *
     * @param status 필터링할 펀딩 상태 (ALL, PROCEEDING, UPCOMING, ENDED 등)
     * @return 공개 펀딩 목록 DTO 리스트
     */
    List<CrowdFundingDTO.PublicList> getFundingList(String status);

    /**
     * 특정 펀딩 프로젝트의 상세 정보를 조회합니다.
     *
     * @param fundingId 조회할 펀딩 프로젝트 식별자
     * @return 펀딩 상세 정보 DTO
     */
    CrowdFundingDTO.Detail getDetail(Long fundingId);

    /**
     * 회원이 특정 펀딩에 결제를 진행하기 전 확인해야 할 결제 화면용 정보를 조회합니다.
     *
     * @param fundingId 펀딩 프로젝트 식별자
     * @param memberId  조회하는 회원의 식별자
     * @return 결제 화면 구성을 위한 뷰 DTO
     */
    CrowdFundingDTO.PaymentView getPaymentInfo(Long fundingId, Long memberId);

    /**
     * 완료된 펀딩 결제 내역에 대한 영수증 상세 정보를 조회합니다.
     *
     * @param fundingId 펀딩 프로젝트 식별자
     * @param paymentId 결제 내역 식별자
     * @param memberId  조회 및 권한 검증을 위한 회원의 식별자
     * @return 결제 영수증 정보 DTO
     */
    CrowdFundingDTO.Receipt getPaymentReceipt(Long fundingId, Long paymentId, Long memberId);

    /**
     * 특정 회원의 펀딩 후원 결제 내역을 무한 스크롤 페이징 방식으로 조회합니다.
     *
     * @param memberId      조회할 회원의 고유 식별자
     * @param lastPaymentId 이전 페이지의 마지막 결제 내역 식별자 (커서 기반 페이징)
     * @param size          조회할 데이터의 크기
     * @return 후원 내역 데이터가 포함된 슬라이스(Slice) 객체
     */
    Slice<FundingPaymentDTO.History> getMyFundingHistory(Long memberId, Long lastPaymentId, int size);

    /**
     * 펀딩 종료일 이후 일정 기간이 경과하여 방치된 펀딩 목록을 조회합니다.
     *
     * @return 기한 만료 방치 펀딩 엔티티 리스트
     */
    List<CrowdFundingVO> getExpiredEndedFundings();

    /**
     * 아티스트가 회원이 제안한 펀딩을 검토한 후, 수정 및 승낙(오픈) 처리합니다.
     *
     * @param cmd 펀딩 수정 및 승낙 처리를 위한 커맨드 객체
     */
    void editAndAccept(CrowdFundingServiceCommand.EditAccept cmd);

    /**
     * 아티스트가 회원이 제안한 펀딩을 거절(반려) 처리합니다.
     *
     * @param fundingId 펀딩 프로젝트 식별자
     * @param artistId  검증을 위한 아티스트 식별자
     * @param reason    반려 사유 상세 내역
     */
    void reject(Long fundingId, Long artistId, String reason);

    /**
     * 기한이 정상적으로 종료된 펀딩을 최종 성공 처리하고, 모금액을 아티스트의 정산 대기열로 이관합니다.
     *
     * @param fundingId 펀딩 프로젝트 식별자
     * @param artistId  검증을 위한 아티스트 식별자
     */
    void success(Long fundingId, Long artistId);

    /**
     * 기한이 종료된 펀딩을 최종 실패 처리하고, 참여한 모든 회원의 결제를 일괄 취소(환불)합니다.
     *
     * @param fundingId 펀딩 프로젝트 식별자
     * @param artistId  검증을 위한 아티스트 식별자
     * @param reason    실패 및 환불 처리 사유
     */
    void fail(Long fundingId, Long artistId, String reason);

    /**
     * 진행 중이면서 설정된 기한이 만료된 펀딩 프로젝트의 상태를 시스템 스케줄러를 통해 일괄 종료(ENDED) 상태로 갱신합니다.
     *
     * @return 상태가 업데이트된 총 펀딩 건수
     */
    int updateExpiredFundings();
}