package com.erp_mes.erp.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		// 클라이언트가 웹소켓에 접속할 엔드포인트: /ws/chat
		// SockJS는 웹소켓을 지원하지 않는 구형 브라우저를 위해 대체 통신 기술을 제공
		registry.addEndpoint("/ws/chat")
	    		.setAllowedOriginPatterns("*") // 모든 출처에서 접근 허용
	    		.withSockJS();
	}

   @Override
   public void configureMessageBroker(MessageBrokerRegistry registry) {
      // 서버의 @Controller로 라우팅될 메시지 prefix
      registry.setApplicationDestinationPrefixes("/app");
      // 메시지 브로커로 라우팅될 메시지 prefix (구독)
      registry.enableSimpleBroker("/topic", "/queue", "/user");
      registry.setUserDestinationPrefix("/user");
   }

}
