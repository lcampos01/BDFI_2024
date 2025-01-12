FROM bde2020/spark-master:3.3.0-hadoop3.3



COPY ./init_spark_master.sh /docker-entrypoint-initdb.d/init_spark_master.sh


COPY ./flight_prediction /opt/bitnami/spark/flight_prediction
COPY ./resources /opt/bitnami/spark/resources
COPY ./data /opt/bitnami/spark/data
COPY ./models /opt/bitnami/spark/models




ENV INIT_DAEMON_STEP=setup_spark
ENV constraint:node==spark-master
