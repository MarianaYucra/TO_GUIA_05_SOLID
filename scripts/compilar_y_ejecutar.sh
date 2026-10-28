#!/usr/bin/env bash
# Uso: ./scripts/compilar_y_ejecutar.sh [1-6]
set -e
cd "$(dirname "$0")/.."
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp out com.unsa.hotel.Main "$@"
