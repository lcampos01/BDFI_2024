#!/bin/bash

# Importar los datos como colección 'origin_dest_distances'
mongoimport --db agile_data_science --collection origin_dest_distances --file data/origin_dest_distances.jsonl

# Crear un índice en los campos 'Origin' y 'Dest'
mongo agile_data_science --eval 'db.origin_dest_distances.createIndex({Origin: 1, Dest: 1})'
