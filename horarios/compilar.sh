#!/bin/bash
echo "Compilando proyecto..."
mkdir -p out
find src -name "*.java" > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
if [ $? -eq 0 ]; then
    echo "Compilacion exitosa!"
    echo "Ejecutando..."
    java -cp out Main
else
    echo "Error de compilacion."
fi
