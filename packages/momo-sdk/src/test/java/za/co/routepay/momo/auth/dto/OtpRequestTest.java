package za.co.routepay.momo.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OtpRequestTest {

    @Test
    @DisplayName("builder creates OTP request")
    void builderCreatesRequest() {
        OtpRequest request = OtpRequest.builder()
                .phone("+27821234567")
                .build();

        assertThat(request.getPhone()).isEqualTo("+27821234567");
    }

    @Test
    @DisplayName("all-args constructor works")
    void allArgsConstructor() {
        OtpRequest request = new OtpRequest("+27821234567");
        assertThat(request.getPhone()).isEqualTo("+27821234567");
    }
}
