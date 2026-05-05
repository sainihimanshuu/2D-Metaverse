package com._DMetaverse.backend.config;

import com._DMetaverse.backend.gateway.RoomWebSocketHandler;
import com._DMetaverse.backend.gateway.AuthHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final RoomWebSocketHandler roomWebSocketHandler;
    private final AuthHandshakeInterceptor authHandshakeInterceptor;

    public WebSocketConfig(RoomWebSocketHandler roomWebSocketHandler,
                           AuthHandshakeInterceptor authHandshakeInterceptor) {
        this.roomWebSocketHandler = roomWebSocketHandler;
        this.authHandshakeInterceptor = authHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(roomWebSocketHandler, "/ws")
                .addInterceptors(authHandshakeInterceptor)
                .setAllowedOrigins("*"); 
    }
}