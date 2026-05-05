package com._DMetaverse.backend.gateway;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com._DMetaverse.backend.dto.websocket.WebSocketMessageDTO;
import com._DMetaverse.backend.models.WebSocketMessageType;
import com._DMetaverse.backend.service.RoomMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RoomWebSocketHandler implements WebSocketHandler{
    private final List<WebSocketSession> sessions;
    private final RoomMessageService roomMessageService;

    public RoomWebSocketHandler(RoomMessageService roomMessageService) {
        this.sessions = new CopyOnWriteArrayList<>();
        this.roomMessageService = roomMessageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Inside afterConnectionEstablished, session: " + session.getId());
        sessions.add(session);

        System.out.println("New connection established: " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("Inside handleMessage, session: " + session.getId() + ", payload: " + message.getPayload());
        String payload = (String) message.getPayload();

        ObjectMapper objectMapper = new ObjectMapper();
        WebSocketMessageDTO wsMessage = objectMapper.readValue(payload, WebSocketMessageDTO.class);

        WebSocketMessageType type = WebSocketMessageType.valueOf(wsMessage.getType());

        switch (type) {
            case WebSocketMessageType.JOIN_ROOM:
                roomMessageService.handleJoinRoom(session, wsMessage.getContent());
                break;

            case WebSocketMessageType.LEAVE_ROOM:
                roomMessageService.handleLeaveRoom(session, wsMessage.getContent());
                break;

            case WebSocketMessageType.CHAT_MESSAGE:
                roomMessageService.handleChatMessage(session, wsMessage.getContent());
                break;

            case WebSocketMessageType.POSITION_UPDATE:
                roomMessageService.handlePositionUpdate(session, wsMessage.getContent());
                break;

            default: 
                System.err.println("Unknown message type: " + wsMessage.getType());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("Inside handleTransportError, session: " + session.getId() + ", error: " + exception.getMessage());
        
        if (session.isOpen()) {
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        System.out.println("Inside afterConnectionClosed, session: " + session.getId() + ", closeStatus: " + closeStatus);
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
