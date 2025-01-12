FROM apache/spark:3.3.3  # Replace with chosen version

# Copy Spark configuration scripts (if needed)
COPY /shared-spark /opt/bitnami/spark
COPY ./flight_prediction /opt/bitnami/spark/flight_prediction
COPY ./resources /opt/bitnami/spark/resources
COPY ./inits/init_spark_master.sh /docker-entrypoint-initdb.d/init_spark_master.sh




# Set environment variables (if needed)
ENV SPARK_MASTER_HOST=spark-master  # Adjust if necessary


# CMD to run the service
CMD ["/bin/bash", "/docker-entrypoint-initdb.d/init_spark_master.sh"]
