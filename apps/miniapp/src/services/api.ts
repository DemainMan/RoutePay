/**
 * RoutePay API client — typed wrapper for the FastAPI backend.
 */

const API_URL = process.env.EXPO_PUBLIC_API_URL || "http://localhost:8000";

interface ApiResponse<T> {
  data: T;
  status: number;
}

class ApiClient {
  private baseUrl: string;
  private token: string | null = null;

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl;
  }

  setToken(token: string | null) {
    this.token = token;
  }

  private async request<T>(
    method: string,
    path: string,
    body?: unknown
  ): Promise<T> {
    const headers: Record<string, string> = {
      "Content-Type": "application/json",
    };
    if (this.token) {
      headers["Authorization"] = `Bearer ${this.token}`;
    }

    const response = await fetch(`${this.baseUrl}${path}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });

    if (!response.ok) {
      const error = await response.json().catch(() => ({ detail: "Request failed" }));
      throw new Error(error.detail || `HTTP ${response.status}`);
    }

    return response.json();
  }

  // Auth
  async requestOtp(phone: string) {
    return this.request<{ message: string }>("POST", "/api/v1/auth/request-otp", { phone });
  }

  async verifyOtp(phone: string, otp: string) {
    return this.request<{
      access_token: string;
      token_type: string;
      user_id: number;
      role: string;
    }>("POST", "/api/v1/auth/verify-otp", { phone, otp });
  }

  // Routes
  async getRoutes(start?: string, end?: string) {
    const params = new URLSearchParams();
    if (start) params.set("start", start);
    if (end) params.set("end", end);
    const query = params.toString() ? `?${params}` : "";
    return this.request<Array<{
      id: number;
      name: string;
      start_point: string;
      end_point: string;
      fare_cents: number;
      fare_display: string;
      mode: string;
    }>>("GET", `/api/v1/routes${query}`);
  }

  // Trips
  async startTrip(vehicleId: number, routeId: number) {
    return this.request<{
      id: number;
      fare_cents: number;
      fare_display: string;
      status: string;
      route_name: string;
    }>("POST", "/api/v1/trips/start", { vehicle_id: vehicleId, route_id: routeId });
  }

  async completeTrip(tripId: number) {
    return this.request<{
      id: number;
      fare_cents: number;
      fare_display: string;
      status: string;
    }>("POST", `/api/v1/trips/${tripId}/complete`);
  }

  async getMyTrips() {
    return this.request<Array<{
      id: number;
      fare_cents: number;
      fare_display: string;
      status: string;
      route_name: string;
      created_at: string;
    }>>("GET", "/api/v1/trips/me");
  }

  // Passes
  async buyPass(type: string, routeId?: number) {
    return this.request<{
      id: number;
      type: string;
      valid_until: string;
      is_active: boolean;
    }>("POST", "/api/v1/passes", { type, route_id: routeId });
  }

  async getMyPasses() {
    return this.request<Array<{
      id: number;
      type: string;
      valid_until: string;
      is_active: boolean;
    }>>("GET", "/api/v1/passes/me");
  }
}

export const api = new ApiClient(API_URL);
