#!/bin/bash
# Iniciar Kafka


# Esperar un momento para asegurarse de que Kafka esté completamente iniciado
sleep 10

echo "Creating Kafka topic..."
/usr/bin/kafka-topics \
    --create \
    --bootstrap-server kafka:9092 \
    --replication-factor 1 \
    --partitions 1 \
    --topic flight_delay_classification_request

echo "Kafka topic created."
