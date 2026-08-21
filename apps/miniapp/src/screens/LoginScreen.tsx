import React, { useState } from 'react';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useAuth } from '../contexts/AuthContext';
import type { RootStackParamList } from '../navigation/types';
import { colors, radius, spacing, typography } from '../theme';

type LoginScreenProps = NativeStackScreenProps<RootStackParamList, 'Login'>;

function normalizeSaPhone(raw: string): string | null {
  const cleaned = raw.replace(/[\s()-]/g, '');
  const match = /^(?:\+27|27|0)(\d{9})$/.exec(cleaned);
  return match ? `+27${match[1]}` : null;
}

function LoginScreen({ navigation }: LoginScreenProps) {
  const { requestOtp, isLoading } = useAuth();
  const [phone, setPhone] = useState('');
  const [error, setError] = useState<string | null>(null);

  const handleRequestOtp = async () => {
    const normalized = normalizeSaPhone(phone);
    if (!normalized) {
      setError('Enter a valid SA number, e.g. 082 123 4567');
      return;
    }
    setError(null);
    try {
      await requestOtp(normalized);
      navigation.navigate('Otp', { phone: normalized });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Could not send the OTP. Please try again.');
    }
  };

  return (
    <SafeAreaView style={styles.safe} edges={['top', 'bottom']}>
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <ScrollView contentContainerStyle={styles.scroll} keyboardShouldPersistTaps="handled">
          <View style={styles.hero}>
            <View style={styles.logoBadge}>
              <Text style={styles.logoLetter}>R</Text>
            </View>
            <Text style={styles.brandName}>RoutePay</Text>
            <Text style={styles.tagline}>Taxi fares, sorted.</Text>
          </View>

          <View style={styles.card}>
            <Text style={styles.label}>Cellphone number</Text>
            <TextInput
              style={[styles.input, error ? styles.inputInvalid : null]}
              placeholder="082 123 4567"
              placeholderTextColor={colors.gray400}
              keyboardType="phone-pad"
              textContentType="telephoneNumber"
              autoComplete="tel"
              value={phone}
              maxLength={16}
              onChangeText={(value) => {
                setPhone(value);
                if (error) {
                  setError(null);
                }
              }}
            />
            {!!error && <Text style={styles.errorText}>{error}</Text>}

            <Pressable
              style={({ pressed }) => [styles.cta, pressed && styles.ctaPressed]}
              onPress={() => void handleRequestOtp()}
              disabled={isLoading}
              accessibilityRole="button"
              accessibilityLabel="Request OTP"
            >
              {isLoading ? (
                <ActivityIndicator color={colors.primary} />
              ) : (
                <Text style={styles.ctaText}>Request OTP</Text>
              )}
            </Pressable>

            <Text style={styles.hint}>We will send a 6-digit verification code by SMS.</Text>
          </View>
        </ScrollView>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: colors.primary,
  },
  flex: {
    flex: 1,
  },
  scroll: {
    flexGrow: 1,
  },
  hero: {
    alignItems: 'center',
    paddingTop: spacing.xl,
    paddingBottom: spacing.xl,
  },
  logoBadge: {
    width: 72,
    height: 72,
    borderRadius: radius.lg,
    backgroundColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: spacing.md,
  },
  logoLetter: {
    ...typography.hero,
    color: colors.primary,
  },
  brandName: {
    ...typography.hero,
    color: colors.black,
  },
  tagline: {
    ...typography.body,
    color: colors.gray800,
    marginTop: spacing.xs,
  },
  card: {
    flex: 1,
    backgroundColor: colors.white,
    borderTopLeftRadius: radius.lg,
    borderTopRightRadius: radius.lg,
    padding: spacing.lg,
  },
  label: {
    ...typography.heading,
    color: colors.black,
    marginBottom: spacing.sm,
  },
  input: {
    borderWidth: 1,
    borderColor: colors.gray200,
    borderRadius: radius.md,
    paddingHorizontal: spacing.md,
    paddingVertical: 14,
    fontSize: 18,
    color: colors.black,
    backgroundColor: colors.gray100,
  },
  inputInvalid: {
    borderColor: colors.error,
  },
  errorText: {
    ...typography.small,
    color: colors.error,
    marginTop: spacing.sm,
  },
  cta: {
    marginTop: spacing.lg,
    backgroundColor: colors.black,
    borderRadius: radius.md,
    paddingVertical: 16,
    alignItems: 'center',
  },
  ctaPressed: {
    opacity: 0.85,
  },
  ctaText: {
    ...typography.heading,
    color: colors.primary,
  },
  hint: {
    ...typography.small,
    color: colors.gray600,
    textAlign: 'center',
    marginTop: spacing.md,
  },
});

export default LoginScreen;
