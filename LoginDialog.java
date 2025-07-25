import javax.swing.*;
import java.awt.*;

/**
 * Diálogo modal de autenticación para la aplicación de cifrado.
 * 
 * Esta clase proporciona una interfaz gráfica de login que solicita al usuario
 * sus credenciales (nombre de usuario, contraseña y respuesta de seguridad)
 * antes de permitir el acceso a la aplicación principal. Implementa validación
 * de credenciales usando el sistema de autenticación local.
 * 
 * <p>Características del diálogo:</p>
 * <ul>
 *   <li>Interfaz modal que bloquea acceso hasta autenticación exitosa</li>
 *   <li>Validación de usuario y contraseña contra almacenamiento local</li>
 *   <li>Pregunta de seguridad fija para verificación adicional</li>
 *   <li>Limpieza automática de campos tras fallos de autenticación</li>
 * </ul>
 * 
 * @author DenReanin
 * @version 1.0
 * @since 2025-07-25
 * 
 * @see UserAuth
 * @see JDialog
 */
public class LoginDialog extends JDialog {
    /** Campo de texto para el nombre de usuario */
    private JTextField usernameField;
    
    /** Campo de contraseña (oculta el texto ingresado) */
    private JPasswordField passwordField;
    
    /** Campo para la respuesta de la pregunta de seguridad */
    private JTextField securityAnswerField;
    
    /** Indica si la autenticación fue exitosa */
    private boolean succeeded;

    /**
     * Constructor que crea e inicializa el diálogo de login.
     * 
     * Configura la interfaz gráfica con campos de entrada para credenciales,
     * establece el layout y listeners de eventos, y configura las propiedades
     * del diálogo modal.
     * 
     * @param parent ventana padre que será bloqueada por este diálogo modal
     */
    public LoginDialog(Frame parent) {
        super(parent, "Login - Aplicacion de Cifrado", true);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Usuario: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(usernameLabel, cs);

        usernameField = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(usernameField, cs);

        JLabel passwordLabel = new JLabel("Contrasena: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(passwordLabel, cs);

        passwordField = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(passwordField, cs);

        JLabel securityQuestionLabel = new JLabel("Cual es tu color favorito?");
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(securityQuestionLabel, cs);

        securityAnswerField = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(securityAnswerField, cs);

        getContentPane().add(panel, BorderLayout.CENTER);

        JButton loginButton = new JButton("Iniciar Sesion");

        loginButton.addActionListener(e -> {
            // Autenticacion simple con usuarios locales
            if (UserAuth.authenticate(getUsername(), getPassword()) && checkSecurityAnswer()) {
                succeeded = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginDialog.this, 
                    "Usuario, contrasena o respuesta de seguridad incorrectos", 
                    "Error de Login", JOptionPane.ERROR_MESSAGE);
                resetLoginFields();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);

        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(new Dimension(500, 250));
    }

    /**
     * Limpia todos los campos de entrada del formulario de login.
     * 
     * Este método se ejecuta automáticamente después de un fallo de
     * autenticación para permitir al usuario intentar nuevamente
     * con credenciales limpias.
     */
    private void resetLoginFields() {
        usernameField.setText("");
        passwordField.setText("");
        securityAnswerField.setText("");
    }

    /**
     * Verifica la respuesta a la pregunta de seguridad.
     * 
     * Compara la respuesta ingresada con la respuesta esperada ("azul"),
     * ignorando diferencias de mayúsculas/minúsculas y espacios adicionales.
     * 
     * @return true si la respuesta es correcta, false en caso contrario
     */
    private boolean checkSecurityAnswer() {
        return "azul".equalsIgnoreCase(securityAnswerField.getText().trim());
    }

    /**
     * Obtiene el nombre de usuario ingresado en el formulario.
     * 
     * @return nombre de usuario sin espacios adicionales al inicio o final
     */
    public String getUsername() {
        return usernameField.getText().trim();
    }

    /**
     * Obtiene la contraseña ingresada en el formulario.
     * 
     * Convierte el array de caracteres del JPasswordField a String
     * para su uso en la autenticación.
     * 
     * @return contraseña como String
     */
    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    /**
     * Indica si el proceso de autenticación fue exitoso.
     * 
     * Este método debe ser consultado por la aplicación principal
     * después de mostrar el diálogo para determinar si se debe
     * continuar con la ejecución de la aplicación.
     * 
     * @return true si las credenciales fueron validadas correctamente,
     *         false si la autenticación falló o fue cancelada
     */
    public boolean isSucceeded() {
        return succeeded;
    }
}
