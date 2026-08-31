# RoutePay Documentation

Complete documentation for the RoutePay project — a MoMo-powered Mini App for
cashless taxi fare payments in South Africa, built for the **MoMo Mini App
Hackathon 2026**.

## 📚 Documentation Index

### For Judges
- [PITCH.md](PITCH.md) — 1-page pitch narrative
- [DEMO_SCRIPT.md](DEMO_SCRIPT.md) — 3-minute demo walkthrough
- [JUDGE_QA.md](JUDGE_QA.md) — Anticipated questions and answers
- [PRE_DEMO_CHECKLIST.md](PRE_DEMO_CHECKLIST.md) — Pre-demo preparation
- [FINAL_AUDIT.md](FINAL_AUDIT.md) — Final status report

### For Developers
- [API.md](API.md) — MoMo API integration guide
- [DECISIONS.md](DECISIONS.md) — Architecture decisions
- [QA_AUDIT.md](QA_AUDIT.md) — Security audit results

### For Operators
- [../README.md](../README.md) — Main project README
- [../scripts/demo.md](../scripts/demo.md) — Demo commands

## 🚀 Quick Links

- **GitHub:** <https://github.com/DemainMan/RoutePay>
- **Hackathon:** MoMo Mini App Hackathon 2026 (Track 3: Travel & Mobility)
- **Demo:** `bash scripts/demo.sh`
- **Dashboard:** http://localhost:3000
- **Landing:** http://localhost:3001
- **Swagger UI:** http://localhost:8080/swagger-ui.html

## 🧱 Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2.5, Maven |
| Database | H2 (dev), PostgreSQL 16 (prod) |
| MoMo SDK | Custom Java client (5 API groups) |
| Mini App | React Native (Expo SDK 50), TypeScript |
| Dashboard | Next.js 14, TypeScript |
| Landing | Next.js 14, TypeScript |
