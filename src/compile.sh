#!/bin/bash

MPJ_HOME=/home/dnllzc/Desktop/dos-protection/mpj-v0_44

javac -cp .:$MPJ_HOME/lib/mpj.jar *.java

$MPJ_HOME/bin/mpjrun.sh -np 8 detection 0 $MPJ_HOME/conf/mpj8.conf enp0s3
