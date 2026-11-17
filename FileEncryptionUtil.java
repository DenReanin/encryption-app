import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * Utilidad para el cifrado y descifrado de archivos usando cifrado híbrido RSA-OAEP + AES-256-GCM.
 *
 * Cifra nuevos archivos con AES-256-GCM (IV aleatorio de 96 bits + etiqueta de autenticación
 * de 128 bits, cabecera "AGCM"), manteniendo compatibilidad hacia atrás al descifrar archivos
 * cifrados con el esquema AES clásico anterior.
 *
 * @author DenReanin
 * @version 2.0
 * @since 2025-07-25
 * @see RSAUtil
 * @see LocalStorage
 */
public class FileEncryptionUtil {
    private static final int AES_KEY_BITS = 256;
    private static final int GCM_IV_BYTES = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final byte[] GCM_MAGIC = "AGCM".getBytes(StandardCharsets.US_ASCII);
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Genera una clave AES-256 aleatoria criptográficamente segura.
     */
    public static Key generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_BITS, RANDOM);
        return keyGen.generateKey();
    }

    /**
     * Cifra unos bytes con AES-256-GCM generando un IV único de 12 bytes.
     */
    private static byte[] encryptAesGcm(byte[] plaintext, Key key) throws Exception {
        byte[] iv = new byte[GCM_IV_BYTES];
        RANDOM.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        byte[] ciphertext = cipher.doFinal(plaintext);

        byte[] out = new byte[GCM_MAGIC.length + GCM_IV_BYTES + ciphertext.length];
        System.arraycopy(GCM_MAGIC, 0, out, 0, GCM_MAGIC.length);
        System.arraycopy(iv, 0, out, GCM_MAGIC.length, GCM_IV_BYTES);
        System.arraycopy(ciphertext, 0, out, GCM_MAGIC.length + GCM_IV_BYTES, ciphertext.length);
        return out;
    }

    /**
     * Descifra archivos en formato AES-GCM ("AGCM") o en formato legado AES/ECB.
     */
    private static byte[] decryptAesCompat(byte[] inputBytes, Key key) throws Exception {
        if (inputBytes.length > GCM_MAGIC.length + GCM_IV_BYTES
                && Arrays.equals(Arrays.copyOfRange(inputBytes, 0, GCM_MAGIC.length), GCM_MAGIC)) {
            byte[] iv = Arrays.copyOfRange(inputBytes, GCM_MAGIC.length, GCM_MAGIC.length + GCM_IV_BYTES);
            byte[] ciphertext = Arrays.copyOfRange(inputBytes, GCM_MAGIC.length + GCM_IV_BYTES, inputBytes.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            return cipher.doFinal(ciphertext);
        }
        Cipher legacyCipher = Cipher.getInstance("AES");
        legacyCipher.init(Cipher.DECRYPT_MODE, key);
        return legacyCipher.doFinal(inputBytes);
    }

    /**
     * Guarda una clave AES cifrada en el almacenamiento local usando RSA-OAEP.
     */
    public static void saveKey(String filename, Key key, PublicKey publicKey) throws Exception {
        byte[] encryptedKey = RSAUtil.encryptWithPublicKey(key.getEncoded(), publicKey);
        String encryptedKeyBase64 = Base64.getEncoder().encodeToString(encryptedKey);
        LocalStorage.saveKey(filename, encryptedKeyBase64);
    }

    /**
     * Recupera y descifra una clave AES desde el almacenamiento local.
     */
    public static Key getKey(String filename, PrivateKey privateKey) throws Exception {
        String keyFilename = filename.endsWith(".enc") ? filename.substring(0, filename.length() - 4) : filename;

        String encryptedKeyBase64 = LocalStorage.getKey(keyFilename);
        if (encryptedKeyBase64 != null) {
            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBase64);
            byte[] decryptedKeyBytes = RSAUtil.decryptWithPrivateKey(encryptedKeyBytes, privateKey);
            return new SecretKeySpec(decryptedKeyBytes, "AES");
        }
        throw new IllegalArgumentException("No se encontro ninguna clave para el archivo: " + filename);
    }

    /**
     * Elimina una clave del almacenamiento local después de descifrar un archivo.
     */
    public static void deleteKey(String filename) throws Exception {
        String keyFilename = filename.endsWith(".enc") ? filename.substring(0, filename.length() - 4) : filename;
        LocalStorage.deleteKey(keyFilename);
    }

    /**
     * Cifra un archivo usando AES-256-GCM y elimina el archivo original.
     */
    public static void encryptFile(File inputFile, File outputFile, Key key) throws Exception {
        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());
        byte[] outputBytes = encryptAesGcm(inputBytes, key);

        File parentDirectory = outputFile.getParentFile();
        if (parentDirectory != null && !parentDirectory.exists()) {
            parentDirectory.mkdirs();
        }

        Files.write(outputFile.toPath(), outputBytes);
        Files.delete(inputFile.toPath());
    }

    /**
     * Cifra un archivo para un usuario específico usando AES-256-GCM en su carpeta privada.
     */
    public static void encryptFilePrivate(File inputFile, File outputFile, Key key, String username) throws Exception {
        File directory = new File(System.getProperty("user.home") + "/Documents/cs/encriptado_privado/" + username);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());
        byte[] outputBytes = encryptAesGcm(inputBytes, key);

        Path outputPath = Paths.get(directory.getPath(), outputFile.getName());
        Files.write(outputPath, outputBytes);
        Files.delete(inputFile.toPath());
    }

    /**
     * Descifra un archivo (AES-GCM o legado) y lo guarda en la carpeta estándar de descifrado.
     */
    public static void decryptFile(File inputFile, File outputFile, Key key) throws Exception {
        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());
        byte[] outputBytes = decryptAesCompat(inputBytes, key);

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
     * Descifra un archivo privado específico de un usuario (AES-GCM o legado).
     */
    public static void decryptFilePrivate(File inputFile, File outputFile, PrivateKey privateKey, String username) throws Exception {
        Key aesKey = getKey(inputFile.getName(), privateKey);

        byte[] inputBytes = Files.readAllBytes(inputFile.toPath());
        byte[] outputBytes = decryptAesCompat(inputBytes, aesKey);

        File directory = new File(System.getProperty("user.home") + "/Documents/cs/desencriptado_privado/" + username);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Path outputPath = Paths.get(directory.getPath(), outputFile.getName());
        Files.write(outputPath, outputBytes);

        deleteKey(inputFile.getName());
        Files.delete(inputFile.toPath());
    }

    @Deprecated
    public static void saveAdmin(String username, String passwordHash, PublicKey publicKey, byte[] encryptedPrivateKey) throws Exception {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String encryptedPrivateKeyBase64 = Base64.getEncoder().encodeToString(encryptedPrivateKey);
        LocalStorage.saveAdmin(username, passwordHash, publicKeyBase64, encryptedPrivateKeyBase64);
    }

    public static void savePrivateKey(String username, String filename, Key aesKey, PublicKey userPublicKey) throws Exception {
        byte[] encryptedAESKey = RSAUtil.encryptWithPublicKey(aesKey.getEncoded(), userPublicKey);
        String encryptedKeyBase64 = Base64.getEncoder().encodeToString(encryptedAESKey);
        LocalStorage.savePrivateKey(username, filename, encryptedKeyBase64);
    }

    public static Key getPrivateKey(String username, String filename, PrivateKey userPrivateKey) throws Exception {
        String keyFilename = filename.endsWith(".enc") ? filename.substring(0, filename.length() - 4) : filename;

        String encryptedKeyBase64 = LocalStorage.getPrivateKey(username, keyFilename);
        if (encryptedKeyBase64 != null) {
            byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyBase64);
            byte[] decryptedKeyBytes = RSAUtil.decryptWithPrivateKey(encryptedKeyBytes, userPrivateKey);
            return new SecretKeySpec(decryptedKeyBytes, "AES");
        }
        throw new IllegalArgumentException("No se encontro ninguna clave privada para el usuario " + username + " y archivo: " + filename);
    }
}
