# Deadlock Detection & Recovery System

## Description

A custom `LockManager` that simulates real-world deadlock scenarios and automatically detects and resolves them using cycle detection in a wait graph.

---

## Features

* ReentrantLock-based lock system
* Deadlock detection using cycle detection
* Wait-for graph analysis
* Deadlock recovery mechanism
* Thread interruption handling

---

## Deadlock Scenario

* Thread-1: A → B
* Thread-2: B → C
* Thread-3: C → A

---

## Run

```bash
javac *.java
java DeadlockDemo
```

---

## Concepts

* deadlock detection
* resource allocation graph
* concurrency control
* thread interruption
* lock ownership tracking
