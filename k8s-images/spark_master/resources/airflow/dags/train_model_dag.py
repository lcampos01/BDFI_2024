import os
from datetime import datetime, timedelta
import iso8601

from airflow import DAG
from airflow.providers.docker.operators.docker import DockerOperator

PROJECT_HOME = os.getenv("PROJECT_HOME")

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': iso8601.parse_date("2016-12-01"),
    'retries': 3,
    'retry_delay': timedelta(minutes=5),
}

training_dag = DAG(
    'agile_data_science_batch_prediction_model_training',
    default_args=default_args,
    schedule_interval=None
)

train_classifier_model_operator = DockerOperator(
    task_id='pyspark_train_classifier_model',
    image='jorgealmansa/custom-spark-submit:3.3.0',
    api_version='auto',
    auto_remove=True,
    mount_tmp_dir=False, 
    command=(
        "/spark/bin/spark-submit "
        "--master spark://spark-master:7077 "
        "--conf spark.jars.ivy=/opt/bitnami/spark/.ivy2 "
        "--conf spark.driver.extraJavaOptions=-Duser.home=/opt/bitnami/spark "
        "--conf spark.executor.extraJavaOptions=-Duser.home=/opt/bitnami/spark "
        "--conf spark.hadoop.security.authentication=none "
        "--conf spark.hadoop.security.authorization=false "
        "/opt/bitnami/spark/resources/train_spark_mllib_model.py ."
    ),
    docker_url='unix://var/run/docker.sock',
    network_mode='practica_docker_default',
    dag=training_dag
)
