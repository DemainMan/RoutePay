package za.co.routepay.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.routepay.api.exception.InvalidOtpException;

import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.*;

class OtpServiceTest {

    private OtpService otpService;

    @BeforeEach
    void setUp() {
        otpService = new OtpService();
    }

    @Test
    void verify_wrong_otp_throws() {
        otpService.issue("+27821234567");
        assertThatThrownBy(() -> otpService.verify("+27821234567", "000000"))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessageContaining("Invalid OTP");
    }

    @Test
    void verify_no_otp_requested_throws() {
        assertThatThrownBy(() -> otpService.verify("+27821234567", "123456"))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessageContaining("No OTP requested");
    }

    @Test
    void verify_lockout_after_5_attempts() {
        otpService.issue("+27821234567");

        // 5 wrong attempts
        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> otpService.verify("+27821234567", "000000"))
                    .isInstanceOf(InvalidOtpException.class);
        }

        // 6th attempt — should be locked out
        assertThatThrownBy(() -> otpService.verify("+27821234567", "000000"))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessageContaining("Too many failed attempts");
    }

    @Test
    void after_lockout_new_issue_works() {
        otpService.issue("+27821234567");

        // Lock it out
        for (int i = 0; i < 5; i++) {
            assertThatThrownBy(() -> otpService.verify("+27821234567", "000000"))
                    .isInstanceOf(InvalidOtpException.class);
        }

        // Issue new OTP — should work
        otpService.issue("+27821234567");

        // Wrong OTP should give "Invalid" not "Too many"
        assertThatThrownBy(() -> otpService.verify("+27821234567", "000000"))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessageContaining("Invalid OTP");
    }

    @Test
    void verify_expired_otp_throws() throws Exception {
        otpService.issue("+27821234567");

        // Use reflection to set expiry to past
        var storeField = OtpService.class.getDeclaredField("store");
        storeField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, Object> store = (ConcurrentHashMap<String, Object>) storeField.get(otpService);
        Object entry = store.get("+27821234567");

        var expiresAtField = entry.getClass().getDeclaredField("expiresAt");
        expiresAtField.setAccessible(true);
        expiresAtField.setLong(entry, System.currentTimeMillis() - 1000);

        assertThatThrownBy(() -> otpService.verify("+27821234567", "123456"))
                .isInstanceOf(InvalidOtpException.class)
                .hasMessageContaining("expired");
    }
}
