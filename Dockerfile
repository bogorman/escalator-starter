# ============================================================================
# Escalator Starter - Multi-stage Docker Build
# ============================================================================
# Build: docker build -t escalator-starter .
# Run:   docker compose up
# ============================================================================

# ----------------------------------------------------------------------------
# Stage 1: Build everything (Scala backend + ScalaJS frontend + webpack)
# ----------------------------------------------------------------------------
FROM ghcr.io/graalvm/jdk-community:25 AS builder

WORKDIR /app

# Install sbt
RUN microdnf install -y curl tar gzip && \
    curl -fL https://github.com/sbt/sbt/releases/download/v1.10.7/sbt-1.10.7.tgz | tar xz -C /usr/local && \
    ln -s /usr/local/sbt/bin/sbt /usr/local/bin/sbt

# Install Node.js for webpack
RUN curl -fsSL https://rpm.nodesource.com/setup_20.x | bash - && \
    microdnf install -y nodejs

# Cache sbt dependencies - copy only build definition files first
COPY build.sbt version.sbt ./
COPY project/build.properties project/plugins.sbt project/Common.scala project/Dependencies.scala project/Projects.scala project/Commands.scala project/JavaVersionCheck.scala project/R2Publishing.scala ./project/

# Pre-fetch sbt dependencies (this layer is cached unless build files change)
RUN sbt update

# Copy source code
COPY modules ./modules
COPY .scalafmt.conf ./

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
FROM ghcr.io/graalvm/jdk-community:25

WORKDIR /app

# Create non-root user
RUN microdnf install -y shadow-utils && \
    groupadd -r escalator && useradd -r -g escalator escalator

# Copy built artifacts
COPY --from=builder /app/modules/backend/target/universal/stage ./
COPY --from=builder /app/modules/frontend/src/main/static/public ./public

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
