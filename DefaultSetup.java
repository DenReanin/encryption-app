import java.security.KeyPair;

/**
 * Clase para inicializar datos por defecto de la aplicación de cifrado.
 * 
 * Esta clase se encarga de crear un usuario por defecto para facilitar
 * el primer uso de la aplicación sin necesidad de registro manual.
 * 
 * @author DenReanin
 * @version 1.0
 * @since 2025-07-25
 */
public class DefaultSetup {
    
    /**
     * Crea un usuario por defecto si no existe previamente.
     * 
     * El usuario creado tiene las siguientes características:
     * - Nombre de usuario: "usuario"
     * - Contraseña: "1234" (hasheada con SimpleHash)
     * - Par de claves RSA generadas automáticamente
     * 
     * @throws Exception si ocurre un error durante la generación de claves
     *                   o el almacenamiento de datos
     * 
     * @see LocalStorage#userExists(String)
     * @see RSAUtil#generateKeyPair()
     * @see SimpleHash#hashPassword(String)
     */
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

            // Convertir las claves a Base64
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
    
    /**
     * Método principal que ejecuta la creación del usuario por defecto.
     * 
     * Este método permite ejecutar la clase de forma independiente
     * para inicializar los datos por defecto de la aplicación.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) { // Caja de herramientas - es obligatoria llevarla pero puede estar vacia, solo es util se se necesita.
        createDefaultUser();
    }
}