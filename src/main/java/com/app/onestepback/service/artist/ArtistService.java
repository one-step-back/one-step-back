package com.app.onestepback.service.artist;

import com.app.onestepback.domain.dto.artist.ArtistBankAccountDTO;
import com.app.onestepback.domain.dto.artist.ArtistDTO;
import com.app.onestepback.domain.dto.artist.ArtistHomeDTO;
import com.app.onestepback.domain.dto.artist.ArtistPageDTO;
import com.app.onestepback.domain.dto.member.SessionUser;
import com.app.onestepback.domain.type.artist.ArtistSortType;
import com.app.onestepback.service.artist.cmd.ArtistRegisterCMD;
import com.app.onestepback.service.artist.cmd.ArtistUpdateCmd;

import java.util.List;

/**
 * 아티스트와 관련된 핵심 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * <p>
 * 아티스트의 등록, 수정, 조회 및 홈 화면 구성, 계좌 정보 관리 등의 기능을 제공합니다.
 * </p>
 */
public interface ArtistService {

    /**
     * 신규 아티스트 정보를 등록합니다.
     *
     * @param cmd 아티스트 등록에 필요한 데이터가 담긴 커맨드 객체
     * @return 등록된 아티스트의 고유 식별자(ID)
     */
    Long saveArtist(ArtistRegisterCMD cmd);

    /**
     * 특정 아티스트의 상세 페이지 정보를 조회합니다.
     * <p>
     * 조회 성능 향상을 위해 캐싱 처리가 권장되며, 요청한 사용자(viewerId)의
     * 구독 상태 등에 따라 동적인 데이터를 제공할 수 있습니다.
     * </p>
     *
     * @param artistId 조회할 아티스트의 고유 식별자
     * @param viewerId 현재 페이지를 조회하고 있는 사용자의 고유 식별자 (비회원일 경우 null)
     * @return 아티스트 상세 정보 DTO
     */
    ArtistPageDTO getArtistDetail(Long artistId, Long viewerId);

    /**
     * 기존 아티스트의 프로필 및 블로그 정보를 수정합니다.
     * <p>
     * 회원(Member) 정보와 아티스트(Artist) 정보가 동시에 업데이트되며,
     * 정보 변경 시 관련 캐시를 무효화(Evict)해야 합니다.
     * </p>
     *
     * @param cmd 아티스트 수정에 필요한 데이터가 담긴 커맨드 객체
     * @return 업데이트가 반영된 최신 세션 사용자 정보
     */
    SessionUser updateArtist(ArtistUpdateCmd cmd);

    /**
     * 특정 아티스트의 정산용 계좌 정보를 조회합니다.
     *
     * @param artistId 계좌 정보를 조회할 아티스트의 고유 식별자
     * @return 계좌 정보 DTO (등록된 계좌가 없을 경우 null 반환)
     */
    ArtistBankAccountDTO getBankAccount(Long artistId);

    /**
     * 특정 회원이 구독하거나 관리 중인 아티스트 목록 정보를 조회합니다.
     *
     * @param memberId 조회할 회원의 고유 식별자
     * @return 아티스트 목록 요약 정보 DTO
     */
    ArtistDTO.MyArtists getMyArtists(Long memberId);

    /**
     * 전체 아티스트 목록을 지정된 정렬 기준에 따라 조회합니다.
     *
     * @param memberId 현재 접속 중인 회원의 고유 식별자 (구독 상태 확인용)
     * @param sort     아티스트 목록 정렬 기준 (예: RANDOM, LATEST 등)
     * @return 아티스트 목록 DTO 리스트
     */
    List<ArtistDTO.ListInfo> getArtistList(Long memberId, ArtistSortType sort);

    /**
     * 아티스트의 홈 화면 구성에 필요한 복합 정보(펀딩, 피드, 멤버십 등)를 조회합니다.
     *
     * @param artistId 홈 화면을 조회할 아티스트의 고유 식별자
     * @return 홈 화면 구성 정보가 통합된 DTO
     */
    ArtistHomeDTO.Info getArtistHomeInfo(Long artistId);

    /**
     * 아티스트의 정산용 계좌 정보를 등록 또는 갱신합니다.
     *
     * @param artistId 대상 아티스트의 고유 식별자
     * @param bankInfo 등록 또는 변경할 계좌 정보 DTO
     */
    void updateAccount(Long artistId, ArtistBankAccountDTO bankInfo);
}