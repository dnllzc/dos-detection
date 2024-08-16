#!/bin/bash

$(sudo tcpdump -i any port 8080 and '(tcp-syn|tcp-ack)!=0')
