import type { Trip } from "@/lib/api";
import { STATUS_LABELS, formatGHS, formatTime } from "@/lib/format";

interface TripTableProps {
  trips: Trip[];
}

export default function TripTable({ trips }: TripTableProps) {
  if (trips.length === 0) {
    return <div className="empty">No trips found.</div>;
  }

  return (
    <div className="table-wrap">
      <table className="table">
        <thead>
          <tr>
            <th scope="col">Trip</th>
            <th scope="col">Route</th>
            <th scope="col">Driver</th>
            <th scope="col">Vehicle</th>
            <th scope="col">Passengers</th>
            <th scope="col">Fare</th>
            <th scope="col">Payment</th>
            <th scope="col">Status</th>
            <th scope="col">Started</th>
          </tr>
        </thead>
        <tbody>
          {trips.map((trip) => (
            <tr key={trip.id}>
              <td className="td-strong">{trip.id}</td>
              <td>{trip.route}</td>
              <td>{trip.driver}</td>
              <td className="td-muted">{trip.vehicle}</td>
              <td>{trip.passengers}</td>
              <td className="td-strong">{formatGHS(trip.fare)}</td>
              <td>
                <span className="pay-tag">
                  <span
                    className={`pay-dot pay-dot--${trip.paymentMethod}`}
                    aria-hidden="true"
                  />
                  {trip.paymentMethod === "momo" ? "MoMo" : "Cash"}
                </span>
              </td>
              <td>
                <span className={`badge badge--${trip.status}`}>
                  {STATUS_LABELS[trip.status] ?? trip.status}
                </span>
              </td>
              <td className="td-muted">{formatTime(trip.startedAt)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
