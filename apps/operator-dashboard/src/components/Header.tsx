"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const NAV_LINKS = [
  { href: "/", label: "Dashboard" },
  { href: "/trips", label: "Trips" },
  { href: "/earnings", label: "Earnings" },
];

export default function Header() {
  const pathname = usePathname();

  return (
    <header className="header">
      <div className="container header__inner">
        <Link href="/" className="brand" aria-label="RoutePay home">
          <span className="brand__mark">R</span>
          <span>
            Route<span className="brand__pay">Pay</span>
          </span>
        </Link>
        <nav className="nav" aria-label="Primary">
          {NAV_LINKS.map(({ href, label }) => {
            const active =
              href === "/" ? pathname === "/" : pathname.startsWith(href);
            return (
              <Link
                key={href}
                href={href}
                className={`nav__link${active ? " nav__link--active" : ""}`}
                aria-current={active ? "page" : undefined}
              >
                {label}
              </Link>
            );
          })}
        </nav>
        <div className="header__right">
          <span className="operator-chip">
            <span className="operator-avatar">KO</span>
            <span>
              <span className="operator-name">Kwame Osei</span>
              Fleet operator
            </span>
          </span>
        </div>
      </div>
    </header>
  );
}
