# Contributing to RoutePay

Thank you for your interest in contributing to RoutePay! This document provides guidelines and instructions for contributing.

## Getting Started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/your-username/RoutePay.git`
3. Create a branch: `git checkout -b feature/your-feature`
4. Install dependencies: `uv sync`
5. Make your changes
6. Run tests: `uv run pytest`
7. Run linter: `uv run ruff check .`
8. Run type checker: `uv run mypy .`
9. Commit your changes
10. Push and create a Pull Request

## Code Style

- Use Python 3.11+ features (type hints, `StrEnum`, `match`, etc.)
- All code must pass `ruff check` and `mypy --strict`
- Use `structlog` for logging (never `print()`)
- Use `Decimal` for monetary values (never `float`)
- Use `async`/`await` for all I/O operations
- Write docstrings for all public functions

## Testing

- Write tests for all new features
- Maintain ≥80% code coverage
- Use `pytest-asyncio` for async tests
- Run the full test suite before submitting: `uv run pytest`

## Commit Messages

Use [Conventional Commits](https://www.conventionalcommits.org/):
- `feat:` new feature
- `fix:` bug fix
- `docs:` documentation
- `test:` adding tests
- `refactor:` code refactoring
- `chore:` maintenance

## Questions?

Open an issue on GitHub or reach out to the maintainers.
