package za.co.routepay.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import za.co.routepay.api.dto.AuthResponse;
import za.co.routepay.api.dto.OtpVerifyRequest;
import za.co.routepay.api.entity.User;
import za.co.routepay.api.repository.UserRepository;
import za.co.routepay.api.security.JwtTokenProvider;
import za.co.routepay.momo.MoMoClient;
import za.co.routepay.momo.auth.AuthClient;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private MoMoClient moMoClient;

    @Mock
    private OtpService otpService;

    @InjectMocks
    private AuthService authService;

    private static final String PHONE = "+27821234567";
    private static final String OTP = "123456";
    private static final String TOKEN = "test-jwt-token";

    @Test
    void request_otp_delegates_to_otp_service() {
        AuthClient authClient = mock(AuthClient.class);
        when(moMoClient.getAuth()).thenReturn(authClient);
        when(otpService.issue(PHONE)).thenReturn("654321");

        String returnedOtp = authService.requestOtp(PHONE);

        assertThat(returnedOtp).isEqualTo("654321");
        verify(otpService).issue(PHONE);
        verify(authClient).requestOtp(PHONE);
    }

    @Test
    void verify_otp_delegates_to_otp_service() {
        OtpVerifyRequest request = OtpVerifyRequest.builder().phone(PHONE).otp(OTP).build();

        when(userRepository.findByPhone(PHONE)).thenReturn(Optional.of(
                User.builder().id(1L).phone(PHONE).name("Test").role(User.UserRole.COMMUTER).build()
        ));
        when(jwtTokenProvider.generateToken(PHONE, "COMMUTER")).thenReturn(TOKEN);

        authService.verifyOtp(request);

        verify(otpService).verify(PHONE, OTP);
    }

    @Test
    void verify_otp_creates_new_user_when_not_exists() {
        OtpVerifyRequest request = OtpVerifyRequest.builder().phone(PHONE).otp(OTP).build();

        when(userRepository.findByPhone(PHONE)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtTokenProvider.generateToken(eq(PHONE), eq("COMMUTER"))).thenReturn(TOKEN);

        AuthResponse response = authService.verifyOtp(request);

        verify(userRepository).save(argThat(user ->
                user.getPhone().equals(PHONE) &&
                user.getRole() == User.UserRole.COMMUTER
        ));
        assertThat(response.getToken()).isEqualTo(TOKEN);
    }

    @Test
    void verify_otp_returns_nested_auth_response() {
        OtpVerifyRequest request = OtpVerifyRequest.builder().phone(PHONE).otp(OTP).build();

        when(userRepository.findByPhone(PHONE)).thenReturn(Optional.of(
                User.builder().id(42L).phone(PHONE).name("Test User").role(User.UserRole.COMMUTER).build()
        ));
        when(jwtTokenProvider.generateToken(PHONE, "COMMUTER")).thenReturn(TOKEN);

        AuthResponse response = authService.verifyOtp(request);

        assertThat(response.getToken()).isEqualTo(TOKEN);
        assertThat(response.getUser()).isNotNull();
        assertThat(response.getUser().getId()).isEqualTo(42L);
        assertThat(response.getUser().getPhoneNumber()).isEqualTo(PHONE);
        assertThat(response.getUser().getName()).isEqualTo("Test User");
        assertThat(response.getUser().getRole()).isEqualTo("COMMUTER");
    }

    @Test
    void verify_otp_existing_user_not_duplicated() {
        OtpVerifyRequest request = OtpVerifyRequest.builder().phone(PHONE).otp(OTP).build();
        User existing = User.builder().id(10L).phone(PHONE).name("Existing").role(User.UserRole.COMMUTER).build();

        when(userRepository.findByPhone(PHONE)).thenReturn(Optional.of(existing));
        when(jwtTokenProvider.generateToken(PHONE, "COMMUTER")).thenReturn(TOKEN);

        authService.verifyOtp(request);

        verify(userRepository, never()).save(any());
    }
}
