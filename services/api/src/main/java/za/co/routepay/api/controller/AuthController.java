package za.co.routepay.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.routepay.api.dto.*;
import za.co.routepay.api.service.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "OTP-based auth via MTN MoMo")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/otp/request")
    @Operation(summary = "Request an OTP for a phone number")
    public ResponseEntity<Map<String, String>> requestOtp(@Valid @RequestBody OtpRequest request) {
        String otp = authService.requestOtp(request.getPhone());
        return ResponseEntity.ok(Map.of("message", "OTP sent", "phone", request.getPhone(), "otp", otp));
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP and get JWT token")
    public ResponseEntity<AuthResponse> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(response);
    }
}
