package com._DMetaverse.backend.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com._DMetaverse.backend.realtime.RoomRuntimeManager;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class RoomMessageService {
    private RoomRuntimeManager roomRuntimeManager;

    RoomMessageService(RoomRuntimeManager roomRuntimeManager) {
        this.roomRuntimeManager = roomRuntimeManager;
    }

    public void handleJoinRoom(WebSocketSession session, JsonNode content) {
        System.out.println("Inside RoomMessageService.handleJoinRoom, session: " + session + ", content: " + content);
        
        roomRuntimeManager.joinRoomRuntime(content);
    }

    public void handleLeaveRoom(WebSocketSession session, JsonNode content) {
        System.out.println("Inside RoomMessageService.handleLeaveRoom, session: " + session + ", content: " + content);

        roomRuntimeManager.leaveRoomRuntime(content);
    }

    public void handleChatMessage(WebSocketSession session, JsonNode content) {
        System.out.println("Inside RoomMessageService.handleChatMessage, session: " + session + ", content: " + content);

        //TODO: Will be handling chat functionality in the next iteration
    }

    public void handlePositionUpdate(WebSocketSession session, JsonNode content) {
        System.out.println("Inside RoomMessageService.handlePositionUpdate, session: " + session + ", content: " + content);
    }
}
