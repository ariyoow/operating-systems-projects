# Parallel Download Manager (Java)

## Description

A multithreaded download system that splits a file into blocks and downloads them concurrently using a thread pool. Includes benchmarking for performance evaluation.

---

## Features

* Thread pool execution
* Block-based downloading
* Integrity check using checksum
* Speedup benchmarking
* Dynamic thread scaling

---

## Run

```bash
javac *.java
java Main
```

---

## Benchmarking

Tests multiple thread counts:

* 1, 2, 4, 8, 16, 24, 32, 40 threads

Measures:

* Execution time
* Speedup ratio

---

## Concepts

* multithreading (ExecutorService)
* blocking queue
* synchronization
* performance analysis
