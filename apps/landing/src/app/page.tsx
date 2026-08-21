export default function Home() {
  return (
    <>
      <section className="hero">
        <h1>
          Pay Your Taxi Fare<br />
          with <span>MoMo</span>
        </h1>
        <p>
          Cashless, fast, and secure payments for South African commuters.
          Skip the cash queue — tap, pay, and ride.
        </p>
        <div className="hero-buttons">
          <a href="#features" className="btn btn-primary">
            Get Started
          </a>
          <a href="#operators" className="btn btn-outline">
            Operator Dashboard
          </a>
        </div>
      </section>

      <section id="features" className="section">
        <h2 className="section-title">Why RoutePay?</h2>
        <p className="section-subtitle">
          Built for the way South Africans actually commute
        </p>
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon">⚡</div>
            <h3>Tap to Pay</h3>
            <p>
              Instant MoMo payments — no cash, no card machine, no waiting.
              Pay your fare in under 3 seconds.
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">🎫</div>
            <h3>Travel Passes</h3>
            <p>
              Save more with daily, weekly, or monthly passes.
              One purchase, unlimited rides on your route.
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-icon">📍</div>
            <h3>Real-time Tracking</h3>
            <p>
              Live trip updates so you always know where your ride is.
              Operators see every vehicle on every route.
            </p>
          </div>
        </div>
      </section>

      <section id="how-it-works" className="section" style={{ background: '#FAFAFA' }}>
        <h2 className="section-title">How It Works</h2>
        <p className="section-subtitle">Three steps to cashless commuting</p>
        <div className="steps">
          <div className="step">
            <div className="step-number">1</div>
            <h3>Register</h3>
            <p>Sign up with your phone number. Verify with a quick OTP — no email needed.</p>
          </div>
          <div className="step">
            <div className="step-number">2</div>
            <h3>Select Route</h3>
            <p>Browse available taxi routes. Pick your origin, destination, and boarding stop.</p>
          </div>
          <div className="step">
            <div className="step-number">3</div>
            <h3>Pay with MoMo</h3>
            <p>Confirm your trip and pay instantly through MoMo. Your digital ticket is ready.</p>
          </div>
        </div>
      </section>

      <section id="operators" className="operator-section">
        <div className="operator-content">
          <div>
            <h2>
              Built for <span>Operators</span> Too
            </h2>
            <p>
              RoutePay isn&apos;t just for commuters. Operators get a powerful dashboard
              to monitor trips, track earnings, and manage their fleet — all in real time.
            </p>
          </div>
          <div>
            <ul className="operator-benefits">
              <li>
                <span className="benefit-icon">✓</span>
                Instant MoMo payouts — no more waiting for cash reconciliation
              </li>
              <li>
                <span className="benefit-icon">✓</span>
                Live trip monitoring across all your routes
              </li>
              <li>
                <span className="benefit-icon">✓</span>
                Earnings dashboard with daily, weekly, and monthly breakdowns
              </li>
              <li>
                <span className="benefit-icon">✓</span>
                Passenger analytics — know your peak hours and busiest routes
              </li>
              <li>
                <span className="benefit-icon">✓</span>
                Travel pass management — set pricing and track active passes
              </li>
            </ul>
          </div>
        </div>
      </section>

      <footer className="footer">
        <div className="footer-brand">
          Route<span>Pay</span>
        </div>
        <p>Cashless taxi travel for South Africa</p>
        <p>
          Built for the{' '}
          <a href="https://momodeveloper.mtn.com" target="_blank" rel="noopener noreferrer">
            MoMo Mini App Hackathon 2026
          </a>{' '}
          — Track 3: Travel &amp; Mobility
        </p>
        <p style={{ marginTop: '1rem', fontSize: '0.8rem' }}>
          © 2026 RoutePay. MIT License.
        </p>
      </footer>
    </>
  );
}
