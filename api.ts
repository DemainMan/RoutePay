import axios from "axios";

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8000/api/v1";

export const api = axios.create({
  baseURL: API_URL,
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  if (typeof window !== "undefined") {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

export interface OperatorDashboard {
  today_revenue_cents: number;
  week_revenue_cents: number;
  total_trips: number;
  avg_fare_cents: number;
  top_routes: { route_id: number; route_name: string; trips: number; revenue_cents: number }[];
  daily_revenue: { date: string; revenue_cents: number }[];
  revenue_by_route: { route_name: string; revenue_cents: number }[];
}

export async function getDashboard(operatorId: number) {
  const res = await api.get(`/operator/dashboard?operator_id=${operatorId}`);
  return res.data as OperatorDashboard;
}

export function formatCents(cents: number): string {
  return `R${(cents / 100).toFixed(2)}`;
}
