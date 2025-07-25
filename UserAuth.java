/**
 * Clase para autenticación de usuarios del sistema de cifrado.
 * 
 * Esta clase proporciona funcionalidades básicas de autenticación mediante
 * verificación de contraseñas hasheadas. Se utiliza como capa de seguridad
 * para validar el acceso de usuarios a funcionalidades específicas del sistema.
 * 
 * @author DenReanin
 * @version 1.0
 * @since 2025-07-25
 * 
 * @see SimpleHash
 * @see LocalStorage
 */
public class UserAuth {

    /**
     * Autentica un usuario verificando su contraseña contra el hash almacenado.
     * 
     * Este método busca el hash de contraseña almacenado para el usuario especificado
     * y lo compara con la contraseña proporcionada utilizando el sistema de hash
     * implementado en SimpleHash.
     * 
     * @param username nombre del usuario a autenticar
     * @param password contraseña en texto plano a verificar
     * @return true si la autenticación es exitosa, false en caso contrario
     * 
     * @see LocalStorage#getUserPasswordHash(String)
     * @see SimpleHash#verifyPassword(String, String)
     */
    public static boolean authenticate(String username, String password) {
        try {
            String storedHash = LocalStorage.getUserPasswordHash(username);
            if (storedHash != null) {
                return SimpleHash.verifyPassword(password, storedHash);
            }
        } catch (Exception e) {
            e.printStackTrace();  // Manejo adecuado de la excepcion
        }
        return false;
    }
}
