# DESPLIEGUE DE LA APLICACIÓN
Se despliegan los contenedores:
```
docker-compose up -d --build
```
Vemos que se crean los contenedores:
```
docker ps
```
### Interfaz web
Posteriormente, accedemos a la web de la predicción de vuelos (http://localhost:5000/flights/delays/predict_kafka):
```
docker logs flask_app
```
![image](https://github.com/user-attachments/assets/2291aaa4-2db5-4725-964a-3b438375b102)

### MongoDB
Observamos como en Mongo se escriben las requests y responses:
```
docker exec -ti mongo bash -c "mongo agile_data_science --eval 'db.origin_dest_distances.findOne()'"
```
```
docker exec -it mongo bash -c "mongo agile_data_science --eval 'db.flight_delay_classification_response.count()'"
docker exec -it mongo bash -c "mongo agile_data_science --eval 'db.flight_delay_classification_response.findOne()'"
```
### Kafka
Observamos como en Kafka los topics que se han creado en kafka:
```
docker exec -it kafka bash -c "/opt/bitnami/kafka/bin/kafka-topics.sh --list --bootstrap-server kafka:9092"
```
Posteriormente, observamos las requests y responses guardadas en los respectivos topics:
```
docker exec -it kafka /opt/bitnami/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --topic flight_delay_classification_request \
    --from-beginning
```
```
docker exec -it kafka /opt/bitnami/kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9092 \
    --topic flight_delay_classification_response \
    --from-beginning
```

### Spark
Accediendo a http://localhost:8083/ podemos observar como el servicio está corriendo correctamente con sus respectivos workers.
![image](https://github.com/user-attachments/assets/3f7816a9-3f6a-4cb6-948c-e91b46817072)

### Airflow
Accedemos a http://localhost:8080 podemos observar la interfaz de airflow:

Podemos observar como detecta correctamente el modelo a entrenar:
![image](https://github.com/user-attachments/assets/3ee9f8a0-a976-49b4-b3bd-1a54d9af3e6c)

Pero no hemos podido dockerizar esta parte debido a que el spark-submit no lo realiza correctamente el contenedor Spark desplegado por Airflow:
![image](https://github.com/user-attachments/assets/849b0e96-fd75-45e2-abda-963fc0a8a9dc)

El error que nos aparece es el siguiente:
```
[2025-01-12 22:35:05,779] {docker.py:307} INFO - : An error occurred while calling None.org.apache.spark.api.java.JavaSparkContext.
: org.apache.hadoop.security.KerberosAuthException: failure to login: javax.security.auth.login.LoginException: java.lang.NullPointerException: invalid null input: name
```
