import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;


public class FileEncryptionUtil {

    // Genera una clave para el cifrado
    public static Key generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");  // Utiliza el algoritmo AES
        keyGen.init(128, new SecureRandom());  // Inicializa el generador de claves con una longitud de clave de 128 bits
        return keyGen.generateKey();  // Genera y devuelve la clave
    }

    // Guarda la clave en almacenamiento local
    public static void saveKey(String filename, Key key, PublicKey publicKey) throws Exception {
        // Cifra la clave AES con la clave publica RSA
        byte[] encryptedKey = RSAUtil.encryptWithPublicKey(key.getEncoded(), publicKey);

        // Convierte la clave cifrada a Base64
        String encryptedKeyBase64 = Base64.getEncoder().encodeToString(encryptedKey);

        // Guarda en almacenamiento local
        LocalStorage.saveKey(filename, encryptedKeyBase64);
    }

    public static Key getKey(String filename, PrivateKey privateKey) throws Exception {
        // Si el archivo termina en .enc, quitar esa extension para buscar la clave original
        String keyFilename = filename.endsWith(".enc") ? filename.substring(0, filename.length() - 4) : filename;

        String encryptedKeyBase64 = LocalStorage.getKey(keyFilename);
        if (encryptedKeyBase64 != null) {
            System.out.println("Buscando clave para: " + keyFilename); // Depuracion
            System.out.println("Encrypted Key (Base64): " + encryptedKeyBase64); // Depuracion

            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBase64);
            byte[] decryptedKeyBytes = RSAUtil.decryptWithPrivateKey(encryptedKeyBytes, privateKey);
            return new SecretKeySpec(decryptedKeyBytes, "AES");
        } else {
            System.out.println("No se encontro clave para: " + keyFilename); // Depuracion
            throw new IllegalArgumentException("No se encontro ninguna clave para el archivo: " + filename);
        }
    }

    // Elimina la clave del almacenamiento local despues de descifrar un archivo
    public static void deleteKey(String filename) throws Exception {
        String keyFilename = filename.endsWith(".enc") ? filename.substring(0, filename.length() - 4) : filename;
        LocalStorage.deleteKey(keyFilename);
    }

    // Cifra el archivo y lo guarda en la ubicacion especificada
    public static void encryptFile(File inputFile, File outputFile, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");  // Obtiene una instancia del cifrador AES
        cipher.init(Cipher.ENCRYPT_MODE, key);  // Inicializa el cifrador en modo de cifrado con la clave

        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());  // Lee todos los bytes del archivo de entrada
        byte[] outputBytes = cipher.doFinal(inputBytes);  // Cifra los bytes del archivo de entrada

        // Crear el directorio padre del archivo de salida si no existe
        File parentDirectory = outputFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();  // Crea el directorio si no existe
        }

        Files.write(outputFile.toPath(), outputBytes);  // Escribe los bytes cifrados en el archivo de salida especificado

        Files.delete(inputFile.toPath());  // Borra el archivo original despues de cifrarlo
    }

    // Cifra el archivo y lo guarda en una carpeta especifica para cifrado privado
    public static void encryptFilePrivate(File inputFile, File outputFile, Key key, String username) throws Exception {
        File directory = new File(System.getProperty("user.home") + "/Documents/cs/encriptado_privado/" + username);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        Cipher cipher = Cipher.getInstance("AES");  // Obtiene una instancia del cifrador AES
        cipher.init(Cipher.ENCRYPT_MODE, key);  // Inicializa el cifrador en modo de cifrado con la clave

        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());  // Lee todos los bytes del archivo de entrada
        byte[] outputBytes = cipher.doFinal(inputBytes);  // Cifra los bytes del archivo de entrada

        if (!directory.exists()) {
            directory.mkdirs();  // Crea el directorio si no existe
        }

        Path outputPath = Paths.get(directory.getPath(), outputFile.getName());
        Files.write(outputPath, outputBytes);  // Escribe los bytes cifrados en el archivo de salida

        Files.delete(inputFile.toPath());  // Borra el archivo original despues de cifrarlo
    }

    // Descifra el archivo y lo guarda en una carpeta especifica
    public static void decryptFile(File inputFile, File outputFile, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");  // Obtiene una instancia del cifrador AES
        cipher.init(Cipher.DECRYPT_MODE, key);  // Inicializa el cifrador en modo de descifrado con la clave

        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());  // Lee todos los bytes del archivo cifrado
        byte[] outputBytes = cipher.doFinal(inputBytes);  // Descifra los bytes del archivo cifrado

        File directory = new File(System.getProperty("user.home") + "/Documents/cs/desencriptado");  // Define el directorio donde se guardara el archivo descifrado
        if (!directory.exists()) {
            directory.mkdirs();  // Crea el directorio si no existe
        }

        Path outputPath = Paths.get(directory.getPath(), outputFile.getName());
        Files.write(outputPath, outputBytes);  // Escribe los bytes descifrados en el archivo de salida

        deleteKey(inputFile.getName());  // Elimina la clave del almacenamiento despues de descifrar el archivo

        Files.delete(inputFile.toPath());  // Borra el archivo cifrado despues de descifrarlo
    }

    public static void decryptFilePrivate(File inputFile, File outputFile, PrivateKey privateKey, String username) throws Exception {
        // Recupera la clave AES cifrada para este archivo y usuario especifico
        Key aesKey = getKey(inputFile.getName(), privateKey);

        // Inicializa el descifrado con la clave AES
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);

        // Lee el archivo cifrado
        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());

        // Descifra el archivo
        byte[] outputBytes = cipher.doFinal(inputBytes);

        // Define el directorio de salida para el archivo descifrado
        File directory = new File(System.getProperty("user.home") + "/Documents/cs/desencriptado_privado/" + username);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Guarda el archivo descifrado
        Path outputPath = Paths.get(directory.getPath(), outputFile.getName());
        Files.write(outputPath, outputBytes);

        // Elimina la clave del almacenamiento si es necesario
        deleteKey(inputFile.getName());

        Files.delete(inputFile.toPath());
    }

    // Metodo para guardar administrador en almacenamiento local
    public static void saveAdmin(String username, String passwordHash, PublicKey publicKey, byte[] encryptedPrivateKey) throws Exception {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String encryptedPrivateKeyBase64 = Base64.getEncoder().encodeToString(encryptedPrivateKey);
        
        LocalStorage.saveAdmin(username, passwordHash, publicKeyBase64, encryptedPrivateKeyBase64);
    }

    // ===== METODOS PARA CIFRADO PRIVADO =====

    /**
     * Guarda una clave AES cifrada para un usuario especifico (cifrado privado)
     */
    public static void savePrivateKey(String username, String filename, Key aesKey, PublicKey userPublicKey) throws Exception {
        byte[] encryptedAESKey = RSAUtil.encryptWithPublicKey(aesKey.getEncoded(), userPublicKey);
        String encryptedKeyBase64 = Base64.getEncoder().encodeToString(encryptedAESKey);
        LocalStorage.savePrivateKey(username, filename, encryptedKeyBase64);
    }

    /**
     * Obtiene una clave AES descifrada para un usuario especifico (cifrado privado)
     */
    public static Key getPrivateKey(String username, String filename, PrivateKey userPrivateKey) throws Exception {
        // Si el archivo termina en .enc, quitar esa extension para buscar la clave original
        String keyFilename = filename.endsWith(".enc") ? filename.substring(0, filename.length() - 4) : filename;

        String encryptedKeyBase64 = LocalStorage.getPrivateKey(username, keyFilename);
        if (encryptedKeyBase64 != null) {
            System.out.println("Buscando clave privada para usuario " + username + ": " + keyFilename);

            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBase64);
            byte[] decryptedKeyBytes = RSAUtil.decryptWithPrivateKey(encryptedKeyBytes, userPrivateKey);
            return new SecretKeySpec(decryptedKeyBytes, "AES");
        } else {
            System.out.println("No se encontro clave privada para usuario " + username + ": " + keyFilename);
            throw new IllegalArgumentException("No se encontro ninguna clave privada para el usuario " + username + " y archivo: " + filename);
        }
    }
}

