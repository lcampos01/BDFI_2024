FROM bitnami/kafka:3.4.0

USER root

COPY kafka-init.sh /opt/bitnami/kafka/kafka-init.sh

RUN chmod 777 /opt/bitnami/kafka/kafka-init.sh

ENTRYPOINT [ "/opt/bitnami/kafka/kafka-init.sh" ]


ENV KAFKA_CFG_NODE_ID=0
ENV KAFKA_CFG_PROCESS_ROLES=controller,broker
ENV KAFKA_CFG_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093
ENV KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT
ENV KAFKA_CFG_CONTROLLER_QUORUM_VOTERS=0@kafka:9093
ENV KAFKA_CFG_CONTROLLER_LISTENER_NAMES=CONTROLLER
ENV KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092



