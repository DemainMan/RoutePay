export interface User {
  id: number;
  phone: string;
  name: string;
  role: 'COMMUTER' | 'OPERATOR' | 'ADMIN';
}

export interface AuthResponse {
  token: string;
  phone: string;
  name: string;
  role: string;
}

export interface Route {
  id: number;
  name: string;
  originName: string;
  destName: string;
  fare: number;
  currency: string;
  active: boolean;
  createdAt: string;
}

export interface Trip {
  id: number;
  routeId: number;
  routeName: string;
  status: 'BOOKED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  farePaid: number;
  momoReference: string;
  createdAt: string;
}

export interface TravelPass {
  id: number;
  passType: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  validFrom: string;
  validUntil: string;
  pricePaid: number;
  status: 'ACTIVE' | 'EXPIRED' | 'CANCELLED';
  momoReference: string;
}

export interface OtpRequest {
  phone: string;
}

export interface OtpVerifyRequest {
  phone: string;
  otp: string;
}

export interface BookTripRequest {
  routeId: number;
  boardingStopId?: number;
  alightingStopId?: number;
}

export interface PurchasePassRequest {
  passType: 'DAILY' | 'WEEKLY' | 'MONTHLY';
}

export interface TripUpdate {
  tripId: number;
  status: string;
  latitude?: number;
  longitude?: number;
}
