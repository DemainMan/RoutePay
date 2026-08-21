import Link from "next/link";
import TripTable from "@/components/TripTable";
import { getTrips } from "@/lib/api";

export const dynamic = "force-dynamic";

const FILTERS = [
  { value: "all", label: "All" },
  { value: "completed", label: "Completed" },
  { value: "in_progress", label: "In progress" },
  { value: "pending", label: "Pending" },
  { value: "failed", label: "Failed" },
] as const;

type StatusFilter = (typeof FILTERS)[number]["value"];

interface TripsPageProps {
  searchParams?: { status?: string };
}

export default async function TripsPage({ searchParams }: TripsPageProps) {
  const requested = searchParams?.status as StatusFilter | undefined;
  const status: StatusFilter =
    requested && FILTERS.some((filter) => filter.value === requested)
      ? requested
      : "all";

  const trips = await getTrips();
  const filtered =
    status === "all" ? trips : trips.filter((trip) => trip.status === status);

  return (
    <div className="page">
      <section className="page-head">
        <div>
          <h1>Trips</h1>
          <p className="page-sub">
            Monitor every trip across your fleet in real time.
          </p>
        </div>
      </section>

      <section className="card">
        <div className="card__head">
          <div className="chip-row">
            {FILTERS.map((filter) => (
              <Link
                key={filter.value}
                href={
                  filter.value === "all"
                    ? "/trips"
                    : `/trips?status=${filter.value}`
                }
                className={`chip${status === filter.value ? " chip--active" : ""}`}
              >
                {filter.label}
              </Link>
            ))}
          </div>
          <span className="footnote">
            Showing {filtered.length} of {trips.length} trips
          </span>
        </div>
        <TripTable trips={filtered} />
      </section>
    </div>
  );
}
