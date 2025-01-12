#!/bin/bash
mongod &
# Esperar un tiempo fijo para que MongoDB esté listo
echo "Esperando 10 segundos para que MongoDB se inicie..."
sleep 10

# Ejecutar el script de importación
chmod +x /data/resources/import_distances.sh
echo "Ejecutando script"
bash /data/resources/import_distances.sh

# Mantener el contenedor activo ejecutando MongoDB
wait
