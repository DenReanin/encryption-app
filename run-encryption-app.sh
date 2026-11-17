#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"

if [ ! -f "FileEncryptionApp.jar" ]; then
  echo "[Encryption App] Compilando FileEncryptionApp.jar..."
  java BuildApp.java
fi

if [ "${1:-}" = "--setup-only" ]; then
  echo "[Encryption App] JAR compilado correctamente."
  exit 0
fi

if [ "${1:-}" = "--test" ]; then
  exec java -cp build TestEncryptionApp
fi

if [ -z "${DISPLAY:-}${WAYLAND_DISPLAY:-}" ]; then
  echo "[Encryption App] Sin servidor gráfico DISPLAY/WAYLAND_DISPLAY. Ejecutando verificación criptográfica:"
  exec java -cp build TestEncryptionApp
fi

exec java -jar FileEncryptionApp.jar
