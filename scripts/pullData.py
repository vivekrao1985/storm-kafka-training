#!/usr/bin/env python

import logging, time
import sys

from kafka.client import KafkaClient
from kafka.consumer import SimpleConsumer

def getMessages(topic):
    client = KafkaClient("localhost:9092")
    consumer = SimpleConsumer(client, "test-group", topic)

    for message in consumer:
        print(message)

def main(argv):
    getMessages(argv[0])

if __name__ == "__main__":
    main(sys.argv[1:])