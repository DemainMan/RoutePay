package za.co.routepay.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.routepay.api.exception.InvalidOtpException;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_MS = 5 * 60 * 1000; // 5 minutes
    private static final int MAX_ATTEMPTS = 5;

    private final SecureRandom random = new SecureRandom();
    private final ConcurrentHashMap<String, OtpEntry> store = new ConcurrentHashMap<>();

    public String issue(String phone) {
        String otp = generateOtp();
        OtpEntry entry = new OtpEntry();
        entry.otpHash = hash(otp);
        entry.expiresAt = System.currentTimeMillis() + OTP_EXPIRY_MS;
        entry.attempts = 0;
        store.put(normalize(phone), entry);

        // In mock mode, log the OTP so demos still work
        String masked = phone.substring(0, Math.min(6, phone.length())) + "****";
        log.info("OTP issued for {}: {} (expires in 5 min)", masked, otp);
        return otp;
    }

    public void verify(String phone, String otp) {
        String key = normalize(phone);
        OtpEntry entry = store.get(key);

        if (entry == null) {
            throw new InvalidOtpException("No OTP requested for this number");
        }

        if (System.currentTimeMillis() > entry.expiresAt) {
            store.remove(key);
            throw new InvalidOtpException("OTP has expired. Please request a new one.");
        }

        if (entry.attempts >= MAX_ATTEMPTS) {
            store.remove(key);
            throw new InvalidOtpException("Too many failed attempts. Please request a new OTP.");
        }

        entry.attempts++;

        if (!constantTimeEquals(hash(otp), entry.otpHash)) {
            throw new InvalidOtpException("Invalid OTP. " + (MAX_ATTEMPTS - entry.attempts) + " attempts remaining.");
        }

        // Success — remove OTP (single use)
        store.remove(key);
    }

    private String generateOtp() {
        int max = (int) Math.pow(10, OTP_LENGTH);
        int otp = random.nextInt(max);
        return String.format("%0" + OTP_LENGTH + "d", otp);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(a.getBytes(), b.getBytes());
    }

    private String normalize(String phone) {
        if (phone == null) return "";
        String p = phone.replaceAll("[\\s\\-]", "");
        if (p.startsWith("0") && p.length() == 10) {
            p = "+27" + p.substring(1);
        }
        return p;
    }

    private static class OtpEntry {
        String otpHash;
        long expiresAt;
        int attempts;
    }
}
