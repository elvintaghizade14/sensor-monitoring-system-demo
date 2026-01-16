# System Baseline & Capacity Planning

## 1. Scenario Definition: The "Baseline"

This document defines the architectural baseline for the Sensor Monitoring System. It serves as the reference point for
infrastructure sizing, load testing, and performance optimization.

The system is designed to handle a **High-Throughput / Write-Intensive** workload typical of a mid-sized industrial IoT
environment.

### Core Assumptions

| Variable                | Symbol | Value         | Description                                                 |
|:------------------------|:------:|:--------------|:------------------------------------------------------------|
| **Number of Sensors**   |  $N$   | **10,000**    | Active devices emitting data.                               |
| **Reporting Frequency** |  $F$   | **1 Hz**      | One message per second per device.                          |
| **Payload Size**        |  $P$   | **150 Bytes** | Average JSON payload size (ID, Timestamp, Value, Metadata). |
| **Retention Policy**    |  $R$   | **3 Months**  | Duration raw data is kept hot/warm.                         |

Conservative estimate for a JSON payload like:</br>
`{"sensor_id": "wh-1-sec-b-temp-001", "value": 24.5, "ts": 1705386000}`

---

## 2. Capacity Calculations (Napkin Math)

Based on the assumptions above, the system must sustain the following loads:

### A. Throughput & Network

* **Request Rate:** $10,000 \text{ sensors} \times 1 \text{ msg/s} = \textbf{10,000 RPS}$
* **Ingress Bandwidth:** $10,000 \times 150 \text{ bytes} \approx \textbf{1.5 MB/s} \text{ (12 Mbps)}$
    * *Verdict #1:* Easily handled by standard 1Gbps NICs.
    * *Verdict #2:* Warehouse Service (UDP): A single Java Virtual Thread application can easily handle 10k RPS on a
      modern CPU, provided the processing is non-blocking.
    * *Verdict #3:* RabbitMQ: 10k msgs/s is a moderate load. A standard single-node RabbitMQ can handle 20k-50k msgs/s
      depending on message size and durability settings.

### B. Storage Growth

* **Per Second:** $1.5 \text{ MB}$ (raw payload) + DB Overhead $\approx 2 \text{ MB/s}$
* **Per Day:** $2 \text{ MB} \times 86,400 \text{ s} \approx \textbf{172 GB / Day}$
* **Per Month (Raw):** $\approx \textbf{5.1 TB}$
* **Per Month (Compressed):** $\approx \textbf{510 GB}$
    * *Note:* Using **TimescaleDB** compression (est. 90% ratio) is mandatory to fit data on standard SSDs.

### C. I/O Operations (IOPS)

* **Raw Writes:** 10,000 IOPS (Too high for standard cloud disks).
* **Batched Writes (Batch Size=500):** $10,000 / 500 = \textbf{20 IOPS}$.
    * *Verdict:* **Micro-batching is an architectural requirement.**

### Summary Table (Napkin Math)

| Metric                  | Value (Baseline) | Note                                    |
|:------------------------|:----------------:|:----------------------------------------|
| **Throughput**          |  $~10,000 RPS$   | **Manageable by single Java instance.** |
| **Bandwidth**           |   $~1.5 MB/s$    | **Very Low. Not a concern.**            |
| **Storage (Raw)**       | $~172 GB / Day$  | **Critical. Too high for standard DB.** |
| **Storage (Timescale)** |  $~17 GB / Day$  | **Manageable on SSD.**                  |
| **IOPS (Raw)**          |  $~10,000 IOPS$  | **Critical. Requires high-end SSDs.**   |
| **IOPS (Batched)**      |    $~20 IOPS$    | **Trivial. Must implement batching.**   |

---

## 3. Key Design Decisions

### 1. Protocol: UDP for Ingestion

* **Context:** 10k RPS requires low overhead.
* **Decision:** We use UDP instead of TCP/HTTP for the initial hop.
* **Trade-off:** We accept a small margin of packet loss in exchange for massive throughput gains and lower energy
  consumption on sensor devices.

### 2. Storage: TimescaleDB (PostgreSQL Extension)

* **Context:** Storing 5TB/month of raw data is expensive and slow to query.
* **Decision:** Use TimescaleDB for automatic Hypertable partitioning and Columnar Compression.
* **Benefit:** Reduces storage costs by ~90% and keeps query speeds constant as dataset grows.

### 3. Write Strategy: Micro-Batching

* **Context:** Database disk I/O (IOPS) is the primary bottleneck.
* **Decision:** The Central Service aggregates incoming messages into buffers (Size: 500) before flushing to the DB.
* **Benefit:** Reduces Database load from 10,000 transactions/sec to ~20 transactions/sec.

---

## 4. Future Scalability (Path to x100)

To scale to 1 Million Sensors, the following upgrades are planned:

    1. Load Balancer: Introduce NGINX (Layer 4 UDP) before Warehouse Service.

    2. Broker: Migrate from RabbitMQ to Apache Kafka or RabbitMQ Streams.
    
    3. Storage: Implement Tiered Storage (Move data >7 days old to S3/Object Storage).