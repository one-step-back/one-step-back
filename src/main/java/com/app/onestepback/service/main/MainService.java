package com.app.onestepback.service.main;

import com.app.onestepback.domain.dto.main.MainPageDTO;

/**
 * 애플리케이션의 메인 뷰 구성을 위한 비즈니스 로직을 정의하는 서비스 인터페이스입니다.
 * <p>
 * 급상승 아티스트, 주목받는 펀딩, 실시간 인기 피드 등
 * 메인 화면 노출에 필요한 다중 도메인의 데이터를 통합하여 제공합니다.
 * </p>
 */
public interface MainService {

    /**
     * 메인 페이지 렌더링에 필요한 모든 집계 데이터를 단일 객체로 반환합니다.
     *
     * @return 메인 페이지 통합 데이터가 포함된 DTO
     */
    MainPageDTO.Info getMainPageData();
}