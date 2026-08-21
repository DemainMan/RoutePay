const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";
const REQUEST_TIMEOUT_MS = 4000;

export type PaymentMethod = "momo" | "cash";

export type TripStatus = "completed" | "in_progress" | "pending" | "failed";

export interface Trip {
  id: string;
  route: string;
  driver: string;
  vehicle: string;
  passengers: number;
  fare: number;
  paymentMethod: PaymentMethod;
  status: TripStatus;
  startedAt: string;
  endedAt?: string;
}

export interface DashboardStats {
  todaysTrips: number;
  totalEarnings: number;
  activeRoutes: number;
  activePasses: number;
  tripsDelta: number;
  earningsDelta: number;
}

export interface DailyEarnings {
  date: string;
  label: string;
  trips: number;
  momo: number;
  cash: number;
  revenue: number;
}

function hoursAgo(hours: number): string {
  return new Date(Date.now() - hours * 3_600_000).toISOString();
}

export const MOCK_TRIPS: Trip[] = [
  {
    id: "TRP-1087",
    route: "Circle → Madina",
    driver: "Kwame Mensah",
    vehicle: "GR-5421-23",
    passengers: 14,
    fare: 462,
    paymentMethod: "momo",
    status: "completed",
    startedAt: hoursAgo(0.4),
    endedAt: hoursAgo(0.1),
  },
  {
    id: "TRP-1086",
    route: "Kaneshie → Kasoa",
    driver: "Yaw Boateng",
    vehicle: "GX-2284-22",
    passengers: 12,
    fare: 540,
    paymentMethod: "cash",
    status: "in_progress",
    startedAt: hoursAgo(0.2),
  },
  {
    id: "TRP-1085",
    route: "Tudu → Adenta",
    driver: "Akosua Frimpong",
    vehicle: "GR-7719-24",
    passengers: 15,
    fare: 495,
    paymentMethod: "momo",
    status: "completed",
    startedAt: hoursAgo(0.9),
    endedAt: hoursAgo(0.4),
  },
  {
    id: "TRP-1084",
    route: "Circle → Tema",
    driver: "Kofi Asante",
    vehicle: "GT-3390-21",
    passengers: 11,
    fare: 605,
    paymentMethod: "momo",
    status: "failed",
    startedAt: hoursAgo(1.4),
  },
  {
    id: "TRP-1083",
    route: "Madina → Airport",
    driver: "Abena Owusu",
    vehicle: "GR-1108-23",
    passengers: 9,
    fare: 387,
    paymentMethod: "cash",
    status: "completed",
    startedAt: hoursAgo(1.8),
    endedAt: hoursAgo(1.2),
  },
  {
    id: "TRP-1082",
    route: "Kasoa → Kaneshie",
    driver: "Yaw Boateng",
    vehicle: "GX-2284-22",
    passengers: 13,
    fare: 585,
    paymentMethod: "momo",
    status: "completed",
    startedAt: hoursAgo(2.3),
    endedAt: hoursAgo(1.7),
  },
  {
    id: "TRP-1081",
    route: "Achimota → Dansoman",
    driver: "Kwabena Osei",
    vehicle: "GR-9034-22",
    passengers: 10,
    fare: 350,
    paymentMethod: "cash",
    status: "pending",
    startedAt: hoursAgo(2.6),
  },
  {
    id: "TRP-1080",
    route: "Circle → Madina",
    driver: "Kwame Mensah",
    vehicle: "GR-5421-23",
    passengers: 16,
    fare: 528,
    paymentMethod: "momo",
    status: "completed",
    startedAt: hoursAgo(3.1),
    endedAt: hoursAgo(2.5),
  },
  {
    id: "TRP-1079",
    route: "Tudu → Adenta",
    driver: "Akosua Frimpong",
    vehicle: "GR-7719-24",
    passengers: 8,
    fare: 264,
    paymentMethod: "cash",
    status: "completed",
    startedAt: hoursAgo(3.4),
    endedAt: hoursAgo(2.9),
  },
  {
    id: "TRP-1078",
    route: "Circle → Tema",
    driver: "Kofi Asante",
    vehicle: "GT-3390-21",
    passengers: 12,
    fare: 660,
    paymentMethod: "momo",
    status: "failed",
    startedAt: hoursAgo(4.2),
  },
];

export const MOCK_STATS: DashboardStats = {
  todaysTrips: 132,
  totalEarnings: 8946.5,
  activeRoutes: 14,
  activePasses: 342,
  tripsDelta: 12.4,
  earningsDelta: 8.1,
};

const WEEK_TEMPLATE = [
  { trips: 96, momo: 3264, cash: 1152 },
  { trips: 104, momo: 3640, cash: 1092 },
  { trips: 88, momo: 2904, cash: 1232 },
  { trips: 112, momo: 4032, cash: 1176 },
  { trips: 126, momo: 4914, cash: 1386 },
  { trips: 134, momo: 5226, cash: 1474 },
  { trips: 121, momo: 4598, cash: 1201 },
];

export const MOCK_EARNINGS: DailyEarnings[] = WEEK_TEMPLATE.map((day, index) => {
  const date = new Date(Date.now() - (WEEK_TEMPLATE.length - 1 - index) * 86_400_000);
  return {
    date: date.toISOString().slice(0, 10),
    label: date.toLocaleDateString("en-US", { weekday: "short" }),
    trips: day.trips,
    momo: day.momo,
    cash: day.cash,
    revenue: day.momo + day.cash,
  };
});

async function getJson<T>(path: string): Promise<T | null> {
  const controller = new AbortController();
  const timeout = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);
  try {
    const response = await fetch(`${API_BASE_URL}${path}`, {
      cache: "no-store",
      signal: controller.signal,
      headers: { Accept: "application/json" },
    });
    if (!response.ok) {
      return null;
    }
    return (await response.json()) as T;
  } catch {
    return null;
  } finally {
    clearTimeout(timeout);
  }
}

export async function getStats(): Promise<DashboardStats> {
  return (await getJson<DashboardStats>("/api/operator/stats")) ?? MOCK_STATS;
}

export async function getTrips(): Promise<Trip[]> {
  const payload = await getJson<{ trips: Trip[] }>("/api/operator/trips");
  return payload?.trips ?? MOCK_TRIPS;
}

export async function getEarnings(days = 7): Promise<DailyEarnings[]> {
  const payload = await getJson<{ days: DailyEarnings[] }>(
    `/api/operator/earnings?days=${days}`
  );
  return payload?.days?.length ? payload.days : MOCK_EARNINGS;
}
