#!/bin/bash

set -e

echo "[1/4] Force-killing any Zombie containers..."
docker rm -f sensor-broker warehouse-service || true

echo "[2/4] Cleaning up networks and volumes..."
docker compose -f infra/docker-compose.yml down --remove-orphans

echo "[3/4] Building project with Gradle..."
./gradlew clean build test jacocoTestReport -x javadoc

echo "[4/4] Starting Docker Infrastructure..."
docker compose -f infra/docker-compose.yml up -d --build

echo "[5/5] Tailing logs (Press Ctrl+C to stop watching)..."
docker compose -f infra/docker-compose.yml logs -f
