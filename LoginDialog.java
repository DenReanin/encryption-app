import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField securityAnswerField;
    private boolean succeeded;

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

    private void resetLoginFields() {
        usernameField.setText("");
        passwordField.setText("");
        securityAnswerField.setText("");
    }

    private boolean checkSecurityAnswer() {
        return "azul".equalsIgnoreCase(securityAnswerField.getText().trim());
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
