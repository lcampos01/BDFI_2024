#!/bin/bash
# Iniciar Kafka
#/opt/bitnami/kafka/bin/kafka-server-start.sh /opt/bitnami/kafka/config/server.properties &

# Esperar un momento para asegurarse de que Kafka esté completamente iniciado
sleep 10

#/opt/bitnami/kafka/bin/kafka-server-start.sh /opt/bitnami/kafka/config/server.properties

# Crear el tema
kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic flight_delay_classification_request

# Listar los temas
#/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list
