package za.co.routepay.api.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-for-jwt-provider-minimum-32-chars!!";
    private static final long EXPIRY_MS = 86400000; // 24 hours

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        provider = new JwtTokenProvider(SECRET, EXPIRY_MS);
    }

    @Test
    void generate_and_parse_token() {
        String token = provider.generateToken("+27821234567", "COMMUTER");

        assertThat(token).isNotEmpty();
        assertThat(provider.getPhoneFromToken(token)).isEqualTo("+27821234567");
        assertThat(provider.getRoleFromToken(token)).isEqualTo("COMMUTER");
    }

    @Test
    void validate_valid_token() {
        String token = provider.generateToken("+27821234567", "OPERATOR");
        assertThat(provider.validateToken(token)).isTrue();
    }

    @Test
    void validate_expired_token() throws Exception {
        JwtTokenProvider shortLived = new JwtTokenProvider(SECRET, 1); // 1ms expiry
        String token = shortLived.generateToken("+27821234567", "COMMUTER");
        Thread.sleep(10); // wait for expiry
        assertThat(shortLived.validateToken(token)).isFalse();
    }

    @Test
    void validate_tampered_token() {
        String token = provider.generateToken("+27821234567", "COMMUTER");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";
        assertThat(provider.validateToken(tampered)).isFalse();
    }

    @Test
    void get_role_from_token_operator() {
        String token = provider.generateToken("+27821234567", "OPERATOR");
        assertThat(provider.getRoleFromToken(token)).isEqualTo("OPERATOR");
    }

    @Test
    void short_secret_throws() {
        assertThatThrownBy(() -> new JwtTokenProvider("short", 86400000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JWT_SECRET");
    }

    @Test
    void blank_secret_throws() {
        assertThatThrownBy(() -> new JwtTokenProvider("", 86400000))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void null_secret_throws() {
        assertThatThrownBy(() -> new JwtTokenProvider(null, 86400000))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
