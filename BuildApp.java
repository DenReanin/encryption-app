import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * Compila las fuentes Java y empaqueta FileEncryptionApp.jar usando jdk.compiler.
 */
public class BuildApp {
    public static void main(String[] args) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("System JavaCompiler (jdk.compiler) no disponible.");
        }

        Path root = Paths.get("").toAbsolutePath();
        Path buildDir = root.resolve("build");
        Files.createDirectories(buildDir);

        List<String> compilerArgs = new ArrayList<>();
        compilerArgs.add("-encoding");
        compilerArgs.add("UTF-8");
        compilerArgs.add("-d");
        compilerArgs.add(buildDir.toString());

        try (var stream = Files.list(root)) {
            stream.filter(p -> p.toString().endsWith(".java"))
                  .filter(p -> !p.getFileName().toString().equals("BuildApp.java"))
                  .sorted()
                  .forEach(p -> compilerArgs.add(p.toString()));
        }

        int rc = compiler.run(null, System.out, System.err, compilerArgs.toArray(new String[0]));
        if (rc != 0) {
            System.exit(rc);
        }

        Path jarPath = root.resolve("FileEncryptionApp.jar");
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "FileEncryptionApp");

        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarPath), manifest);
             var walk = Files.walk(buildDir)) {
            walk.filter(Files::isRegularFile).forEach(file -> {
                String entryName = buildDir.relativize(file).toString().replace('\\', '/');
                try {
                    jos.putNextEntry(new JarEntry(entryName));
                    Files.copy(file, jos);
                    jos.closeEntry();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        System.out.println("Build OK -> " + jarPath);
    }
}
