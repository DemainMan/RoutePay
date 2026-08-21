import React from 'react';
import { ActivityIndicator, StyleSheet, Text, View } from 'react-native';
import {
  DefaultTheme,
  NavigationContainer,
  type Theme,
} from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';
import { Ionicons } from '@expo/vector-icons';
import LoginScreen from '../screens/LoginScreen';
import OtpScreen from '../screens/OtpScreen';
import RoutesScreen from '../screens/RoutesScreen';
import TripsScreen from '../screens/TripsScreen';
import PassesScreen from '../screens/PassesScreen';
import ProfileScreen from '../screens/ProfileScreen';
import { useAuth } from '../contexts/AuthContext';
import type { MainTabParamList, RootStackParamList } from './types';
import { colors, spacing, typography } from '../theme';

const Stack = createNativeStackNavigator<RootStackParamList>();
const Tab = createBottomTabNavigator<MainTabParamList>();

const navigationTheme: Theme = {
  ...DefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    primary: colors.black,
    background: colors.gray100,
    card: colors.white,
    text: colors.black,
    border: colors.gray200,
  },
};

function renderTabIcon(routeName: keyof MainTabParamList, color: string, size: number) {
  switch (routeName) {
    case 'Routes':
      return <Ionicons name="map-outline" size={size} color={color} />;
    case 'Trips':
      return <Ionicons name="receipt-outline" size={size} color={color} />;
    case 'Passes':
      return <Ionicons name="card-outline" size={size} color={color} />;
    case 'Profile':
      return <Ionicons name="person-circle-outline" size={size} color={color} />;
    default:
      return null;
  }
}

function MainTabs() {
  return (
    <Tab.Navigator
      screenOptions={({ route }) => ({
        headerShown: false,
        tabBarActiveTintColor: colors.black,
        tabBarInactiveTintColor: colors.gray600,
        tabBarLabelStyle: styles.tabLabel,
        tabBarIcon: ({ color, size }) => renderTabIcon(route.name, color, size),
      })}
    >
      <Tab.Screen name="Routes" component={RoutesScreen} />
      <Tab.Screen name="Trips" component={TripsScreen} />
      <Tab.Screen name="Passes" component={PassesScreen} />
      <Tab.Screen name="Profile" component={ProfileScreen} />
    </Tab.Navigator>
  );
}

function AppNavigator() {
  const { token, isRestoringSession } = useAuth();

  if (isRestoringSession) {
    return (
      <View style={styles.splash}>
        <Text style={styles.splashBrand}>RoutePay</Text>
        <ActivityIndicator color={colors.black} />
      </View>
    );
  }

  return (
    <NavigationContainer theme={navigationTheme}>
      <Stack.Navigator
        screenOptions={{
          headerStyle: { backgroundColor: colors.primary },
          headerTintColor: colors.black,
          headerTitleStyle: styles.headerTitle,
          headerShadowVisible: false,
        }}
      >
        {token ? (
          <Stack.Screen name="Main" component={MainTabs} options={{ headerShown: false }} />
        ) : (
          <>
            <Stack.Screen name="Login" component={LoginScreen} options={{ headerShown: false }} />
            <Stack.Screen name="Otp" component={OtpScreen} options={{ title: 'Verify' }} />
          </>
        )}
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const styles = StyleSheet.create({
  tabLabel: {
    fontSize: 11,
    fontWeight: '600',
  },
  headerTitle: {
    ...typography.heading,
    color: colors.black,
  },
  splash: {
    flex: 1,
    backgroundColor: colors.primary,
    alignItems: 'center',
    justifyContent: 'center',
  },
  splashBrand: {
    ...typography.hero,
    color: colors.black,
    marginBottom: spacing.md,
  },
});

export default AppNavigator;
