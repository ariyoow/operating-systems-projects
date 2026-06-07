# AoS vs SoA Memory Layout Benchmark

## Description

A simulation comparing **Array of Structures (AoS)** and **Structure of Arrays (SoA)** to analyze the impact of memory layout on CPU cache performance.

---

## Features

* Large-scale particle simulation
* Two memory layouts:

  * AoS (Array of Structures)
  * SoA (Structure of Arrays)
* Performance benchmarking
* Memory scalability testing
* Cache locality analysis

---

## Run

```bash
javac *.java
java Main
```

---

## What is Compared?

### AoS

Each particle stores:

* x, y, z, vx, vy, vz together

### SoA

Separate arrays for:

* positions
* velocities

---

## Concepts

* CPU cache locality
* memory access patterns
* data-oriented design
* performance benchmarking
