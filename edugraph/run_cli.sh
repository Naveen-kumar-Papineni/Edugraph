#!/bin/bash
# EduGraph - Dynamic Interactive Runner
cd "$(dirname "$0")"
mkdir -p out
echo "Compiling EduGraph..."
javac -d out -sourcepath src $(find src -name "*.java")
if [ $? -ne 0 ]; then
  echo "Compilation failed. Check errors above."
  exit 1
fi
echo "Starting EduGraph..."
echo ""
java -cp out main.Main
