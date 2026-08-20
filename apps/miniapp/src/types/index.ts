/**
 * Shared types for the RoutePay mini app.
 */

export interface User {
  id: number;
  phone: string;
  name: string;
  role: "COMMUTER" | "OPERATOR" | "DRIVER";
}

export interface Route {
  id: number;
  name: string;
  start_point: string;
  end_point: string;
  fare_cents: number;
  fare_display: string;
  mode: "TAXI" | "BUS" | "TRAIN" | "WALKING" | "E_HAILING";
}

export interface Trip {
  id: number;
  commuter_id: number;
  vehicle_id: number;
  route_id: number;
  fare_cents: number;
  fare_display: string;
  status: "PLANNED" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED";
  created_at: string;
  route_name: string;
  vehicle_registration: string;
}

export interface Pass {
  id: number;
  commuter_id: number;
  route_id: number | null;
  type: "DAILY" | "WEEKLY" | "MONTHLY";
  valid_until: string;
  created_at: string;
  is_active: boolean;
}

export interface Transaction {
  id: number;
  momo_ref: string;
  type: "COLLECTION" | "DISBURSEMENT" | "REMITTANCE" | "PAYMENT";
  amount_cents: number;
  amount_display: string;
  status: "PENDING" | "SUCCESSFUL" | "FAILED" | "REJECTED" | "TIMEOUT";
  created_at: string;
}
