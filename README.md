# DOS Detection Project

## Overview

Denial of Service (DoS) is a well-known attack vector aimed at using up the available resources of servers to deny non-malicious users the use of the service. This project focuses on the basics of preventing DoS attacks by learning about packet filtering and traffic monitoring. While the implementation does not follow state-of-the-art protection mechanisms, it provides educational benefits by diving into these fundamental concepts.

## Features

- **Traffic Monitoring**: Utilizes `tcpdump` to monitor network traffic on a specific port.
- **Anomaly Detection**: Detects anomalies in traffic based on source IP addresses and cumulative traffic patterns.

## Requirements

- Java Development Kit (JDK)
- `tcpdump` installed on a Linux based system
- Sudo privileges for running `tcpdump` or root access
- MPJ Express for MPI support
- Basic web server or `SocketServer` for testing

## Installation

1. Ensure Java is installed on your system.
2. Install `tcpdump` on your Linux system:
   ```sh
   sudo apt-get install tcpdump
   ```
3. Install MPJ Express for MPI support. Follow the [MPJ Express installation guide](https://github.com/kevinmilner/mpj-express/tree/master).
4. Clone this repository:
   ```sh
   git clone https://github.com/yourusername/dos-detection.git
   cd dos-detection
   ```

## Compilation

1. Set the MPJ_HOME environment variable:
   ```sh
   export MPJ_HOME=/path/to/mpj-express
   ```
2. Compile the Java classes:
   ```sh
   sudo javac -cp .:$MPJ_HOME/lib/mpj.jar *.java 
   ```
Or just run the auto compile and run script:
   ```sh
   sudo ./compile.sh
   ```

## Usage

1. **Run the detection application**:
   ```sh
   sudo $MPJ_HOME/bin/mpjrun.sh -np 2 detection 0 $MPJ_HOME/conf/mpj8.conf enp0s3
   ```

## How It Works

1. **Traffic Monitoring**:
   The application executes `tcpdump` using `ProcessBuilder` and reads its output.
   ```java
   String cmd2 = "tcpdump -i any port 8080 and '(tcp-syn|tcp-ack)!=0'";
   ProcessBuilder pb2 = new ProcessBuilder();
   pb2.command("bash", "-c", cmd2);
   Process p = pb2.start();
   ```
2. **Packet Distribution**:
   `DistributedQueues` class is responsible for distributing packets to queues based on their source.
   ```java
   DistributedQueues.distributePackets(attributes);
   ```
3. **Anomaly Detection**:
   `PacketChecker` class checks for anomalies in the packets based on the source and time.
   ```java
   PacketChecker.checkAll();
   ```
4. **Queue Management**:
   `Queues` class is responsible for queues for different sources and their records.
   ```java
   LinkedList<Queues> queues = Queues.queues;
   ```
5. **Main Detection Logic**:
   The `detection` class runs the main logic.
   ```java
   public static void main(String[] args) throws IOException, InterruptedException {
       ...
   }
   ```
