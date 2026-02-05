# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Quick Start

### Docker (Recommended for deployment)
```bash
cp .env.example .env
# Edit .env with your secrets
docker compose up -d
```

### Local Development
See "Development Commands" below.

## Project Overview

This is an eSCALAtor starter project - an opinionated full-stack Scala application with the following tech stack:
- **Backend**: Scala 2.13, Pekko (formerly Akka) + Pekko HTTP
- **Frontend**: Laminar (Scala.js reactive UI library)
- **Database**: PostgreSQL with Quill for query generation
- **Build Tool**: SBT with cross-compilation support for shared code

## Build Commands

### Database Operations
```bash
source source_local.sh                  # Set DB environment variables
sbt dbMigrate                           # Run database migrations (from modules/db/migration)
sbt dbSeed                              # Seed database with initial data (from modules/db/seed)
sbt dbGenerate                          # Generate Scala code from database schema
sbt dbReset                             # Drop and reset database
sbt rmGenerated                         # Remove auto-generated database files
```

### Development Commands
```bash
# Terminal 1 - Backend server
./compile
sbt backend/run

# Terminal 2 - Frontend compilation (watch mode)
sbt
project frontend
~fastLinkJS

# Terminal 3 - Webpack dev server
export NODE_OPTIONS=--openssl-legacy-provider
npm install
npm start
```

### Code Formatting
```bash
sbt scalafmtAll                         # Format all Scala code using Scalafmt
```

## Architecture

### Module Structure
- **modules/escalator/** - Core eSCALAtor framework (symlinked dependency)
- **modules/common/** - Shared JVM utilities and persistence abstractions
- **modules/shared-common/** - Cross-platform shared models and validators
- **modules/shared/** - Application-specific shared models (User, Attribute, etc.)
- **modules/persistence/** - Database access layer with Quill-based tables
- **modules/core/** - Core business logic, repositories, email, and background workers
- **modules/backend/** - HTTP server with Pekko HTTP, routes, controllers, auth
- **modules/frontend/** - Laminar-based SPA with routing and components
- **modules/db/** - Database migrations, seeds, and code generation

### Key Patterns

**Database Code Generation**: The project uses automatic code generation from database schema:
- Migration files define schema in `modules/db/migration/`
- `sbt dbGenerate` creates models in `modules/shared/src/main/scala/com/escalatorstarter/models/`
- Database access code generated in `modules/persistence/src/main/scala/postgres/tables/`
- Custom repository methods go in `modules/core/src/main/scala/com/escalatorstarter/core/repositories/`
- Generated files contain "THIS FILE IS AUTO-GENERATED" header - do not edit

**Cross-Platform Code Sharing**: Shared models between frontend and backend using Scala.js cross-compilation
- SharedCommon and Shared modules compile to both JVM and JS
- Models use serialization via Circe for API communication

**Authentication**: Session-based authentication with secure cookies
- Login/registration flow in frontend
- Session management in `modules/backend/.../auth/session/`

## Publishing Artifacts

Shared Scala.js facades and libraries are published to `maven.escalator.dev` (Cloudflare R2).

### Publishing a library
```bash
# Set R2 credentials (get from Cloudflare Dashboard → R2 → API Tokens)
export R2_ACCOUNT_ID=xxx
export R2_ACCESS_KEY_ID=xxx
export R2_SECRET_ACCESS_KEY=xxx

# Publish
sbt <project>/publish
```

### Consuming artifacts
Dependencies from maven.escalator.dev are auto-resolved (resolver already configured in Dependencies.scala).

## Important Configuration Files

- `source_local.sh` - Database connection environment variables (update before running)
- `modules/backend/src/main/resources/application.conf` - Backend configuration
- `.scalafmt.conf` - Scala formatting rules
- `modules/db/generators/src/main/scala/Codegen.scala` - Configure package name and app name for code generation

## Development Notes

- Frontend runs on http://localhost:30190
- Backend API runs on port 30099
- The project recently migrated from Akka to Pekko (Apache Pekko)
- Uses Flyway for database migrations
- Kamon integration for monitoring
- Bootstrap V5 templates in `modules/frontend/src/main/static/public/`