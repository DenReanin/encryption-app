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

/**
 * Utilidad para el cifrado y descifrado de archivos usando cifrado híbrido RSA+AES.
 * 
 * Esta clase proporciona métodos para el cifrado y descifrado tanto público como privado
 * de archivos, utilizando una combinación de algoritmos RSA para proteger las claves AES
 * y AES para el cifrado simétrico de los archivos. Incluye funcionalidades para la
 * gestión de claves, almacenamiento seguro y operaciones de cifrado para múltiples usuarios.
 * 
 * El sistema implementa:
 * - Cifrado público: Archivos accesibles por cualquier usuario del sistema
 * - Cifrado privado: Archivos específicos para usuarios individuales
 * - Gestión automática de directorios de cifrado y descifrado
 * - Limpieza automática de archivos originales después del cifrado
 * 
 * @author DenReanin
 * @version 1.0
 * @since 2025-07-25
 * @see RSAUtil
 * @see LocalStorage
 */
public class FileEncryptionUtil {

    /**
     * Genera una clave AES aleatoria para el cifrado simétrico de archivos.
     * 
     * Utiliza el algoritmo AES con una longitud de clave de 128 bits y un generador
     * de números aleatorios seguro para crear una clave criptográficamente fuerte.
     * 
     * @return Clave AES generada aleatoriamente
     * @throws Exception Si ocurre un error durante la generación de la clave
     * @see KeyGenerator
     * @see SecureRandom
     */
    public static Key generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128, new SecureRandom());
        return keyGen.generateKey();
    }

    /**
     * Guarda una clave AES cifrada en el almacenamiento local usando RSA.
     * 
     * Este método cifra la clave AES con la clave pública RSA proporcionada,
     * la codifica en Base64 y la almacena usando el sistema de almacenamiento local.
     * La clave cifrada queda asociada con el nombre del archivo especificado.
     * 
     * @param filename Nombre del archivo al que se asociará la clave
     * @param key Clave AES a cifrar y almacenar
     * @param publicKey Clave pública RSA para cifrar la clave AES
     * @throws Exception Si ocurre un error durante el cifrado o almacenamiento
     * @see RSAUtil#encryptWithPublicKey(byte[], PublicKey)
     * @see LocalStorage#saveKey(String, String)
     */
    public static void saveKey(String filename, Key key, PublicKey publicKey) throws Exception {
        byte[] encryptedKey = RSAUtil.encryptWithPublicKey(key.getEncoded(), publicKey);
        String encryptedKeyBase64 = Base64.getEncoder().encodeToString(encryptedKey);
        LocalStorage.saveKey(filename, encryptedKeyBase64);
    }

    /**
     * Recupera y descifra una clave AES desde el almacenamiento local.
     * 
     * Busca la clave AES cifrada asociada al archivo especificado, la descifra
     * usando la clave privada RSA y devuelve la clave AES reconstruida. Si el
     * nombre del archivo termina en ".enc", se elimina esta extensión para
     * buscar la clave original.
     * 
     * @param filename Nombre del archivo cuya clave se desea recuperar
     * @param privateKey Clave privada RSA para descifrar la clave AES
     * @return Clave AES descifrada y reconstruida
     * @throws Exception Si no se encuentra la clave o ocurre un error durante el descifrado
     * @throws IllegalArgumentException Si no se encuentra ninguna clave para el archivo
     * @see LocalStorage#getKey(String)
     * @see RSAUtil#decryptWithPrivateKey(byte[], PrivateKey)
     */
    public static Key getKey(String filename, PrivateKey privateKey) throws Exception {
        String keyFilename = filename.endsWith(".enc") ? filename.substring(0, filename.length() - 4) : filename;

        String encryptedKeyBase64 = LocalStorage.getKey(keyFilename);
        if (encryptedKeyBase64 != null) {
            System.out.println("Buscando clave para: " + keyFilename);
            System.out.println("Encrypted Key (Base64): " + encryptedKeyBase64);

            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBase64);
            byte[] decryptedKeyBytes = RSAUtil.decryptWithPrivateKey(encryptedKeyBytes, privateKey);
            return new SecretKeySpec(decryptedKeyBytes, "AES");
        } else {
            System.out.println("No se encontro clave para: " + keyFilename);
            throw new IllegalArgumentException("No se encontro ninguna clave para el archivo: " + filename);
        }
    }

    /**
     * Elimina una clave del almacenamiento local después de descifrar un archivo.
     * 
     * Remueve permanentemente la clave AES asociada al archivo especificado del
     * sistema de almacenamiento local. Si el nombre del archivo termina en ".enc",
     * se elimina esta extensión para localizar la clave correcta.
     * 
     * @param filename Nombre del archivo cuya clave se desea eliminar
     * @throws Exception Si ocurre un error durante la eliminación
     * @see LocalStorage#deleteKey(String)
     */
    public static void deleteKey(String filename) throws Exception {
        String keyFilename = filename.endsWith(".enc") ? filename.substring(0, filename.length() - 4) : filename;
        LocalStorage.deleteKey(keyFilename);
    }

    /**
     * Cifra un archivo usando AES y lo guarda en la ubicación especificada.
     * 
     * Este método lee completamente el archivo de entrada, lo cifra usando la clave AES
     * proporcionada, y escribe el resultado cifrado al archivo de salida. El archivo
     * original se elimina después del cifrado exitoso. Si el directorio de salida no
     * existe, se crea automáticamente.
     * 
     * @param inputFile Archivo original a cifrar
     * @param outputFile Archivo de destino para guardar el contenido cifrado
     * @param key Clave AES para el cifrado
     * @throws Exception Si ocurre un error durante el cifrado, lectura o escritura
     * @see Cipher
     * @see Files#readAllBytes(Path)
     * @see Files#write(Path, byte[])
     */
    public static void encryptFile(File inputFile, File outputFile, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());
        byte[] outputBytes = cipher.doFinal(inputBytes);

        File parentDirectory = outputFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }

        Files.write(outputFile.toPath(), outputBytes);
        Files.delete(inputFile.toPath());
    }

    /**
     * Cifra un archivo para un usuario específico y lo guarda en su carpeta privada.
     * 
     * Este método cifra el archivo usando AES y lo almacena en un directorio específico
     * del usuario dentro de la estructura de carpetas de cifrado privado. El directorio
     * se crea automáticamente si no existe. El archivo original se elimina después del
     * cifrado exitoso.
     * 
     * @param inputFile Archivo original a cifrar
     * @param outputFile Archivo de destino para guardar el contenido cifrado
     * @param key Clave AES para el cifrado
     * @param username Nombre del usuario propietario del archivo cifrado
     * @throws Exception Si ocurre un error durante el cifrado, lectura o escritura
     * @see #encryptFile(File, File, Key)
     */
    public static void encryptFilePrivate(File inputFile, File outputFile, Key key, String username) throws Exception {
        File directory = new File(System.getProperty("user.home") + "/Documents/cs/encriptado_privado/" + username);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());
        byte[] outputBytes = cipher.doFinal(inputBytes);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path outputPath = Paths.get(directory.getPath(), outputFile.getName());
        Files.write(outputPath, outputBytes);
        Files.delete(inputFile.toPath());
    }

    /**
     * Descifra un archivo y lo guarda en la carpeta de archivos descifrados.
     * 
     * Este método descifra un archivo cifrado usando la clave AES proporcionada
     * y guarda el resultado en el directorio estándar de descifrado. Después del
     * descifrado exitoso, elimina tanto la clave del almacenamiento como el archivo
     * cifrado original.
     * 
     * @param inputFile Archivo cifrado a descifrar
     * @param outputFile Archivo de destino para guardar el contenido descifrado
     * @param key Clave AES para el descifrado
     * @throws Exception Si ocurre un error durante el descifrado, lectura o escritura
     * @see #deleteKey(String)
     */
    public static void decryptFile(File inputFile, File outputFile, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());
        byte[] outputBytes = cipher.doFinal(inputBytes);

        File directory = new File(System.getProperty("user.home") + "/Documents/cs/desencriptado");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path outputPath = Paths.get(directory.getPath(), outputFile.getName());
        Files.write(outputPath, outputBytes);

        deleteKey(inputFile.getName());
        Files.delete(inputFile.toPath());
    }

    /**
     * Descifra un archivo privado específico de un usuario.
     * 
     * Este método recupera la clave AES del archivo cifrado privado usando la clave
     * privada del usuario, descifra el archivo y lo guarda en el directorio de
     * descifrado privado específico del usuario. Después del descifrado exitoso,
     * elimina la clave del almacenamiento y el archivo cifrado original.
     * 
     * @param inputFile Archivo cifrado privado a descifrar
     * @param outputFile Archivo de destino para guardar el contenido descifrado
     * @param privateKey Clave privada del usuario para descifrar la clave AES
     * @param username Nombre del usuario propietario del archivo
     * @throws Exception Si ocurre un error durante el descifrado o si no se encuentra la clave
     * @see #getKey(String, PrivateKey)
     * @see #deleteKey(String)
     */
    public static void decryptFilePrivate(File inputFile, File outputFile, PrivateKey privateKey, String username) throws Exception {
        Key aesKey = getKey(inputFile.getName(), privateKey);

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);

        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());
        byte[] outputBytes = cipher.doFinal(inputBytes);

        File directory = new File(System.getProperty("user.home") + "/Documents/cs/desencriptado_privado/" + username);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path outputPath = Paths.get(directory.getPath(), outputFile.getName());
        Files.write(outputPath, outputBytes);

        deleteKey(inputFile.getName());
        Files.delete(inputFile.toPath());
    }

    /**
     * Guarda un administrador en el almacenamiento local con sus claves RSA.
     * 
     * Este método almacena la información completa de un administrador incluyendo
     * su hash de contraseña y sus claves RSA (pública y privada cifrada) en el
     * sistema de almacenamiento local. Las claves se codifican en Base64 antes
     * del almacenamiento.
     * 
     * @param username Nombre de usuario del administrador
     * @param passwordHash Hash de la contraseña del administrador
     * @param publicKey Clave pública RSA del administrador
     * @param encryptedPrivateKey Clave privada RSA cifrada del administrador
     * @throws Exception Si ocurre un error durante el almacenamiento
     * @see LocalStorage#saveAdmin(String, String, String, String)
     * @deprecated Este método utiliza una funcionalidad obsoleta de LocalStorage
     */
    @Deprecated
    public static void saveAdmin(String username, String passwordHash, PublicKey publicKey, byte[] encryptedPrivateKey) throws Exception {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String encryptedPrivateKeyBase64 = Base64.getEncoder().encodeToString(encryptedPrivateKey);
        
        LocalStorage.saveAdmin(username, passwordHash, publicKeyBase64, encryptedPrivateKeyBase64);
    }

    // ===== MÉTODOS PARA CIFRADO PRIVADO =====

    /**
     * Guarda una clave AES cifrada para un usuario específico (cifrado privado).
     * 
     * Este método cifra la clave AES proporcionada usando la clave pública RSA del usuario
     * específico y almacena la clave cifrada en el sistema de almacenamiento local asociada
     * al usuario y archivo correspondientes. Esto permite que solo el usuario propietario
     * pueda descifrar posteriormente el archivo.
     * 
     * @param username Nombre del usuario propietario del archivo
     * @param filename Nombre del archivo al que se asociará la clave
     * @param aesKey Clave AES a cifrar y almacenar
     * @param userPublicKey Clave pública RSA del usuario para cifrar la clave AES
     * @throws Exception Si ocurre un error durante el cifrado o almacenamiento
     * @see RSAUtil#encryptWithPublicKey(byte[], PublicKey)
     * @see LocalStorage#savePrivateKey(String, String, String)
     */
    public static void savePrivateKey(String username, String filename, Key aesKey, PublicKey userPublicKey) throws Exception {
        byte[] encryptedAESKey = RSAUtil.encryptWithPublicKey(aesKey.getEncoded(), userPublicKey);
        String encryptedKeyBase64 = Base64.getEncoder().encodeToString(encryptedAESKey);
        LocalStorage.savePrivateKey(username, filename, encryptedKeyBase64);
    }

    /**
     * Obtiene una clave AES descifrada para un usuario específico (cifrado privado).
     * 
     * Este método recupera la clave AES cifrada asociada al usuario y archivo específicos
     * desde el almacenamiento local, la descifra usando la clave privada RSA del usuario
     * y devuelve la clave AES reconstruida. Si el nombre del archivo termina en ".enc",
     * se elimina esta extensión para buscar la clave original.
     * 
     * @param username Nombre del usuario propietario del archivo
     * @param filename Nombre del archivo cuya clave se desea recuperar
     * @param userPrivateKey Clave privada RSA del usuario para descifrar la clave AES
     * @return Clave AES descifrada y reconstruida
     * @throws Exception Si no se encuentra la clave o ocurre un error durante el descifrado
     * @throws IllegalArgumentException Si no se encuentra la clave privada para el usuario y archivo
     * @see LocalStorage#getPrivateKey(String, String)
     * @see RSAUtil#decryptWithPrivateKey(byte[], PrivateKey)
     */
    public static Key getPrivateKey(String username, String filename, PrivateKey userPrivateKey) throws Exception {
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

