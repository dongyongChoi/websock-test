package io.github.cni274.websockettest.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cni274.websockettest.websocket.model.ChatMessage;
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
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final Map<String, List<WebSocketSession>> roomSessionMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 클라이언트가 웹소켓에 연결 되었을 때
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        ChatMessage chatMessage = new ChatMessage("server", "admin", "👋 서버: 연결을 환영합니다!");
        TextMessage welcomeMessage = new TextMessage(objectMapper.writeValueAsString(chatMessage));
        // 세션이 연결되었을 때 알림 메시지 전송
        session.sendMessage(welcomeMessage);
    }

    /**
     * 클라이언트로부터 메시지를 받았을 때
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("session: {}", session.getId());
        log.info("room count: {}", roomSessionMap.size());

        // 1. JSON -> 객체로 변환
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        String roomId = chatMessage.getRoomId();

        // 2. 해당 방 세션 리스트 가져오기 (없으면 새로 생성)
        List<WebSocketSession> sessionList = roomSessionMap.computeIfAbsent(roomId, k -> new CopyOnWriteArrayList<>());

        // 3. 방에 세션이 등록되어 있지 않으면 추가
        if (!sessionList.contains(session)) {
            sessionList.add(session);
        }

        // 해당 방의 모든 세션에게 메시지 전달
        for (WebSocketSession s : sessionList) {
            if (!s.isOpen()) {
                continue;
            }

            s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        }
    }

    /**
     * 클라이언트 연결 종료 시 호출
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        roomSessionMap.forEach((roomId, sessionList) -> sessionList.remove(session));
    }
}
