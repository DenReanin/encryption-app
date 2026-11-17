import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad de hashing seguro para contraseñas usando PBKDF2WithHmacSHA256.
 *
 * Genera sal aleatoria de 128 bits por cada contraseña y aplica 65.536
 * iteraciones de PBKDF2-HMAC-SHA256. Mantiene compatibilidad hacia atrás en
 * {@link #verifyPassword(String, String)} con hashes legados SHA-256.
 *
 * @author DenReanin
 * @version 2.0
 * @since 2025-07-25
 */
public class SimpleHash {
    private static final String PBKDF2_PREFIX = "pbkdf2";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH_BITS = 256;
    private static final int SALT_BYTES = 16;
    private static final String LEGACY_SALT = "SeguridadCS2024";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Genera un hash PBKDF2WithHmacSHA256 con sal aleatoria.
     *
     * Formato devuelto: {@code pbkdf2$65536$<saltBase64>$<hashBase64>}.
     */
    public static String hashPassword(String password) {
        try {
            byte[] salt = new byte[SALT_BYTES];
            RANDOM.nextBytes(salt);
            byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS);
            Base64.Encoder enc = Base64.getEncoder();
            return PBKDF2_PREFIX + "$" + ITERATIONS + "$" + enc.encodeToString(salt) + "$" + enc.encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifica una contraseña contra hashes PBKDF2 modernos o SHA-256 legados
     * usando comparación en tiempo constante.
     */
    public static boolean verifyPassword(String password, String storedHash) {
        if (password == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }
        try {
            if (storedHash.startsWith(PBKDF2_PREFIX + "$")) {
                String[] parts = storedHash.split("\\$");
                if (parts.length != 4) {
                    return false;
                }
                int iters = Integer.parseInt(parts[1]);
                byte[] salt = Base64.getDecoder().decode(parts[2]);
                byte[] expected = Base64.getDecoder().decode(parts[3]);
                byte[] actual = pbkdf2(password.toCharArray(), salt, iters, expected.length * 8);
                return MessageDigest.isEqual(expected, actual);
            }
            byte[] legacyExpected = Base64.getDecoder().decode(storedHash);
            byte[] legacyActual = legacySha256(password);
            return MessageDigest.isEqual(legacyExpected, legacyActual);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Genera el formato legado SHA-256 (solo para tests de migración).
     */
    public static String legacyHashForTesting(String password) {
        try {
            return Base64.getEncoder().encodeToString(legacySha256(password));
        } catch (Exception e) {
            throw new RuntimeException("Error hashing legacy password", e);
        }
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLengthBits) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLengthBits);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return skf.generateSecret(spec).getEncoded();
        } finally {
            spec.clearPassword();
        }
    }

    private static byte[] legacySha256(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String saltedPassword = password + LEGACY_SALT;
        return md.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
    }
}
