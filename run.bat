@echo off
echo Compiling and running File Encryption Application...
javac *.java
if %errorlevel% equ 0 (
    java FileEncryptionApp
) else (
    echo Compilation failed!
    pause
)
pause
