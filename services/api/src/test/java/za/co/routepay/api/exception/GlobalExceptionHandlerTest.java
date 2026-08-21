package za.co.routepay.api.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    private HttpServletRequest mockRequest() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRequestURI()).thenReturn("/api/test");
        return req;
    }

    @Test
    void handle_not_found_returns_404() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(new NotFoundException("User not found"), mockRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("status", 404);
        assertThat(response.getBody()).containsEntry("message", "User not found");
        assertThat(response.getBody()).doesNotContainKey("stackTrace");
    }

    @Test
    void handle_invalid_otp_returns_401() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleInvalidOtp(new InvalidOtpException("Invalid OTP"), mockRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("status", 401);
        assertThat(response.getBody()).containsEntry("message", "Invalid OTP");
    }

    @Test
    void handle_validation_returns_400() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("request", "phone", "Phone is required");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<Map<String, Object>> response =
                handler.handleValidation(ex, mockRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("status", 400);
        assertThat(response.getBody().get("message").toString()).contains("phone: Phone is required");
    }

    @Test
    void handle_general_returns_500_no_stacktrace() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleGeneral(new RuntimeException("Something broke"), mockRequest());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("status", 500);
        assertThat(response.getBody()).containsEntry("message", "Internal server error");
        assertThat(response.getBody()).doesNotContainKey("stackTrace");
        assertThat(response.getBody()).doesNotContainKey("trace");
    }

    @Test
    void error_body_contains_timestamp_and_path() {
        ResponseEntity<Map<String, Object>> response =
                handler.handleNotFound(new NotFoundException("Not found"), mockRequest());

        assertThat(response.getBody()).containsKey("timestamp");
        assertThat(response.getBody()).containsEntry("path", "/api/test");
        assertThat(response.getBody()).containsEntry("error", "Not Found");
    }
}
