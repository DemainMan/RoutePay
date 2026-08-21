import Link from "next/link";
import StatCard from "@/components/StatCard";
import TripTable from "@/components/TripTable";
import { getStats, getTrips } from "@/lib/api";
import { formatDelta, formatGHS } from "@/lib/format";

export const dynamic = "force-dynamic";

export default async function DashboardPage() {
  const [stats, trips] = await Promise.all([getStats(), getTrips()]);
  const recentTrips = trips.slice(0, 6);

  return (
    <div className="page">
      <section className="page-head">
        <div>
          <h1>Dashboard</h1>
          <p className="page-sub">
            Live overview of your fleet, fares and MoMo settlements.
          </p>
        </div>
        <Link href="/trips" className="btn btn--primary">
          View all trips
        </Link>
      </section>

      <section className="stat-grid" aria-label="Key stats">
        <StatCard
          label="Today's Trips"
          value={stats.todaysTrips.toLocaleString()}
          sub={`${formatDelta(stats.tripsDelta)} vs yesterday`}
          tone={stats.tripsDelta >= 0 ? "positive" : "negative"}
          accent="yellow"
        />
        <StatCard
          label="Total Earnings"
          value={formatGHS(stats.totalEarnings)}
          sub={`${formatDelta(stats.earningsDelta)} vs yesterday`}
          tone={stats.earningsDelta >= 0 ? "positive" : "negative"}
          accent="green"
        />
        <StatCard
          label="Active Routes"
          value={stats.activeRoutes.toLocaleString()}
          sub="Serving commuters right now"
          accent="black"
        />
        <StatCard
          label="Active Passes"
          value={stats.activePasses.toLocaleString()}
          sub="Valid commuter passes today"
          accent="yellow"
        />
      </section>

      <section className="card">
        <div className="card__head">
          <h2 className="card__title">Recent trips</h2>
          <Link href="/trips" className="card__link">
            View all →
          </Link>
        </div>
        <TripTable trips={recentTrips} />
      </section>
    </div>
  );
}
