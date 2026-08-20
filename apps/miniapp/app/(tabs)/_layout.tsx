import { Tabs } from "expo-router";
import { Colors } from "../../src/constants/theme";

export default function TabsLayout() {
  return (
    <Tabs
      screenOptions={{
        tabBarActiveTintColor: Colors.primary,
        tabBarInactiveTintColor: Colors.textSecondary,
        headerStyle: { backgroundColor: Colors.primary },
        headerTintColor: Colors.accent,
        headerTitleStyle: { fontWeight: "bold" },
      }}
    >
      <Tabs.Screen
        name="index"
        options={{ title: "Home", tabBarLabel: "Home" }}
      />
      <Tabs.Screen
        name="routes"
        options={{ title: "Routes", tabBarLabel: "Routes" }}
      />
      <Tabs.Screen
        name="scan"
        options={{ title: "Scan QR", tabBarLabel: "Scan" }}
      />
      <Tabs.Screen
        name="passes"
        options={{ title: "Passes", tabBarLabel: "Passes" }}
      />
      <Tabs.Screen
        name="profile"
        options={{ title: "Profile", tabBarLabel: "Profile" }}
      />
    </Tabs>
  );
}
