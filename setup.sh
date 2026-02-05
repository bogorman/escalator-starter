#!/bin/bash
set -e

# ============================================================================
# Escalator Project Setup
# ============================================================================
# Usage: ./setup.sh MyProjectName [com.mycompany]
#
# Creates a new Escalator project from this template.
# Run this AFTER creating repo from GitHub template.
# ============================================================================

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

info() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# Check arguments
if [ -z "$1" ]; then
    echo "Usage: ./setup.sh MyProjectName [com.mycompany]"
    echo ""
    echo "Arguments:"
    echo "  MyProjectName  - CamelCase project name (e.g., MyAwesomeApp)"
    echo "  com.mycompany  - Package name (default: com.<projectname lowercase>)"
    echo ""
    echo "Example:"
    echo "  ./setup.sh BetZilla com.betzilla"
    exit 1
fi

PROJECT_NAME="$1"
PROJECT_LOWER=$(echo "$PROJECT_NAME" | tr '[:upper:]' '[:lower:]')
PACKAGE_NAME="${2:-com.$PROJECT_LOWER}"

info "Setting up new Escalator project:"
info "  Project Name: $PROJECT_NAME"
info "  Package:      $PACKAGE_NAME"
info "  Database:     $PROJECT_LOWER"
echo ""

# Check for required tools
command -v gsed >/dev/null 2>&1 || error "gsed is required. Install with: brew install gnu-sed"
command -v ruby >/dev/null 2>&1 || error "ruby is required"
command -v psql >/dev/null 2>&1 || warn "psql not found - you'll need to create the database manually"

# Update rename_project.rb with actual values
info "Configuring rename script..."
gsed -i "s/NEW_PACKAGE = \"<CHANGE THIS>\"/NEW_PACKAGE = \"$PACKAGE_NAME\"/" rename_project.rb
gsed -i "s/NEW_APP = \"<CHANGE THIS>\"/NEW_APP = \"$PROJECT_NAME\"/" rename_project.rb

# Run the rename
info "Renaming project files and folders..."
ruby rename_project.rb

# Update source_local.sh with new database name
info "Updating source_local.sh..."
gsed -i "s/DB_NAME=\"escalatorstarter\"/DB_NAME=\"$PROJECT_LOWER\"/" source_local.sh

# Create database (optional - may fail if DB doesn't exist yet)
if command -v createdb >/dev/null 2>&1; then
    info "Creating database '$PROJECT_LOWER'..."
    source source_local.sh
    createdb -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" "$PROJECT_LOWER" 2>/dev/null || warn "Database may already exist or connection failed"
else
    warn "Skipping database creation - createdb not found"
    warn "Create the database manually: createdb $PROJECT_LOWER"
fi

# Clean up template files
info "Cleaning up template files..."
rm -f rename_project.rb
rm -f REFERENCES
rm -f TODO

# Update README
info "Updating README..."
cat > README.md << EOF
# $PROJECT_NAME

An Escalator-based full-stack Scala application.

## Quick Start

### Prerequisites
- JDK 21+ (GraalVM 25 recommended)
- Node.js 18+
- PostgreSQL
- sbt

### Development

\`\`\`bash
# Set environment variables
source source_local.sh

# Initialize database
sbt dbMigrate
sbt dbSeed
sbt dbGenerate

# Terminal 1 - Backend
sbt backend/run

# Terminal 2 - Frontend (watch mode)
sbt
project frontend
~fastLinkJS

# Terminal 3 - Webpack
npm install
npm start
\`\`\`

Open http://localhost:30190

### Docker Deployment

\`\`\`bash
cp .env.example .env
# Edit .env with your settings
docker compose up -d
\`\`\`

## Project Structure

- \`modules/backend/\` - Pekko HTTP server
- \`modules/frontend/\` - Laminar SPA
- \`modules/shared/\` - Shared models (JVM + JS)
- \`modules/persistence/\` - Database layer
- \`modules/core/\` - Business logic
- \`modules/db/\` - Migrations and seeds
EOF

# Initialize git if not already
if [ ! -d ".git" ]; then
    info "Initializing git repository..."
    git init
    git add -A
    git commit -m "Initial commit: $PROJECT_NAME from escalator-starter"
fi

echo ""
info "✅ Setup complete!"
echo ""
echo "Next steps:"
echo "  1. Update source_local.sh with your database credentials"
echo "  2. Run: source source_local.sh"
echo "  3. Run: sbt dbMigrate dbSeed dbGenerate"
echo "  4. Start developing!"
echo ""
echo "For Docker deployment, see README.md"
