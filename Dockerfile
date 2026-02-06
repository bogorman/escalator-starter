# ============================================================================
# Escalator Starter - Multi-stage Docker Build
# ============================================================================
# Build: docker build -t escalator-starter .
# Run:   docker compose up
# ============================================================================

# ----------------------------------------------------------------------------
# Stage 1: Build everything (Scala backend + ScalaJS frontend + webpack)
# ----------------------------------------------------------------------------
# Note: Using Temurin instead of GraalVM due to x86-64-v3 CPU requirement on GraalVM 25
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Install sbt
RUN apt-get update && apt-get install -y curl && \
    curl -fL https://github.com/sbt/sbt/releases/download/v1.10.7/sbt-1.10.7.tgz | tar xz -C /usr/local && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt && \
    rm -rf /var/lib/apt/lists/*

# Install Node.js for webpack
RUN curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs

# Install git for submodule clone
RUN apt-get update && apt-get install -y git && rm -rf /var/lib/apt/lists/*

# Java 21 module opens for Quill/Kryo serialization during macro expansion
# Increase memory and add all required module opens
ENV SBT_OPTS="-Xmx8g -Xms4g -Xss512m --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED --add-exports jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED"

# Cache sbt dependencies - copy only build definition files first
COPY build.sbt version.sbt ./
COPY project/build.properties project/plugins.sbt project/*.scala ./project/

# Pre-fetch sbt dependencies (this layer is cached unless build files change)
RUN sbt --error update

# Copy source code
COPY modules ./modules
COPY .scalafmt.conf ./

# Clone escalator submodule (git submodules don't work with COPY)
# Remove any existing directory first (may exist from COPY if checked in)
RUN rm -rf modules/escalator && git clone --depth 1 https://github.com/bogorman/escalator.git modules/escalator

# Build the backend (creates distribution) - use --error to suppress warnings
RUN sbt --error backend/stage

# Build the ScalaJS frontend
RUN sbt --error frontend/fullLinkJS

# Install npm dependencies and build webpack
COPY package.json package-lock.json webpack.config.js tailwind.config.js postcss.config.js scala-version.js ./
RUN npm ci

COPY modules/frontend/src/main/static ./modules/frontend/src/main/static
ENV NODE_OPTIONS=--openssl-legacy-provider
RUN npm run build

# ----------------------------------------------------------------------------
# Stage 2: Runtime image
# ----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Create non-root user with home directory and install curl + flyway
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r escalator && useradd -r -g escalator -m -d /home/escalator escalator

# Install Flyway for database migrations
ARG FLYWAY_VERSION=10.10.0
RUN curl -fsSL https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/${FLYWAY_VERSION}/flyway-commandline-${FLYWAY_VERSION}-linux-x64.tar.gz | tar xz -C /opt && \
    ln -s /opt/flyway-${FLYWAY_VERSION}/flyway /usr/local/bin/flyway

# Copy built artifacts
COPY --from=builder /app/modules/backend/target/universal/stage ./
COPY --from=builder /app/dist ./public

# Copy database migrations and seeds (if they exist)
COPY db/migrations ./db/migrations/ 2>/dev/null || true
COPY db/seeds ./db/seeds/ 2>/dev/null || true

# Copy entrypoint script
COPY docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

# Set ownership
RUN chown -R escalator:escalator /app

USER escalator

# Expose backend port
EXPOSE 30099

# Health check
# Healthcheck: verify HTTP server responds (404 is OK, means server is up)
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -s -o /dev/null -w '%{http_code}' http://localhost:30099/ | grep -qE '^[2-5][0-9][0-9]$' || exit 1

# Default environment (override in compose.yaml or Coolify)
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application (entrypoint handles migrations)
ENTRYPOINT ["/app/docker-entrypoint.sh"]

# ----------------------------------------------------------------------------
# Stage 3: Nginx for serving static files + proxying to backend
# ----------------------------------------------------------------------------
FROM nginx:alpine AS nginx

# Remove default config
RUN rm /etc/nginx/conf.d/default.conf

# Copy nginx config
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

# Copy built static files from builder (webpack outputs to dist/)
COPY --from=builder /app/dist /usr/share/nginx/html

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -q --spider http://localhost/nginx-health || exit 1

EXPOSE 80
