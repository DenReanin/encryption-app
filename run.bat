@echo off
title Encryption App - DenReanin
echo.
echo  ========================================
echo   ENCRYPTION APP v1.0 - DenReanin
echo  ========================================
echo.
echo  Iniciando aplicacion...
echo.

REM Verificar si Java esta instalado
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo  ERROR: Java no esta instalado o no esta en el PATH
    echo.
    echo  Por favor instala Java desde:
    echo  https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

REM Verificar si existe el archivo JAR
if exist "EncryptionApp.jar" (
    echo  JAR encontrado, ejecutando...
    java -jar EncryptionApp.jar
    goto end
)

echo  JAR no encontrado, compilando codigo fuente...
echo.

REM Compilar archivos Java individualmente
echo  Compilando archivos...
javac FileEncryptionApp.java RSAUtil.java FileEncryptionUtil.java LocalStorage.java UserAuth.java SimpleHash.java LoginDialog.java RoundedBorder.java DefaultSetup.java 2>nul

if %errorlevel% neq 0 (
    echo  ERROR: Fallo la compilacion
    echo  Verifica que todos los archivos .java esten presentes
    echo.
    pause
    exit /b 1
)

echo  Compilacion exitosa!
echo  Ejecutando aplicacion...
echo.

java FileEncryptionApp

:end
if %errorlevel% neq 0 (
    echo.
    echo  La aplicacion se cerro con errores
    echo  Codigo de error: %errorlevel%
    echo.
)
pause
