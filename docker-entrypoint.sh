#!/bin/bash
set -e

# Run database migrations if flyway and migrations exist
if command -v flyway &> /dev/null && [ -d "/app/db/migrations" ]; then
    echo "Running database migrations..."
    export FLYWAY_URL="jdbc:postgresql://${DB_HOST:-localhost}:${DB_PORT:-5432}/${DB_NAME}?user=${DB_USER}"
    export FLYWAY_PASSWORD="${DB_PASSWORD}"
    
    # Run migrations
    flyway -locations="filesystem:/app/db/migrations" migrate
    
    # Run seeds if they exist and SEED_DB is set
    if [ -d "/app/db/seeds" ] && [ "${SEED_DB:-false}" = "true" ]; then
        echo "Running database seeds..."
        flyway -outOfOrder="true" -locations="filesystem:/app/db/seeds" migrate
    fi
fi

# Start the application
exec bin/backend "$@"
