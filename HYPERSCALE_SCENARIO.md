# System Hyperscale & Capacity Planning (x100)

## 1. Scenario Definition: The "Hyperscale" Target

This document defines the architectural target for the **x100 Growth Phase** (1 Million Sensors). It identifies the
breaking points of the Baseline architecture and mandates specific infrastructure upgrades.

The system is designed to handle a **Massive-Scale / Data-Intensive** workload typical of smart cities or large-scale
logistics networks.

### Core Assumptions

| Variable                | Symbol | Value         | Description                            |
|:------------------------|:------:|:--------------|:---------------------------------------|
| **Number of Sensors**   |  $N$   | **1,000,000** | Active devices emitting data.          |
| **Reporting Frequency** |  $F$   | **1 Hz**      | One message per second per device.     |
| **Payload Size**        |  $P$   | **150 Bytes** | Average JSON payload size.             |
| **Retention Policy**    |  $R$   | **1 Year**    | 7 Days Hot (SSD) / 358 Days Cold (S3). |

---

## 2. Capacity Calculations (Napkin Math)

Based on the x100 assumptions, the system must sustain the following loads:

### A. Throughput & Network (The First Bottleneck)

* **Request Rate:** $1,000,000 \text{ sensors} \times 1 \text{ msg/s} = \textbf{1,000,000 RPS}$
* **Ingress Bandwidth:** $1,000,000 \times 150 \text{ bytes} \approx \textbf{150 MB/s} \text{ (1.2 Gbps)}$
    * *Verdict #1:* **FAILURE POINT.** A standard 1Gbps NIC cannot handle 1.2Gbps of traffic. **Requirement:** Upgrade
      to 10Gbps Networking or Link Aggregation.
    * *Verdict #2:* **Warehouse Service:** A single instance cannot handle 1M UDP packets/sec reliably due to OS buffer
      limits. **Requirement:** Layer 4 Load Balancer + Cluster of 3-5 Warehouse instances.
    * *Verdict #3:* **Broker:** RabbitMQ (Standard) will likely choke on routing costs. **Requirement:** Migrate to
      **Apache Kafka** or **RabbitMQ Streams** for log-based persistence.

### B. Storage Growth (The Cost Explosion)

* **Per Second:** $150 \text{ MB}$ (raw) + Overhead $\approx 200 \text{ MB/s}$
* **Per Day:** $200 \text{ MB} \times 86,400 \text{ s} \approx \textbf{17.2 TB / Day}$
* **Per Month (Raw):** $\approx \textbf{516 TB}$
* **Per Month (Compressed):** $\approx \textbf{51 TB}$
    * *Verdict:* Even with compression, storing 50TB/month on local SSDs is cost-prohibitive. **Requirement:** Tiered
      Storage (S3/Object Storage) is mandatory for data older than 7 days.

### C. I/O Operations (IOPS)

* **Raw Writes:** 1,000,000 IOPS.
* **Batched Writes (Batch Size=2000):** $1,000,000 / 2000 = \textbf{500 IOPS}$.
    * *Verdict:* 500 Write Transactions/sec is manageable for a high-performance database, but requires larger batch
      sizes (increased from 500 to 2000).

### Summary Table (Hyperscale)

| Metric                   |    Value (x100)     |     Status     | Architectural Requirement         |
|:-------------------------|:-------------------:|:--------------:|:----------------------------------|
| **Throughput**           | **~1,000,000 RPS**  |  **Critical**  | **L4 Load Balancer + Clustering** |
| **Bandwidth**            |    **~1.2 Gbps**    |  **Critical**  | **10Gbps Network Infrastructure** |
| **Storage (Raw)**        | **~17.2 TB / Day**  |  **Critical**  | **Tiered Storage (S3 Offload)**   |
| **Storage (Compressed)** |  **~1.7 TB / Day**  |    **High**    | **Aggressive Downsampling**       |
| **IOPS (Raw)**           | **~1,000,000 IOPS** | **Impossible** | **Must Batch**                    |
| **IOPS (Batched)**       |    **~500 IOPS**    |   **Stable**   | **Batch Size: 2,000+**            |

---

## 3. Key Design Decisions (x100 Upgrades)

### 1. Ingestion: Horizontal Scaling with L4 LB

* **Problem:** Single node cannot process 1M packets/sec; OS network stack becomes the bottleneck.
* **Decision:** Introduce NGINX/HAProxy as a UDP Load Balancer distributing traffic to a cluster of Warehouse Services.
* **Benefit:** Linearly scalable ingestion. If load increases to 2M, simply add more Warehouse nodes.

### 2. Broker: Apache Kafka (or RabbitMQ Streams)

* **Problem:** Standard RabbitMQ exchanges struggle with 1M messages/s due to per-message routing overhead.
* **Decision:** Switch to a Commit Log model (Kafka).
* **Benefit:** High throughput sequential writes; decouples consumers from producers completely. Consumers can process
  historical data in parallel by replaying the log.

### 3. Storage: Tiered Architecture (Hot/Cold)

* **Problem:** Storing 500TB of historical data on SSDs is financially unsustainable.
* **Decision:** Use TimescaleDB Tiered Storage.
    * **Hot:** Recent 7 days on NVMe SSD (Fast R/W).
    * **Cold:** Older data moved to S3/MinIO (Cheap, slower).
* **Benefit:** Reduces storage cost by ~90% while keeping data queryable via standard SQL.

### 4. Data Resolution: Downsampling

* **Problem:** Querying 1 year of raw data (31 million rows per sensor) is slow.
* **Decision:** Implement Continuous Aggregates.
  * **Keep Raw (1s) for 7 days.**
  * **Rollup to 1-minute averages for 3 months.**
  * **Rollup to 1-hour averages for 1 year.**
* **Benefit:** Reduces historical dataset size by a factor of 60x to 3600x.

---