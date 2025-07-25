#!/bin/bash
echo "Compiling and running File Encryption Application..."
javac *.java
if [ $? -eq 0 ]; then
    java FileEncryptionApp
else
    echo "Compilation failed!"
fi
