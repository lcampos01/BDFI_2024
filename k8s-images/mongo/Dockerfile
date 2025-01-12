FROM mongo:4.0

# Copia el archivo JSON a la imagen
COPY ./origin_dest_distances.jsonl /data/origin_dest_distances.jsonl

# Crea el directorio para los datos (si no existe ya en la imagen base)
RUN mkdir -p /data

# El comando que se ejecutará al iniciar el contenedor
CMD ["bash", "-c", \
    "mongod --bind_ip_all & \
    echo 'Esperando a que MongoDB esté listo...' && \
    until mongo --host 127.0.0.1 --eval 'print(\"MongoDB iniciado\")' &>/dev/null; do \
        sleep 2; \
    done && \
    echo 'Importando datos...' && \
    mongoimport --db agile_data_science --collection origin_dest_distances --file /data/origin_dest_distances.jsonl && \
    mongo agile_data_science --eval 'db.origin_dest_distances.createIndex({Origin: 1, Dest: 1})' && \
    echo 'Importación completada.' && \
    wait"]
