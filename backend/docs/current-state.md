# Current State

## Purpose

This document records the current implementation reality of the repository.

Use it to avoid confusing the target architecture with what is already built.

## Project State Summary

Current state:

- early scaffold
- Spring Boot backend
- basic JPA model and repository setup
- architecture documented
- MVP boundaries documented
- real-time runtime not implemented yet

This repository currently contains more design context than working feature depth.

## Current Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring WebSocket
- Spring Data JPA
- MySQL
- Maven

## What Exists Today

### Application setup

Present:

- Spring Boot application entrypoint
- Maven build
- application properties with MySQL config

### Domain models

Present model classes:

- `User`
- `Room`
- `Map`
- `Avatar`

### Repositories

Present repositories:

- `UserRepository`
- `RoomRepository`
- `MapRepository`
- `AvatarRepository`

### DTOs

Present DTO packages:

- auth DTOs
- room DTOs

### Controllers and services

Present but currently empty:

- `AuthController`
- `RoomController`
- `AuthService`
- `RoomService`

## What Is Not Implemented Yet

Not implemented yet:

- WebSocket connection flow
- logical session management
- room manager
- room runtime
- presence tracking
- movement validation
- interaction handling
- reconnect grace handling
- room snapshots
- moderation workflow
- audit event flow

## Implementation Interpretation

Right now, the codebase should be treated as:

- a Spring backend foundation
- a small initial data layer
- a documented architecture and MVP plan

It should not be treated as:

- a working real-time backend
- a distributed room system
- a near-complete gameplay server

## Current Constraints

- keep implementation single-process for v1
- keep data model simple
- do not add microservices
- do not add Redis for live room state
- do not build speculative infrastructure
- do not move live room state into the DB

## Short-Term Build Target

The immediate product goal is still:

1. authenticated connect
2. simple session create or resume
3. join room
4. see room occupants
5. move in room
6. leave room
7. reconnect within grace using fresh snapshot recovery

## Notes For Future Sessions

- Always compare new implementation work against `docs/mvp-scope.md`.
- Use `docs/architecture.md` for target boundaries, not as proof that a feature already exists.
- Keep `docs/current-state.md` updated when real implementation milestones are reached.
