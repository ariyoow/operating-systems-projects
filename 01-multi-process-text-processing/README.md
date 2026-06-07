# Multi-Process File Processing (C)

## Description

A process-based program using `fork()` and `exec()` to process a numeric file in parallel. Each child process handles a portion of the input using shared memory.

---

## Features

* Multi-process execution
* Shared memory communication (System V IPC)
* Parallel computation of:

  * Sum
  * Average
  * Maximum
* Process synchronization via parent aggregation

---

## Build

```bash
gcc main.c -o main
gcc child.c -o child
```

---

## Run

```bash
./main <num_processes> <input_file>
```

Example:

```bash
./main 4 input.txt
```

---

## Concepts

* fork / exec
* shared memory (shmget, shmat)
* IPC
* parallel processing
