#!/bin/bash

# Actualizar e instalar dependencias
apt-get update
apt-get install -y curl apt-transport-https gnupg
curl -fsSL https://scala.jfrog.io/artifactory/api/gpg/key/public | gpg --dearmor -o /usr/share/keyrings/sbt-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/sbt-keyring.gpg] https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list
apt update
apt install sbt -y

# Compilar y empaquetar con sbt
cd /opt/bitnami/spark/flight_prediction
sbt compile
sbt package

# Ejecutar Spark-submit
/opt/bitnami/spark/bin/spark-submit \
  --class es.upm.dit.ging.predictor.MakePrediction \
  --master spark://spark-master:7077 \
  --packages org.mongodb.spark:mongo-spark-connector_2.12:10.1.1,org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.3 \
  /opt/bitnami/spark/flight_prediction/target/scala-2.12/flight_prediction_2.12-0.1.jar
