#!/usr/bin/env python
import logging, time
import sys

from kafka.client import KafkaClient
from kafka.producer import SimpleProducer

def sendMessages(numRecords, topic):
    client = KafkaClient("localhost:9092")
    producer = SimpleProducer(client)

    for x in range(0, int(numRecords) - 1):
        producer.send_messages(topic, "test-" + str(x))

def main(argv):
    sendMessages(argv[0], argv[1])

if __name__ == "__main__":
    logging.basicConfig(
        format='%(asctime)s.%(msecs)s:%(name)s:%(thread)d:%(levelname)s:%(process)d:%(message)s',
        level=logging.DEBUG
        )
    main(sys.argv[1:])