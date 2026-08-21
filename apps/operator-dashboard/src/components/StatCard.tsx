import type { CSSProperties } from "react";

type Accent = "yellow" | "black" | "green" | "red";

const ACCENT_COLORS: Record<Accent, string> = {
  yellow: "var(--rp-yellow)",
  black: "var(--rp-black)",
  green: "var(--rp-green)",
  red: "var(--rp-red)",
};

interface StatCardProps {
  label: string;
  value: string;
  sub?: string;
  tone?: "neutral" | "positive" | "negative";
  accent?: Accent;
}

export default function StatCard({
  label,
  value,
  sub,
  tone = "neutral",
  accent = "yellow",
}: StatCardProps) {
  const toneClass =
    tone === "positive"
      ? " stat-card__sub--positive"
      : tone === "negative"
        ? " stat-card__sub--negative"
        : "";

  return (
    <div
      className="card stat-card"
      style={{ "--accent": ACCENT_COLORS[accent] } as CSSProperties}
    >
      <p className="stat-card__label">{label}</p>
      <p className="stat-card__value">{value}</p>
      {sub ? <p className={`stat-card__sub${toneClass}`}>{sub}</p> : null}
    </div>
  );
}
