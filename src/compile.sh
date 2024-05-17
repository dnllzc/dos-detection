#!/bin/bash

MPJ_HOME=/home/dnllzc/Desktop/dos-protection/mpj-v0_44

sudo javac -cp .:$MPJ_HOME/lib/mpj.jar DistributedQueues.java detection.java PacketChecker.java Queues.java Attributes.java

sudo java -cp .:$MPJ_HOME/lib/mpj.jar:. detection
