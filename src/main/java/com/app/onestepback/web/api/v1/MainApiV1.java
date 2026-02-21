package com.app.onestepback.web.api.v1;

import com.app.onestepback.domain.dto.main.MainPageDTO;
import com.app.onestepback.global.common.response.ApiResponse;
import com.app.onestepback.service.main.MainService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/main")
@RequiredArgsConstructor
public class MainApiV1 {

    private final MainService mainService;

    /**
     * 애플리케이션 메인 페이지 렌더링에 필요한 통합 데이터를 한 번의 호출로 조회합니다.
     * <p>
     * 급상승 중인 아티스트, 주목받는 펀딩, 실시간 인기 피드 정보를 포함합니다.
     * </p>
     *
     * @return 메인 뷰 구성을 위한 종합 데이터 응답 객체
     */
    @GetMapping
    public ResponseEntity<?> getMainPage() {
        MainPageDTO.Info mainPageData = mainService.getMainPageData();
        return ResponseEntity.ok(ApiResponse.ok(mainPageData));
    }
}