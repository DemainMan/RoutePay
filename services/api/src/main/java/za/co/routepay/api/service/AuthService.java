package za.co.routepay.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.routepay.api.dto.*;
import za.co.routepay.api.entity.User;
import za.co.routepay.api.exception.InvalidOtpException;
import za.co.routepay.api.exception.NotFoundException;
import za.co.routepay.api.repository.UserRepository;
import za.co.routepay.api.security.JwtTokenProvider;
import za.co.routepay.momo.MoMoClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MoMoClient moMoClient;
    private final OtpService otpService;

    public String requestOtp(String phone) {
        log.info("Requesting OTP for phone={}", phone);
        // Generate and store OTP via our service (not MoMo mock)
        String otp = otpService.issue(phone);
        // Also call MoMo SDK (no-op in mock mode, needed for real integration)
        moMoClient.getAuth().requestOtp(phone);
        return otp;
    }

    public AuthResponse verifyOtp(OtpVerifyRequest request) {
        log.info("Verifying OTP for phone={}", request.getPhone());

        // Validate OTP via our service (not MoMo mock)
        otpService.verify(request.getPhone(), request.getOtp());

        // Find or create user
        User user = userRepository.findByPhone(request.getPhone())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .phone(request.getPhone())
                            .name("Commuter " + request.getPhone().substring(Math.max(0, request.getPhone().length() - 4)))
                            .role(User.UserRole.COMMUTER)
                            .build();
                    return userRepository.save(newUser);
                });

        // Generate JWT
        String token = jwtTokenProvider.generateToken(user.getPhone(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .phoneNumber(user.getPhone())
                        .name(user.getName())
                        .role(user.getRole().name())
                        .build())
                .build();
    }

    public User findByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException("User not found: " + phone));
    }
}
