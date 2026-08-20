"use client";

import { useEffect, useState } from "react";
import { getDashboard, formatCents, type OperatorDashboard } from "@/lib/api";
import {
  BarChart,
  Bar,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

export default function DashboardPage() {
  const [data, setData] = useState<OperatorDashboard | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getDashboard(1)
      .then((d) => {
        setData(d);
        setLoading(false);
      })
      .catch((e) => {
        setError(e.message);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-momo-gray">
        <div className="text-2xl font-semibold">Loading dashboard...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-momo-gray p-8">
        <div className="bg-white p-8 rounded-lg shadow-lg max-w-2xl">
          <h1 className="text-2xl font-bold text-momo-red mb-4">Connection Error</h1>
          <p className="text-gray-700 mb-2">Could not load dashboard data.</p>
          <p className="text-sm text-gray-500 font-mono bg-gray-100 p-3 rounded">
            {error}
          </p>
          <p className="text-sm text-gray-600 mt-4">
            Make sure the API is running on http://localhost:8000
          </p>
        </div>
      </div>
    );
  }

  if (!data) return null;

  return (
    <div className="min-h-screen bg-momo-gray">
      <header className="bg-momo-black text-white p-6 shadow-lg">
        <div className="max-w-7xl mx-auto flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-momo-yellow rounded-lg flex items-center justify-center text-momo-black font-bold text-xl">
              R
            </div>
            <div>
              <h1 className="text-2xl font-bold">RoutePay Operator</h1>
              <p className="text-sm text-gray-400">Thabo&apos;s Taxi Service</p>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <button className="bg-momo-yellow text-momo-black px-4 py-2 rounded-lg font-semibold hover:opacity-90">
              Request Payout
            </button>
            <div className="w-10 h-10 bg-momo-yellow rounded-full flex items-center justify-center text-momo-black font-bold">
              TM
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto p-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
          <KpiCard
            label="Today's Revenue"
            value={formatCents(data.today_revenue_cents)}
            trend="+12%"
            trendUp
          />
          <KpiCard
            label="This Week"
            value={formatCents(data.week_revenue_cents)}
            trend="+8%"
            trendUp
          />
          <KpiCard
            label="Total Trips"
            value={data.total_trips.toString()}
            trend="+24 today"
            trendUp
          />
          <KpiCard
            label="Average Fare"
            value={formatCents(data.avg_fare_cents)}
            trend="MoMo paid"
            trendUp={false}
          />
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          <div className="bg-white p-6 rounded-lg shadow">
            <h2 className="text-lg font-semibold mb-4">Revenue — Last 30 Days</h2>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={data.daily_revenue}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip formatter={(value: number) => formatCents(value)} />
                <Line
                  type="monotone"
                  dataKey="revenue_cents"
                  stroke="#FFCC00"
                  strokeWidth={3}
                  dot={{ fill: "#000000", r: 4 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>

          <div className="bg-white p-6 rounded-lg shadow">
            <h2 className="text-lg font-semibold mb-4">Revenue by Route</h2>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={data.revenue_by_route}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="route_name" angle={-15} textAnchor="end" height={80} />
                <YAxis />
                <Tooltip formatter={(value: number) => formatCents(value)} />
                <Bar dataKey="revenue_cents" fill="#000000" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="p-6 border-b">
            <h2 className="text-lg font-semibold">Top Performing Routes</h2>
          </div>
          <table className="w-full">
            <thead className="bg-momo-gray">
              <tr>
                <th className="text-left p-4 font-semibold">Route</th>
                <th className="text-right p-4 font-semibold">Trips</th>
                <th className="text-right p-4 font-semibold">Revenue</th>
              </tr>
            </thead>
            <tbody>
              {data.top_routes.map((r) => (
                <tr key={r.route_id} className="border-t hover:bg-momo-gray/50">
                  <td className="p-4 font-medium">{r.route_name}</td>
                  <td className="p-4 text-right">{r.trips}</td>
                  <td className="p-4 text-right font-semibold text-momo-green">
                    {formatCents(r.revenue_cents)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="mt-6 bg-momo-black text-white p-6 rounded-lg">
          <h3 className="text-lg font-semibold mb-3 text-momo-yellow">
            MoMo API Integration
          </h3>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-3 text-sm">
            <ApiPill name="Collections" />
            <ApiPill name="Disbursements" />
            <ApiPill name="Remittances" />
            <ApiPill name="Payments" />
            <ApiPill name="Auth" />
          </div>
        </div>
      </div>
    </div>
  );
}

function KpiCard({
  label,
  value,
  trend,
  trendUp,
}: {
  label: string;
  value: string;
  trend: string;
  trendUp: boolean;
}) {
  return (
    <div className="bg-white p-6 rounded-lg shadow hover:shadow-lg transition">
      <p className="text-sm text-gray-500 mb-1">{label}</p>
      <p className="text-3xl font-bold text-momo-black">{value}</p>
      <p
        className={`text-sm mt-2 ${
          trendUp ? "text-momo-green" : "text-gray-500"
        }`}
      >
        {trend}
      </p>
    </div>
  );
}

function ApiPill({ name }: { name: string }) {
  return (
    <div className="flex items-center gap-2">
      <div className="w-2 h-2 rounded-full bg-momo-green animate-pulse" />
      <span>{name}</span>
    </div>
  );
}
