const BASE_URL = process.env.EXPO_PUBLIC_API_URL || 'http://localhost:8080';

class ApiClient {
  private token: string | null = null;

  setToken(token: string) { this.token = token; }

  private async request<T>(method: string, path: string, body?: any): Promise<T> {
    const headers: Record<string, string> = { 'Content-Type': 'application/json' };
    if (this.token) headers['Authorization'] = `Bearer ${this.token}`;
    
    const response = await fetch(`${BASE_URL}${path}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    });
    
    if (!response.ok) {
      const error = await response.json().catch(() => ({ message: 'Request failed' }));
      throw new Error(error.message || `HTTP ${response.status}`);
    }
    
    return response.json();
  }

  // Auth
  requestOtp(phone: string) { return this.request('POST', '/api/auth/otp/request', { phone }); }
  verifyOtp(phone: string, otp: string) { return this.request<any>('POST', '/api/auth/otp/verify', { phone, otp }); }

  // Routes
  getRoutes() { return this.request<any[]>('GET', '/api/routes'); }
  getRoute(id: number) { return this.request<any>('GET', `/api/routes/${id}`); }

  // Trips
  bookTrip(routeId: number) { return this.request<any>('POST', '/api/trips', { routeId }); }
  getTrips() { return this.request<any[]>('GET', '/api/trips'); }

  // Passes
  purchasePass(passType: string) { return this.request<any>('POST', '/api/passes', { passType }); }
  getPasses() { return this.request<any[]>('GET', '/api/passes'); }
}

export const api = new ApiClient();
