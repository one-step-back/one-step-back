package com.app.onestepback.service.feed;

import com.app.onestepback.domain.dto.feed.FeedDetailDTO;
import com.app.onestepback.domain.dto.feed.FeedListDTO;
import com.app.onestepback.domain.dto.feed.FeedSearchCond;
import com.app.onestepback.global.common.Slice;
import com.app.onestepback.service.feed.cmd.FeedModifyCmd;
import com.app.onestepback.service.feed.cmd.FeedWriteCmd;

/**
 * 아티스트의 피드(게시글)와 관련된 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * <p>
 * 피드의 작성, 조회, 수정, 삭제(Soft Delete) 및
 * 미디어 파일(이미지, 비디오) 처리 상태와의 연동을 총괄합니다.
 * </p>
 */
public interface FeedService {

    /**
     * 새로운 피드를 작성하고 첨부된 미디어 파일을 연결합니다.
     * <p>
     * 피드가 성공적으로 작성되면 아티스트를 구독 중인 회원들에게 실시간 알림을 브로드캐스트합니다.
     * </p>
     *
     * @param cmd 피드 작성에 필요한 데이터가 담긴 커맨드 객체
     */
    void write(FeedWriteCmd cmd);

    /**
     * 특정 조건에 맞는 피드 목록을 무한 스크롤(Slice) 방식으로 조회합니다.
     *
     * @param cond 조회할 피드의 검색 조건(아티스트 ID, 페이징 정보 등)이 담긴 객체
     * @return 피드 목록 데이터가 포함된 슬라이스(Slice) 객체
     */
    Slice<FeedListDTO> getList(FeedSearchCond cond);

    /**
     * 특정 피드의 상세 정보를 조회하고 조회수를 1 증가시킵니다.
     *
     * @param feedId   조회할 피드의 고유 식별자
     * @param artistId 피드가 속한 아티스트의 고유 식별자
     * @param viewerId 현재 피드를 조회하고 있는 사용자의 고유 식별자 (권한 확인용)
     * @return 피드 상세 정보 DTO
     */
    FeedDetailDTO getDetail(long feedId, long artistId, Long viewerId);

    /**
     * 피드 수정을 위해 원본 데이터를 조회합니다. (조회수는 증가하지 않습니다)
     *
     * @param feedId   수정할 피드의 고유 식별자
     * @param artistId 피드가 속한 아티스트의 고유 식별자 (소유권 검증)
     * @return 수정을 위한 피드 원본 데이터 DTO
     */
    FeedDetailDTO getForEdit(long feedId, long artistId);

    /**
     * 기존에 작성된 피드의 내용 및 첨부 파일을 수정합니다.
     *
     * @param cmd 피드 수정에 필요한 데이터가 담긴 커맨드 객체
     */
    void modify(FeedModifyCmd cmd);

    /**
     * 특정 피드를 논리적으로 삭제(Soft Delete) 처리합니다.
     *
     * @param feedId   삭제할 피드의 고유 식별자
     * @param artistId 피드가 속한 아티스트의 고유 식별자 (소유권 검증)
     */
    void softDelete(long feedId, long artistId);
}