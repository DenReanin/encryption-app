import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.security.Key;
import java.security.KeyPair;

/**
 * Aplicación principal del sistema de cifrado híbrido RSA+AES con interfaz gráfica.
 * 
 * Esta clase representa la aplicación principal que proporciona una interfaz gráfica
 * de usuario (GUI) moderna para el sistema de cifrado de archivos. Implementa tanto
 * cifrado público como privado utilizando algoritmos RSA+AES, gestión de usuarios
 * y un sistema de almacenamiento local seguro.
 * 
 * La aplicación incluye:
 * - Interfaz gráfica moderna con diseño oscuro
 * - Cifrado/descifrado público (accesible por todos los usuarios)
 * - Cifrado/descifrado privado (específico por usuario)
 * - Gestión de usuarios con claves RSA individuales
 * - Sistema de autenticación mediante LoginDialog
 * - Visualización de claves almacenadas
 * 
 * @author DenReanin
 * @version 1.0
 * @since 2025-07-25
 * @see FileEncryptionUtil
 * @see LocalStorage
 * @see RSAUtil
 * @see LoginDialog
 */
public class FileEncryptionApp {
    /** Par de claves RSA del sistema para cifrado público */
    private KeyPair rsaKeyPair;

    /**
     * Constructor de la aplicación de cifrado.
     * 
     * Inicializa el par de claves RSA del sistema cargándolas desde el almacenamiento
     * local si existen, o generando nuevas claves si es la primera ejecución.
     * Las claves del sistema se utilizan para el cifrado público.
     * 
     * @see LocalStorage#systemKeysExist()
     * @see RSAUtil#generateKeyPair()
     * @see LocalStorage#saveSystemRSAKeys(String, String)
     */
    public FileEncryptionApp() {
        try {
            if (LocalStorage.systemKeysExist()) {
                String publicKeyBase64 = LocalStorage.getSystemPublicKey();
                String privateKeyBase64 = LocalStorage.getSystemPrivateKey();
                rsaKeyPair = RSAUtil.base64ToKeyPair(publicKeyBase64, privateKeyBase64);
                System.out.println("Claves del sistema cargadas desde almacenamiento local");
            } else {
                rsaKeyPair = RSAUtil.generateKeyPair();
                String publicKeyBase64 = RSAUtil.publicKeyToBase64(rsaKeyPair.getPublic());
                String privateKeyBase64 = RSAUtil.privateKeyToBase64(rsaKeyPair.getPrivate());
                LocalStorage.saveSystemRSAKeys(publicKeyBase64, privateKeyBase64);
                System.out.println("Nuevas claves del sistema generadas y guardadas");
            }
        } catch (Exception ex) {
            System.err.println("Error al manejar claves del sistema: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Inicia la aplicación y construye la interfaz gráfica de usuario.
     * 
     * Este método configura la codificación UTF-8, establece el Look and Feel del sistema,
     * crea la ventana principal con todos sus componentes (paneles de título, centro y estado),
     * configura los ActionListeners para los botones y hace visible la aplicación.
     * 
     * @see SwingUtilities#invokeLater(Runnable)
     * @see UIManager#setLookAndFeel(String)
     */
    public void startApp() {
        System.setProperty("file.encoding", "UTF-8");
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Usar look and feel por defecto
            }
            
            JFrame frame = new JFrame("Aplicacion de Cifrado de Archivos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 500);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setBackground(new Color(32, 35, 42));
            frame.add(mainPanel);

            JPanel titlePanel = createTitlePanel();
            mainPanel.add(titlePanel, BorderLayout.NORTH);

            JPanel centerPanel = createCenterPanel();
            mainPanel.add(centerPanel, BorderLayout.CENTER);

            JPanel statusPanel = createStatusPanel();
            mainPanel.add(statusPanel, BorderLayout.SOUTH);

            JLabel statusLabel = (JLabel) statusPanel.getComponent(0);
            JButton encryptButton = findButtonByText(centerPanel, "Cifrar Archivo");
            JButton decryptButton = findButtonByText(centerPanel, "Descifrar Archivo");
            JButton createUserButton = findButtonByText(centerPanel, "Crear Usuario");
            JButton viewKeysButton = findButtonByText(centerPanel, "Ver Claves");

            setupActionListeners(frame, statusLabel, encryptButton, decryptButton, createUserButton, viewKeysButton);

            frame.setVisible(true);
        });
    }

    /**
     * Crea el panel de título con el encabezado de la aplicación.
     * 
     * Construye un panel con fondo oscuro que contiene el título principal
     * y subtítulo de la aplicación con tipografía moderna y colores elegantes.
     * 
     * @return Panel de título configurado con los elementos gráficos
     */
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(24, 26, 31));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        
        JLabel titleLabel = new JLabel("Sistema de Cifrado Hibrido RSA+AES");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(106, 176, 76));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Cifrado publico y privado con gestion de usuarios");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(150, 155, 165));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        titlePanel.setLayout(new GridLayout(2, 1, 0, 5));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        return titlePanel;
    }

    /**
     * Crea el panel central con los botones principales de la aplicación.
     * 
     * Construye un panel con layout GridBagLayout que contiene los botones principales:
     * cifrar archivo, descifrar archivo, crear usuario y ver claves. Incluye un
     * separador visual entre las funciones de cifrado y gestión de usuarios.
     * 
     * @return Panel central con todos los botones de acción configurados
     * @see #createModernButton(String, Color, Color)
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(40, 44, 52));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        centerPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton encryptButton = createModernButton("Cifrar Archivo", new Color(106, 176, 76), new Color(40, 44, 52));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(encryptButton, gbc);

        JButton decryptButton = createModernButton("Descifrar Archivo", new Color(231, 76, 60), new Color(40, 44, 52));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        centerPanel.add(decryptButton, gbc);

        JSeparator separator = new JSeparator();
        separator.setPreferredSize(new Dimension(400, 2));
        separator.setBackground(new Color(60, 65, 75));
        separator.setForeground(new Color(60, 65, 75));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.insets = new Insets(25, 10, 25, 10);
        centerPanel.add(separator, gbc);

        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.gridwidth = 1;

        JButton createUserButton = createModernButton("Crear Usuario", new Color(52, 152, 219), new Color(40, 44, 52));
        gbc.gridx = 0; gbc.gridy = 3;
        centerPanel.add(createUserButton, gbc);

        JButton viewKeysButton = createModernButton("Ver Claves", new Color(230, 126, 34), new Color(40, 44, 52));
        gbc.gridx = 1; gbc.gridy = 3;
        centerPanel.add(viewKeysButton, gbc);

        return centerPanel;
    }

    /**
     * Crea un botón moderno con estilo personalizado y efectos hover.
     * 
     * Genera un botón con diseño moderno, colores personalizados, tipografía
     * específica y efectos de hover que cambian el color y borde cuando el
     * cursor pasa sobre el botón.
     * 
     * @param text Texto a mostrar en el botón
     * @param bgColor Color de fondo del botón
     * @param textColor Color del texto del botón
     * @return Botón configurado con estilo moderno y efectos hover
     */
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

    /**
     * Crea el panel de estado que muestra información al usuario.
     * 
     * Construye un panel inferior que contiene una etiqueta de estado para
     * mostrar mensajes informativos sobre las operaciones realizadas.
     * 
     * @return Panel de estado configurado con la etiqueta informativa
     */
    private JPanel createStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(24, 26, 31));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        JLabel statusLabel = new JLabel("Listo para cifrar y descifrar archivos de forma segura");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusLabel.setForeground(new Color(150, 155, 165));
        
        statusPanel.add(statusLabel);
        return statusPanel;
    }

    /**
     * Busca un botón por su texto dentro de un contenedor de forma recursiva.
     * 
     * Recorre recursivamente todos los componentes de un contenedor buscando
     * un JButton que contenga el texto especificado.
     * 
     * @param container Contenedor donde buscar el botón
     * @param text Texto a buscar en los botones
     * @return El botón encontrado o null si no se encuentra
     */
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

    /**
     * Configura los ActionListeners para todos los botones de la aplicación.
     * 
     * Este método establece los manejadores de eventos para cada botón:
     * - Cifrar archivo: Permite elegir entre cifrado público y privado
     * - Descifrar archivo: Permite elegir entre descifrado público y privado
     * - Crear usuario: Abre diálogo para registrar nuevos usuarios
     * - Ver claves: Muestra las claves almacenadas en consola
     * 
     * @param frame Ventana principal de la aplicación
     * @param statusLabel Etiqueta de estado para mostrar mensajes
     * @param encryptButton Botón de cifrado
     * @param decryptButton Botón de descifrado
     * @param createUserButton Botón de creación de usuarios
     * @param viewKeysButton Botón de visualización de claves
     */
    private void setupActionListeners(JFrame frame, JLabel statusLabel, JButton encryptButton, 
                                    JButton decryptButton, JButton createUserButton, JButton viewKeysButton) {
        
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

    /**
     * Realiza el cifrado público de un archivo seleccionado por el usuario.
     * 
     * Este método permite al usuario seleccionar un archivo mediante un JFileChooser,
     * genera una clave AES aleatoria, cifra el archivo usando las claves del sistema
     * y guarda tanto el archivo cifrado como la clave protegida por RSA. El archivo
     * cifrado queda disponible para ser descifrado por cualquier usuario del sistema.
     * 
     * @param parent Ventana padre para los diálogos
     * @param statusLabel Etiqueta de estado para mostrar el resultado de la operación
     * @see FileEncryptionUtil#generateKey()
     * @see FileEncryptionUtil#encryptFile(File, File, Key)
     * @see FileEncryptionUtil#saveKey(String, Key, java.security.PublicKey)
     */
    private void encryptFilePublic(JFrame parent, JLabel statusLabel) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo para cifrado publico");
        fileChooser.setApproveButtonText("Cifrar");
        
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File inputFile = fileChooser.getSelectedFile();
            
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

    /**
     * Realiza el cifrado privado de un archivo para un usuario específico.
     * 
     * Este método solicita el nombre del usuario propietario, verifica que exista,
     * permite seleccionar un archivo y lo cifra usando las claves RSA específicas
     * del usuario. Solo el usuario especificado podrá descifrar posteriormente
     * el archivo.
     * 
     * @param parent Ventana padre para los diálogos
     * @param statusLabel Etiqueta de estado para mostrar el resultado de la operación
     * @see LocalStorage#userExists(String)
     * @see FileEncryptionUtil#savePrivateKey(String, String, Key, java.security.PublicKey)
     */
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

    /**
     * Realiza el descifrado público de un archivo cifrado.
     * 
     * Este método permite al usuario navegar directamente a la carpeta de archivos
     * públicos cifrados, seleccionar un archivo .enc y descifrarlo usando las
     * claves del sistema. El archivo descifrado se guarda en la carpeta de
     * descifrado público.
     * 
     * @param parent Ventana padre para los diálogos
     * @param statusLabel Etiqueta de estado para mostrar el resultado de la operación
     * @see FileEncryptionUtil#getKey(String, java.security.PrivateKey)
     * @see FileEncryptionUtil#decryptFile(File, File, Key)
     */
    private void decryptFilePublic(JFrame parent, JLabel statusLabel) {
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

    /**
     * Realiza el descifrado privado de un archivo específico de un usuario.
     * 
     * Este método solicita el nombre del usuario, verifica que exista, navega
     * a su carpeta privada de archivos cifrados, permite seleccionar un archivo
     * y lo descifra usando las claves privadas del usuario. Solo archivos cifrados
     * específicamente para ese usuario pueden ser descifrados.
     * 
     * @param parent Ventana padre para los diálogos
     * @param statusLabel Etiqueta de estado para mostrar el resultado de la operación
     * @see LocalStorage#userExists(String)
     * @see FileEncryptionUtil#getPrivateKey(String, String, java.security.PrivateKey)
     */
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

    /**
     * Método principal que inicia la aplicación de cifrado.
     * 
     * Este método realiza la configuración inicial creando el usuario por defecto
     * si no existe, muestra el diálogo de login para autenticación y, si el login
     * es exitoso, inicia la aplicación principal.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     * @see DefaultSetup#createDefaultUser()
     * @see LoginDialog
     */
    public static void main(String[] args) {
        try {
            DefaultSetup.createDefaultUser();
        } catch (Exception e) {
            System.err.println("Error en configuracion inicial: " + e.getMessage());
        }
        
        LoginDialog loginDlg = new LoginDialog(null);
        loginDlg.setVisible(true);

        if (loginDlg.isSucceeded()) {
            FileEncryptionApp app = new FileEncryptionApp();
            app.startApp();
        }
    }
}
