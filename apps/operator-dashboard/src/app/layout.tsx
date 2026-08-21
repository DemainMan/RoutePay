import type { Metadata } from "next";
import { Inter } from "next/font/google";
import Header from "@/components/Header";
import "./globals.css";

const inter = Inter({ subsets: ["latin"], display: "swap" });

export const metadata: Metadata = {
  title: "RoutePay Operator Dashboard",
  description: "Monitor trips, routes and MoMo earnings across your fleet.",
};

export default function RootLayout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <Header />
        <main className="main">
          <div className="container">{children}</div>
        </main>
      </body>
    </html>
  );
}
