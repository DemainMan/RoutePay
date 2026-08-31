#!/bin/bash
echo "Stopping all RoutePay services..."

# Stop backend
if [ -f /tmp/routepay-backend.pid ]; then
  kill $(cat /tmp/routepay-backend.pid) 2>/dev/null
  rm /tmp/routepay-backend.pid
  echo "Backend stopped"
fi

# Stop dashboard
if [ -f /tmp/routepay-dashboard.pid ]; then
  kill $(cat /tmp/routepay-dashboard.pid) 2>/dev/null
  rm /tmp/routepay-dashboard.pid
  echo "Dashboard stopped"
fi

# Stop landing
if [ -f /tmp/routepay-landing.pid ]; then
  kill $(cat /tmp/routepay-landing.pid) 2>/dev/null
  rm /tmp/routepay-landing.pid
  echo "Landing stopped"
fi

# Stop mobile
if [ -f /tmp/routepay-mobile.pid ]; then
  kill $(cat /tmp/routepay-mobile.pid) 2>/dev/null
  rm /tmp/routepay-mobile.pid
  echo "Mobile stopped"
fi

# Fallback
pkill -f "routepay-api" 2>/dev/null
pkill -f "next-server" 2>/dev/null
pkill -f "expo" 2>/dev/null
pkill -f "metro" 2>/dev/null

echo "All services stopped"
