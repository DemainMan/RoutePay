package za.co.routepay.momo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoMoConnectionExceptionTest {

    @Test
    @DisplayName("constructor with message")
    void constructorWithMessage() {
        MoMoConnectionException ex = new MoMoConnectionException("Connection timed out");
        assertThat(ex.getMessage()).isEqualTo("Connection timed out");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("constructor with cause")
    void constructorWithCause() {
        RuntimeException cause = new RuntimeException("DNS resolution failed");
        MoMoConnectionException ex = new MoMoConnectionException("Cannot reach MoMo", cause);

        assertThat(ex.getMessage()).isEqualTo("Cannot reach MoMo");
        assertThat(ex.getCause()).isEqualTo(cause);
    }
}
