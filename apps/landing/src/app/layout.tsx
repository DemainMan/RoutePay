import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'RoutePay — Cashless Taxi Travel',
  description: 'Pay your taxi fare with MoMo. Cashless, fast, and secure payments for South African commuters.',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
