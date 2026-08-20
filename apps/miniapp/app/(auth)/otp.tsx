import React, { useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  SafeAreaView,
  Alert,
} from "react-native";
import { router, useLocalSearchParams } from "expo-router";
import { api } from "../../src/services/api";
import { Colors, Spacing, BorderRadius } from "../../src/constants/theme";

export default function OtpScreen() {
  const { phone } = useLocalSearchParams<{ phone: string }>();
  const [otp, setOtp] = useState("");
  const [loading, setLoading] = useState(false);

  const handleVerify = async () => {
    if (otp.length !== 4) {
      Alert.alert("Error", "OTP must be 4 digits");
      return;
    }
    setLoading(true);
    try {
      const result = await api.verifyOtp(phone!, otp);
      api.setToken(result.access_token);
      // TODO: Store token securely
      router.replace("/(tabs)");
    } catch (e: any) {
      Alert.alert("Error", e.message || "Invalid OTP");
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>Enter OTP</Text>
        <Text style={styles.subtitle}>
          We sent a code to {phone}
        </Text>

        <TextInput
          style={styles.otpInput}
          value={otp}
          onChangeText={setOtp}
          placeholder="1234"
          keyboardType="number-pad"
          maxLength={4}
          textAlign="center"
        />

        <TouchableOpacity
          style={[styles.button, loading && styles.buttonDisabled]}
          onPress={handleVerify}
          disabled={loading}
        >
          <Text style={styles.buttonText}>
            {loading ? "Verifying..." : "Verify"}
          </Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.resendButton}
          onPress={() => api.requestOtp(phone!)}
        >
          <Text style={styles.resendText}>Resend OTP</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Colors.background,
  },
  content: {
    flex: 1,
    padding: Spacing.lg,
    justifyContent: "center",
    alignItems: "center",
  },
  title: {
    fontSize: 28,
    fontWeight: "bold",
    color: Colors.accent,
  },
  subtitle: {
    fontSize: 16,
    color: Colors.textSecondary,
    marginTop: Spacing.sm,
    marginBottom: Spacing.xl,
  },
  otpInput: {
    borderWidth: 2,
    borderColor: Colors.primary,
    borderRadius: BorderRadius.lg,
    padding: Spacing.lg,
    fontSize: 32,
    fontWeight: "bold",
    width: 200,
    backgroundColor: Colors.surface,
  },
  button: {
    backgroundColor: Colors.primary,
    borderRadius: BorderRadius.md,
    padding: Spacing.md,
    alignItems: "center",
    width: "100%",
    marginTop: Spacing.xl,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  buttonText: {
    color: Colors.accent,
    fontSize: 18,
    fontWeight: "bold",
  },
  resendButton: {
    marginTop: Spacing.lg,
  },
  resendText: {
    color: Colors.textSecondary,
    fontSize: 14,
  },
});
