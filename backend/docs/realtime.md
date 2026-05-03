# Realtime Design

## Purpose

This document defines how real-time communication works, who owns what state, and how room events should flow.

Core rule:

- WebSocket is transport.
- Session is continuity.
- Room runtime is truth.

## WebSocket Architecture

### Gateway responsibilities

The gateway is responsible for:

- Terminating WebSocket connections
- Authenticating the client
- Binding `connectionId -> sessionId -> userId`
- Tracking heartbeat and connection health
- Performing lightweight validation
- Forwarding intents to room authority
- Delivering authoritative server events back to the client

The gateway must not:

- Decide room membership authoritatively
- Validate movement as the source of truth
- Compute visibility as the source of truth
- Derive room events on behalf of room authority

### Session responsibilities

The session layer is responsible for:

- Creating or resuming logical sessions
- Binding reconnect tokens or session versions
- Enforcing duplicate-session policy
- Preserving continuity across brief network loss

Recommended policy:

- One active logical session wins.
- A newer validated connection replaces the older socket binding.

### Room runtime responsibilities

The room runtime is responsible for:

- Admitting users into rooms
- Maintaining presence state
- Validating movement and interactions
- Applying room rules
- Handling disconnect and reconnect grace
- Broadcasting authoritative room events

## Connection Handling

On connection establishment:

1. Client opens a WebSocket.
2. Gateway authenticates via the auth layer.
3. Session is created or resumed.
4. Reconnect token or version is issued or validated.
5. Gateway returns:
   - heartbeat interval
   - server time
   - session state
   - reconnect window

The gateway should keep only transport-level runtime state:

- socket status
- heartbeat timestamps
- outbound queue state
- current session binding

Inbound client messages should carry:

- `sessionId`
- client message sequence
- room context where required
- reconnect or session version when resuming

Gateway-side validation should remain lightweight:

- authenticated session exists
- payload shape is valid
- rate limit is not exceeded
- message is not obviously stale

## Event Flows

### Join Room

1. Client sends join request over the existing WebSocket.
2. Gateway validates the session.
3. Gateway resolves `roomId -> room runtime owner`.
4. Gateway forwards the join intent.
5. Room runtime validates:
   - room exists
   - access is allowed
   - moderation does not block entry
   - occupancy is within limit
   - duplicate presence rules are satisfied
6. Room runtime creates or restores presence.
7. Room runtime returns a full room snapshot.
8. Gateway delivers the snapshot.
9. Room runtime broadcasts join or presence events to relevant occupants.

Important rule:

- Room membership is finalized only by room authority.

### Leave Room

1. Client sends leave request, or a room transition implies leave.
2. Gateway routes the request to the room runtime owner.
3. Room runtime removes or deactivates the presence.
4. Room runtime updates occupancy and room sequence.
5. Room runtime emits departure events to affected occupants.

After leave:

- The session remains connected.
- The session no longer has active room presence.

### Movement

1. Client sends movement intent.
2. Gateway performs lightweight validation.
3. Gateway forwards the intent to room authority.
4. Room runtime validates:
   - session is active in the room
   - sequence is acceptable
   - movement speed is valid
   - destination is allowed
   - portal or zone rules are satisfied
5. Room runtime updates authoritative avatar state.
6. Room runtime computes visibility and proximity effects.
7. Room runtime emits ordered outbound deltas.
8. Gateway delivers updates to relevant connections.

Important rule:

- Client movement messages are intents, not accepted movement facts.

### Interaction

1. Client sends interaction intent.
2. Gateway forwards the intent to room authority.
3. Room runtime validates:
   - room membership
   - proximity or interaction rule
   - permissions
   - moderation restrictions
   - object or hotspot rule if relevant
4. Room runtime applies the accepted result.
5. Room runtime emits:
   - direct response to initiator
   - room-facing events for affected users where needed

### Room Transition

1. Client triggers a room change explicitly or via a portal.
2. Source room validates the transition.
3. Target room runtime owner is resolved.
4. Source presence is removed in a controlled handoff.
5. Target room admits the user and generates a new snapshot.
6. Client receives the target room state.

Invariant:

- A user must not remain logically active in both source and target rooms.

## Broadcast Model

Broadcasting must always originate from room authority.

### Rules

- The room runtime is the single source of truth for room events.
- Gateways deliver events but do not invent them.
- Each room maintains a monotonic sequence or version.
- Accepted room mutations emit ordered outbound events.

### Delivery scopes

- room-wide events for membership or room-level changes
- proximity or visibility-scoped events for movement and local interactions
- direct responses for request results or rejections
- privileged or admin-only events where required

### Operational policies

- Use interest management to reduce unnecessary fan-out.
- Batch or tick movement updates rather than broadcasting every raw input.
- Maintain per-connection outbound queues at the gateway.
- Allow drop or supersede policy only for transient updates such as stale movement deltas.

Never drop semantic events such as:

- join accepted
- kicked
- restriction applied
- room transition result
- interaction accepted
- interaction rejected

## Disconnect And Reconnect

### Transport loss

On socket disconnect or heartbeat timeout:

1. Gateway marks the connection closed.
2. Session enters reconnect grace handling.
3. Room runtime marks presence as disconnected instead of removing it immediately.
4. Room runtime starts a grace timer.
5. Nearby users may receive degraded presence state if needed.

### Reconnect

1. Client reconnects to any gateway.
2. Gateway authenticates and submits resume data.
3. Session layer validates reconnect token or version.
4. Gateway resolves current room ownership.
5. Room runtime validates grace eligibility and session ownership.
6. Room runtime restores active presence.
7. Room runtime sends either:
   - delta replay from the last acknowledged room sequence, if supported
   - fresh room snapshot, which is safer for MVP

### Grace expiry

If the client does not reconnect in time:

- runtime removes presence
- occupancy is decremented
- active interactions are cleaned up
- final leave event is broadcast
- audit events are emitted asynchronously if required

### Failure rules

- Reconnect must be idempotent.
- Only one connection binding becomes active.
- Revalidation is required if moderation or access changed during disconnect.
- Latest room ownership must win if reassignment occurred during disconnect.

## MVP Realtime Stance

For v1:

- Use WebSocket as the only real-time transport.
- Keep the backend as a single application.
- Keep room runtime in memory.
- Use fresh snapshot recovery on reconnect.
- Use simple full-room broadcast if room size is small enough.

Do not add early:

- replay-based recovery
- distributed socket routing
- broker-based event fan-out
- cross-node room handoff
