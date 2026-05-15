package com._DMetaverse.backend.gateway;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com._DMetaverse.backend.models.User;
import com._DMetaverse.backend.repository.UserRepository;
import com._DMetaverse.backend.util.JwtUtil;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    public static final String AUTHENTICATED_USER_ID_ATTR = "authenticatedUserId";
    public static final String AUthENTICATED_USERNAME_ATTR = "authenticatedUsername";

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthHandshakeInterceptor(JwtUtil jwtUtil, UserRepository userRepository){
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        String token = request.getHeaders().getFirst("Authorization");
        Optional<User> user = validateTokenAndGetUserId(token);

        if (user.isPresent()) {
            User authenticatedUser = user.get();
            attributes.put(AUTHENTICATED_USER_ID_ATTR, authenticatedUser.getUserId());
            attributes.put(AUthENTICATED_USERNAME_ATTR, authenticatedUser.getUsername());
            return true;
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    
    }

    private Optional<User> validateTokenAndGetUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring("Bearer ".length());
            try {
                String username = jwtUtil.extractUsername(jwt);
                return userRepository.findByUsername(username);
            } catch (RuntimeException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
