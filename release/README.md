# File Encryption Application

A secure file encryption application built with Java Swing that implements hybrid RSA + AES encryption.

## Features

- **Hybrid Encryption**: RSA 2048-bit + AES 128-bit
- **Modern UI**: Dark theme interface
- **Multi-User Support**: Public and private encryption modes
- **Local Storage**: No database dependencies
- **Cross-Platform**: Pure Java 8+ compatibility

## Quick Start

### Prerequisites
- Java 8 or higher

### Running the Application

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

## Default Credentials

- **Username:** `usuario`
- **Password:** `1234`
- **Security Question Answer:** `azul`

## How to Use

1. Launch the application
2. Login with default credentials or create a new user
3. Choose encryption mode:
   - **Public**: Files accessible to all users
   - **Private**: Files accessible only to specific user
4. Select files to encrypt/decrypt

## Architecture

- **FileEncryptionApp.java**: Main GUI application
- **RSAUtil.java**: RSA encryption utilities  
- **FileEncryptionUtil.java**: File encryption logic
- **LocalStorage.java**: Local data persistence
- **LoginDialog.java**: Authentication interface

## Security

- RSA 2048-bit key generation
- AES 128-bit file encryption
- SHA-256 password hashing
- Local file-based key storage

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Feel free to fork this project and submit pull requests.

---

**Note:** This is an educational project. For production use, consider additional security measures.
