# 🕹️ 2D Metaverse MVP — Week 1

A minimal 2D metaverse inspired by [gather.town](https://www.gather.town), built using **Spring Boot**, **TypeScript**, and **WebRTC**.  
The goal of this MVP is to get a fully working prototype within 7 days, focusing on real-time rooms, movement, and proximity-based communication.

---

<!-- copilot: project-type=fullstack -->
<!-- copilot: backend=java,springboot -->
<!-- copilot: frontend=typescript,html-canvas -->
<!-- copilot: realtime=websocket,webrtc -->
<!-- copilot: domain=metaverse,avatar,room,auth,proximity-voice -->

## 🚀 MVP Features (Week 1)

✅ **User Authentication**

- Signup, Login, and Logout using JWT (access + refresh tokens)
- Simple avatar selection during signup

✅ **Rooms**

- Create, Join, and Leave rooms
- Each room corresponds to a shared 2D map instance

✅ **Movement**

- Users can move around a 2D canvas using arrow keys
- Real-time position synchronization via WebSocket

✅ **Proximity-based Audio/Video (WebRTC)**

- Users can see and hear nearby avatars
- WebRTC peer connections are established based on proximity

✅ **Frontend**

- Minimal UI using HTML Canvas + TypeScript
- Handles input, rendering, and communication

✅ **Backend**

- Spring Boot REST API + WebSocket server for signaling
- JWT-based authentication
- MySQL

---

## 🧱 Architecture Overview

```text
                   ┌────────────────────────┐
                   │        Frontend        │
                   │  (TypeScript + Canvas) │
                   ├──────────┬─────────────┤
                   │ HTTP API │  WebSocket  │
                   └────┬─────┴────┬────────┘
                        │          │
             ┌──────────┘          └──────────┐
             │                                │
     ┌────────────────────┐     ┌────────────────────┐
     │   Spring Boot API  │     │  WebSocket Server  │
     │(Auth + Room Mgmt)  │     │ (Realtime movement)│
     └─────────┬─────────┘      └──────────┬─────────┘
              │                             │
              │                             │
         ┌───────────┐                 ┌───────────┐
         │DB: MySQL  │                 │  WebRTC   │
         │ (Users,   │                 │  Signaling│
         │  Rooms)   │                 └───────────┘
         └───────────┘
```
