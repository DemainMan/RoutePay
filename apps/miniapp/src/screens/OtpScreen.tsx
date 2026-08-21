import React, { useCallback, useEffect, useRef, useState } from 'react';
import {
  ActivityIndicator,
  KeyboardAvoidingView,
  Platform,
  Pressable,
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

type OtpScreenProps = NativeStackScreenProps<RootStackParamList, 'Otp'>;

const OTP_LENGTH = 6;

function OtpScreen({ route }: OtpScreenProps) {
  const { phone } = route.params;
  const { verifyOtp, requestOtp, isLoading } = useAuth();
  const [otp, setOtp] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [isResending, setIsResending] = useState(false);
  const inputRef = useRef<TextInput>(null);

  const handleVerify = useCallback(
    async (code: string) => {
      setError(null);
      try {
        await verifyOtp(phone, code);
      } catch (err) {
        setOtp('');
        setError(err instanceof Error ? err.message : 'Verification failed. Please try again.');
      }
    },
    [phone, verifyOtp],
  );

  useEffect(() => {
    if (otp.length === OTP_LENGTH) {
      void handleVerify(otp);
    }
  }, [otp, handleVerify]);

  const handleChange = (value: string) => {
    const digits = value.replace(/\D/g, '').slice(0, OTP_LENGTH);
    setOtp(digits);
    if (error) {
      setError(null);
    }
  };

  const handleResend = async () => {
    setIsResending(true);
    setError(null);
    try {
      await requestOtp(phone);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Could not resend the code.');
    } finally {
      setIsResending(false);
    }
  };

  return (
    <SafeAreaView style={styles.safe} edges={['top', 'bottom']}>
      <KeyboardAvoidingView
        style={styles.flex}
        behavior={Platform.OS === 'ios' ? 'padding' : undefined}
      >
        <View style={styles.content}>
          <Text style={styles.title}>Enter verification code</Text>
          <Text style={styles.subtitle}>
            We sent a 6-digit code to {phone}. Enter it below to continue.
          </Text>

          <Pressable style={styles.boxesWrap} onPress={() => inputRef.current?.focus()}>
            <View style={styles.boxesRow} pointerEvents="none">
              {Array.from({ length: OTP_LENGTH }).map((_, index) => (
                <View key={index} style={[styles.box, index < otp.length && styles.boxFilled]}>
                  <Text style={styles.boxText}>{otp[index] ?? ''}</Text>
                </View>
              ))}
            </View>
            <TextInput
              ref={inputRef}
              style={styles.hiddenInput}
              value={otp}
              onChangeText={handleChange}
              keyboardType="number-pad"
              textContentType="oneTimeCode"
              maxLength={OTP_LENGTH}
              autoFocus
              caretHidden
              cursorColor="transparent"
              selectionColor="transparent"
            />
          </Pressable>

          {!!error && (
            <View style={styles.errorBanner}>
              <Text style={styles.errorBannerText}>{error}</Text>
            </View>
          )}

          <Pressable
            style={({ pressed }) => [
              styles.cta,
              (isLoading || otp.length !== OTP_LENGTH) && styles.ctaDisabled,
              pressed && styles.ctaPressed,
            ]}
            onPress={() => void handleVerify(otp)}
            disabled={isLoading || otp.length !== OTP_LENGTH}
            accessibilityRole="button"
            accessibilityLabel="Verify and continue"
          >
            {isLoading ? (
              <ActivityIndicator color={colors.primary} />
            ) : (
              <Text style={styles.ctaText}>Verify and continue</Text>
            )}
          </Pressable>

          <Pressable
            onPress={() => void handleResend()}
            disabled={isResending || isLoading}
            accessibilityRole="button"
            accessibilityLabel="Resend code"
          >
            <Text style={styles.resendText}>
              {isResending ? 'Sending a new code...' : 'Did not receive it? Resend code'}
            </Text>
          </Pressable>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: colors.white,
  },
  flex: {
    flex: 1,
  },
  content: {
    flex: 1,
    padding: spacing.lg,
  },
  title: {
    ...typography.title,
    color: colors.black,
    marginTop: spacing.md,
  },
  subtitle: {
    ...typography.body,
    color: colors.gray600,
    marginTop: spacing.sm,
  },
  boxesWrap: {
    alignSelf: 'stretch',
    marginTop: spacing.xl,
  },
  boxesRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  box: {
    width: 48,
    height: 56,
    borderRadius: radius.md,
    borderWidth: 1,
    borderColor: colors.gray200,
    backgroundColor: colors.gray100,
    alignItems: 'center',
    justifyContent: 'center',
  },
  boxFilled: {
    borderWidth: 2,
    borderColor: colors.black,
    backgroundColor: colors.white,
  },
  boxText: {
    fontSize: 24,
    fontWeight: '700',
    color: colors.black,
  },
  hiddenInput: {
    ...StyleSheet.absoluteFillObject,
    opacity: 0,
  },
  errorBanner: {
    backgroundColor: colors.error,
    borderRadius: radius.md,
    padding: spacing.md,
    marginTop: spacing.lg,
  },
  errorBannerText: {
    ...typography.small,
    color: colors.white,
    textAlign: 'center',
  },
  cta: {
    marginTop: spacing.xl,
    backgroundColor: colors.black,
    borderRadius: radius.md,
    paddingVertical: 16,
    alignItems: 'center',
  },
  ctaDisabled: {
    opacity: 0.4,
  },
  ctaPressed: {
    opacity: 0.85,
  },
  ctaText: {
    ...typography.heading,
    color: colors.primary,
  },
  resendText: {
    ...typography.small,
    color: colors.gray600,
    textAlign: 'center',
    marginTop: spacing.lg,
    textDecorationLine: 'underline',
  },
});

export default OtpScreen;
