import os
from airflow import DAG
from airflow.operators.bash import BashOperator
from datetime import datetime, timedelta

PROJECT_HOME = os.getenv("PROJECT_HOME")
if not PROJECT_HOME:
    raise ValueError("PROJECT_HOME environment variable is not set.")

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2016, 12, 1),  # Usar datetime estándar
    'retries': 3,
    'retry_delay': timedelta(minutes=5),
}

training_dag = DAG(
    'agile_data_science_batch_prediction_model_training',
    default_args=default_args,
    schedule_interval=None  # Ejecutar manualmente
)

# Bash commands para PySpark
pyspark_bash_command = """
spark-submit --master {{ params.master }} \
  {{ params.base_path }}/{{ params.filename }} \
  {{ params.base_path }}
"""

# Operadores
train_classifier_model_operator = BashOperator(
    task_id="pyspark_train_classifier_model",
    bash_command=pyspark_bash_command,
    params={
        "master": "local[8]",
        "filename": "resources/train_spark_mllib_model.py",
        "base_path": f"{PROJECT_HOME}/",
    },
    dag=training_dag
)

# Dependencias (descomentarlas si son necesarias)
# train_classifier_model_operator.set_upstream(extract_features_operator)

