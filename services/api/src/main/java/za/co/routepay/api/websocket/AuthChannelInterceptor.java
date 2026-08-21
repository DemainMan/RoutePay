package za.co.routepay.api.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import za.co.routepay.api.security.JwtTokenProvider;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                // Check query parameter (SockJS passes it as header)
                String url = accessor.getFirstNativeHeader("url");
                if (url != null && url.contains("token=")) {
                    token = url.substring(url.indexOf("token=") + 6);
                    int ampIdx = token.indexOf('&');
                    if (ampIdx > 0) token = token.substring(0, ampIdx);
                }
            }

            if (token != null && tokenProvider.validateToken(token)) {
                String phone = tokenProvider.getPhoneFromToken(token);
                var authorities = List.of(new SimpleGrantedAuthority("ROLE_COMMUTER"));
                var auth = new UsernamePasswordAuthenticationToken(phone, null, authorities);
                accessor.setUser(auth);
                log.debug("WebSocket STOMP CONNECT authenticated: {}", phone);
            } else {
                log.warn("WebSocket STOMP CONNECT rejected: invalid token");
            }
        }

        return message;
    }
}
