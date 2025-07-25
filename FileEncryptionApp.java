import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.security.Key;
import java.security.KeyPair;


public class FileEncryptionApp {
    private KeyPair rsaKeyPair;

    public FileEncryptionApp() {
        // Inicializa rsaKeyPair - cargar o crear claves persistentes del sistema
        try {
            if (LocalStorage.systemKeysExist()) {
                // Cargar claves existentes del sistema
                String publicKeyBase64 = LocalStorage.getSystemPublicKey();
                String privateKeyBase64 = LocalStorage.getSystemPrivateKey();
                rsaKeyPair = RSAUtil.base64ToKeyPair(publicKeyBase64, privateKeyBase64);
                System.out.println("Claves del sistema cargadas desde almacenamiento local");
            } else {
                // Generar nuevas claves del sistema y guardarlas
                rsaKeyPair = RSAUtil.generateKeyPair();
                String publicKeyBase64 = RSAUtil.publicKeyToBase64(rsaKeyPair.getPublic());
                String privateKeyBase64 = RSAUtil.privateKeyToBase64(rsaKeyPair.getPrivate());
                LocalStorage.saveSystemRSAKeys(publicKeyBase64, privateKeyBase64);
                System.out.println("Nuevas claves del sistema generadas y guardadas");
            }
        } catch (Exception ex) {
            System.err.println("Error al manejar claves del sistema: " + ex.getMessage());
            ex.printStackTrace(); // Manejo de excepciones adecuado
        }
    }

