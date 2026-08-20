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
import { router } from "expo-router";
import { api } from "../../src/services/api";
import { Colors, Spacing, BorderRadius } from "../../src/constants/theme";

export default function LoginScreen() {
  const [phone, setPhone] = useState("+27");
  const [loading, setLoading] = useState(false);

  const handleRequestOtp = async () => {
    if (phone.length < 10) {
      Alert.alert("Error", "Please enter a valid phone number");
      return;
    }
    setLoading(true);
    try {
      await api.requestOtp(phone);
      router.push({ pathname: "/(auth)/otp", params: { phone } });
    } catch (e: any) {
      Alert.alert("Error", e.message || "Failed to send OTP");
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <View style={styles.logoContainer}>
          <Text style={styles.logo}>RoutePay</Text>
          <Text style={styles.tagline}>Every ride. One tap.</Text>
        </View>

        <View style={styles.form}>
          <Text style={styles.label}>Phone Number</Text>
          <TextInput
            style={styles.input}
            value={phone}
            onChangeText={setPhone}
            placeholder="+27 82 123 4567"
            keyboardType="phone-pad"
            autoComplete="tel"
          />

          <TouchableOpacity
            style={[styles.button, loading && styles.buttonDisabled]}
            onPress={handleRequestOtp}
            disabled={loading}
          >
            <Text style={styles.buttonText}>
              {loading ? "Sending..." : "Continue"}
            </Text>
          </TouchableOpacity>
        </View>

        <Text style={styles.footer}>
          By continuing, you agree to our Terms of Service
        </Text>
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
  },
  logoContainer: {
    alignItems: "center",
    marginBottom: Spacing.xxl,
  },
  logo: {
    fontSize: 42,
    fontWeight: "bold",
    color: Colors.accent,
  },
  tagline: {
    fontSize: 16,
    color: Colors.textSecondary,
    marginTop: Spacing.sm,
  },
  form: {
    gap: Spacing.md,
  },
  label: {
    fontSize: 14,
    fontWeight: "600",
    color: Colors.text,
  },
  input: {
    borderWidth: 1,
    borderColor: Colors.border,
    borderRadius: BorderRadius.md,
    padding: Spacing.md,
    fontSize: 18,
    backgroundColor: Colors.surface,
  },
  button: {
    backgroundColor: Colors.primary,
    borderRadius: BorderRadius.md,
    padding: Spacing.md,
    alignItems: "center",
    marginTop: Spacing.sm,
  },
  buttonDisabled: {
    opacity: 0.6,
  },
  buttonText: {
    color: Colors.accent,
    fontSize: 18,
    fontWeight: "bold",
  },
  footer: {
    textAlign: "center",
    color: Colors.textSecondary,
    fontSize: 12,
    marginTop: Spacing.xl,
  },
});
