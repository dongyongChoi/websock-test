package io.github.cni274.websockettest.websocket.config;

import io.github.cni274.websockettest.websocket.handler.RoomWebSocketHandler;
import io.github.cni274.websockettest.websocket.handler.SimpleWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final SimpleWebSocketHandler simpleWebSocketHandler;
    private final RoomWebSocketHandler roomWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(simpleWebSocketHandler, "/ws-chat").setAllowedOrigins("*");
        registry.addHandler(roomWebSocketHandler, "/ws-chat").setAllowedOrigins("*");
    }
}
