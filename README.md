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
Nos logeamos, detecta el modelo a entrenar pero al conectarlo con un nuevo contenedor de Spark no funciona correctamente.
![image](https://github.com/user-attachments/assets/95dd0745-aa70-4ced-8be6-6842e268456e)
Observamos que no es capaz de entrenar el modelo:
![image](https://github.com/user-attachments/assets/dcf37cc9-2bc1-445f-a59e-a9b9c2305040)

El error que nos sale es el siguiente:
```
[2025-01-12 22:35:05,779] {docker.py:307} INFO - : An error occurred while calling None.org.apache.spark.api.java.JavaSparkContext.
: org.apache.hadoop.security.KerberosAuthException: failure to login: javax.security.auth.login.LoginException: java.lang.NullPointerException: invalid null input: name
```
### Airflow
Accedemos a http://localhost:8080 podemos observar la interfaz de airflow:
