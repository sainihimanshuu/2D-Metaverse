# Next Steps

## Purpose

This document records the recommended near-term implementation order for the MVP.

It is intentionally short and practical.

## Immediate Priorities

Build in this order:

1. Auth flow and basic user persistence
2. WebSocket connection entrypoint
3. In-memory session manager
4. In-memory room manager
5. Room runtime with join and leave
6. Presence tracking and room snapshots
7. Movement handling
8. Reconnect grace handling
9. Basic room access checks
10. Basic interaction support if needed

## First Meaningful Milestone

A good first milestone is:

- user connects
- user joins a room
- user receives a room snapshot
- user appears to others in the room
- user can move
- other users receive movement updates

## After That

Next improvements after the first milestone:

- reconnect within grace window
- better room snapshot structure
- simple role-based room access
- basic moderation such as kick if product-critical

## Still Out Of Scope

Do not pull these forward unless requirements change:

- microservices
- Redis
- distributed room routing
- replay-based recovery
- room migration
- hot failover
- analytics pipeline
- advanced visibility engine
- generalized interaction rule system

## Update Rule

When a major milestone is completed:

- update `docs/current-state.md`
- update this file
- keep `AGENTS.md` aligned if implementation stance changes
