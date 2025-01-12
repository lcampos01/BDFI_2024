#!/bin/bash

sleep 5

# Ejecutar Spark-submit
/spark/bin/spark-submit \
  --class es.upm.dit.ging.predictor.MakePrediction \
  --deploy-mode cluster --master spark://spark-master:7077 \
  --packages org.mongodb.spark:mongo-spark-connector_2.12:10.1.1,org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.3 \
  opt/bitnami/spark/flight_prediction/target/scala-2.12/flight_prediction_2.12-0.1.jar
  
  
  

