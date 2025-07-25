import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Clase para manejar el almacenamiento local de datos usando archivos Properties.
 * 
 * Esta clase reemplaza las funcionalidades de una base de datos MySQL,
 * proporcionando almacenamiento persistente para usuarios, administradores,
 * claves de cifrado y configuraciones del sistema. Utiliza archivos .properties
 * para mantener la simplicidad y portabilidad del sistema.
 * 
 * <p>Estructura de archivos de almacenamiento:</p>
 * <ul>
 *   <li><strong>admins.properties</strong> - Información de administradores del sistema</li>
 *   <li><strong>users.properties</strong> - Datos de usuarios y claves del sistema</li>
 *   <li><strong>keys.properties</strong> - Claves de cifrado público</li>
 *   <li><strong>private_keys_[usuario].properties</strong> - Claves privadas por usuario</li>
 * </ul>
 * 
 * <p>Todas las contraseñas se almacenan como hashes SHA-256 y las claves RSA
 * se guardan en formato Base64 para facilitar su persistencia.</p>
 * 
 * @author DenReanin
 * @version 1.0
 * @since 2025-07-25
 * 
 * @see Properties
 * @see Files
 * @see SimpleHash
 */
public class LocalStorage {
    /** Directorio base para almacenamiento de todos los datos de la aplicación */
    private static final String DATA_DIR = System.getProperty("user.home") + "/Documents/cs/data";
    
    /** Archivo para información de administradores del sistema */
    private static final String ADMINS_FILE = DATA_DIR + "/admins.properties";
    
    /** Archivo principal para usuarios y claves del sistema */
    private static final String USERS_FILE = DATA_DIR + "/users.properties";
    
    /** Archivo para claves de cifrado público */
    private static final String KEYS_FILE = DATA_DIR + "/keys.properties";

