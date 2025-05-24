package io.github.cni274.websockettest.websocket.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
public class ChatMessage {
    private final String roomId;
    private final String sender;
    private final String message;
}
