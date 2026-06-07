# Operating Systems Projects

This repository contains a set of Operating Systems projects implemented in **C and Java**, focusing on process management, concurrency, synchronization, deadlock handling, and memory performance analysis.

The projects were developed as part of a university Operating Systems course and demonstrate both theoretical and practical system-level concepts.

---

## 🔧 Topics Covered

* Multi-process programming (`fork`, `exec`)
* Inter-process communication (Shared Memory)
* Multithreading (Thread Pools, Executors)
* Synchronization & Race Conditions
* Deadlock Detection & Recovery
* Lock Management (Ticket Lock, ReentrantLock)
* Performance benchmarking
* Memory layout optimization (AoS vs SoA)

---

## 📁 Projects Overview

### 1. Multi-Process File Processing (C)

Parallel processing of a numeric file using multiple processes and shared memory.

### 2. Parallel Download Manager (Java)

A multithreaded file download system with chunk verification and benchmarking.

### 3. Advanced Concurrent Download Client

Enhanced version with synchronization, retry policies, and ticket-based locking.

### 4. Deadlock Detection & Recovery System

A custom LockManager that detects circular wait conditions and resolves deadlocks.

### 5. AoS vs SoA Performance Benchmark

Simulation comparing memory layouts and their effect on cache performance.

---

## ⚙️ How to Run

Each project has its own folder with compilation and execution instructions.

---

## 📌 Notes

* Designed for educational purposes
* Focus on OS concepts and performance behavior
* Written without external frameworks (low-level implementation)
