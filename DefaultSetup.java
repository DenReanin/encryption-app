import java.security.KeyPair;

/**
 * Clase para inicializar datos por defecto de la aplicacion
 * Crea un usuario por defecto para facilitar el primer uso
 */
public class DefaultSetup {
    
    public static void createDefaultUser() {
        try {
            // Verificar si ya existe el usuario por defecto
            if (LocalStorage.userExists("usuario")) {
                System.out.println("Usuario por defecto ya existe.");
                return;
            }
            
            System.out.println("Creando usuario por defecto...");
            
            // Generar el par de claves RSA
            KeyPair keyPair = RSAUtil.generateKeyPair();

            // Convertir las claves a Base64 (sin cifrado adicional para simplificar)
            String publicKeyBase64 = RSAUtil.publicKeyToBase64(keyPair.getPublic());
            String privateKeyBase64 = RSAUtil.privateKeyToBase64(keyPair.getPrivate());

            // Generar hash de la contrasena del usuario
            String userPassword = "1234";
            String passwordHash = SimpleHash.hashPassword(userPassword);

            // Guardar en almacenamiento local
            LocalStorage.saveUser("usuario", passwordHash, publicKeyBase64, privateKeyBase64);
            
            System.out.println("Usuario por defecto creado exitosamente:");
            System.out.println("Usuario: usuario");
            System.out.println("Contrasena: 1234");
            System.out.println("Pregunta de seguridad: azul");
            
        } catch (Exception e) {
            System.err.println("Error creando usuario por defecto: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        createDefaultUser();
    }
}
