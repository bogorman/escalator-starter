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
# Cache mounts persist sbt compilation output (target dirs) across builds for incremental compilation
RUN --mount=type=cache,target=/app/modules/escalator/target \
    --mount=type=cache,target=/app/modules/backend/target \
    --mount=type=cache,target=/app/modules/shared/target \
    --mount=type=cache,target=/app/modules/frontend/target \
    --mount=type=cache,target=/app/project/target \
    --mount=type=cache,target=/root/.sbt \
    --mount=type=cache,target=/root/.cache/coursier \
    sbt --error backend/stage && \
    cp -r /app/modules/backend/target/universal/stage /app/backend-stage

# Build the ScalaJS frontend
RUN --mount=type=cache,target=/app/modules/escalator/target \
    --mount=type=cache,target=/app/modules/backend/target \
    --mount=type=cache,target=/app/modules/shared/target \
    --mount=type=cache,target=/app/modules/frontend/target \
    --mount=type=cache,target=/app/project/target \
    --mount=type=cache,target=/root/.sbt \
    --mount=type=cache,target=/root/.cache/coursier \
    sbt --error frontend/fullLinkJS

# Install npm dependencies and build webpack
COPY package.json package-lock.json webpack.config.js tailwind.config.js postcss.config.js scala-version.js ./
RUN npm ci

COPY modules/frontend/src/main/static ./modules/frontend/src/main/static
ENV NODE_OPTIONS=--openssl-legacy-provider
# Webpack needs access to frontend target dir for the ScalaJS output
RUN --mount=type=cache,target=/app/modules/frontend/target \
    npm run build

# ----------------------------------------------------------------------------
# Stage 2: Runtime image (serves both API and static files)
# ----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

# Create non-root user with home directory and install curl + flyway
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r escalator && useradd -r -g escalator -m -d /home/escalator escalator

# Install Flyway and set permissions for escalator user
ENV FLYWAY_VERSION=10.6.0
RUN curl -fL https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/${FLYWAY_VERSION}/flyway-commandline-${FLYWAY_VERSION}-linux-x64.tar.gz \
    | tar xz -C /opt && \
    chmod -R a+rx /opt/flyway-${FLYWAY_VERSION} && \
    ln -s /opt/flyway-${FLYWAY_VERSION}/flyway /usr/local/bin/flyway

# Copy built artifacts
COPY --from=builder /app/backend-stage ./

# Copy static files to public directory (served by backend)
COPY --from=builder /app/dist ./public

# Copy database migrations and seeds
COPY modules/db/migration ./db/migration
COPY modules/db/seed ./db/seed

# Copy entrypoint script
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

# Set ownership
RUN chown -R escalator:escalator /app

USER escalator

# Expose backend port (serves both API and static files)
EXPOSE 30099

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -s -o /dev/null -w '%{http_code}' http://localhost:30099/ | grep -qE '^[2-5][0-9][0-9]$' || exit 1

# Default environment (override in compose.yaml or Coolify)
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Static files directory - backend serves files from here
ENV STATIC_DIR="/app/public"

# Run migrations then start the application
ENTRYPOINT ["./docker-entrypoint.sh"]
