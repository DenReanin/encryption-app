# Aplicación de Cifrado de Archivos

Una aplicación segura de cifrado de archivos desarrollada con Java Swing que implementa cifrado híbrido RSA + AES.

## Contexto Académico

Este proyecto fue desarrollado como parte de la asignatura **COMPRESIÓN Y SEGURIDAD** del **Grado en Ingeniería Multimedia** en la **Universidad de Alicante (UA)**. 

El proyecto original se ha expandido y mejorado significativamente más allá de los requisitos académicos iniciales, añadiendo funcionalidades avanzadas de GUI, documentación profesional y distribución multiplataforma.

## Características

- **Cifrado Híbrido**: RSA 2048-bit + AES 128-bit
- **Interfaz Moderna**: Tema oscuro y diseño intuitivo
- **Soporte Multi-Usuario**: Modos de cifrado público y privado
- **Almacenamiento Local**: Sin dependencias de base de datos
- **Multiplataforma**: Compatibilidad pura con Java 8+

## Inicio Rápido

### Requisitos Previos
- Java 8 o superior

### Ejecutar la Aplicación

**Windows:**
```cmd
run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

**Manual:**
```bash
javac *.java
java FileEncryptionApp
```

## Credenciales por Defecto

- **Usuario:** `usuario`
- **Contraseña:** `1234`
- **Respuesta Pregunta de Seguridad:** `azul`

## Cómo Usar

1. Ejecutar la aplicación
2. Iniciar sesión con las credenciales por defecto o crear un nuevo usuario
3. Elegir modo de cifrado:
   - **Público**: Archivos accesibles para todos los usuarios
   - **Privado**: Archivos accesibles solo para el usuario específico
4. Seleccionar archivos para cifrar/descifrar

## Arquitectura

- **FileEncryptionApp.java**: Aplicación GUI principal
- **RSAUtil.java**: Utilidades de cifrado RSA  
- **FileEncryptionUtil.java**: Lógica de cifrado de archivos
- **LocalStorage.java**: Persistencia de datos local
- **LoginDialog.java**: Interfaz de autenticación

## Seguridad

- Generación de claves RSA de 2048-bit
- Cifrado de archivos AES de 128-bit
- Hash de contraseñas SHA-256
- Almacenamiento local de claves basado en archivos

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - consulta el archivo [LICENSE](LICENSE) para más detalles.

## Contribuciones

Siéntete libre de hacer fork de este proyecto y enviar pull requests.

## AI Assistance & Attribution

Partes de este proyecto utilizaron asistencia de IA:

### **Desarrollo**
- **Código principal**: 100% humano, revisado y retocado con GitHub Copilot
- **Documentación Javadoc**: Generada con asistencia de GitHub Copilot
- **Sitio web**: Creado con herramientas de IA (HTML/CSS/JS)
- **Formato de archivos**: README y documentación mejorados con IA

### **Autoría**
- **Algoritmos y lógica**: [@DenReanin](https://github.com/DenReanin)
- **Arquitectura de seguridad**: [@DenReanin](https://github.com/DenReanin)
- **Presentación y documentación**: Asistencia de IA

---

**Nota:** Este es un proyecto educativo. Para uso en producción, considera medidas de seguridad adicionales.
