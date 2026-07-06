import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JLabel statusLabel;

    public RegisterFrame() {
        setTitle("Register New Account");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BACKGROUND);
        panel.setLayout(null);

        // Title
        JLabel title = new JLabel("CREATE NEW ACCOUNT");
        title.setBounds(80, 40, 400, 40);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.TITLE_FONT);
        panel.add(title);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(100, 100, 100, 25);
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(100, 130, 300, 40);
        usernameField.setFont(UITheme.NORMAL_FONT);
        usernameField.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        panel.add(usernameField);

        // Email Label
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(100, 175, 100, 25);
        emailLabel.setForeground(Color.WHITE);
        panel.add(emailLabel);

        // Email Field
        emailField = new JTextField();
        emailField.setBounds(100, 205, 300, 40);
        emailField.setFont(UITheme.NORMAL_FONT);
        emailField.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        panel.add(emailField);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(100, 250, 100, 25);
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 280, 300, 40);
        passwordField.setFont(UITheme.NORMAL_FONT);
        passwordField.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        panel.add(passwordField);

        // Password Requirements
        JLabel requirements = new JLabel(
            "<html>Password must contain: 8+ chars, uppercase, lowercase, digit</html>"
        );
        requirements.setBounds(100, 320, 300, 40);
        requirements.setForeground(Color.LIGHT_GRAY);
        requirements.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(requirements);

        // Status Label
        statusLabel = new JLabel("");
        statusLabel.setBounds(100, 360, 300, 20);
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel);

        // Register Button
        registerButton = new JButton("REGISTER");
        registerButton.setBounds(100, 390, 300, 45);
        registerButton.setBackground(UITheme.BUTTON);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(UITheme.BUTTON_FONT);
        registerButton.setBorderPainted(false);
        registerButton.addActionListener(e -> performRegister());
        panel.add(registerButton);


        // Back Button
        JButton backButton = new JButton("BACK");
        backButton.setBounds(100, 450, 300, 45);
        backButton.setBackground(new Color(200, 50, 50));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(UITheme.BUTTON_FONT);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> {
            new WelcomeFrame();
            dispose();
        });
        panel.add(backButton);

        add(panel);
        SwingEnterKeyBinder.bindEnterToButton(getRootPane(), registerButton);
        setVisible(true);
    }


    private void performRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        // ✅ Validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            updateStatus("Please fill all fields", Color.RED);
            return;
        }

        if (username.length() < 3) {
            updateStatus("Username must be at least 3 characters", Color.RED);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            updateStatus("Invalid email format", Color.RED);
            return;
        }

        if (password.length() < 8) {
            updateStatus("Password must be at least 8 characters", Color.RED);
            return;
        }

        registerButton.setEnabled(false);
        updateStatus("Registering...", Color.YELLOW);

        new Thread(() -> {
            try {
                String result = BackendConnector.runCommand(
                    "register " + username + " " + email + " " + password
                );

                System.out.println("[REGISTER RESULT] " + result);

                if (result.equalsIgnoreCase("REGISTER_SUCCESS") || result.equalsIgnoreCase("USER_CREATED") || result.equalsIgnoreCase("SUCCESS")) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Account created successfully!\nYou can now login.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        new LoginFrame();
                        dispose();
                    });
                } else if (result.equals("USER_EXISTS")) {
                    updateStatus("Username already exists!", Color.RED);
                } else if (result.equals("WEAK_PASSWORD")) {
                    updateStatus("Password is too weak!", Color.RED);
                } else if (result.equals("INVALID_EMAIL")) {
                    updateStatus("Invalid email format!", Color.RED);
                } else {
                    updateStatus("Registration failed: " + result, Color.RED);
                }
            } catch (Exception e) {
                updateStatus("Error: " + e.getMessage(), Color.RED);
            } finally {
                registerButton.setEnabled(true);
            }
        }).start();
    }

    private void updateStatus(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
        });
    }
}
