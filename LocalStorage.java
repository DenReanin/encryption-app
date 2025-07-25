import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Clase para manejar el almacenamiento local de datos
 * Reemplaza las funcionalidades de la base de datos MySQL
 */
public class LocalStorage {
    private static final String DATA_DIR = System.getProperty("user.home") + "/Documents/cs/data";
    private static final String ADMINS_FILE = DATA_DIR + "/admins.properties";
    private static final String USERS_FILE = DATA_DIR + "/users.properties";
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
     * Guarda informacion de administrador
     */
    public static void saveAdmin(String username, String passwordHash, String publicKeyBase64, String encryptedPrivateKeyBase64) throws IOException {
        Properties props = loadProperties(ADMINS_FILE);
        props.setProperty(username + ".password_hash", passwordHash);
        props.setProperty(username + ".public_key", publicKeyBase64);
        props.setProperty(username + ".private_key_encrypted", encryptedPrivateKeyBase64);
        saveProperties(props, ADMINS_FILE);
    }

    /**
     * Obtiene el hash de contrasena del administrador
     */
    public static String getAdminPasswordHash(String username) throws IOException {
        Properties props = loadProperties(ADMINS_FILE);
        return props.getProperty(username + ".password_hash");
    }

    /**
     * Guarda informacion de usuario
     */
    public static void saveUser(String username, String passwordHash, String publicKeyBase64, String encryptedPrivateKeyBase64) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        props.setProperty(username + ".password_hash", passwordHash);
        props.setProperty(username + ".public_key", publicKeyBase64);
        props.setProperty(username + ".private_key_encrypted", encryptedPrivateKeyBase64);
        saveProperties(props, USERS_FILE);
    }

    /**
     * Obtiene el hash de contrasena del usuario
     */
    public static String getUserPasswordHash(String username) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty(username + ".password_hash");
    }

    /**
     * Obtiene la clave publica del usuario
     */
    public static String getUserPublicKey(String username) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty(username + ".public_key");
    }

    /**
     * Obtiene la clave privada del usuario en Base64
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

    // Metodos auxiliares
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

    private static void saveProperties(Properties props, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            props.store(fos, "Auto-generated file for local storage");
        }
    }

    /**
     * Guarda las claves RSA del sistema
     */
    public static void saveSystemRSAKeys(String publicKeyBase64, String privateKeyBase64) throws IOException {
        Properties props = loadProperties(USERS_FILE);
        props.setProperty("system.public_key", publicKeyBase64);
        props.setProperty("system.private_key", privateKeyBase64);
        saveProperties(props, USERS_FILE);
    }

    /**
     * Obtiene la clave publica del sistema
     */
    public static String getSystemPublicKey() throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty("system.public_key");
    }

    /**
     * Obtiene la clave privada del sistema
     */
    public static String getSystemPrivateKey() throws IOException {
        Properties props = loadProperties(USERS_FILE);
        return props.getProperty("system.private_key");
    }

    /**
     * Verifica si las claves del sistema existen
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
