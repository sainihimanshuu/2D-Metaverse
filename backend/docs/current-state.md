# Current State

## Purpose

This document records the current implementation reality of the repository.

Use it to distinguish between:

- what is documented as the target architecture
- what is actually implemented in code today

## Project State Summary

Current state:

- early backend implementation
- Spring Boot application with JPA-backed room data
- in-memory room runtime primitives present
- no connection-facing realtime gateway yet
- no session or reconnect system yet

This repository is no longer just a scaffold, but it is still far from a working end-to-end realtime backend.

## Current Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring WebSocket
- Spring Data JPA
- MySQL
- Maven
- Lombok

## What Exists Today

### Application setup

Present:

- Spring Boot application entrypoint
- Maven build
- MySQL datasource configuration in `application.properties`
- Hibernate auto-update enabled for schema changes

### Persistent domain model

Present entity classes:

- `User`
- `Room`
- `RoomMembership`

Present enum types:

- `RoomRole`
- `RoomStatus`
- `RoomType`

The persistent model currently covers:

- users
- rooms
- room ownership
- room membership records

### Repositories

Present repositories:

- `UserRepository`
- `RoomRepository`
- `RoomMembershipRepository`

These already support basic lookup patterns such as:

- user lookup by username
- room lookup by owner
- room membership lookup by user and room
- room occupancy counting through memberships

### Service layer

Present service:

- `RoomService`

`RoomService` currently implements:

- create room
- add member to room
- remove member from room
- membership existence check
- fetch room by id

This is database-oriented room and membership logic. It is not yet the full live room authority described in the architecture docs.

### In-memory realtime primitives

Present realtime classes:

- `RoomRuntime`
- `RoomRuntimeManager`
- `RoomSnapshot`
- `UserSnapshot`

Current runtime behavior includes:

- one in-memory runtime instance per active room id
- in-memory active user tracking inside a room runtime
- add user to runtime
- remove user from runtime
- update user position in memory
- produce a room snapshot from in-memory runtime state

This is the beginning of the room runtime boundary, but it is still a local in-process utility layer rather than a complete realtime flow.

### Tests

Present tests:

- application context load test only

## What Does Not Exist Yet

Not implemented yet:

- WebSocket gateway handlers
- authenticated connection flow
- logical session management
- reconnect grace handling
- join flow that bridges persistence and runtime
- leave flow tied to socket or session lifecycle
- event broadcasting to room occupants
- server-side movement validation rules
- visibility rules
- interaction handling
- moderation workflow
- audit event flow
- REST controllers or WebSocket controllers for public API access

## Important Gaps And Limitations

The current code has meaningful implementation, but it is still incomplete in several important ways:

- realtime code exists without transport integration
- room runtime state is in memory only and is not wired to actual client connections
- membership persistence and runtime presence are separate and not yet coordinated by a join or reconnect flow
- there is no session boundary yet, so socket lifecycle and user continuity are not implemented
- the runtime manager is process-local only, which matches MVP intent but not multi-instance deployment

## Implementation Interpretation

Right now, the codebase should be treated as:

- a single-process Spring backend
- a working initial persistence layer for users, rooms, and memberships
- a first pass at in-memory room runtime state
- a documented architecture with partial implementation

It should not be treated as:

- a complete multiplayer backend
- a working realtime gateway
- an implemented reconnect-safe session system
- a distributed room ownership system

## Current Constraints

- keep implementation single-process for v1
- keep room runtime authoritative for live state as realtime features are added
- keep persistence focused on durable room and membership records
- do not move live presence state into the database
- do not introduce microservices, brokers, or distributed caches
- avoid speculative abstractions ahead of actual join, move, leave, and reconnect flows

## Near-Term Build Target

The immediate product target still appears to be:

1. authenticated connect
2. logical session create or resume
3. join room through a room runtime
4. full room snapshot on join
5. see other occupants
6. move within room with runtime-owned state updates
7. leave room cleanly
8. reconnect within grace with snapshot recovery

## Notes For Future Sessions

- Treat `docs/architecture.md` as the boundary guide, not as proof that a feature already exists.
- Treat the current realtime code as partial room runtime groundwork, not as a complete join or presence system.
- Update this file whenever controllers, gateway code, session handling, or reconnect logic become real code instead of planned scope.
