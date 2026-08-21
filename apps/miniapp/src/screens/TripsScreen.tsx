import React, { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  RefreshControl,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { api } from '../api/client';
import { colors, radius, shadow, spacing, typography } from '../theme';

interface Trip {
  id: number;
  status: string;
  routeName?: string;
  origin?: string;
  destination?: string;
  fare?: number;
  bookedAt?: string;
}

interface StatusBadge {
  label: string;
  bg: string;
  fg: string;
}

const STATUS_BADGES: Record<string, StatusBadge> = {
  BOOKED: { label: 'BOOKED', bg: colors.primary, fg: colors.black },
  IN_PROGRESS: { label: 'IN PROGRESS', bg: colors.green, fg: colors.white },
  COMPLETED: { label: 'COMPLETED', bg: colors.gray400, fg: colors.white },
};

function badgeFor(status: string): StatusBadge {
  return (
    STATUS_BADGES[status] ?? { label: status.toUpperCase(), bg: colors.gray200, fg: colors.gray800 }
  );
}

function formatFare(fare: number): string {
  return `R${fare.toFixed(2)}`;
}

function formatDateTime(value?: string): string {
  if (!value) {
    return '';
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return '';
  }
  return date.toLocaleString('en-ZA', {
    day: 'numeric',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function TripsScreen() {
  const [trips, setTrips] = useState<Trip[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  const loadTrips = useCallback(async () => {
    try {
      const data = await api.getTrips();
      setTrips(data as Trip[]);
      setLoadError(null);
    } catch (err) {
      setLoadError(err instanceof Error ? err.message : 'Could not load trips.');
    }
  }, []);

  useEffect(() => {
    void (async () => {
      await loadTrips();
      setIsLoading(false);
    })();
  }, [loadTrips]);

  const handleRefresh = useCallback(async () => {
    setIsRefreshing(true);
    await loadTrips();
    setIsRefreshing(false);
  }, [loadTrips]);

  const renderTrip = useCallback(({ item }: { item: Trip }) => {
    const badge = badgeFor(item.status);
    return (
      <View style={styles.card}>
        <View style={styles.cardTopRow}>
          <Text style={styles.tripName}>
            {item.routeName ??
              (item.origin && item.destination
                ? `${item.origin} trip`
                : `Trip #${item.id}`)}
          </Text>
          <View style={[styles.badge, { backgroundColor: badge.bg }]}>
            <Text style={[styles.badgeText, { color: badge.fg }]}>{badge.label}</Text>
          </View>
        </View>
        {!!(item.origin && item.destination) && (
          <Text style={styles.tripPath}>
            {item.origin} → {item.destination}
          </Text>
        )}
        <View style={styles.cardBottomRow}>
          <Text style={styles.tripDate}>{formatDateTime(item.bookedAt)}</Text>
          {typeof item.fare === 'number' && (
            <Text style={styles.tripFare}>{formatFare(item.fare)}</Text>
          )}
        </View>
      </View>
    );
  }, []);

  if (isLoading) {
    return (
      <SafeAreaView style={styles.safe} edges={['top']}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>My Trips</Text>
          <Text style={styles.headerSubtitle}>Your booked and completed taxi trips</Text>
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
        <Text style={styles.headerTitle}>My Trips</Text>
        <Text style={styles.headerSubtitle}>Your booked and completed taxi trips</Text>
      </View>
      {loadError ? (
        <View style={styles.centered}>
          <Text style={styles.errorText}>{loadError}</Text>
        </View>
      ) : (
        <FlatList
          data={trips}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderTrip}
          contentContainerStyle={styles.listContent}
          ListEmptyComponent={
            <View style={styles.empty}>
              <Text style={styles.emptyText}>
                No trips yet. Book your first trip from the Routes tab.
              </Text>
            </View>
          }
          refreshControl={
            <RefreshControl
              refreshing={isRefreshing}
              onRefresh={() => void handleRefresh()}
              tintColor={colors.black}
            />
          }
        />
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
  card: {
    ...shadow,
    backgroundColor: colors.white,
    borderRadius: radius.md,
    padding: spacing.md,
    marginBottom: spacing.sm,
  },
  cardTopRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  tripName: {
    ...typography.heading,
    color: colors.black,
    flex: 1,
    marginRight: spacing.sm,
  },
  badge: {
    borderRadius: radius.sm,
    paddingVertical: spacing.xs,
    paddingHorizontal: spacing.sm,
  },
  badgeText: {
    ...typography.caption,
  },
  tripPath: {
    ...typography.small,
    color: colors.gray600,
    marginTop: spacing.xs,
  },
  cardBottomRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginTop: spacing.md,
  },
  tripDate: {
    ...typography.small,
    color: colors.gray600,
  },
  tripFare: {
    ...typography.heading,
    color: colors.black,
  },
  empty: {
    padding: spacing.xl,
    alignItems: 'center',
  },
  emptyText: {
    ...typography.body,
    color: colors.gray600,
    textAlign: 'center',
  },
  errorText: {
    ...typography.body,
    color: colors.error,
    textAlign: 'center',
  },
});

export default TripsScreen;
