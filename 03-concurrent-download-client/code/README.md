# Advanced Concurrent Download Client

## Description

An improved version of a multithreaded download system with synchronization, retry policies, logging, and custom lock implementation (Ticket Lock).

---

## Features

* Ticket-based locking system
* Race condition handling
* Retry & backoff mechanism
* Thread-safe checksum aggregation
* Event logging system
* Missing block detection

---

## Key Components

* `TicketLock` → fairness-based lock
* `LogEvent` → system event tracking
* `DownloadWorker` → concurrent block downloader
* `DownloadClient` → coordinator

---

## Run

```bash
javac *.java
java Main
```

---

## Concepts

* concurrency control
* custom lock implementation
* thread coordination
* deadlock avoidance strategies
* logging in concurrent systems
