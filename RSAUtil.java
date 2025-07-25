import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Utilidad para operaciones criptográficas RSA.
 * 
 * Esta clase proporciona métodos para generar pares de claves RSA,
 * cifrar y descifrar datos, y convertir claves entre diferentes formatos
 * (objetos Java y Base64). Utiliza claves RSA de 2048 bits para garantizar
 * seguridad adecuada.
 * 
 * @author DenReanin
 * @version 1.0
 * @since 2025-07-25
 * 
 * @see KeyPairGenerator
 * @see Cipher
 * @see Base64
 */
public class RSAUtil {
    /** Tamaño de clave RSA en bits (2048 bits para seguridad moderna) */
    private static final int KEY_SIZE = 2048;
    
    /** Algoritmo de cifrado RSA */
    private static final String ALGORITHM = "RSA";

    /**
     * Genera un nuevo par de claves RSA (pública y privada).
     * 
     * Crea un par de claves RSA de 2048 bits usando un generador
     * criptográficamente seguro. Las claves generadas son adecuadas
     * para cifrado asimétrico y firma digital.
     * 
     * @return par de claves RSA (pública y privada)
     * @throws NoSuchAlgorithmException si el algoritmo RSA no está disponible
     * 
     * @see KeyPairGenerator#getInstance(String)
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Cifra datos usando una clave pública RSA.
     * 
     * Utiliza la clave pública RSA para cifrar los datos proporcionados.
     * Los datos cifrados solo pueden ser descifrados con la clave privada
     * correspondiente. Ideal para cifrar claves simétricas pequeñas.
     * 
     * @param data datos a cifrar (máximo ~245 bytes para RSA 2048)
     * @param publicKey clave pública RSA para cifrado
     * @return datos cifrados como array de bytes
     * @throws Exception si hay error en el cifrado o datos demasiado grandes
     * 
     * @see Cipher#doFinal(byte[])
     */
    public static byte[] encryptWithPublicKey(byte[] data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * Descifra datos usando una clave privada RSA.
     * 
     * Utiliza la clave privada RSA para descifrar datos que fueron
     * previamente cifrados con la clave pública correspondiente.
     * 
     * @param data datos cifrados a descifrar
     * @param privateKey clave privada RSA para descifrado
     * @return datos originales descifrados como array de bytes
     * @throws Exception si hay error en el descifrado o clave incorrecta
     * 
     * @see Cipher#doFinal(byte[])
     */
    public static byte[] decryptWithPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * Convierte una clave pública RSA a formato Base64.
     * 
     * Codifica la clave pública en formato Base64 para almacenamiento
     * seguro en archivos de texto o bases de datos.
     * 
     * @param publicKey clave pública RSA a convertir
     * @return representación Base64 de la clave pública
     * 
     * @see Base64.Encoder#encodeToString(byte[])
     */
    public static String publicKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Convierte una clave privada RSA a formato Base64.
     * 
     * Codifica la clave privada en formato Base64 para almacenamiento
     * seguro. La clave privada debe mantenerse confidencial.
     * 
     * @param privateKey clave privada RSA a convertir
     * @return representación Base64 de la clave privada
     * 
     * @see Base64.Encoder#encodeToString(byte[])
     */
    public static String privateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * Convierte una cadena Base64 a clave pública RSA.
     * 
     * Decodifica una representación Base64 y reconstruye la clave pública
     * RSA original usando la especificación X.509.
     * 
     * @param base64 representación Base64 de la clave pública
     * @return clave pública RSA reconstruida
     * @throws Exception si hay error en la decodificación o formato inválido
     * 
     * @see X509EncodedKeySpec
     * @see KeyFactory#generatePublic(java.security.spec.KeySpec)
     */
    public static PublicKey base64ToPublicKey(String base64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(spec);
    }

    /**
     * Convierte una cadena Base64 a clave privada RSA.
     * 
     * Decodifica una representación Base64 y reconstruye la clave privada
     * RSA original usando la especificación PKCS#8.
     * 
     * @param base64 representación Base64 de la clave privada
     * @return clave privada RSA reconstruida
     * @throws Exception si hay error en la decodificación o formato inválido
     * 
     * @see PKCS8EncodedKeySpec
     * @see KeyFactory#generatePrivate(java.security.spec.KeySpec)
     */
    public static PrivateKey base64ToPrivateKey(String base64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(spec);
    }

    /**
     * Convierte representaciones Base64 a un par de claves RSA completo.
     * 
     * Toma las representaciones Base64 de claves pública y privada
     * y las convierte en un objeto KeyPair funcional.
     * 
     * @param publicKeyBase64 representación Base64 de la clave pública
     * @param privateKeyBase64 representación Base64 de la clave privada
     * @return par de claves RSA reconstruido
     * @throws Exception si hay error en la conversión de cualquiera de las claves
     * 
     * @see #base64ToPublicKey(String)
     * @see #base64ToPrivateKey(String)
     */
    public static KeyPair base64ToKeyPair(String publicKeyBase64, String privateKeyBase64) throws Exception {
        PublicKey publicKey = base64ToPublicKey(publicKeyBase64);
        PrivateKey privateKey = base64ToPrivateKey(privateKeyBase64);
        return new KeyPair(publicKey, privateKey);
    }
}
