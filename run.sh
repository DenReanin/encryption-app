#!/bin/bash

# Encryption App v1.0 - DenReanin
# Script de ejecución para Linux/macOS

echo ""
echo "=========================================="
echo "  ENCRYPTION APP v1.0 - DenReanin"
echo "=========================================="
echo ""
echo "Iniciando aplicación..."
echo ""

# Verificar si Java está instalado
if ! command -v java &> /dev/null; then
    echo "ERROR: Java no está instalado"
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
if [ -f "EncryptionApp.jar" ]; then
    echo "JAR encontrado, ejecutando..."
    java -jar EncryptionApp.jar
    exit $?
fi

echo "JAR no encontrado, compilando código fuente..."
echo ""

# Verificar si javac está disponible
if ! command -v javac &> /dev/null; then
    echo "ERROR: javac no está instalado (necesitas JDK)"
    echo "Instala el JDK: sudo apt install default-jdk"
    echo ""
    read -p "Presiona Enter para continuar..."
    exit 1
fi

echo "Compilando archivos..."
javac FileEncryptionApp.java RSAUtil.java FileEncryptionUtil.java LocalStorage.java UserAuth.java SimpleHash.java LoginDialog.java RoundedBorder.java DefaultSetup.java 2>/dev/null

# Verificar si hubo errores de compilación
if [ $? -ne 0 ]; then
    echo "ERROR: Falló la compilación"
    echo "Verifica que todos los archivos .java estén presentes"
    echo ""
    read -p "Presiona Enter para continuar..."
    exit 1
fi

echo "Compilación exitosa!"
echo "Ejecutando aplicación..."
echo ""

java FileEncryptionApp

# Verificar si hubo errores
if [ $? -ne 0 ]; then
    echo ""
    echo "La aplicación se cerró con errores"
    echo "Código de error: $?"
    echo ""
    read -p "Presiona Enter para continuar..."
fi
