package io.github.cni274.websockettest.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class RoomWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, List<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getRoomId(session);
        roomSessions.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>()).add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = getRoomId(session);
        List<WebSocketSession> sessions = roomSessions.getOrDefault(roomId, List.of());

        for (WebSocketSession s : sessions) {
            if (!s.isOpen()) {
                continue;
            }

            s.sendMessage(new TextMessage("HELLO : " + message.getPayload()));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("connection closed");
        roomSessions.values().forEach(list -> list.remove(session));
    }

    private String getRoomId(WebSocketSession session) {
        String query = session.getUri().getQuery();
        log.info("query: {}", query);
        if (query != null && query.startsWith("roomId=")) {
            return query.split("=")[1];
        }

        return "default";
    }
}
