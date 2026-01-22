## Star Query RDF Engine (HAI914I)
A high-performance Java-based RDF engine designed to evaluate Star Queries using the Hexastore indexing approach. This project was developed as part of the HAI914I module (NoSQL) at the University of Montpellier.

## Authors
Each file has an author specified.

- Antoine Colonna d'Istria <antoine.colonnadistria@yahoo.com> for the main RDF engine
- Federico Ulliana <federico.ulliana@inria.fr> & Guillaume Pérution-Khili for the skeleton
- Mehdi Bahktar & Théo Foutel-Rodier for the concurrent engine

## Supervisor

- Federico Ulliana <federico.ulliana@inria.fr>

## Overview
The primary objective of this project is to implement an efficient system for querying RDF data. Unlike traditional relational databases, this engine uses a specialized Hexastore index—which maintains six separate indices for all permutations of Subject (S), Predicate (P), and Object (O) in order to provide O(1) or O(log N) lookups for RDF triple patterns.

## Requirements
- Java 21
- Maven
- RDF4J parser
- Oracle: InteGraal 1.7.2

## Build & Run
1. Clone the repository

```bash
git clone https://github.com/acolonnadistria/rdf-sparql-engine.git
cd rdf-sparql-engine
```

2. Build the project
```bash
mvn clean install
```

3. Create the dataset
```bash
mvn exec:java -Dexec.mainClass="qengine.watdiv.DatasetPreparer"
```

4. Run the performance test
```bash
mvn exec:java -Dexec.mainClass="qengine.benchmark.PerformanceTests"
```

5. Run the statistics analysis
```bash
cp data/output/statistics/*.json analysis
cd analysis && python3 analysis.py
```

## Key Features
- Dictionary Encoding: Maps long RDF URIs/Literals to compact integer IDs to reduce memory footprint and speed up comparisons.
- Hexastore Indexing: Implementation of the six core indices (SPO, SOP, PSO, POS, OSP, OPS).
- Giant-Table Implementation: A baseline Row-Store storage for performance benchmarking.
- Star Query Evaluation: Optimized evaluation of SPARQL-style star queries (multiple triple patterns sharing a single variable).
- Oracle Verification: Integration with the InteGraal library to verify result correctness and completeness.
- Comparaison to Concurrent RDF Hexastores
- Unit tests: each RDF engine is tested by a series of JUnit tests

## Project structure
- `qengine.store` : Core logic for the Dictionary and Hexastore index structures.
- `qengine.model` : Internal representations of RDF Triples and Star Queries.
- `qengine.parser` : Logic to parse .nt (N-Triples) and .queryset files.
- `qengine.program` : Main entry points and usage examples.
- `qengine.statistics` : Components for tracking query selectivity and engine performance.

## Results
The engine was evaluated using the WatDiv (Waterloo SPARQL Diversity Test Suite) benchmark. The tests were conducted on a Framework Laptop 13 (AMD Ryzen 7 7840U, 32 GiB RAM) running Ubuntu 24.04.3 LTS.

0. Dataset Distribution (500k triplets & 2M triplets) / Data preparation

If we just run WatDiv without any smapling.

![Distribution of queries before sampling](readme-images/nb-answers-bin-1M-queries-500k-triplets.png)

After removing duplicates and sampling it by number of answers. (Queries with 100+ answers will be unreliable as the dataset is much smaller).

![Distribution of queries after sampling](nb-sample-answers-bin-1M-queries-500k-triplets.png)

1. Evaluating performance (500K triplets)

The primary performance benchmark compared the specialized Hexastore index against a baseline Giant Table implementation using 500k RDF triplets.

| Query Selectivity (Bin) | Hexastore Avg Response (ms) | Giant Table Avg Response (ms) |
|------------------------|-----------------------------|--------------------------------|
| 0–1 answers (High)     | 1.373 ± 0.918               | 68.387 ± 12.798               |
| 1–20 answers (Medium)  | 0.063 ± 0.078               | 36.621 ± 2.714                |
| 20–100 answers (Low)   | 0.052 ± 0.004               | 35.881 ± 2.953                |
| 100+ answers (Very Low)| 0.285 ± 0.346               | 22.800 ± 23.198               |

**Key Finding**: The Hexastore is significantly faster than the Giant Table, with speed improvements ranging from 80x to over 600x.

2. Evaluating performance (2M triplets)

Giant Table was not tested as it was too slow.

| Bin     | Hexastore (ms) |
|---------|----------------|
| 0–1     | 8.099 ± 2.778  |
| 1–20    | 2.503 ± 3.117  |
| 20–100  | 0.086 ± 0.039  |
| 100+    | 1.482 ± 1.780  |

3. Loading times

While the Hexastore provides superior query speeds, it requires building six distinct B-tree indices. However, in our benchmarks, the Hexastore maintained a much lower loading time compared to the Giant Table. The Giant Table loading exceeded 140 seconds for the test dataset, while the Hexastore completed in only a few seconds

![Loading times](readme-images/loading-time-statistics.png)

4. Comparison to the concurrent engine (Théo Foutel-Rodier & Mehdi Bakhtar)

![Distribution of queries before sampling](readme-images/nb-answers-bin-1M-queries-500k-triplets.png)

4. Factor Analysis (Two-Squared Experiment)

We performed a regression analysis to identify the main factors affecting system performance.
The number of triplets is the most important factor (≈ 0.616), with JVM heap size (4 GB vs. 16 GB) also having a strong impact (≈ 0.355).

5. Correctness & Completeness
In all scenarios, correctness and completeness were achieved and are at 100%.
