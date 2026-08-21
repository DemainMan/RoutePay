import StatCard from "@/components/StatCard";
import { getEarnings } from "@/lib/api";
import { formatGHS } from "@/lib/format";

export const dynamic = "force-dynamic";

export default async function EarningsPage() {
  const days = await getEarnings();

  const totalRevenue = days.reduce((sum, day) => sum + day.revenue, 0);
  const totalTrips = days.reduce((sum, day) => sum + day.trips, 0);
  const totalMomo = days.reduce((sum, day) => sum + day.momo, 0);
  const totalCash = totalRevenue - totalMomo;
  const averageFare = totalTrips > 0 ? totalRevenue / totalTrips : 0;
  const momoShare =
    totalRevenue > 0 ? Math.round((totalMomo / totalRevenue) * 100) : 0;
  const maxRevenue = Math.max(...days.map((day) => day.revenue));
  const bestIndex = days.findIndex((day) => day.revenue === maxRevenue);

  return (
    <div className="page">
      <section className="page-head">
        <div>
          <h1>Earnings</h1>
          <p className="page-sub">
            Revenue settled through RoutePay over the last 7 days.
          </p>
        </div>
      </section>

      <section className="stat-grid" aria-label="Earnings summary">
        <StatCard
          label="7-Day Revenue"
          value={formatGHS(totalRevenue)}
          sub="All routes combined"
          accent="yellow"
        />
        <StatCard
          label="Trips Settled"
          value={totalTrips.toLocaleString()}
          sub="Completed and paid trips"
          accent="black"
        />
        <StatCard
          label="Average Fare"
          value={formatGHS(averageFare)}
          sub="Per trip"
          accent="green"
        />
        <StatCard
          label="MoMo Share"
          value={`${momoShare}%`}
          sub={`${formatGHS(totalCash)} collected in cash`}
          accent="green"
        />
      </section>

      <section className="card">
        <div className="card__head">
          <h2 className="card__title">Daily revenue</h2>
          <span className="footnote">Best day highlighted in green</span>
        </div>
        <div className="card__body">
          <div className="chart" role="img" aria-label="Daily revenue bar chart">
            {days.map((day, index) => (
              <div
                key={day.date}
                className="chart__col"
                title={`${day.label}: ${formatGHS(day.revenue)}`}
              >
                <div className="chart__track">
                  <div
                    className={`chart__bar${
                      index === bestIndex ? " chart__bar--best" : ""
                    }`}
                    style={{
                      height: `${Math.round((day.revenue / maxRevenue) * 100)}%`,
                    }}
                  />
                </div>
                <span className="chart__label">{day.label}</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="card">
        <div className="card__head">
          <h2 className="card__title">Earnings breakdown</h2>
        </div>
        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th scope="col">Day</th>
                <th scope="col">Date</th>
                <th scope="col">Trips</th>
                <th scope="col">MoMo</th>
                <th scope="col">Cash</th>
                <th scope="col">Total</th>
              </tr>
            </thead>
            <tbody>
              {days.map((day) => (
                <tr key={day.date}>
                  <td className="td-strong">{day.label}</td>
                  <td className="td-muted">{day.date}</td>
                  <td>{day.trips}</td>
                  <td>{formatGHS(day.momo)}</td>
                  <td>{formatGHS(day.cash)}</td>
                  <td className="td-strong">{formatGHS(day.revenue)}</td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr>
                <td>Total</td>
                <td></td>
                <td>{totalTrips}</td>
                <td>{formatGHS(totalMomo)}</td>
                <td>{formatGHS(totalCash)}</td>
                <td>{formatGHS(totalRevenue)}</td>
              </tr>
            </tfoot>
          </table>
        </div>
      </section>
    </div>
  );
}