    public void startApp() {
        // Configurar UTF-8 para caracteres especiales
        System.setProperty("file.encoding", "UTF-8");
        
        // Ejecuta la interfaz grafica en el hilo de despacho de eventos
        SwingUtilities.invokeLater(() -> {
            // Configurar Look and Feel moderno
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Usar look and feel por defecto
            }
            
            // Crear el marco principal
            JFrame frame = new JFrame("Aplicacion de Cifrado de Archivos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null); // Centrar en pantalla
            frame.setResizable(false);

            // Panel principal con diseno moderno
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setBackground(new Color(32, 35, 42)); // Fondo oscuro moderno
            frame.add(mainPanel);

            // Panel de titulo
            JPanel titlePanel = createTitlePanel();
            mainPanel.add(titlePanel, BorderLayout.NORTH);

            // Panel central con botones
            JPanel centerPanel = createCenterPanel();
            mainPanel.add(centerPanel, BorderLayout.CENTER);

            // Panel de estado
            JPanel statusPanel = createStatusPanel();
            mainPanel.add(statusPanel, BorderLayout.SOUTH);

            // Crear referencias a los componentes para los ActionListeners
            JLabel statusLabel = (JLabel) statusPanel.getComponent(0);
            JButton encryptButton = findButtonByText(centerPanel, "Cifrar Archivo");
            JButton decryptButton = findButtonByText(centerPanel, "Descifrar Archivo");
            JButton createUserButton = findButtonByText(centerPanel, "Crear Usuario");
            JButton viewKeysButton = findButtonByText(centerPanel, "Ver Claves");

            // Configurar ActionListeners
            setupActionListeners(frame, statusLabel, encryptButton, decryptButton, createUserButton, viewKeysButton);

            // Hacer visible el marco
            frame.setVisible(true);
        });
    }

    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(24, 26, 31)); // Fondo mas oscuro para contraste
        titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        
        JLabel titleLabel = new JLabel("Sistema de Cifrado Hibrido RSA+AES");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(106, 176, 76)); // Verde moderno
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Cifrado publico y privado con gestion de usuarios");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(150, 155, 165)); // Gris suave
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        titlePanel.setLayout(new GridLayout(2, 1, 0, 5));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(40, 44, 52)); // Fondo gris oscuro elegante
        centerPanel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        centerPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Boton Cifrar
        JButton encryptButton = createModernButton("Cifrar Archivo", new Color(106, 176, 76), new Color(40, 44, 52));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(encryptButton, gbc);

        // Boton Descifrar
        JButton decryptButton = createModernButton("Descifrar Archivo", new Color(231, 76, 60), new Color(40, 44, 52));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        centerPanel.add(decryptButton, gbc);

        // Separador
        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(400, 2));
        separator.setBackground(new Color(60, 65, 75));
        separator.setForeground(new Color(60, 65, 75));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.insets = new Insets(25, 10, 25, 10);
        centerPanel.add(separator, gbc);

        // Reset insets
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridwidth = 1;

        // Boton Crear Usuario
        JButton createUserButton = createModernButton("Crear Usuario", new Color(52, 152, 219), new Color(40, 44, 52));
        gbc.gridx = 0; gbc.gridy = 3;
        centerPanel.add(createUserButton, gbc);

        // Boton Ver Claves
        JButton viewKeysButton = createModernButton("Ver Claves", new Color(230, 126, 34), new Color(40, 44, 52));
        gbc.gridx = 1; gbc.gridy = 3;
        centerPanel.add(viewKeysButton, gbc);

        return centerPanel;
    }

    private JButton createModernButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setPreferredSize(new Dimension(210, 55));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        // Efecto hover mejorado
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bgColor.brighter(), 2),
                    BorderFactory.createEmptyBorder(6, 13, 6, 13)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            }
        });
        
        return button;
    }

    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(24, 26, 31)); // Mismo color que el titulo
        statusPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        JLabel statusLabel = new JLabel("Listo para cifrar y descifrar archivos de forma segura");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(150, 155, 165)); // Gris suave
        
        statusPanel.add(statusLabel);
        return statusPanel;
    }

    private JButton findButtonByText(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().contains(text)) {
                    return button;
                }
            } else if (component instanceof Container) {
                JButton found = findButtonByText((Container) component, text);
                if (found != null) return found;
            }
        }
        return null;
    }

    private void setupActionListeners(JFrame frame, JLabel statusLabel, JButton encryptButton, 
                                    JButton decryptButton, JButton createUserButton, JButton viewKeysButton) {
        
        // ActionListener para cifrado
        encryptButton.addActionListener(e -> {
            Object[] options = {"Cifrado Publico", "Cifrado Privado"};
            int choice = JOptionPane.showOptionDialog(frame,
                "Que tipo de cifrado deseas usar?\n\n" +
                "Publico: Accesible por cualquier usuario del sistema\n" +
                "Privado: Solo accesible por el usuario especifico",
                "Seleccionar Tipo de Cifrado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

            if (choice == 0) {
                encryptFilePublic(frame, statusLabel);
            } else if (choice == 1) {
                encryptFilePrivate(frame, statusLabel);
            }
        });

        // ActionListener para descifrado
        decryptButton.addActionListener(e -> {
            Object[] options = {"Descifrado Publico", "Descifrado Privado"};
            int choice = JOptionPane.showOptionDialog(frame,
                "Que tipo de descifrado deseas usar?\n\n" +
                "Publico: Para archivos cifrados publicamente\n" +
                "Privado: Para archivos cifrados privadamente",
                "Seleccionar Tipo de Descifrado",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

            if (choice == 0) {
                decryptFilePublic(frame, statusLabel);
            } else if (choice == 1) {
                decryptFilePrivate(frame, statusLabel);
            }
        });

        // ActionListener para crear usuario
        createUserButton.addActionListener(e -> {
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();
            JTextField securityAnswerField = new JTextField("azul");
            
            panel.add(new JLabel("Nombre de usuario:"));
            panel.add(usernameField);
            panel.add(new JLabel("Contrasena:"));
            panel.add(passwordField);
            panel.add(new JLabel("Respuesta de seguridad:"));
            panel.add(securityAnswerField);

            int result = JOptionPane.showConfirmDialog(frame, panel, 
                "Crear Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                
                if (!username.isEmpty() && !password.isEmpty()) {
                    try {
                        if (LocalStorage.userExists(username)) {
                            JOptionPane.showMessageDialog(frame, 
                                "El usuario '" + username + "' ya existe.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Registrar usuario usando LocalStorage directamente
                        try {
                            // Generar claves RSA para el usuario
                            KeyPair userKeyPair = RSAUtil.generateKeyPair();
                            String publicKeyBase64 = RSAUtil.publicKeyToBase64(userKeyPair.getPublic());
                            String privateKeyBase64 = RSAUtil.privateKeyToBase64(userKeyPair.getPrivate());
                            String passwordHash = SimpleHash.hashPassword(password);
                            
                            LocalStorage.saveUser(username, passwordHash, publicKeyBase64, privateKeyBase64);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            statusLabel.setText("Error al crear usuario: " + ex.getMessage());
                            return;
                        }
                        statusLabel.setText("Usuario '" + username + "' creado exitosamente");
                        JOptionPane.showMessageDialog(frame, 
                            "Usuario '" + username + "' creado exitosamente!\n\n" +
                            "Credenciales:\n" +
                            "   Usuario: " + username + "\n" +
                            "   Contrasena: " + password + "\n" +
                            "   Pregunta de seguridad: azul\n\n" +
                            "Ya puede hacer login y cifrar archivos privados.", 
                            "Usuario Creado", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        statusLabel.setText("Error al crear usuario: " + ex.getMessage());
                        JOptionPane.showMessageDialog(frame, 
                            "Error al crear el usuario:\n" + ex.getMessage(), 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, 
                        "Por favor, complete todos los campos.", 
                        "Datos Incompletos", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // ActionListener para ver claves
        viewKeysButton.addActionListener(e -> {
            LocalStorage.listAllKeys();
            statusLabel.setText("Claves mostradas en consola");
            
            String username = JOptionPane.showInputDialog(frame, 
                "Ver claves privadas de usuario especifico:\n\n" +
                "Deje vacio para solo ver claves publicas",
                "Ver Claves Privadas", JOptionPane.QUESTION_MESSAGE);
                
            if (username != null && !username.trim().isEmpty()) {
                try {
                    if (LocalStorage.userExists(username.trim())) {
                        LocalStorage.listPrivateKeys(username.trim());
                        statusLabel.setText("Claves privadas de '" + username + "' mostradas en consola");
                    } else {
                        statusLabel.setText("Usuario '" + username + "' no existe");
                        JOptionPane.showMessageDialog(frame, 
                            "El usuario '" + username + "' no existe.", 
                            "Usuario No Encontrado", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Error al mostrar claves privadas");
                    System.err.println("Error al mostrar claves privadas: " + ex.getMessage());
                }
            }
        });
    }

    // Metodo para cifrado publico (usando claves del sistema)
    private void encryptFilePublic(JFrame parent, JLabel statusLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo para cifrado publico");
        fileChooser.setApproveButtonText("Cifrar");
        
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileChooser.getSelectedFile();
            
            // Crear directorio publico y especificar la ruta completa
            File publicDirectory = new File(System.getProperty("user.home") + "/Documents/cs/encriptado");
            if (!publicDirectory.exists()) {
                publicDirectory.mkdirs();
            }
            File outputFile = new File(publicDirectory, inputFile.getName() + ".enc");

            try {
                Key key = FileEncryptionUtil.generateKey();
                FileEncryptionUtil.encryptFile(inputFile, outputFile, key);
                FileEncryptionUtil.saveKey(inputFile.getName(), key, rsaKeyPair.getPublic());

                statusLabel.setText("Archivo '" + inputFile.getName() + "' cifrado publicamente con exito");
                JOptionPane.showMessageDialog(parent,
                    "Cifrado exitoso!\n\n" +
                    "Archivo original: " + inputFile.getName() + "\n" +
                    "Archivo cifrado: " + outputFile.getName() + "\n" +
                    "Ubicacion: " + publicDirectory.getAbsolutePath() + "\n\n" +
                    "Puede ser descifrado por cualquier usuario del sistema.",
                    "Cifrado Publico Completado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                statusLabel.setText("Error al cifrar: " + ex.getMessage());
                JOptionPane.showMessageDialog(parent,
                    "Error durante el cifrado:\n\n" + ex.getMessage(),
                    "Error de Cifrado", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Metodo para cifrado privado (usando claves del usuario especifico)
    private void encryptFilePrivate(JFrame parent, JLabel statusLabel) {
        String username = JOptionPane.showInputDialog(parent, 
            "Cifrado Privado\n\n" +
            "Introduzca el nombre del usuario propietario del archivo:\n" +
            "(Solo este usuario podra descifrar el archivo)",
            "Especificar Usuario", JOptionPane.QUESTION_MESSAGE);
            
        if (username != null && !username.trim().isEmpty()) {
            try {
                if (!LocalStorage.userExists(username.trim())) {
                    JOptionPane.showMessageDialog(parent, 
                        "El usuario '" + username + "' no existe.\n\n" +
                        "Puede crear el usuario usando el boton 'Crear Usuario'.", 
                        "Usuario No Encontrado", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Seleccionar archivo para cifrado privado de " + username);
                fileChooser.setApproveButtonText("Cifrar para " + username);
                
                if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                    File inputFile = fileChooser.getSelectedFile();
                    
                    // Crear directorio privado del usuario
                    File userDirectory = new File(System.getProperty("user.home") + "/Documents/cs/encriptado_privado/" + username.trim());
                    if (!userDirectory.exists()) {
                        userDirectory.mkdirs();
                    }
                    File outputFile = new File(userDirectory, inputFile.getName() + ".enc");

                    Key key = FileEncryptionUtil.generateKey();
                    FileEncryptionUtil.encryptFile(inputFile, outputFile, key);
                    
                    // Guardar clave usando el sistema de claves privadas
                    String publicKeyBase64 = LocalStorage.getUserPublicKey(username.trim());
                    java.security.PublicKey userPublicKey = RSAUtil.base64ToPublicKey(publicKeyBase64);
                    FileEncryptionUtil.savePrivateKey(username.trim(), inputFile.getName(), key, userPublicKey);

                    statusLabel.setText("Archivo '" + inputFile.getName() + "' cifrado privadamente para " + username);
                    JOptionPane.showMessageDialog(parent,
                        "Cifrado privado exitoso!\n\n" +
                        "Usuario propietario: " + username + "\n" +
                        "Archivo original: " + inputFile.getName() + "\n" +
                        "Archivo cifrado: " + outputFile.getName() + "\n" +
                        "Ubicacion privada: " + userDirectory.getAbsolutePath() + "\n\n" +
                        "Solo '" + username + "' puede descifrar este archivo.",
                        "Cifrado Privado Completado", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                statusLabel.setText("Error al cifrar privadamente: " + ex.getMessage());
                JOptionPane.showMessageDialog(parent,
                    "Error durante el cifrado privado:\n\n" + ex.getMessage(),
                    "Error de Cifrado Privado", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Metodo para descifrado publico
    private void decryptFilePublic(JFrame parent, JLabel statusLabel) {
        // Navegar directamente a la carpeta publica
        File publicDirectory = new File(System.getProperty("user.home") + "/Documents/cs/encriptado");
        if (!publicDirectory.exists()) {
            JOptionPane.showMessageDialog(parent,
                "No se encontro la carpeta de archivos publicos.\n\n" +
                "Cifre algun archivo publico primero.",
                "Carpeta No Encontrada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser(publicDirectory);
        fileChooser.setDialogTitle("Seleccionar archivo cifrado publico (.enc)");
        fileChooser.setApproveButtonText("Descifrar");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".enc");
            }
            public String getDescription() {
                return "Archivos cifrados (*.enc)";
            }
        });
        
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileChooser.getSelectedFile();
            File outputFile = new File(inputFile.getPath().replace(".enc", ""));

            try {
                Key key = FileEncryptionUtil.getKey(inputFile.getName(), rsaKeyPair.getPrivate());
                FileEncryptionUtil.decryptFile(inputFile, outputFile, key);

                statusLabel.setText("Archivo '" + outputFile.getName() + "' descifrado publicamente con exito");
                JOptionPane.showMessageDialog(parent,
                    "Descifrado exitoso!\n\n" +
                    "Archivo cifrado: " + inputFile.getName() + "\n" +
                    "Archivo descifrado: " + outputFile.getName() + "\n" +
                    "Ubicacion: " + outputFile.getParent() + "\n\n" +
                    "El archivo original ha sido recuperado.",
                    "Descifrado Publico Completado", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                statusLabel.setText("Error al descifrar: " + ex.getMessage());
                JOptionPane.showMessageDialog(parent,
                    "Error durante el descifrado:\n\n" + 
                    ex.getMessage() + "\n\n" +
                    "Verifique que el archivo fue cifrado publicamente.",
                    "Error de Descifrado", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // Metodo para descifrado privado
    private void decryptFilePrivate(JFrame parent, JLabel statusLabel) {
        String username = JOptionPane.showInputDialog(parent, 
            "Descifrado Privado\n\n" +
            "Introduzca su nombre de usuario para acceder\n" +
            "a sus archivos cifrados privados:",
            "Autenticacion de Usuario", JOptionPane.QUESTION_MESSAGE);
            
        if (username != null && !username.trim().isEmpty()) {
            try {
                if (!LocalStorage.userExists(username.trim())) {
                    JOptionPane.showMessageDialog(parent, 
                        "El usuario '" + username + "' no existe.\n\n" +
                        "Verifique el nombre de usuario.", 
                        "Usuario No Encontrado", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Navegar directamente a la carpeta privada del usuario
                File userDirectory = new File(System.getProperty("user.home") + "/Documents/cs/encriptado_privado/" + username.trim());
                if (!userDirectory.exists() || userDirectory.listFiles().length == 0) {
                    JOptionPane.showMessageDialog(parent,
                        "No se encontraron archivos cifrados para el usuario '" + username + "'.\n\n" +
                        "Cifre archivos privados primero.",
                        "Sin Archivos Privados", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                JFileChooser fileChooser = new JFileChooser(userDirectory);
                fileChooser.setDialogTitle("Seleccionar archivo cifrado privado de " + username + " (.enc)");
                fileChooser.setApproveButtonText("Descifrar");
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    public boolean accept(File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".enc");
                    }
                    public String getDescription() {
                        return "Archivos cifrados privados (*.enc)";
                    }
                });
                
                if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                    File inputFile = fileChooser.getSelectedFile();
                    File outputFile = new File(inputFile.getPath().replace(".enc", ""));

                    String privateKeyBase64 = LocalStorage.getUserPrivateKey(username.trim());
                    java.security.PrivateKey privateKey = RSAUtil.base64ToPrivateKey(privateKeyBase64);
                    Key key = FileEncryptionUtil.getPrivateKey(username.trim(), inputFile.getName(), privateKey);
                    FileEncryptionUtil.decryptFile(inputFile, outputFile, key);

                    statusLabel.setText("Archivo '" + outputFile.getName() + "' descifrado privadamente para " + username);
                    JOptionPane.showMessageDialog(parent,
                        "Descifrado privado exitoso!\n\n" +
                        "Usuario: " + username + "\n" +
                        "Archivo cifrado: " + inputFile.getName() + "\n" +
                        "Archivo descifrado: " + outputFile.getName() + "\n" +
                        "Ubicacion: " + outputFile.getParent() + "\n\n" +
                        "Su archivo privado ha sido recuperado.",
                        "Descifrado Privado Completado", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                statusLabel.setText("Error al descifrar privadamente: " + ex.getMessage());
                JOptionPane.showMessageDialog(parent,
                    "Error durante el descifrado privado:\n\n" + 
                    ex.getMessage() + "\n\n" +
                    "Verifique que el archivo fue cifrado para este usuario.",
                    "Error de Descifrado Privado", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        // Crear usuario por defecto si no existe
        try {
            DefaultSetup.createDefaultUser();
        } catch (Exception e) {
            System.err.println("Error en configuracion inicial: " + e.getMessage());
        }
        
        // Mostrar el dialogo de login unico
        LoginDialog loginDlg = new LoginDialog(null);
        loginDlg.setVisible(true);

        if (loginDlg.isSucceeded()) {
            // Inicia la aplicacion
            FileEncryptionApp app = new FileEncryptionApp();
            app.startApp();
        }
    }
}
