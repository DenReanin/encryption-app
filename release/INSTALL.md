# 🔐 Encryption App - Guía de Instalación

## Requisitos del Sistema
- **Java 8 o superior** instalado en tu sistema
- **Windows, macOS o Linux**
- Mínimo 50 MB de espacio libre

## 📥 Instalación Rápida

### Windows:
1. Descomprime el archivo ZIP
2. Ejecuta `run.bat` 
   O bien:
   ```cmd
   java -jar EncryptionApp.jar
   ```

### macOS / Linux:
1. Descomprime el archivo ZIP
2. Dale permisos de ejecución al script:
   ```bash
   chmod +x run.sh
   ```
3. Ejecuta la aplicación:
   ```bash
   ./run.sh
   ```
   O bien:
   ```bash
   java -jar EncryptionApp.jar
   ```

## 🚀 Primer Uso

1. **Crear Usuario**: Al iniciar por primera vez, crea un nuevo usuario
2. **Generar Claves**: El sistema generará automáticamente tus claves RSA
3. **Cifrar Archivos**: Selecciona archivos para cifrar con seguridad híbrida
4. **Descifrar**: Usa tus claves para recuperar archivos cifrados

## 🔧 Solución de Problemas

### Error: "No se puede ejecutar Java"
- Verifica que Java esté instalado: `java -version`
- Descarga Java desde: https://www.oracle.com/java/technologies/downloads/

### Error: "Archivo no encontrado"
- Asegúrate de que todos los archivos estén en la misma carpeta
- Verifica que EncryptionApp.jar no esté corrupto

### Problemas de Permisos
- En Linux/Mac: `chmod +x run.sh`
- En Windows: Ejecuta como administrador si es necesario

## 📝 Archivos Incluidos

- `EncryptionApp.jar` - Aplicación principal
- `run.bat` - Script de ejecución para Windows  
- `run.sh` - Script de ejecución para Linux/Mac
- `README.md` - Documentación del proyecto
- `LICENSE` - Términos de licencia
- `INSTALL.md` - Este archivo de instalación

## 🔗 Enlaces Útiles

- **Código Fuente**: https://github.com/DenReanin/encryption-app
- **Documentación Web**: https://denreanin.github.io/encryption-app/
- **Reportar Problemas**: https://github.com/DenReanin/encryption-app/issues

## 💡 Consejos de Seguridad

1. **Backup de Claves**: Guarda una copia de seguridad de tus claves
2. **Contraseñas Fuertes**: Usa contraseñas robustas para tus usuarios
3. **Archivos Sensibles**: No compartas tus claves privadas
4. **Verificación**: Prueba el descifrado antes de eliminar originales

---
**Desarrollado por DenReanin** | **Versión 1.0** | **Java + RSA + AES**
