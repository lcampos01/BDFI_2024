#!/bin/bash
set -e  # Detener el script si ocurre algún error

# Instalar dependencias del sistema
apt-get update && apt-get install -y \
    python3 python3-pip libatlas-base-dev gfortran

# Instalar dependencias de Python
pip3 install --no-cache-dir findspark numpy

# Verificar la instalación
python3 -m pip show findspark
python3 -m pip show numpy

# Ejecutar el script de entrenamiento
python3 /opt/bitnami/spark/resources/train_spark_mllib_model.py

