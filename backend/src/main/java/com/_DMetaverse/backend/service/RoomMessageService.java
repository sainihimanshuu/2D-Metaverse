package com._DMetaverse.backend.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;

import com._DMetaverse.backend.dto.roomruntime.RoomSnapshot;
import com._DMetaverse.backend.gateway.AuthHandshakeInterceptor;
import com._DMetaverse.backend.models.User;
import com._DMetaverse.backend.realtime.RoomRuntimeManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RoomMessageService {
    private final RoomRuntimeManager roomRuntimeManager;
    private final RoomService roomService;
    private final ObjectMapper objectMapper;

    RoomMessageService(RoomRuntimeManager roomRuntimeManager,
                       RoomService roomService,
                       ObjectMapper objectMapper) {
        this.roomRuntimeManager = roomRuntimeManager;
        this.roomService = roomService;
        this.objectMapper = objectMapper;
    }

    public void handleJoinRoom(WebSocketSession session, JsonNode content) throws IOException {
        System.out.println("Inside RoomMessageService.handleJoinRoom, session: " + session + ", content: " + content);

        Long authenticatedUserId = getAuthenticatedUserId(session);
        Long roomId = getRequiredRoomId(content);

        roomService.validateRoomJoinAccess(authenticatedUserId, roomId);
        User user = roomService.getUserById(authenticatedUserId);
        RoomSnapshot snapshot = roomRuntimeManager.joinRoomRuntime(roomId, user);

        sendMessage(session, Map.of(
                "type", "ROOM_SNAPSHOT",
                "content", snapshot
        ));
    }

    public void handleLeaveRoom(WebSocketSession session, JsonNode content) {
        System.out.println("Inside RoomMessageService.handleLeaveRoom, session: " + session + ", content: " + content);

        Long authenticatedUserId = getAuthenticatedUserId(session);
        Long roomId = getRequiredRoomId(content);

        roomRuntimeManager.leaveRoomRuntime(roomId, authenticatedUserId);
    }

    public void handleChatMessage(WebSocketSession session, JsonNode content) {
        System.out.println("Inside RoomMessageService.handleChatMessage, session: " + session + ", content: " + content);

        //TODO: Will be handling chat functionality in the next iteration
    }

    public void handlePositionUpdate(WebSocketSession session, JsonNode content) {
        System.out.println("Inside RoomMessageService.handlePositionUpdate, session: " + session + ", content: " + content);
    }

    private Long getAuthenticatedUserId(WebSocketSession session) {
        Object value = session.getAttributes().get(AuthHandshakeInterceptor.AUTHENTICATED_USER_ID_ATTR);
        if (value instanceof Long userId) {
            return userId;
        }

        throw new IllegalStateException("Unauthenticated websocket session");
    }

    private Long getRequiredRoomId(JsonNode content) {
        if (content == null || content.get("roomId") == null) {
            throw new IllegalArgumentException("roomId is required");
        }

        return content.get("roomId").asLong();
    }

    private void sendMessage(WebSocketSession session, Object payload) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
    }
}
