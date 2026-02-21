package com.app.onestepback.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 실시간 양방향 통신을 위한 WebSocket 및 STOMP 메시지 브로커 구성 클래스입니다.
 * <p>
 * 클라이언트 접속 엔드포인트를 정의하고 CORS 정책 및 브라우저 호환성을 설정하며,
 * 메시지의 발행(Publish) 및 구독(Subscribe)을 처리하기 위한 라우팅 경로를 구성합니다.
 * </p>
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 클라이언트가 WebSocket 연결을 시도할 엔드포인트를 등록합니다.
     *
     * @param registry STOMP 엔드포인트 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 클라이언트 간의 메시지 라우팅을 담당할 메시지 브로커를 구성합니다.
     *
     * @param registry 메시지 브로커 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");
        registry.setApplicationDestinationPrefixes("/pub");
    }
}