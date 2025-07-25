import java.security.MessageDigest;
import java.util.Base64;

/**
 * Implementacion simple de hashing para reemplazar bcrypt temporalmente
 * NOTA: En produccion se debe usar bcrypt real para mayor seguridad
 */
public class SimpleHash {
    private static final String SALT = "SeguridadCS2024";
    
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String saltedPassword = password + SALT;
            byte[] hash = md.digest(saltedPassword.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    public static boolean verifyPassword(String password, String hash) {
        String computedHash = hashPassword(password);
        return computedHash.equals(hash);
    }
}
