package za.co.routepay.momo.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoMoApiExceptionTest {

    @Test
    @DisplayName("constructor with status code and response body")
    void constructorWithStatusCodeAndBody() {
        MoMoApiException ex = new MoMoApiException("Payment failed", 400, "{\"error\":\"bad request\"}");

        assertThat(ex.getMessage()).isEqualTo("Payment failed");
        assertThat(ex.getStatusCode()).isEqualTo(400);
        assertThat(ex.getResponseBody()).isEqualTo("{\"error\":\"bad request\"}");
        assertThat(ex.getCause()).isNull();
    }

    @Test
    @DisplayName("constructor with cause")
    void constructorWithCause() {
        RuntimeException cause = new RuntimeException("connection refused");
        MoMoApiException ex = new MoMoApiException("Cannot connect", cause);

        assertThat(ex.getMessage()).isEqualTo("Cannot connect");
        assertThat(ex.getCause()).isEqualTo(cause);
        assertThat(ex.getStatusCode()).isEqualTo(0);
        assertThat(ex.getResponseBody()).isNull();
    }
}
