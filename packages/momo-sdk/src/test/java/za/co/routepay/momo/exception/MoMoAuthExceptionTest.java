package za.co.routepay.momo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoMoAuthExceptionTest {

    @Test
    @DisplayName("constructor with message")
    void constructorWithMessage() {
        MoMoAuthException ex = new MoMoAuthException("Token expired");
        assertThat(ex.getMessage()).isEqualTo("Token expired");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("constructor with cause")
    void constructorWithCause() {
        RuntimeException cause = new RuntimeException("network error");
        MoMoAuthException ex = new MoMoAuthException("Auth failed", cause);

        assertThat(ex.getMessage()).isEqualTo("Auth failed");
        assertThat(ex.getCause()).isEqualTo(cause);
    }
}
