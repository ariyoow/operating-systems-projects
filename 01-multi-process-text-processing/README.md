# Multi-Process File Processing (C - fork & shared memory)

## Overview

This project implements a multi-process program using `fork()` and `exec()` in C. The program processes a numeric input file in parallel using multiple child processes and aggregates the results using shared memory.

Each child process is responsible for a portion of the file, computes local statistics, and writes the result back to shared memory. The parent process collects all partial results and computes the final output.

---

## Features

* Multi-process architecture using `fork()`
* Process execution using `exec()`
* Inter-process communication using System V shared memory (`shmget`, `shmat`)
* Parallel processing of input data
* Computation of:

  * Sum
  * Average
  * Maximum value
  * Count of numbers
* Performance measurement using `gettimeofday`

---

## Architecture

### Parent Process

* Reads input file
* Divides workload among child processes
* Creates shared memory segment
* Spawns child processes using `fork + exec`
* Collects results and computes global statistics

### Child Process

* Reads assigned segment of file
* Computes local:

  * sum
  * max
  * average
* Writes results to shared memory

---

## Input Format

A `.txt` file containing integers:

```
N
x1 x2 x3 ... xN
```

Where:

* `N` = total number of integers
* `xi` = integer values

---

## Output

The program prints:

* Total numbers processed
* Global average
* Global maximum
* Execution time

---

## How to Compile

```bash
gcc main.c -o main
gcc child.c -o child
```

---

## How to Run

```bash
./main <num_processes> <input_file>
```

Example:

```bash
./main 4 input.txt
```

---

## Concepts Used

* Process creation (`fork`)
* Program replacement (`exec`)
* Shared memory (IPC)
* Parallel computation
* CPU workload distribution

---

## Notes

* Each child works independently on a segment of the file.
* Shared memory is used to avoid file contention.
* Proper cleanup is done using `shmctl`.
