package com.sctrcd.qzr;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * This configuration class sets up the Stomp message broker, through which we
 * can stream events to web clients. The endpoint keeps its SockJS fallback, so
 * the page connects with a plain WebSocket to <code>/drools/websocket</code>,
 * the raw WebSocket URL that SockJS serves beside its own transports.
 *
 * @author Stephen Masters
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/queue");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/drools").withSockJS();
    }
}
