mkdir -p scripts
cat > scripts/import_pois.sh <<'EOF'
#!/usr/bin/env bash
set -euo pipefail

# Параметри підключення (візьміть з application.properties)
HOST=localhost
PORT=5432
DB=realtor
USER=postgres
PASS=postgres

# Директорія з GeoJSON
DIR=D:/LNU/Diploma/real-estate-broker/src/main/resources/states

for f in "$DIR"/*.geojson; do
  echo "Імпортую $f..."
  ogr2ogr \
    -f "PostgreSQL" \
    PG:"host=$HOST port=$PORT dbname=$DB user=$USER password=$PASS" \
    "$f" \
    -nln points_of_interest \
    -append \
    -nlt POINT \
    -lco FID=id \
    -lco GEOMETRY_NAME=geom \
    -dialect sqlite \
    -sql "
      SELECT
        -- 1) зібрали категорію з першого непустого тега
        COALESCE(
          amenity,
          leisure,
          shop,
          highway,
          railway,
          public_transport
        ) AS category,

        -- 2) для name беремо його ж, або якщо NULL — підставляємо category
        COALESCE(
          name,
          COALESCE(
            amenity,
            leisure,
            shop,
            highway,
            railway,
            public_transport
          )
        ) AS name,

        -- 3) геометрію в колонку geom
        geometry AS geom
      FROM ogrgeojson
    "
done

echo "Усі GeoJSON імпортовано успішно."
EOF
