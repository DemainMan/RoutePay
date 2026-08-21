package za.co.routepay.momo.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthTokenTest {

    @Test
    @DisplayName("builder creates token with defaults")
    void builderCreatesWithDefaults() {
        AuthToken token = AuthToken.builder()
                .accessToken("jwt-token-123")
                .phone("+27821234567")
                .build();

        assertThat(token.getAccessToken()).isEqualTo("jwt-token-123");
        assertThat(token.getTokenType()).isEqualTo("Bearer");
        assertThat(token.getPhone()).isEqualTo("+27821234567");
        assertThat(token.isMock()).isFalse();
    }

    @Test
    @DisplayName("all fields settable")
    void allFieldsSettable() {
        AuthToken token = AuthToken.builder()
                .accessToken("jwt-456")
                .tokenType("Bearer")
                .expiresIn(7200)
                .phone("+27821234567")
                .mock(true)
                .build();

        assertThat(token.getAccessToken()).isEqualTo("jwt-456");
        assertThat(token.getExpiresIn()).isEqualTo(7200);
        assertThat(token.isMock()).isTrue();
    }
}
