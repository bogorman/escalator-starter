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
ENV SBT_OPTS="-Xmx2g --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.lang.invoke=ALL-UNNAMED"

# Cache sbt dependencies - copy only build definition files first
COPY build.sbt version.sbt ./
COPY project/build.properties project/plugins.sbt project/*.scala ./project/

# Pre-fetch sbt dependencies (this layer is cached unless build files change)
RUN sbt update

# Copy source code
COPY modules ./modules
COPY .scalafmt.conf ./

# Clone escalator submodule (git submodules don't work with COPY)
# Remove any existing directory first (may exist from COPY if checked in)
RUN rm -rf modules/escalator && git clone --depth 1 https://github.com/bogorman/escalator.git modules/escalator

# Build the backend (creates distribution)
RUN sbt backend/stage

# Build the ScalaJS frontend
RUN sbt frontend/fullLinkJS

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

# Cache bust v2
# Create non-root user with home directory and install curl for healthcheck
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/* && \
    groupadd -r escalator && useradd -r -g escalator -m -d /home/escalator escalator

# Copy built artifacts
COPY --from=builder /app/modules/backend/target/universal/stage ./
COPY --from=builder /app/dist ./public

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

# Run the application
ENTRYPOINT ["bin/backend"]

# ----------------------------------------------------------------------------
# Stage 3: Nginx for serving static files + proxying to backend
# ----------------------------------------------------------------------------
FROM nginx:alpine AS nginx

# Remove default config
RUN rm /etc/nginx/conf.d/default.conf

# Copy nginx config
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

# Copy built static files from builder
# Copy built static files from builder (webpack outputs to dist/)
COPY --from=builder /app/dist /usr/share/nginx/html

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD wget -q --spider http://localhost/nginx-health || exit 1

EXPOSE 80
