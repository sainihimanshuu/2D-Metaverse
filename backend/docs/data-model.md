# Data Model

## Purpose

This document defines the main entities and the boundary between durable data and live runtime state.

Core rule:

- Durable data belongs in the database.
- Live room truth belongs in memory.

## Core Entities

### User

Represents an authenticated person.

Key data:

- `userId`
- authentication identity reference
- display name
- avatar reference
- account status
- created and updated timestamps

### User Session

Represents a logical live session, separate from the WebSocket connection.

Key data:

- `sessionId`
- `userId`
- current status
- reconnect token or session version
- current room reference if active
- created and last-seen timestamps
- disconnect grace expiry

### Client Connection

Represents the current transport connection.

Key data:

- connection ID
- session ID
- gateway instance ID
- socket status
- heartbeat timestamps

Note:

- This is usually runtime-only state, not durable domain data.

### Room

Represents a collaboration space.

Key data:

- `roomId`
- room name
- room type
- owner user ID
- access mode
- max occupancy
- map or layout reference
- default visibility mode
- room status
- created and updated timestamps

### Room Membership Policy

Represents room access and room-specific role assignment.

Key data:

- room ID
- user or principal reference
- role such as owner, admin, member, guest
- membership status
- optional invite or access attributes

### Room Runtime State

Represents the authoritative live state of an active room.

Key data:

- room ID
- active occupants
- occupancy count
- avatar positions
- movement states
- visibility and proximity state
- active interaction state
- active moderation effects
- reconnect grace timers
- room sequence or version

Constraint:

- This state is authoritative at runtime and should remain in memory.

### Room Assignment

Represents which runtime currently owns a room.

Key data:

- room ID
- room runtime ID
- assignment epoch or version
- assignment status
- lease or heartbeat metadata

### Map or Layout

Represents the spatial definition of a room.

Key data:

- `mapId`
- layout asset reference
- dimensions
- blocked zones
- walkable zones
- portals and transitions
- hotspot or object definitions
- version

### Room Object or Hotspot

Represents an interactable element in a room.

Key data:

- object ID
- room ID or map ID
- object type
- coordinates or region
- interaction rules
- visibility rules
- active or inactive status

### Presence

Represents a user's live avatar presence inside a room.

Key data:

- room ID
- user ID
- session ID
- avatar state
- position
- facing or direction
- zone
- presence status such as active, disconnected, idle
- last accepted client sequence
- last updated timestamp

### Interaction

Represents a validated user-to-user or user-to-object interaction.

Key data:

- interaction ID
- room ID
- initiator session or user ID
- target user or object ID
- interaction type
- lifecycle state
- timestamps
- outcome or rejection reason

Note:

- For the MVP, this is mostly transient runtime state.

### Moderation Action

Represents an administrative action affecting a room or user.

Key data:

- action ID
- room ID
- target user ID
- actor user ID
- action type
- reason
- duration if temporary
- created timestamp
- status

### Invite or Access Token

Represents controlled room access.

Key data:

- token ID
- room ID
- token value or hash
- issuer user ID
- scope
- expiry
- usage limits or status

### Audit Event

Represents an append-only historical record.

Key data:

- event ID
- event type
- timestamp
- actor ID
- target ID
- room ID if applicable
- session ID if applicable
- payload or metadata reference

## Relationships

### User relationships

- One user can have many historical sessions.
- One user can have at most one active logical room presence under the current rules.
- One user can own many rooms.
- One user can have many room membership entries.
- One user can be the actor or target of many moderation actions.

### Room relationships

- One room belongs to one owner user.
- One room references one active map or layout.
- One room can have many membership entries.
- One room can have many objects or hotspots.
- One room can have many audit events.
- One active room can have one current room assignment.
- One active room can have many in-memory presences.

### Session and presence relationships

- One session belongs to one user.
- One active session can link to one active presence.
- One presence belongs to one room.
- One presence belongs to one user.
- One presence is associated with one active session.

### Map and object relationships

- One map or layout can be reused by many rooms.
- One map or layout can define many room objects or hotspots.
- One room object or hotspot belongs to one room layout context.

### Moderation and audit relationships

- One moderation action is initiated by one actor user.
- One moderation action targets one user, one room, or both depending on type.
- One moderation action should generate one or more audit events.

## Storage Boundaries

### Stored In The Database

Use the database for durable, slower-changing, or historically important data.

### Identity and user data

- users
- auth identity mappings
- avatar and profile metadata
- account status

### Room configuration

- room definitions
- room metadata
- room access mode
- occupancy limits
- room rules and policies
- owner, admin, and member assignments
- map and layout references
- portal and hotspot definitions
- invite and access token records

### Moderation and governance

- moderation actions
- durable bans or restrictions
- room lock state if it must survive restart

### Operational history

- audit events
- room lifecycle logs
- session history if retained
- analytics facts if persisted

### Optional future persistence

- saved room or world state
- persistent object state
- replay metadata

### Kept In Memory

Use memory for latency-sensitive, transient, room-authoritative data.

### Live room state

- active occupants
- avatar positions
- movement state
- facing or direction
- current zone
- visibility and proximity sets
- active interactions
- per-room sequence counters
- reconnect grace timers
- temporary active moderation effects

### Live session and connection state

- active WebSocket connections
- connection-to-session bindings
- heartbeat status
- reconnect-in-progress state
- session-to-room routing hints

### Live routing state

- room ID to room runtime owner mapping
- runtime liveness metadata
- room-runtime-local room index

### Hybrid State

Some data exists in memory for speed and in durable storage for recovery or audit.

### Session records

- DB: historical and security-related metadata if needed
- Memory: current active session and reconnect state

### Room assignment

- Memory or cache: fast routing lookup
- Durable coordination store: control-plane source of truth if multi-node recovery requires it

### Moderation restrictions

- DB: durable restriction and audit trail
- Memory: immediate enforcement for active rooms

### Room access rules

- DB: source of truth
- Memory: loaded and cached copy for fast room admission

## MVP Data Model Stance

For the solo-developer MVP, keep the schema minimal.

Required first tables:

- `users`
- `rooms`
- `room_memberships`

Optional early tables:

- `moderation_actions`
- `audit_events`

Do not create tables for:

- live presence
- connection state
- room assignment
- visibility state
- interaction state
- reconnect timers

Those belong in memory for v1.
