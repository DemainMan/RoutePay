import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from 'react';
import * as SecureStore from 'expo-secure-store';
import { api } from '../api/client';

export interface AuthUser {
  id: number;
  phoneNumber: string;
  name?: string | null;
}

interface VerifyResponse {
  token: string;
  user: AuthUser;
}

interface AuthContextValue {
  user: AuthUser | null;
  token: string | null;
  isLoading: boolean;
  isRestoringSession: boolean;
  requestOtp: (phone: string) => Promise<void>;
  verifyOtp: (phone: string, otp: string) => Promise<void>;
  signOut: () => Promise<void>;
}

const TOKEN_KEY = 'routepay.auth.token';
const USER_KEY = 'routepay.auth.user';

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isRestoringSession, setIsRestoringSession] = useState(true);

  useEffect(() => {
    let mounted = true;

    const restore = async () => {
      try {
        const [storedToken, storedUser] = await Promise.all([
          SecureStore.getItemAsync(TOKEN_KEY),
          SecureStore.getItemAsync(USER_KEY),
        ]);

        if (mounted && storedToken && storedUser) {
          const parsedUser = JSON.parse(storedUser) as AuthUser;
          api.setToken(storedToken);
          setToken(storedToken);
          setUser(parsedUser);
        }
      } catch {
        // Corrupt or unavailable storage - start unauthenticated.
      } finally {
        if (mounted) {
          setIsRestoringSession(false);
        }
      }
    };

    void restore();

    return () => {
      mounted = false;
    };
  }, []);

  const requestOtp = useCallback(async (phone: string) => {
    setIsLoading(true);
    try {
      await api.requestOtp(phone);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const verifyOtp = useCallback(async (phone: string, otp: string) => {
    setIsLoading(true);
    try {
      const response = (await api.verifyOtp(phone, otp)) as VerifyResponse;
      api.setToken(response.token);
      await Promise.all([
        SecureStore.setItemAsync(TOKEN_KEY, response.token),
        SecureStore.setItemAsync(USER_KEY, JSON.stringify(response.user)),
      ]);
      setToken(response.token);
      setUser(response.user);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const signOut = useCallback(async () => {
    await Promise.all([
      SecureStore.deleteItemAsync(TOKEN_KEY),
      SecureStore.deleteItemAsync(USER_KEY),
    ]).catch(() => undefined);
    setToken(null);
    setUser(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user,
      token,
      isLoading,
      isRestoringSession,
      requestOtp,
      verifyOtp,
      signOut,
    }),
    [user, token, isLoading, isRestoringSession, requestOtp, verifyOtp, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return ctx;
}
