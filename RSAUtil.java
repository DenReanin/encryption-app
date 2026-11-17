import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Utilidad para operaciones criptográficas RSA con relleno seguro OAEP-SHA256.
 *
 * Mantiene compatibilidad hacia atrás en {@link #decryptWithPrivateKey(byte[], PrivateKey)}
 * con claves cifradas previamente usando RSA PKCS#1 v1.5.
 *
 * @author DenReanin
 * @version 2.0
 * @since 2025-07-25
 */
public class RSAUtil {
    private static final int KEY_SIZE = 2048;
    private static final String ALGORITHM = "RSA";
    private static final String OAEP_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    /**
     * Genera un nuevo par de claves RSA de 2048 bits.
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Cifra datos usando una clave pública RSA con esquema OAEP-SHA256.
     */
    public static byte[] encryptWithPublicKey(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(OAEP_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * Descifra datos usando una clave privada RSA (OAEP-SHA256 con fallback a RSA legado).
     */
    public static byte[] decryptWithPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
        try {
            Cipher oaepCipher = Cipher.getInstance(OAEP_TRANSFORMATION);
            oaepCipher.init(Cipher.DECRYPT_MODE, privateKey);
            return oaepCipher.doFinal(data);
        } catch (GeneralSecurityException oaepEx) {
            Cipher legacyCipher = Cipher.getInstance(ALGORITHM);
            legacyCipher.init(Cipher.DECRYPT_MODE, privateKey);
            return legacyCipher.doFinal(data);
        }
    }

    public static String publicKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static String privateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    public static PublicKey base64ToPublicKey(String base64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    public static PrivateKey base64ToPrivateKey(String base64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    public static KeyPair base64ToKeyPair(String publicKeyBase64, String privateKeyBase64) throws Exception {
        PublicKey publicKey = base64ToPublicKey(publicKeyBase64);
        PrivateKey privateKey = base64ToPrivateKey(privateKeyBase64);
        return new KeyPair(publicKey, privateKey);
    }
}
