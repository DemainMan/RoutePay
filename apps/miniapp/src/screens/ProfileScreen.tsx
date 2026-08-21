import React, { useState } from 'react';
import { ActivityIndicator, Alert, Pressable, ScrollView, StyleSheet, Text, View } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { useAuth } from '../contexts/AuthContext';
import { colors, radius, shadow, spacing, typography } from '../theme';

function ProfileScreen() {
  const { user, signOut } = useAuth();
  const [isSigningOut, setIsSigningOut] = useState(false);

  const displayName = user?.name?.trim() || 'Commuter';
  const initials =
    displayName
      .split(' ')
      .map((part) => part[0])
      .filter(Boolean)
      .slice(0, 2)
      .join('')
      .toUpperCase() || 'R';

  const handleSignOut = () => {
    Alert.alert('Log out', 'Are you sure you want to log out of RoutePay?', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Log out',
        style: 'destructive',
        onPress: () => {
          void (async () => {
            setIsSigningOut(true);
            try {
              await signOut();
            } finally {
              setIsSigningOut(false);
            }
          })();
        },
      },
    ]);
  };

  return (
    <SafeAreaView style={styles.safe} edges={['top']}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Profile</Text>
      </View>
      <ScrollView contentContainerStyle={styles.content}>
        <View style={styles.identityRow}>
          <View style={styles.avatar}>
            <Text style={styles.avatarText}>{initials}</Text>
          </View>
          <View style={styles.identityText}>
            <Text style={styles.name}>{displayName}</Text>
            <Text style={styles.phone}>{user?.phoneNumber ?? 'No number linked'}</Text>
          </View>
        </View>

        <View style={styles.detailsCard}>
          <View style={styles.detailRow}>
            <Text style={styles.detailLabel}>PHONE NUMBER</Text>
            <Text style={styles.detailValue}>{user?.phoneNumber ?? '-'}</Text>
          </View>
          <View style={[styles.detailRow, styles.detailRowLast]}>
            <Text style={styles.detailLabel}>USER ID</Text>
            <Text style={styles.detailValue}>{user?.id != null ? `#${user.id}` : '-'}</Text>
          </View>
        </View>

        <Pressable
          style={({ pressed }) => [styles.logoutButton, pressed && styles.logoutPressed]}
          onPress={handleSignOut}
          disabled={isSigningOut}
          accessibilityRole="button"
          accessibilityLabel="Log out"
        >
          {isSigningOut ? (
            <ActivityIndicator size="small" color={colors.error} />
          ) : (
            <Text style={styles.logoutText}>Log out</Text>
          )}
        </Pressable>

        <Text style={styles.version}>RoutePay v0.1.0</Text>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safe: {
    flex: 1,
    backgroundColor: colors.gray100,
  },
  header: {
    backgroundColor: colors.primary,
    paddingHorizontal: spacing.md,
    paddingTop: spacing.sm,
    paddingBottom: spacing.lg,
    borderBottomLeftRadius: radius.lg,
    borderBottomRightRadius: radius.lg,
  },
  headerTitle: {
    ...typography.title,
    color: colors.black,
  },
  content: {
    padding: spacing.md,
    paddingBottom: spacing.xl,
  },
  identityRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: spacing.sm,
    marginBottom: spacing.lg,
  },
  avatar: {
    width: 88,
    height: 88,
    borderRadius: 44,
    backgroundColor: colors.black,
    alignItems: 'center',
    justifyContent: 'center',
    marginRight: spacing.md,
  },
  avatarText: {
    ...typography.hero,
    color: colors.primary,
  },
  identityText: {
    flex: 1,
  },
  name: {
    ...typography.title,
    color: colors.black,
  },
  phone: {
    ...typography.body,
    color: colors.gray600,
    marginTop: spacing.xs,
  },
  detailsCard: {
    ...shadow,
    backgroundColor: colors.white,
    borderRadius: radius.md,
    padding: spacing.md,
    marginBottom: spacing.lg,
  },
  detailRow: {
    paddingVertical: spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: colors.gray200,
  },
  detailRowLast: {
    borderBottomWidth: 0,
    paddingBottom: 0,
  },
  detailLabel: {
    ...typography.caption,
    color: colors.gray600,
  },
  detailValue: {
    ...typography.body,
    color: colors.black,
    marginTop: spacing.xs,
  },
  logoutButton: {
    borderWidth: 1,
    borderColor: colors.error,
    borderRadius: radius.md,
    backgroundColor: colors.white,
    paddingVertical: 16,
    alignItems: 'center',
  },
  logoutPressed: {
    opacity: 0.85,
  },
  logoutText: {
    ...typography.heading,
    color: colors.error,
  },
  version: {
    ...typography.small,
    color: colors.gray600,
    textAlign: 'center',
    marginTop: spacing.lg,
  },
});

export default ProfileScreen;
