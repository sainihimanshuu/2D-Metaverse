# AGENTS.md

## Purpose

This file is the fast-start context for any coding agent working in this repository.

Read this before making changes.

## Project Summary

This repository contains the backend for a room-based 2D metaverse application.

The project is intentionally MVP-first:

- solo developer workflow
- simplicity over scalability
- correctness of room behavior over architectural breadth
- clean boundaries in code without early distributed deployment

## Core Invariants

- Live room state is authoritative in memory.
- One active room has one active room runtime owner.
- Client messages are intents, not truth.
- WebSocket is transport.
- Session is continuity.
- Room runtime is truth.
- Persistence stores durable data, not live room authority.

## Current Implementation Stance

Assume v1 should be built as:

- one backend application
- one relational database
- one in-memory session manager
- one in-memory room manager
- one in-memory room runtime per active room

Keep conceptual boundaries, but do not split deployment early.

## Architecture Rules

- Keep transport logic separate from room rules.
- Keep session continuity separate from socket lifecycle.
- Keep room runtime as the only place that mutates live room state.
- Keep presence, movement, visibility, reconnect handling, and interactions inside the room runtime boundary.
- Keep persistence focused on users, rooms, memberships, and other durable records.

## MVP Scope Rules

Prioritize:

- authenticated connect
- join room
- see other users
- move within room
- leave room
- reconnect within grace
- full room snapshot on join and reconnect

Do not introduce unless explicitly requested:

- microservices
- internal RPC
- message broker
- Redis for live state
- distributed room ownership
- room migration
- replay-based recovery
- warm failover
- generalized rule engines

## Data Rules

Persist durable data such as:

- users
- rooms
- room memberships
- optional moderation actions
- optional audit events

Keep in memory:

- active room presence
- positions
- room occupancy runtime state
- room sequence counters
- reconnect timers
- visibility state
- active interactions

## Terminology

Use these terms consistently:

- `gateway`: the connection-facing layer
- `session`: logical continuity across reconnects
- `room runtime`: the authority for a room's live state
- `room manager`: in-process owner of active room runtime instances
- `presence`: a user's live state inside a room

Prefer `room runtime` over `worker`, `simulator`, or other variants.

## Read These Files Next

For full context, read:

1. `README.md`
2. `docs/current-state.md`
3. `docs/mvp-scope.md`
4. `docs/architecture.md`
5. `docs/realtime.md`
6. `docs/data-model.md`
7. `docs/dev-guidelines.md`
8. `docs/next-steps.md`

## How To Work In This Repo

- Favor the MVP implementation shape over the full target architecture.
- Keep generated code explicit and easy to debug.
- Avoid speculative abstractions.
- If a change looks like distributed-systems preparation rather than direct product work, it is probably premature.
