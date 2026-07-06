import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    public LoginFrame() {
        setTitle("ARS Mart - Login");
        setSize(550, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(UITheme.BACKGROUND);
        mainPanel.setLayout(null);

        // Title
        JLabel title = new JLabel("ARS MART", SwingConstants.CENTER);
        title.setBounds(70, 40, 400, 50);
        title.setForeground(UITheme.TEXT);
        title.setFont(UITheme.TITLE_FONT);
        mainPanel.add(title);

        // Subtitle
        JLabel subtitle = new JLabel("User Login", SwingConstants.CENTER);
        subtitle.setBounds(150, 90, 250, 30);
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setFont(UITheme.NORMAL_FONT);
        mainPanel.add(subtitle);

        // Username Label
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(100, 140, 100, 25);
        usernameLabel.setForeground(Color.WHITE);
        mainPanel.add(usernameLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(100, 170, 280, 40);
        usernameField.setFont(UITheme.NORMAL_FONT);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        mainPanel.add(usernameField);

        // Password Label
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(100, 210, 100, 25);
        passwordLabel.setForeground(Color.WHITE);
        mainPanel.add(passwordLabel);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(100, 240, 280, 40);
        passwordField.setFont(UITheme.NORMAL_FONT);
        passwordField.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        mainPanel.add(passwordField);

        // Status Label
        statusLabel = new JLabel("");
        statusLabel.setBounds(100, 280, 280, 25);
        statusLabel.setForeground(Color.YELLOW);
        mainPanel.add(statusLabel);

        // Login Button
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(100, 320, 280, 45);
        styleButton(loginButton);
        loginButton.addActionListener(e -> performLogin());
        mainPanel.add(loginButton);


        // Register Button
        registerButton = new JButton("CREATE ACCOUNT");
        registerButton.setBounds(100, 380, 280, 45);
        styleButton(registerButton);
        registerButton.addActionListener(e -> {
            new RegisterFrame();
            dispose();
        });
        mainPanel.add(registerButton);

        // Back Button
        JButton backButton = new JButton("BACK");
        backButton.setBounds(100, 440, 280, 45);
        styleButton(backButton);
        backButton.addActionListener(e -> {
            new WelcomeFrame();
            dispose();
        });
        mainPanel.add(backButton);

        add(mainPanel);
        SwingEnterKeyBinder.bindEnterToButton(getRootPane(), loginButton);
        setVisible(true);
    }


    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // ✅ Validation
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            statusLabel.setForeground(Color.RED);
            return;
        }

        // ✅ Disable button during login
        loginButton.setEnabled(false);
        statusLabel.setText("Logging in...");
        statusLabel.setForeground(Color.YELLOW);

        // ✅ Run in background thread
        new Thread(() -> {
            try {
                String result = BackendConnector.runCommand(
                    "userlogin " + username + " " + password
                );


                System.out.println("[LOGIN RESULT] " + result);

                String r = (result == null) ? "" : result.trim();
                String clean = stripAnsi(r);

                boolean isSuccess = clean.toUpperCase().contains("USER_SUCCESS");

                if (isSuccess) {
                    System.out.println("[LOGIN RESULT CHECK] success-token-raw='" + r + "' clean='" + clean + "'");
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(LoginFrame.this,
                            "User login successful!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    });
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
                    updateStatus("Invalid username or password", Color.RED);
                }
            } catch (Exception e) {
                updateStatus("Exception: " + e.getMessage(), Color.RED);
            } finally {
                loginButton.setEnabled(true);
            }
        }).start();
    }

    private void updateStatus(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
        });
    }

    private static String stripAnsi(String s) {
        if (s == null) return "";
        // ANSI escape sequence pattern: ESC [ ... letter
        return s.replaceAll("\\u001B\\[[0-9;]*[A-Za-z]", "");
    }

    private void styleButton(JButton button) {
        button.setBackground(UITheme.BUTTON);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(UITheme.BUTTON_FONT);
        button.setBorderPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UITheme.BUTTON_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(UITheme.BUTTON);
            }
        });
    }
}
