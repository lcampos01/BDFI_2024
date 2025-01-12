#!/bin/bash
# Instalar dependencias de Python
pip install findspark
pip install numpy

# Ejecutar el script de entrenamiento
python3 /opt/bitnami/spark/resources/train_spark_mllib_model.py .
