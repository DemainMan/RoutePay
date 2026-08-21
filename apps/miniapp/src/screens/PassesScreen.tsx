import React, { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  Pressable,
  RefreshControl,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { api } from '../api/client';
import { colors, radius, shadow, spacing, typography } from '../theme';

type PassTypeId = 'DAILY' | 'WEEKLY' | 'MONTHLY';

interface PassOption {
  id: PassTypeId;
  title: string;
  price: number;
  description: string;
  highlight?: boolean;
}

const PASS_OPTIONS: PassOption[] = [
  { id: 'DAILY', title: 'Daily Pass', price: 25, description: 'Unlimited trips for 24 hours.' },
  {
    id: 'WEEKLY',
    title: 'Weekly Pass',
    price: 99,
    description: '7 days of unlimited travel.',
    highlight: true,
  },
  { id: 'MONTHLY', title: 'Monthly Pass', price: 350, description: '30 days of unlimited travel.' },
];

interface TravelPass {
  id: number;
  passType: string;
  status?: string;
  validFrom?: string;
  validUntil?: string;
  expiresAt?: string;
}

function formatDate(value?: string): string {
  if (!value) {
    return '';
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return '';
  }
  return date.toLocaleDateString('en-ZA', { day: 'numeric', month: 'short', year: 'numeric' });
}

function passTypeLabel(passType: string): string {
  return PASS_OPTIONS.find((option) => option.id === passType)?.title ?? passType;
}

function PassesScreen() {
  const [passes, setPasses] = useState<TravelPass[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [purchasingId, setPurchasingId] = useState<PassTypeId | null>(null);

  const loadPasses = useCallback(async () => {
    try {
      const data = await api.getPasses();
      setPasses(data as TravelPass[]);
      setLoadError(null);
    } catch (err) {
      setLoadError(err instanceof Error ? err.message : 'Could not load your passes.');
    }
  }, []);

  useEffect(() => {
    void (async () => {
      await loadPasses();
      setIsLoading(false);
    })();
  }, [loadPasses]);

  const handleRefresh = useCallback(async () => {
    setIsRefreshing(true);
    await loadPasses();
    setIsRefreshing(false);
  }, [loadPasses]);

  const purchase = useCallback(
    async (option: PassOption) => {
      setPurchasingId(option.id);
      try {
        await api.purchasePass(option.id);
        Alert.alert('Pass purchased', `${option.title} is now active on your account.`);
        await loadPasses();
      } catch (err) {
        Alert.alert('Purchase failed', err instanceof Error ? err.message : 'Please try again.');
      } finally {
        setPurchasingId(null);
      }
    },
    [loadPasses],
  );

  const confirmPurchase = useCallback(
    (option: PassOption) => {
      Alert.alert('Confirm purchase', `Buy the ${option.title} for R${option.price}?`, [
        { text: 'Cancel', style: 'cancel' },
        { text: 'Buy pass', onPress: () => void purchase(option) },
      ]);
    },
    [purchase],
  );

  if (isLoading) {
    return (
      <SafeAreaView style={styles.safe} edges={['top']}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>Travel Passes</Text>
          <Text style={styles.headerSubtitle}>Pay once and ride as much as you like</Text>
        </View>
        <View style={styles.centered}>
          <ActivityIndicator size="large" color={colors.black} />
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.safe} edges={['top']}>
      <View style={styles.header}>
        <Text style={styles.headerTitle}>Travel Passes</Text>
        <Text style={styles.headerSubtitle}>Pay once and ride as much as you like</Text>
      </View>
      {loadError ? (
        <View style={styles.centered}>
          <Text style={styles.errorText}>{loadError}</Text>
          <Pressable
            style={styles.retryButton}
            onPress={() => void handleRefresh()}
            accessibilityRole="button"
          >
            <Text style={styles.retryText}>Try again</Text>
          </Pressable>
        </View>
      ) : (
        <ScrollView
          contentContainerStyle={styles.listContent}
          refreshControl={
            <RefreshControl
              refreshing={isRefreshing}
              onRefresh={() => void handleRefresh()}
              tintColor={colors.black}
            />
          }
        >
          {PASS_OPTIONS.map((option) => (
            <View
              key={option.id}
              style={[styles.optionCard, option.highlight && styles.optionCardHighlight]}
            >
              <View style={styles.optionBody}>
                {option.highlight && (
                  <View style={styles.bestValueTag}>
                    <Text style={styles.bestValueText}>BEST VALUE</Text>
                  </View>
                )}
                <Text style={styles.optionTitle}>{option.title}</Text>
                <Text style={styles.optionDescription}>{option.description}</Text>
              </View>
              <Pressable
                style={({ pressed }) => [
                  styles.buyButton,
                  pressed && styles.buyButtonPressed,
                  purchasingId !== null && styles.buyButtonDisabled,
                ]}
                onPress={() => confirmPurchase(option)}
                disabled={purchasingId !== null}
                accessibilityRole="button"
                accessibilityLabel={`Buy ${option.title}`}
              >
                {purchasingId === option.id ? (
                  <ActivityIndicator size="small" color={colors.black} />
                ) : (
                  <Text style={styles.buyButtonText}>R{option.price}</Text>
                )}
              </Pressable>
            </View>
          ))}

          <Text style={styles.sectionTitle}>Your passes</Text>
          {passes.length === 0 ? (
            <Text style={styles.emptyText}>You have no active passes yet.</Text>
          ) : (
            passes.map((pass) => {
              const expiry = formatDate(pass.validUntil ?? pass.expiresAt);
              return (
                <View key={pass.id} style={styles.passCard}>
                  <View style={styles.passBody}>
                    <Text style={styles.passTitle}>{passTypeLabel(pass.passType)}</Text>
                    {!!expiry && <Text style={styles.passExpiry}>Valid until {expiry}</Text>}
                  </View>
                  <View style={styles.activeBadge}>
                    <Text style={styles.activeBadgeText}>
                      {(pass.status ?? 'ACTIVE').toUpperCase()}
                    </Text>
                  </View>
                </View>
              );
            })
          )}
        </ScrollView>
      )}
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
  headerSubtitle: {
    ...typography.small,
    color: colors.gray800,
    marginTop: spacing.xs,
  },
  centered: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: spacing.lg,
  },
  listContent: {
    padding: spacing.md,
    paddingBottom: spacing.xl,
  },
  optionCard: {
    ...shadow,
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.white,
    borderRadius: radius.md,
    padding: spacing.md,
    marginBottom: spacing.sm,
  },
  optionCardHighlight: {
    borderWidth: 2,
    borderColor: colors.black,
  },
  optionBody: {
    flex: 1,
    marginRight: spacing.md,
  },
  bestValueTag: {
    alignSelf: 'flex-start',
    backgroundColor: colors.primary,
    borderRadius: radius.sm,
    paddingVertical: 2,
    paddingHorizontal: spacing.sm,
    marginBottom: spacing.sm,
  },
  bestValueText: {
    ...typography.caption,
    color: colors.black,
  },
  optionTitle: {
    ...typography.heading,
    color: colors.black,
  },
  optionDescription: {
    ...typography.small,
    color: colors.gray600,
    marginTop: spacing.xs,
  },
  buyButton: {
    minWidth: 72,
    alignItems: 'center',
    backgroundColor: colors.primary,
    borderRadius: radius.sm,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.md,
  },
  buyButtonPressed: {
    opacity: 0.85,
  },
  buyButtonDisabled: {
    opacity: 0.6,
  },
  buyButtonText: {
    ...typography.heading,
    color: colors.black,
  },
  sectionTitle: {
    ...typography.heading,
    color: colors.black,
    marginTop: spacing.lg,
    marginBottom: spacing.sm,
  },
  passCard: {
    ...shadow,
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.white,
    borderRadius: radius.md,
    padding: spacing.md,
    marginBottom: spacing.sm,
  },
  passBody: {
    flex: 1,
    marginRight: spacing.md,
  },
  passTitle: {
    ...typography.heading,
    color: colors.black,
  },
  passExpiry: {
    ...typography.small,
    color: colors.gray600,
    marginTop: spacing.xs,
  },
  activeBadge: {
    backgroundColor: colors.green,
    borderRadius: radius.sm,
    paddingVertical: spacing.xs,
    paddingHorizontal: spacing.sm,
  },
  activeBadgeText: {
    ...typography.caption,
    color: colors.white,
  },
  emptyText: {
    ...typography.body,
    color: colors.gray600,
  },
  errorText: {
    ...typography.body,
    color: colors.error,
    textAlign: 'center',
  },
  retryButton: {
    marginTop: spacing.md,
    borderWidth: 1,
    borderColor: colors.black,
    borderRadius: radius.md,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.lg,
  },
  retryText: {
    ...typography.heading,
    color: colors.black,
  },
});

export default PassesScreen;
