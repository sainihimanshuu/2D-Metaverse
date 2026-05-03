# Architecture

## Purpose

This document defines the system shape and the boundaries that matter most for implementation.

Core assumptions:

- Rooms are the unit of authority.
- Live room behavior must be deterministic.
- One room must have exactly one active authority at a time.

## Architecture Layers

### Connection plane

Responsible for:

- Accepting browser WebSocket connections
- Authenticating the client
- Creating or resuming transport sessions
- Performing lightweight validation
- Forwarding client intents to the correct room runtime owner
- Delivering server events back to the client

This plane owns socket state, not room truth.

### Room authority plane

Responsible for:

- Owning authoritative in-memory state for each active room
- Managing room membership
- Validating movement
- Computing visibility and proximity
- Validating interactions
- Handling disconnect and reconnect grace
- Broadcasting authoritative room events

Each room is assigned to exactly one room runtime owner at a time.

### Supporting plane

Responsible for:

- Room metadata and room configuration
- User identity and access checks
- Durable persistence
- Audit and analytics sinks

These concerns should stay off the latency-critical room path whenever possible.

## Key Components

### WebSocket Gateway

Responsible for:

- Accepting WebSocket connections
- Authenticating users or sessions
- Assigning transport-level session binding
- Handling keepalive and heartbeat
- Rate limiting and basic validation
- Forwarding intents to room authority
- Delivering room-runtime-generated events back to clients

Constraint:

- The gateway should remain as stateless as possible except for active socket and transport state.

### Session Manager

Responsible for:

- Mapping authenticated user to active logical session
- Tracking reconnect token or resumable session version
- Preventing invalid duplicate active presence
- Supporting reconnect within a grace window

Constraint:

- The session manager owns continuity, not room state.

### Room Directory

Responsible for:

- Resolving `roomId -> room runtime owner`
- Assigning newly activated rooms
- Updating ownership on recovery or scaling
- Enforcing the one-room-one-authority rule

Constraint:

- Room ownership must be exclusive and versioned.

### Room Runtime

Responsible for:

- Loading room configuration
- Maintaining room runtime state in memory
- Admitting or rejecting joins
- Handling leave, disconnect, reconnect, and transitions
- Validating movement against map and rules
- Computing visibility and proximity
- Validating interactions
- Applying moderation effects
- Broadcasting room updates
- Producing room snapshots

Constraint:

- The room runtime is the only component allowed to mutate a room's live state.

### Room Metadata and Persistence

Responsible for:

- Room definitions
- Access mode and permissions
- Map and layout references
- Owner and admin membership
- Durable room settings
- Optional future persistent world state

Constraint:

- Persistence is not the authority for live movement or presence.

### Audit and Event Sink

Responsible for:

- Moderation logs
- Room lifecycle logs
- Operational event history
- Optional analytics pipelines

Constraint:

- Writes should be asynchronous and off the critical room path.

### Internal Messaging or RPC

Responsible for:

- Gateway to room request forwarding
- Room to gateway event delivery
- Directory lookups
- Control-plane communication

Constraint:

- This is transport between components, not authority for room state.

## Responsibility Map

### Connection and session

- Gateway terminates sockets
- Auth resolves identity
- Session manager owns logical session continuity

### Room authority

- Room runtime owns live room truth
- Presence and visibility logic stays inside the room runtime boundary
- Interaction and rules logic stays inside the room runtime boundary

### Configuration and persistence

- Room configuration provides slower-changing room data
- User profile provides stable user-facing metadata
- Persistence stores durable records only

### Governance and history

- Moderation owns durable moderation intent and records
- Audit and analytics receive asynchronous event output

## Core Flows

### Connect

1. Client opens a WebSocket to a gateway.
2. Gateway authenticates the user.
3. Session manager creates or resumes the logical session.
4. Gateway returns ready state and reconnect settings.

### Join room

1. Client sends a join request.
2. Gateway validates the session.
3. Room directory resolves the room runtime owner.
4. Room runtime validates access, occupancy, and duplicate presence rules.
5. Room runtime creates presence and returns an authoritative snapshot.
6. Gateway delivers the snapshot.
7. Room runtime broadcasts join or presence events to other occupants.

### Movement

1. Client sends movement intent.
2. Gateway validates message shape, session, and rate limit.
3. Gateway forwards intent to room authority.
4. Room runtime validates state, sequence, speed, and destination.
5. Room runtime updates authoritative avatar state.
6. Room runtime emits outbound deltas to relevant users.

### Interaction

1. Client sends interaction intent.
2. Gateway forwards it to the room runtime.
3. Room runtime validates membership, proximity, permissions, and rules.
4. Room runtime applies accepted interaction state.
5. Room runtime emits direct and room-facing results as needed.

### Disconnect and reconnect

1. Gateway detects socket loss.
2. Session manager preserves logical continuity.
3. Room runtime marks the presence as disconnected and starts grace handling.
4. If the user reconnects in time, session binding is restored and the room sends a fresh snapshot.
5. If grace expires, the room removes the presence and emits leave events.

### Room transition

1. Client triggers a room change.
2. Source room validates the transition.
3. Target room runtime owner is resolved.
4. Source presence is removed in a controlled handoff.
5. Target room admits the user and returns a new snapshot.

Invariant:

- A user must never be logically active in both rooms after transition completes.

## State Ownership

### Live runtime state

Owned by the room runtime:

- Active members
- Positions
- Movement state
- Visibility and proximity state
- Active interactions
- Reconnect grace timers
- Room-local moderation effects

### Connection state

Owned by gateway and session logic:

- Active socket connection
- Heartbeat status
- Transport session mapping
- Reconnect binding

### Directory state

Owned by room directory:

- Room owner mapping
- Assignment metadata
- Runtime liveness metadata

### Configuration state

Owned by configuration, profile, and auth services:

- Room definitions
- Access policies
- User profile data
- Role mappings

### Audit and historical state

Owned by audit and persistence:

- Moderation logs
- Room lifecycle records
- Operational event history

## MVP Interpretation

The sections above describe the target model.

For the MVP:

- Keep the same boundaries conceptually.
- Implement them inside one backend application.
- Treat session manager and room directory as internal modules, not separate deployables.
- Keep presence, visibility, interaction rules, and basic moderation inside the room runtime module.
- Keep audit simple and asynchronous.
