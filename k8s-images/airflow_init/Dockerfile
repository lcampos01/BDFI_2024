FROM apache/airflow:2.1.4-python3.8

# Copia el archivo JSON a la imagen
COPY ./airflow /opt/airflow

ENV AIRFLOW_HOME=/opt/airflow
ENV PROJECT_HOME=/opt/airflow
ENV AIRFLOW_CORE_LOAD_EXAMPLES=False
ENV AIRFLOW_CORE_DAGS_FOLDER=/opt/airflow/dags
ENV IRFLOW_CORE_EXECUTOR=SequentialExecutor


# El comando que se ejecutará al iniciar el contenedor
CMD ["bash", "-c", \
    "pip install -r /opt/airflow/requirements.txt -c /opt/airflow/constraints.txt"]

