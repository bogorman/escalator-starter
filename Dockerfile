# ============================================================================
# Escalator Starter - Multi-stage Docker Build
# ============================================================================
# Build: docker build -t escalator-starter .
# Run:   docker compose up
# ============================================================================

# ----------------------------------------------------------------------------
# Stage 1: Build frontend assets
# ----------------------------------------------------------------------------
FROM node:20-alpine AS frontend-builder

WORKDIR /app

# Install frontend dependencies
COPY package.json yarn.lock ./
RUN yarn install --frozen-lockfile

# Copy frontend source and build
COPY webpack.config.js tailwind.config.js postcss.config.js ./
COPY modules/frontend/src/main/static ./modules/frontend/src/main/static

# Build frontend assets
ENV NODE_OPTIONS=--openssl-legacy-provider
RUN yarn build

# ----------------------------------------------------------------------------
# Stage 2: Build Scala backend
# ----------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk AS backend-builder

WORKDIR /app

# Install sbt
RUN apt-get update && apt-get install -y curl && \
    curl -fL https://github.com/sbt/sbt/releases/download/v1.10.7/sbt-1.10.7.tgz | tar xz -C /usr/local && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt && \
    rm -rf /var/lib/apt/lists/*

# Cache dependencies - copy only build definition files first
COPY build.sbt version.sbt ./
COPY project/build.properties project/plugins.sbt project/Common.scala project/Dependencies.scala project/Projects.scala project/Commands.scala project/JavaVersionCheck.scala ./project/

# Pre-fetch dependencies (this layer is cached unless build files change)
RUN sbt update

# Copy source code
COPY modules ./modules
COPY .scalafmt.conf ./

# Build the backend
RUN sbt backend/stage

# ----------------------------------------------------------------------------
# Stage 3: Runtime image
# ----------------------------------------------------------------------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create non-root user
RUN groupadd -r escalator && useradd -r -g escalator escalator

# Copy built artifacts
COPY --from=backend-builder /app/modules/backend/target/universal/stage ./
COPY --from=frontend-builder /app/modules/frontend/src/main/static/public ./public

# Set ownership
RUN chown -R escalator:escalator /app

USER escalator

# Expose backend port
EXPOSE 30099

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:30099/health || exit 1

# Default environment (override in compose.yaml or Coolify)
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["bin/backend"]