    static {
        // Crear directorio de datos si no existe
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
        } catch (IOException e) {
            System.err.println("Error creando directorio de datos: " + e.getMessage());
        }
    }

    /**
     * Guarda información completa de un administrador en el sistema.
     * 
     * Almacena las credenciales y claves criptográficas de un administrador,
     * incluyendo hash de contraseña, clave pública y clave privada cifrada.
     * 
     * @param username nombre único del administrador
     * @param passwordHash hash SHA-256 de la contraseña del administrador
     * @param publicKeyBase64 clave pública RSA en formato Base64
     * @param encryptedPrivateKeyBase64 clave privada RSA cifrada en Base64
     * @throws IOException si hay error al escribir en el archivo de administradores
     * 
     * @deprecated Funcionalidad de administrador no completamente implementada
     * @see SimpleHash#hashPassword(String)
     */
    @Deprecated
    public static void saveAdmin(String username, String passwordHash, String publicKeyBase64, String encryptedPrivateKeyBase64) throws IOException {
        Properties props = loadProperties(ADMINS_FILE);
        props.setProperty(username + ".password_hash", passwordHash);
        props.setProperty(username + ".public_key", publicKeyBase64);
        props.setProperty(username + ".private_key_encrypted", encryptedPrivateKeyBase64);
        saveProperties(props, ADMINS_FILE);
    }

    /**
     * Obtiene el hash de contraseña almacenado para un administrador.
     * 
     * @param username nombre del administrador
     * @return hash de la contraseña o null si el administrador no existe
     * @throws IOException si hay error al leer el archivo de administradores
     * 
     * @deprecated Funcionalidad de administrador no completamente implementada
     */
    @Deprecated
    public static String getAdminPasswordHash(String username) throws IOException {
        Properties props = loadProperties(ADMINS_FILE);
        return props.getProperty(username + ".password_hash");
    }

    /**
     * Guarda información completa de un usuario en el sistema.
     * 
     * Almacena todas las credenciales y claves necesarias para que un usuario
     * pueda utilizar el cifrado privado, incluyendo hash de contraseña y
     * par de claves RSA.
     * 
     * @param username nombre único del usuario
     * @param passwordHash hash SHA-256 de la contraseña del usuario
     * @param publicKeyBase64 clave pública RSA del usuario en Base64
     * @param encryptedPrivateKeyBase64 clave privada RSA del usuario en Base64
     * @throws IOException si hay error al escribir en el archivo de usuarios
     * 
     * @see SimpleHash#hashPassword(String)
     * @see RSAUtil#publicKeyToBase64(java.security.PublicKey)
     */
    public static void saveUser(String username, String passwordHash, String publicKeyBase64, String encryptedPrivateKeyBase64) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        props.setProperty(username + ".password_hash", passwordHash);
        props.setProperty(username + ".public_key", publicKeyBase64);
        props.setProperty(username + ".private_key_encrypted", encryptedPrivateKeyBase64);
        saveProperties(props, USERS_FILE);
    }

    /**
     * Obtiene el hash de contraseña almacenado para un usuario.
     * 
     * Este método es utilizado por el sistema de autenticación para
     * verificar las credenciales del usuario durante el login.
     * 
     * @param username nombre del usuario
     * @return hash SHA-256 de la contraseña o null si el usuario no existe
     * @throws IOException si hay error al leer el archivo de usuarios
     * 
     * @see UserAuth#authenticate(String, String)
     */
    public static String getUserPasswordHash(String username) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty(username + ".password_hash");
    }

    /**
     * Obtiene la clave pública RSA de un usuario específico.
     * 
     * La clave pública se utiliza para cifrar datos que solo ese usuario
     * podrá descifrar con su clave privada correspondiente.
     * 
     * @param username nombre del usuario
     * @return clave pública RSA en formato Base64 o null si no existe
     * @throws IOException si hay error al leer el archivo de usuarios
     * 
     * @see RSAUtil#base64ToPublicKey(String)
     */
    public static String getUserPublicKey(String username) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty(username + ".public_key");
    }

    /**
     * Obtiene la clave privada RSA de un usuario en formato Base64.
     * 
     * La clave privada permite al usuario descifrar archivos que fueron
     * cifrados específicamente para él usando su clave pública.
     * 
     * @param username nombre del usuario
     * @return clave privada RSA en formato Base64 o null si no existe
     * @throws IOException si hay error al leer el archivo de usuarios
     * 
     * @see RSAUtil#base64ToPrivateKey(String)
     */
    public static String getUserPrivateKey(String username) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty(username + ".private_key_encrypted");
    }

    /**
     * Guarda una clave de cifrado asociada a un archivo
     */
    public static void saveKey(String filename, String encryptedKeyBase64) throws IOException {
        Properties props = loadProperties(KEYS_FILE);
        props.setProperty(filename, encryptedKeyBase64);
        saveProperties(props, KEYS_FILE);
    }

    /**
     * Obtiene una clave de cifrado asociada a un archivo
     */
    public static String getKey(String filename) throws IOException {
        Properties props = loadProperties(KEYS_FILE);
        return props.getProperty(filename);
    }

    /**
     * Elimina una clave de cifrado
     */
    public static void deleteKey(String filename) throws IOException {
        Properties props = loadProperties(KEYS_FILE);
        props.remove(filename);
        saveProperties(props, KEYS_FILE);
    }

    /**
     * Verifica si un usuario existe
     */
    public static boolean userExists(String username) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty(username + ".password_hash") != null;
    }

    /**
     * Verifica si un administrador existe
     */
    public static boolean adminExists(String username) throws IOException {
        Properties props = loadProperties(ADMINS_FILE);
        return props.getProperty(username + ".password_hash") != null;
    }

    // Métodos auxiliares para manejo de archivos Properties
    
    /**
     * Carga un archivo Properties desde el sistema de archivos.
     * 
     * Si el archivo no existe, retorna un objeto Properties vacío.
     * Este método es utilizado internamente por todas las operaciones
     * de lectura de datos.
     * 
     * @param filePath ruta absoluta al archivo .properties
     * @return objeto Properties con los datos cargados (vacío si no existe el archivo)
     * @throws IOException si hay error al leer el archivo existente
     */
    private static Properties loadProperties(String filePath) throws IOException {
        Properties props = new Properties();
        File file = new File(filePath);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            }
        }
        return props;
    }

    /**
     * Guarda un objeto Properties en el sistema de archivos.
     * 
     * Escribe todas las propiedades en el archivo especificado,
     * creando el archivo si no existe. Utilizado internamente
     * por todas las operaciones de escritura de datos.
     * 
     * @param props objeto Properties con los datos a guardar
     * @param filePath ruta absoluta donde guardar el archivo
     * @throws IOException si hay error al escribir el archivo
     */
    private static void saveProperties(Properties props, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            props.store(fos, "Auto-generated file for local storage");
        }
    }

    /**
     * Guarda las claves RSA maestras del sistema.
     * 
     * Estas claves se utilizan para el cifrado público de archivos,
     * permitiendo que cualquier usuario del sistema pueda descifrar
     * archivos cifrados públicamente.
     * 
     * @param publicKeyBase64 clave pública RSA del sistema en Base64
     * @param privateKeyBase64 clave privada RSA del sistema en Base64
     * @throws IOException si hay error al guardar las claves del sistema
     * 
     * @see FileEncryptionApp#FileEncryptionApp()
     */
    public static void saveSystemRSAKeys(String publicKeyBase64, String privateKeyBase64) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        props.setProperty("system.public_key", publicKeyBase64);
        props.setProperty("system.private_key", privateKeyBase64);
        saveProperties(props, USERS_FILE);
    }

    /**
     * Obtiene la clave pública maestra del sistema.
     * 
     * @return clave pública RSA del sistema en formato Base64
     * @throws IOException si hay error al leer el archivo de usuarios
     * 
     * @see #systemKeysExist()
     */
    public static String getSystemPublicKey() throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty("system.public_key");
    }

    /**
     * Obtiene la clave privada maestra del sistema.
     * 
     * @return clave privada RSA del sistema en formato Base64
     * @throws IOException si hay error al leer el archivo de usuarios
     * 
     * @see #systemKeysExist()
     */
    public static String getSystemPrivateKey() throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty("system.private_key");
    }

    /**
     * Verifica si las claves maestras del sistema ya han sido generadas.
     * 
     * Este método se utiliza durante la inicialización de la aplicación
     * para determinar si se deben cargar claves existentes o generar nuevas.
     * 
     * @return true si ambas claves del sistema existen, false en caso contrario
     * @throws IOException si hay error al verificar la existencia de las claves
     * 
     * @see FileEncryptionApp#FileEncryptionApp()
     */
    public static boolean systemKeysExist() throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty("system.public_key") != null && 
               props.getProperty("system.private_key") != null;
    }

    /**
     * Guarda una clave AES cifrada para un usuario especifico (cifrado privado)
     */
    public static void savePrivateKey(String username, String filename, String encryptedKeyBase64) throws IOException {
        String privateKeysFile = DATA_DIR + "private_keys_" + username + ".properties";
        Properties props = loadProperties(privateKeysFile);
        props.setProperty(filename, encryptedKeyBase64);
        saveProperties(props, privateKeysFile);
    }

    /**
     * Obtiene una clave AES cifrada para un usuario especifico (cifrado privado)
     */
    public static String getPrivateKey(String username, String filename) throws IOException {
        String privateKeysFile = DATA_DIR + "private_keys_" + username + ".properties";
        Properties props = loadProperties(privateKeysFile);
        return props.getProperty(filename);
    }

    /**
     * Lista todas las claves privadas de un usuario (para depuracion)
     */
    public static void listPrivateKeys(String username) throws IOException {
        String privateKeysFile = DATA_DIR + "private_keys_" + username + ".properties";
        Properties props = loadProperties(privateKeysFile);
        System.out.println("\n=== CLAVES PRIVADAS DE " + username.toUpperCase() + " ===");
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            String truncated = value.length() > 20 ? value.substring(0, 20) + "..." : value;
            System.out.println("Archivo: " + key);
            System.out.println("Clave: " + truncated);
            System.out.println("---");
        }
        System.out.println("=========================\n");
    }

    /**
     * Lista todas las claves publicas almacenadas (para depuracion)
     */
    public static void listAllKeys() {
        try {
            Properties props = loadProperties(KEYS_FILE);
            System.out.println("\n=== CLAVES ALMACENADAS ===");
            if (props.isEmpty()) {
                System.out.println("No hay claves almacenadas.");
            } else {
                for (String key : props.stringPropertyNames()) {
                    System.out.println("Archivo: " + key);
                    System.out.println("Clave: " + props.getProperty(key).substring(0, Math.min(20, props.getProperty(key).length())) + "...");
                    System.out.println("---");
                }
            }
            System.out.println("=========================\n");
        } catch (Exception e) {
            System.err.println("Error listando claves: " + e.getMessage());
        }
    }
}
