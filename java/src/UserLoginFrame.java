import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class UserLoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public UserLoginFrame() {
        setTitle("User Login");
        setSize(500, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BACKGROUND);
        panel.setLayout(null);

        // Title
        JLabel title = new JLabel("USER LOGIN");
        title.setBounds(140, 40, 300, 40);
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(Color.WHITE);
        panel.add(title);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(100, 100, 100, 25);
        usernameLabel.setForeground(Color.WHITE);
        panel.add(usernameLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(100, 130, 280, 40);
        usernameField.setFont(UITheme.NORMAL_FONT);
        usernameField.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        panel.add(usernameField);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(100, 175, 100, 25);
        passwordLabel.setForeground(Color.WHITE);
        panel.add(passwordLabel);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 205, 280, 40);
        passwordField.setFont(UITheme.NORMAL_FONT);
        passwordField.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        panel.add(passwordField);

        // Status Label
        statusLabel = new JLabel("");
        statusLabel.setBounds(100, 250, 280, 20);
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel);

        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(100, 280, 280, 45);
        loginButton.setBackground(UITheme.BUTTON);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(UITheme.BUTTON_FONT);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(e -> performLogin());
        panel.add(loginButton);

        // Back Button
        JButton backButton = new JButton("BACK");
        backButton.setBounds(100, 340, 280, 45);
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
        setVisible(true);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            statusLabel.setForeground(Color.RED);
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setText("Logging in...");
        statusLabel.setForeground(Color.YELLOW);

        new Thread(() -> {
            try {
                String result = BackendConnector.runCommand(
                        "userlogin " + username + " " + password
                );

                System.out.println("[LOGIN RESULT] " + result);

                String r = (result == null) ? "" : result.trim();
                String clean = stripAnsi(r);

                boolean isSuccess = clean.toUpperCase().contains("USER_SUCCESS");

                // Safety: never auto-login on substring matches.


                if (isSuccess) {
                    System.out.println("[LOGIN RESULT CHECK] success-token-raw='" + r + "' clean='" + clean + "'");
                    SwingUtilities.invokeLater(() -> {
                        new UserDashboard(username);
                        dispose();
                    });
                } else if (clean.equals("USER_NOT_FOUND")) {
                    updateStatus("User not found!", Color.RED);
                } else if (clean.equals("WRONG_PASSWORD")) {
                    updateStatus("Wrong password!", Color.RED);
                } else if (clean.startsWith("ERROR")) {
                    updateStatus("Connection error!", Color.RED);
                } else {
                    System.out.println("[LOGIN RESULT CHECK] raw='" + r + "' clean='" + clean + "'");
                    updateStatus("Invalid username or password" , Color.RED);
                }
            } catch (Exception e) {
                updateStatus("Exception: " + e.getMessage(), Color.RED);
            } finally {
                loginButton.setEnabled(true);
            }
        }).start();
    }

    private static String stripAnsi(String s) {
        if (s == null) return "";
        return s.replaceAll("\\u001B\\[[0-9;]*[A-Za-z]", "");
    }

    private void updateStatus(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
        });
    }
}

