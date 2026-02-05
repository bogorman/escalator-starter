#!/bin/bash
# ============================================================================
# Publish local ivy artifacts to maven.escalator.dev (Cloudflare R2)
# ============================================================================
# Usage: ./publish-to-r2.sh <org> <name> <version> [scala-version]
#
# Example: 
#   ./publish-to-r2.sh org.scalablytyped lightweight-charts_sjs1_2.13 5.0.9-dea001
#   ./publish-to-r2.sh com.github.pjfanning pekko-http-circe 0.0.0_1-2d3e5833-SNAPSHOT 2.13
#
# Requires: AWS CLI configured with R2 credentials
#   export AWS_ACCESS_KEY_ID=your_r2_access_key
#   export AWS_SECRET_ACCESS_KEY=your_r2_secret_key
#   export R2_ACCOUNT_ID=your_cloudflare_account_id
# ============================================================================

set -e

ORG="$1"
NAME="$2"
VERSION="$3"
SCALA_VERSION="${4:-2.13}"

if [ -z "$ORG" ] || [ -z "$NAME" ] || [ -z "$VERSION" ]; then
    echo "Usage: $0 <org> <name> <version> [scala-version]"
    echo ""
    echo "Examples:"
    echo "  $0 org.scalablytyped lightweight-charts_sjs1_2.13 5.0.9-dea001"
    echo "  $0 com.github.pjfanning pekko-http-circe_2.13 0.0.0_1-2d3e5833-SNAPSHOT"
    exit 1
fi

# Check R2 credentials
if [ -z "$R2_ACCOUNT_ID" ] || [ -z "$AWS_ACCESS_KEY_ID" ] || [ -z "$AWS_SECRET_ACCESS_KEY" ]; then
    echo "Error: R2 credentials not set"
    echo "  export R2_ACCOUNT_ID=xxx"
    echo "  export AWS_ACCESS_KEY_ID=xxx"
    echo "  export AWS_SECRET_ACCESS_KEY=xxx"
    exit 1
fi

# R2 endpoint
R2_ENDPOINT="https://${R2_ACCOUNT_ID}.r2.cloudflarestorage.com"
BUCKET="scalajs-artifacts"

# Convert org to path (com.example -> com/example)
ORG_PATH=$(echo "$ORG" | tr '.' '/')

# Ivy local cache paths to search
IVY_LOCAL="$HOME/.ivy2/local"
IVY_CACHE="$HOME/.ivy2/cache"

# Find the artifact
ARTIFACT_PATH=""
for BASE in "$IVY_LOCAL" "$IVY_CACHE"; do
    CANDIDATE="$BASE/$ORG/$NAME/$VERSION"
    if [ -d "$CANDIDATE" ]; then
        ARTIFACT_PATH="$CANDIDATE"
        echo "Found artifact at: $ARTIFACT_PATH"
        break
    fi
done

if [ -z "$ARTIFACT_PATH" ]; then
    echo "Error: Artifact not found in ivy cache"
    echo "  Searched: $IVY_LOCAL/$ORG/$NAME/$VERSION"
    echo "  Searched: $IVY_CACHE/$ORG/$NAME/$VERSION"
    exit 1
fi

# Maven destination path
MAVEN_PATH="releases/$ORG_PATH/$NAME/$VERSION"

echo "Publishing to: s3://$BUCKET/$MAVEN_PATH"
echo ""

# Upload files
upload_file() {
    local src="$1"
    local dest="$2"
    echo "  Uploading: $(basename "$src") -> $dest"
    aws s3 cp "$src" "s3://$BUCKET/$dest" \
        --endpoint-url "$R2_ENDPOINT" \
        --quiet
}

# Find and upload jars
if [ -d "$ARTIFACT_PATH/jars" ]; then
    for jar in "$ARTIFACT_PATH/jars"/*.jar; do
        if [ -f "$jar" ]; then
            BASENAME=$(basename "$jar")
            upload_file "$jar" "$MAVEN_PATH/$BASENAME"
        fi
    done
fi

# Find and upload srcs
if [ -d "$ARTIFACT_PATH/srcs" ]; then
    for src in "$ARTIFACT_PATH/srcs"/*.jar; do
        if [ -f "$src" ]; then
            BASENAME=$(basename "$src")
            upload_file "$src" "$MAVEN_PATH/$BASENAME"
        fi
    done
fi

# Find and upload docs
if [ -d "$ARTIFACT_PATH/docs" ]; then
    for doc in "$ARTIFACT_PATH/docs"/*.jar; do
        if [ -f "$doc" ]; then
            BASENAME=$(basename "$doc")
            upload_file "$doc" "$MAVEN_PATH/$BASENAME"
        fi
    done
fi

# Convert ivy.xml to pom.xml (basic conversion)
IVY_XML="$ARTIFACT_PATH/ivys/ivy.xml"
if [ -f "$IVY_XML" ]; then
    POM_FILE="/tmp/${NAME}-${VERSION}.pom"
    
    # Extract basic info from ivy.xml and create minimal pom
    cat > "$POM_FILE" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>$ORG</groupId>
    <artifactId>$NAME</artifactId>
    <version>$VERSION</version>
    <packaging>jar</packaging>
</project>
EOF
    upload_file "$POM_FILE" "$MAVEN_PATH/${NAME}-${VERSION}.pom"
    rm -f "$POM_FILE"
fi

echo ""
echo "✅ Published to https://maven.escalator.dev/$MAVEN_PATH/"
echo ""
echo "Add to build.sbt:"
echo "  \"$ORG\" %% \"${NAME%_$SCALA_VERSION}\" % \"$VERSION\""
