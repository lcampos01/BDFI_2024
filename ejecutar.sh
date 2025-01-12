#!/bin/bash
echo "===================================================================="
echo "				INICIANDO PROGRAMA"
echo "===================================================================="
bash docker compose up --build
sleep 10

echo "===================================================================="
echo "CONFIGURANDO MONGODB"
echo "===================================================================="
bash docker cp ./data/origin_dest_distances.jsonl mongo:data/
bash docker exec -it mongo /data/resources/import_distances.sh


