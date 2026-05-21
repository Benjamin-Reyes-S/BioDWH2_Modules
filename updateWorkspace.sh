#!/usr/bin/env bash
set -euo pipefail

BIO_DWH2_HOME="/home/benjamin.reyes/git/BioDWH2"
POM_FILE="$BIO_DWH2_HOME/src/pom.xml"
WORKSPACE="$BIO_DWH2_HOME/workspace"

NEW_VERSION="${1:-0.68d5}"

BIO_DWH2_JAR="$BIO_DWH2_HOME/src/biodwh2-main/target/BioDWH2-v${NEW_VERSION}.jar"
NEO4J_SERVER_JAR="/home/benjamin.reyes/server_data/ncbi_mikrobiome_kg/BioDWH2/BioDWH2-Neo4j-Server/BioDWH2-Neo4j-Server-v1.3.2.jar"

echo "Using BioDWH2 version: $NEW_VERSION"

mvn -f "$POM_FILE" versions:set -DnewVersion="$NEW_VERSION"
mvn -f "$POM_FILE" clean package -DskipTests

if [[ ! -f "$BIO_DWH2_JAR" ]]; then
  echo "ERROR: BioDWH2 jar not found:"
  echo "$BIO_DWH2_JAR"
  exit 1
fi

if [[ ! -f "$NEO4J_SERVER_JAR" ]]; then
  echo "ERROR: Neo4j server jar not found:"
  echo "$NEO4J_SERVER_JAR"
  exit 1
fi

java -jar "$BIO_DWH2_JAR" -u "$WORKSPACE"
java -jar "$NEO4J_SERVER_JAR" --start "$WORKSPACE"