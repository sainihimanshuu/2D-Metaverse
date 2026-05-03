# Development Guidelines

## Purpose

This document defines how the backend should be structured and what constraints implementation must respect.

It is intentionally opinionated. The goal is to keep the project simple, coherent, and aligned with the MVP-first architecture.

Use this file as the implementation guardrail for both human contributors and GitHub Copilot.

## Folder Structure

Current codebase areas:

- `src/main/java/com/_DMetaverse/backend/controller`
- `src/main/java/com/_DMetaverse/backend/service`
- `src/main/java/com/_DMetaverse/backend/repository`
- `src/main/java/com/_DMetaverse/backend/models`
- `src/main/java/com/_DMetaverse/backend/dto`

Recommended direction as the project grows:

- `controller`
  - HTTP endpoints and WebSocket entrypoints only
- `service`
  - application services and orchestration
- `realtime`
  - session management
  - room manager
  - room runtime and room state
- `domain`
  - room rules, movement validation, interaction logic
- `repository`
  - persistence access only
- `dto`
  - request and response contracts
- `config`
  - Spring and infrastructure configuration

Important:

- Transport-layer code and room-authority code should not be mixed together.

## Coding Conventions

- Keep classes small and explicit.
- Prefer simple domain names over generic framework-heavy names.
- Prefer clear request and response DTOs over overloaded model objects.
- Keep persistence entities separate from runtime room state when possible.
- Use explicit method names for room actions such as join, leave, move, reconnect, and interact.
- Keep branching and side effects localized around room actions.

Documentation and naming should reinforce these ideas:

- client messages are intents
- room runtime is authoritative
- DB is durable storage, not live room truth

## Constraints

These constraints are critical.

### Architecture constraints

- Do not introduce microservices for v1.
- Do not add Redis unless a concrete need appears.
- Do not add a message broker for internal communication in v1.
- Do not move live room state into the database.
- Do not use the database as the source of truth for movement or presence.
- Do not let controller or transport code implement room rules directly.
- Do not let socket objects leak into room-domain logic.

### Realtime constraints

- One active room has one active authority.
- Room mutations must happen through room runtime logic only.
- Movement and interaction messages are intents, not accepted facts.
- Reconnect should restore from fresh snapshot in v1.
- Newest validated connection should replace older socket binding.

### Scope constraints

- Favor direct implementation over abstraction for hypothetical future needs.
- Avoid generic plugin systems, event buses, policy engines, and rule engines in v1.
- Avoid premature optimization for hot-room scaling.
- Keep moderation minimal unless it is core to the first user flow.

### Documentation constraints

- Keep terminology consistent across docs and code.
- Prefer `room runtime` over mixed terms such as worker or simulator.
- Make MVP assumptions explicit when writing new docs.
- Avoid speculative sections unless they support a real planned next step.

## Development Philosophy

### MVP first

Build the smallest system that correctly supports:

- connect
- join room
- see others
- move
- leave
- reconnect within grace

Everything else is secondary.

### Preserve boundaries without distributed complexity

Even in one application, preserve these boundaries:

- transport layer handles connections and delivery
- session layer handles continuity
- room runtime handles live room truth
- persistence layer handles durable storage

This allows later scaling without rewriting the core product model.

### Prefer explicit trade-offs

For v1, prefer:

- fresh snapshot over delta replay
- single instance over distributed coordination
- in-memory room state over shared cache
- full-room broadcast over premature visibility optimization when rooms are small

### Optimize for solo development speed

Prefer solutions that are:

- easy to debug
- easy to reason about
- easy to change
- hard to misuse

Avoid solutions that introduce:

- hidden control flow
- cross-service coupling
- difficult local testing
- operational burden disproportionate to current scale

## Guidance For Copilot

When generating code, prefer:

- single-process implementations
- in-memory room management
- simple session management
- direct service calls inside the application
- minimal tables and DTOs
- explicit room action flows

When generating architecture, avoid suggesting:

- microservices
- event-driven redesign
- distributed caches for live room state
- pub/sub as room authority
- generalized infrastructure before real need
