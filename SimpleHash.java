import java.security.MessageDigest;
import java.util.Base64;

/**
 * Implementación simple de hashing para contraseñas usando SHA-256.
 * 
 * Esta clase proporciona funcionalidad básica de hash para contraseñas
 * utilizando SHA-256 con sal (salt) fija. Es una implementación temporal
 * y simplificada para propósitos educativos.
 * 
 * <p><strong>IMPORTANTE:</strong> En un entorno de producción se debe usar
 * una biblioteca especializada como bcrypt, scrypt o Argon2 que incluya
 * sal aleatoria y múltiples iteraciones para mayor seguridad.</p>
 * 
 * @author DenReanin
 * @version 1.0
 * @since 2025-07-25
 * 
 * @see MessageDigest
 * @see Base64
 */
public class SimpleHash {
    /** Sal fija utilizada para el hashing (en producción debe ser aleatoria) */
    private static final String SALT = "SeguridadCS2024";
    
    /**
     * Genera un hash SHA-256 de una contraseña con sal fija.
     * 
     * Combina la contraseña con una sal predefinida y genera un hash
     * SHA-256 codificado en Base64. Este método es determinístico,
     * siempre produce el mismo hash para la misma contraseña.
     * 
     * @param password contraseña en texto plano a hashear
     * @return hash SHA-256 de la contraseña en formato Base64
     * @throws RuntimeException si hay error en el proceso de hashing
     * 
     * @see MessageDigest#getInstance(String)
     */
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
    
    /**
     * Verifica si una contraseña coincide con un hash almacenado.
     * 
     * Hashea la contraseña proporcionada usando el mismo método que
     * hashPassword() y compara el resultado con el hash almacenado.
     * 
     * @param password contraseña en texto plano a verificar
     * @param hash hash almacenado con el que comparar
     * @return true si la contraseña coincide con el hash, false en caso contrario
     * 
     * @see #hashPassword(String)
     */
    public static boolean verifyPassword(String password, String hash) {
        String computedHash = hashPassword(password);
        return computedHash.equals(hash);
    }
}
