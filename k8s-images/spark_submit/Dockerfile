FROM bde2020/spark-submit:3.3.0-hadoop3.3


# Set environment variable for Spark Master connection
ENV SPARK_MASTER=spark://spark-master:7077
ENV constraint:node==spark-master

COPY ./init_spark_submit.sh /opt/bitnami/spark/init/init_spark_submit.sh
COPY ./flight_prediction /opt/bitnami/spark/flight_prediction
COPY ./resources /opt/bitnami/spark/resources
COPY ./models /opt/bitnami/spark/models
#COPY ./data /opt/bitnami/spark/data


# Define command to execute init script
CMD ["/bin/bash", "/opt/bitnami/spark/init/init_spark_submit.sh"]

