package com._DMetaverse.backend.gateway;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String token = request.getHeaders().getFirst("Authorization");
        String userId = validateTokenAndGetUserId(token);

        if (userId != null) {
            attributes.put("userId", userId);
            return true;
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    
    }

    private String validateTokenAndGetUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring("Bearer ".length());
            try {
                Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(jwt)
                    .getBody();
                return claims.getSubject();
            } catch (JwtException e) {
                return null;
            }
        }
        return null;
    }
}
