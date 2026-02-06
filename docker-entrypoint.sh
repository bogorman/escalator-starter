#!/bin/bash
set -e

echo "=== Starting Escalator App ==="

# Build Flyway JDBC URL from environment variables
FLYWAY_URL="jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"

echo "Running database migrations..."
flyway -url="$FLYWAY_URL" \
       -user="$DB_USER" \
       -password="$DB_PASSWORD" \
       -locations="filesystem:/app/db/migration" \
       -baselineOnMigrate=true \
       -outOfOrder=true \
       migrate

echo "Running database seeds..."
flyway -url="$FLYWAY_URL" \
       -user="$DB_USER" \
       -password="$DB_PASSWORD" \
       -locations="filesystem:/app/db/seed" \
       -table="flyway_seed_history" \
       -baselineOnMigrate=true \
       -outOfOrder=true \
       migrate

echo "Migrations complete. Starting application..."
exec bin/backend
