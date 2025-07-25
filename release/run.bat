@echo off
echo Compiling and running File Encryption Application...
javac *.java
if %errorlevel% equ 0 (
    @echo off
title Encryption App - DenReanin
echo.
echo  ========================================
echo   🔐 ENCRYPTION APP v1.0 - DenReanin
echo  ========================================
echo.
echo  Iniciando aplicación...
echo.

REM Verificar si Java está instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo  ❌ ERROR: Java no está instalado o no está en el PATH
    echo.
    echo  Por favor instala Java desde:
    echo  https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

REM Verificar si existe el archivo JAR
if not exist "EncryptionApp.jar" (
    echo  ❌ ERROR: No se encontró EncryptionApp.jar
    echo  Asegúrate de que todos los archivos estén en la misma carpeta
    echo.
    pause
    exit /b 1
)

echo  ✅ Java detectado
echo  ✅ Archivo JAR encontrado
echo  🚀 Ejecutando Encryption App...
echo.

REM Ejecutar la aplicación
java -jar EncryptionApp.jar

REM Si la aplicación se cierra inesperadamente
if %errorlevel% neq 0 (
    echo.
    echo  ⚠️  La aplicación se cerró con errores
    echo  Código de error: %errorlevel%
    echo.
    pause
)
) else (
    echo Compilation failed!
    pause
)
pause
