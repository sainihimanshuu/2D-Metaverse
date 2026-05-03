# 2D Metaverse Backend

## Overview

Backend for a room-based 2D metaverse application.

This repository uses a room-centric real-time model:

- Live room state is authoritative in memory.
- One active room has one active room runtime owner.
- Client messages are intents, not truth.

The long-term design separates gateway, session, directory, and room runtime concerns. The MVP does not deploy them as separate services yet. For v1, the backend should remain a single application with clear internal module boundaries.

This documentation is written for:

- developer onboarding
- implementation guidance
- GitHub Copilot context

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring WebSocket
- Spring Data JPA
- MySQL
- Maven

## Architecture Summary

### Target model

The target system has three planes:

- Connection plane
  - accepts WebSocket connections
  - authenticates users
  - manages transport sessions
  - routes client intents
- Room authority plane
  - owns authoritative in-memory room state
  - validates joins, movement, interactions, and reconnects
  - broadcasts room events
- Supporting plane
  - stores room metadata and user data
  - handles configuration and persistence
  - records audit and analytics asynchronously

### MVP implementation stance

For a solo-developer MVP, keep the implementation simple:

- Single backend application
- Single relational database
- In-memory room runtime
- No microservices
- No internal RPC layer
- No distributed cache
- No message broker

Keep the code split by responsibility even though deployment stays single-process.

## First Read

For a new developer or Copilot session, read docs in this order:

1. `README.md`
2. `docs/mvp-scope.md`
3. `docs/architecture.md`
4. `docs/realtime.md`
5. `docs/data-model.md`
6. `docs/dev-guidelines.md`

## Documentation Map

- [Architecture](docs/architecture.md): target model, runtime boundaries, and core flows
- [Data Model](docs/data-model.md): entities, relationships, and storage boundaries
- [Realtime](docs/realtime.md): WebSocket, sessions, room events, and reconnect handling
- [MVP Scope](docs/mvp-scope.md): what v1 includes, what it excludes, and what stays for later
- [Development Guidelines](docs/dev-guidelines.md): structure, constraints, and implementation rules

## Guidance For Copilot And Contributors

- Favor the MVP shape over the full distributed target shape.
- Do not introduce new services unless the docs explicitly require them.
- Keep room authority centralized in room runtime logic.
- Do not move live room state into the database.
- Keep transport logic separate from room rules.
- Prefer simple, explicit implementations over generic frameworks or abstractions.
