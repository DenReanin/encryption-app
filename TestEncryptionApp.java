import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.util.Arrays;

/**
 * Suite de pruebas criptográficas automatizadas para encryption-app.
 */
public class TestEncryptionApp {
    private static int failures = 0;

    private static void check(String name, boolean cond) {
        if (cond) {
            System.out.println("  [OK]    " + name);
        } else {
            System.out.println("  [FALLO] " + name);
            failures++;
        }
    }

    public static void main(String[] args) throws Exception {
        Path tempHome = Files.createTempDirectory("enc_app_test_");
        String prevHome = System.getProperty("user.home");
        System.setProperty("user.home", tempHome.toString());

        try {
            System.out.println("1. SimpleHash (PBKDF2WithHmacSHA256 + compatibilidad legado)");
            String h1 = SimpleHash.hashPassword("miClaveSecreta#2026");
            String h2 = SimpleHash.hashPassword("miClaveSecreta#2026");
            check("usa formato pbkdf2$65536", h1.startsWith("pbkdf2$65536$"));
            check("sal aleatoria produce hashes distintos", !h1.equals(h2));
            check("verifyPassword acepta contraseña correcta", SimpleHash.verifyPassword("miClaveSecreta#2026", h1));
            check("verifyPassword rechaza contraseña incorrecta", !SimpleHash.verifyPassword("otra", h1));

            String legacy = SimpleHash.legacyHashForTesting("1234");
            check("verifyPassword mantiene compatibilidad con hash legado SHA-256",
                  SimpleHash.verifyPassword("1234", legacy));

            System.out.println("2. RSAUtil (2048-bit + OAEP-SHA256)");
            KeyPair kp = RSAUtil.generateKeyPair();
            byte[] secret = "clave-simetrica-de-32-bytes-aes!".getBytes(StandardCharsets.UTF_8);
            byte[] encRsa = RSAUtil.encryptWithPublicKey(secret, kp.getPublic());
            byte[] decRsa = RSAUtil.decryptWithPrivateKey(encRsa, kp.getPrivate());
            check("RSA OAEP cifrado/descifrado exacto", Arrays.equals(secret, decRsa));

            KeyPair kpReconstructed = RSAUtil.base64ToKeyPair(
                RSAUtil.publicKeyToBase64(kp.getPublic()),
                RSAUtil.privateKeyToBase64(kp.getPrivate())
            );
            check("claves RSA Base64 reconstruidas funcionan",
                  Arrays.equals(secret, RSAUtil.decryptWithPrivateKey(encRsa, kpReconstructed.getPrivate())));

            System.out.println("3. FileEncryptionUtil (AES-256-GCM)");
            Key aesKey = FileEncryptionUtil.generateKey();
            check("clave AES generada es de 256 bits (32 bytes)", aesKey.getEncoded().length == 32);

            Path workDir = tempHome.resolve("work");
            Files.createDirectories(workDir);
            Path plainPath = workDir.resolve("documento.txt");
            byte[] originalBytes = "Contenido confidencial cifrado con AES-256-GCM".getBytes(StandardCharsets.UTF_8);
            Files.write(plainPath, originalBytes);

            File encFile = workDir.resolve("documento.txt.enc").toFile();
            FileEncryptionUtil.encryptFile(plainPath.toFile(), encFile, aesKey);
            check("archivo cifrado existe y original eliminado", encFile.exists() && !Files.exists(plainPath));
            byte[] encBytes = Files.readAllBytes(encFile.toPath());
            check("archivo cifrado lleva cabecera AGCM",
                  encBytes.length > 16 && new String(encBytes, 0, 4, StandardCharsets.US_ASCII).equals("AGCM"));

            FileEncryptionUtil.saveKey("documento.txt", aesKey, kp.getPublic());
            Key recoveredKey = FileEncryptionUtil.getKey(encFile.getName(), kp.getPrivate());
            FileEncryptionUtil.decryptFile(encFile, new File("documento.txt"), recoveredKey);

            Path decryptedPath = tempHome.resolve("Documents/cs/desencriptado/documento.txt");
            check("archivo descifrado existe en ruta esperada", Files.exists(decryptedPath));
            check("contenido descifrado idéntico al original",
                  Arrays.equals(originalBytes, Files.readAllBytes(decryptedPath)));
        } finally {
            System.setProperty("user.home", prevHome);
            try (var walk = Files.walk(tempHome)) {
                walk.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                });
            }
        }

        if (failures > 0) {
            System.out.println("RESULTADO: " + failures + " fallo(s).");
            System.exit(1);
        }
        System.out.println("RESULTADO: todas las pruebas criptográficas pasan.");
    }
}
