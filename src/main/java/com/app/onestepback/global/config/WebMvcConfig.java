package com.app.onestepback.global.config;

import com.app.onestepback.domain.type.artist.ArtistSortType;
import com.app.onestepback.global.resolver.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.List;

/**
 * Spring MVC 웹 계층의 전역 동작 설정을 담당하는 구성 클래스입니다.
 * <p>
 * 커스텀 아규먼트 리졸버, 데이터 타입 컨버터, 정적 리소스 핸들러 및 네트워크 대역폭 절약을 위한
 * 캐싱/ETag 필터 등록을 수행합니다.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginUserArgumentResolver loginUserArgumentResolver;

    /**
     * 컨트롤러의 파라미터를 동적으로 해석하기 위한 커스텀 아규먼트 리졸버를 등록합니다.
     *
     * @param resolvers 등록할 아규먼트 리졸버 목록
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }

    /**
     * HTTP 요청을 통해 전달된 문자열 데이터를 특정 객체 또는 열거형(Enum)으로 변환하는 컨버터를 등록합니다.
     *
     * @param registry 포맷터 및 컨버터 레지스트리
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToArtistSortTypeConverter());
    }

    /**
     * 애플리케이션의 정적 리소스(이미지, CSS, JS 등) 제공 경로와 캐싱 정책을 설정합니다.
     *
     * @param registry 정적 리소스 핸들러 레지스트리
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**", "/favicon.ico")
                .addResourceLocations("classpath:/static/images/", "classpath:/static/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic());

        registry.addResourceHandler("/css/**", "/js/**")
                .addResourceLocations("classpath:/static/css/", "classpath:/static/js/")
                .setCacheControl(CacheControl.noCache());
    }

    /**
     * HTTP 응답 본문의 변경 여부를 검증하는 ETag 필터를 등록합니다.
     * <p>
     * 클라이언트가 보유한 캐시와 서버의 응답이 동일할 경우 304 상태 코드를 반환하여
     * 불필요한 데이터 전송을 최소화합니다.
     * </p>
     *
     * @return ShallowEtagHeaderFilter 인스턴스
     */
    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    /**
     * 요청 파라미터로 입력된 문자열을 아티스트 정렬 기준을 나타내는 {@link ArtistSortType} 열거형으로 변환합니다.
     */
    private static class StringToArtistSortTypeConverter implements Converter<String, ArtistSortType> {
        @Override
        public ArtistSortType convert(@NonNull String source) {
            if (!StringUtils.hasText(source)) {
                return ArtistSortType.RANDOM;
            }
            try {
                return ArtistSortType.valueOf(source.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ArtistSortType.RANDOM;
            }
        }
    }
}