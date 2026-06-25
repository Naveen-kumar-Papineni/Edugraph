#!/bin/bash
# EduGraph - Web Dashboard Runner
# Compiles the Java backend (API server) and starts it on port 8080.
cd "$(dirname "$0")"
mkdir -p out

echo "Compiling EduGraph API server..."
javac -d out -sourcepath src $(find src -name "*.java")
if [ $? -ne 0 ]; then
  echo "Compilation failed. Check errors above."
  exit 1
fi

PORT="${1:-8080}"
echo "Starting EduGraph web server on http://localhost:$PORT/ ..."
echo "(Press Ctrl+C to stop)"
echo ""
java -cp out api.ApiServer "$PORT"
