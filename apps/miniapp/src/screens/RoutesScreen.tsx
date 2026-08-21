import React, { useCallback, useEffect, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  FlatList,
  Pressable,
  RefreshControl,
  StyleSheet,
  Text,
  View,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { api } from '../api/client';
import { colors, radius, shadow, spacing, typography } from '../theme';

interface TaxiRoute {
  id: number;
  name: string;
  origin: string;
  destination: string;
  fare: number;
}

function formatFare(fare: number): string {
  return `R${fare.toFixed(2)}`;
}

function RoutesScreen() {
  const [routes, setRoutes] = useState<TaxiRoute[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [bookingRouteId, setBookingRouteId] = useState<number | null>(null);

  const loadRoutes = useCallback(async () => {
    try {
      const data = await api.getRoutes();
      setRoutes(data as TaxiRoute[]);
      setLoadError(null);
    } catch (err) {
      setLoadError(err instanceof Error ? err.message : 'Could not load routes.');
    }
  }, []);

  useEffect(() => {
    void (async () => {
      await loadRoutes();
      setIsLoading(false);
    })();
  }, [loadRoutes]);

  const handleRefresh = useCallback(async () => {
    setIsRefreshing(true);
    await loadRoutes();
    setIsRefreshing(false);
  }, [loadRoutes]);

  const bookTrip = useCallback(async (taxiRoute: TaxiRoute) => {
    setBookingRouteId(taxiRoute.id);
    try {
      await api.bookTrip(taxiRoute.id);
      Alert.alert('Trip booked', `${taxiRoute.name} is confirmed. Find it under the Trips tab.`);
    } catch (err) {
      Alert.alert('Booking failed', err instanceof Error ? err.message : 'Please try again.');
    } finally {
      setBookingRouteId(null);
    }
  }, []);

  const confirmBooking = useCallback(
    (taxiRoute: TaxiRoute) => {
      Alert.alert(
        'Book this trip?',
        `${taxiRoute.name}\n${taxiRoute.origin} to ${taxiRoute.destination}\nFare: ${formatFare(taxiRoute.fare)}`,
        [
          { text: 'Cancel', style: 'cancel' },
          { text: 'Book trip', onPress: () => void bookTrip(taxiRoute) },
        ],
      );
    },
    [bookTrip],
  );

  const renderRoute = useCallback(
    ({ item }: { item: TaxiRoute }) => (
      <Pressable
        style={({ pressed }) => [styles.card, pressed && styles.cardPressed]}
        onPress={() => confirmBooking(item)}
        disabled={bookingRouteId !== null}
        accessibilityRole="button"
        accessibilityLabel={`Book ${item.name}`}
      >
        <View style={styles.cardBody}>
          <Text style={styles.routeName}>{item.name}</Text>
          <Text style={styles.routePath}>
            {item.origin} → {item.destination}
          </Text>
        </View>
        <View style={styles.farePill}>
          {bookingRouteId === item.id ? (
            <ActivityIndicator size="small" color={colors.black} />
          ) : (
            <Text style={styles.fareText}>{formatFare(item.fare)}</Text>
          )}
        </View>
      </Pressable>
    ),
    [bookingRouteId, confirmBooking],
  );

  if (isLoading) {
    return (
      <SafeAreaView style={styles.safe} edges={['top']}>
        <View style={styles.header}>
          <Text style={styles.headerTitle}>Routes</Text>
          <Text style={styles.headerSubtitle}>Find your taxi route and book a seat</Text>
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
        <Text style={styles.headerTitle}>Routes</Text>
        <Text style={styles.headerSubtitle}>Find your taxi route and book a seat</Text>
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
        <FlatList
          data={routes}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderRoute}
          contentContainerStyle={styles.listContent}
          ListEmptyComponent={
            <View style={styles.empty}>
              <Text style={styles.emptyText}>No routes available yet. Pull down to refresh.</Text>
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
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.white,
    borderRadius: radius.md,
    padding: spacing.md,
    marginBottom: spacing.sm,
  },
  cardPressed: {
    opacity: 0.85,
  },
  cardBody: {
    flex: 1,
    marginRight: spacing.md,
  },
  routeName: {
    ...typography.heading,
    color: colors.black,
  },
  routePath: {
    ...typography.small,
    color: colors.gray600,
    marginTop: spacing.xs,
  },
  farePill: {
    minWidth: 72,
    alignItems: 'center',
    backgroundColor: colors.primary,
    borderRadius: radius.sm,
    paddingVertical: spacing.xs,
    paddingHorizontal: spacing.sm,
  },
  fareText: {
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

export default RoutesScreen;
