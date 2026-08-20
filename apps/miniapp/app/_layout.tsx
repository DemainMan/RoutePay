import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import { Colors } from "../src/constants/theme";

export default function RootLayout() {
  return (
    <>
      <StatusBar style="dark" />
      <Stack
        screenOptions={{
          headerStyle: { backgroundColor: Colors.primary },
          headerTintColor: Colors.accent,
          headerTitleStyle: { fontWeight: "bold" },
        }}
      >
        <Stack.Screen name="(auth)" options={{ headerShown: false }} />
        <Stack.Screen name="(tabs)" options={{ headerShown: false }} />
        <Stack.Screen
          name="trips/active"
          options={{ title: "Active Trip" }}
        />
        <Stack.Screen
          name="passes/buy"
          options={{ title: "Buy Pass" }}
        />
      </Stack>
    </>
  );
}
