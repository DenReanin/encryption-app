#!/bin/bash

# Encryption App v1.0 - DenReanin
# Script de ejecución para Linux/macOS

echo ""
echo "=========================================="
echo "  🔐 ENCRYPTION APP v1.0 - DenReanin"
echo "=========================================="
echo ""
echo "Iniciando aplicación..."
echo ""

# Verificar si Java está instalado
if ! command -v java &> /dev/null; then
    echo "❌ ERROR: Java no está instalado"
    echo ""
    echo "Por favor instala Java desde:"
    echo "https://www.oracle.com/java/technologies/downloads/"
    echo ""
    echo "En Ubuntu/Debian: sudo apt install default-jre"
    echo "En macOS: brew install openjdk"
    echo ""
    read -p "Presiona Enter para continuar..."
    exit 1
fi

# Verificar si existe el archivo JAR
if [ ! -f "EncryptionApp.jar" ]; then
    echo "❌ ERROR: No se encontró EncryptionApp.jar"
    echo "Asegúrate de que todos los archivos estén en la misma carpeta"
    echo ""
    read -p "Presiona Enter para continuar..."
    exit 1
fi

echo "✅ Java detectado: $(java -version 2>&1 | head -n 1)"
echo "✅ Archivo JAR encontrado"
echo "🚀 Ejecutando Encryption App..."
echo ""

# Ejecutar la aplicación
java -jar EncryptionApp.jar

# Verificar si hubo errores
if [ $? -ne 0 ]; then
    echo ""
    echo "⚠️  La aplicación se cerró con errores"
    echo "Código de error: $?"
    echo ""
    read -p "Presiona Enter para continuar..."
fi
