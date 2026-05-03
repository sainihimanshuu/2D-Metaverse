# MVP Scope

## Purpose

This document defines the implementation boundary for v1.

Goal:

- protect development speed
- avoid premature distributed-systems work
- keep implementation choices aligned with a solo developer workflow

## V1 Includes

Build these in v1:

- Authenticated client connect
- WebSocket-based real-time transport
- Simple session create or resume
- Join one room
- Leave one room
- See other users in the room
- Move avatar within a room
- Basic room access checks
- Reconnect within a fixed grace window
- Fresh room snapshot on join or reconnect

Maybe include in v1 if core to product:

- simple portal or room transition
- room owner or admin role
- simple kick or remove action

## V1 Architecture Shape

V1 should be implemented as:

- one backend application
- one relational database
- one in-memory room manager
- one in-memory session manager

Keep these as internal modules, not separate services:

- gateway
- session manager
- room manager
- room runtime
- auth
- configuration access

Interpretation:

- keep conceptual boundaries
- keep deployment simple

## V1 Data Scope

Required early persistence:

- `users`
- `rooms`
- `room_memberships`

Optional early persistence:

- `moderation_actions`
- `audit_events`

Keep in memory:

- live room presence
- avatar positions
- active room occupancy
- room sequence counters
- reconnect timers
- visibility state
- active interactions

## Hardcoded Or Simplified For V1

Hardcode:

- single deployment region
- single backend instance
- fixed reconnect grace period
- fixed heartbeat interval
- fixed room capacity policy
- newest connection wins duplicate-session policy
- one room type if possible
- one map-loading approach
- simple role model: owner, member, guest

Simplify:

- fresh snapshot on reconnect
- full-room broadcast if room sizes are small
- basic movement validation only
- direct DB reads plus small in-process caching
- moderation only if essential
- structured logs instead of analytics pipeline

Operational assumptions acceptable in v1:

- backend restart drops live room state
- users may need to rejoin after restart
- no perfect continuity across crashes

## Explicitly Not Included In V1

Do not build these in v1:

- microservices
- separate gateway service
- separate room runtime service
- separate session service
- separate room directory service
- separate profile service
- separate moderation service
- separate audit or analytics service
- internal RPC layer
- message broker
- Redis
- distributed room ownership
- room migration between workers
- worker liveness and lease management
- delta replay
- warm failover
- advanced control plane
- multi-region support
- event sourcing
- replay-based recovery

Also defer unless product-critical:

- advanced visibility or proximity engine
- generalized interaction rule engine
- persistent object state
- room editing tools
- analytics pipeline
- complex invite token systems
- rich moderation workflows

## Future Improvements

Short upgrade path after v1:

1. Optimize single-process room performance
2. Reduce unnecessary broadcasts
3. Add simple spatial filtering if needed
4. Move to multiple backend instances only when one instance becomes the bottleneck
5. Split gateway from room runtime only when the deployment model requires it
6. Introduce room directory only when rooms are actually distributed across nodes

Guiding rule:

- Preserve the room-authority model now.
- Delay distributed implementation until usage justifies it.
