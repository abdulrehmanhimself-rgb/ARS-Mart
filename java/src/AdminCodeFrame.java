import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class AdminCodeFrame extends JFrame {

    private JPasswordField codeField;
    private JButton verifyButton;
    private JLabel statusLabel;

    public AdminCodeFrame() {
        setTitle("Admin Security Code");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(UITheme.BACKGROUND);

        // Title
        JLabel title = new JLabel("ENTER ADMIN CODE", SwingConstants.CENTER);
        title.setBounds(80, 40, 350, 40);
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(Color.WHITE);
        panel.add(title);

        // Code Field
        codeField = new JPasswordField();
        codeField.setBounds(100, 120, 280, 40);
        codeField.setFont(UITheme.NORMAL_FONT);
        codeField.setBorder(new LineBorder(Color.DARK_GRAY, 1));
        panel.add(codeField);

        // Status Label
        statusLabel = new JLabel("");
        statusLabel.setBounds(100, 165, 280, 20);
        statusLabel.setForeground(Color.RED);
        panel.add(statusLabel);

        // Verify Button
        verifyButton = new JButton("VERIFY");
        verifyButton.setBounds(150, 195, 180, 45);
        verifyButton.setBackground(UITheme.BUTTON);
        verifyButton.setForeground(Color.WHITE);
        verifyButton.setFont(UITheme.BUTTON_FONT);
        verifyButton.setBorderPainted(false);
        verifyButton.addActionListener(e -> verifyCode());
        panel.add(verifyButton);


        // Back Button
        JButton backButton = new JButton("BACK");
        backButton.setBounds(150, 250, 180, 45);
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
        SwingEnterKeyBinder.bindEnterToButton(getRootPane(), verifyButton);
        setVisible(true);
    }


    private void verifyCode() {
        String code = new String(codeField.getPassword()).trim();

        if (code.isEmpty()) {
            statusLabel.setText("Please enter admin code");
            statusLabel.setForeground(Color.RED);
            return;
        }

        verifyButton.setEnabled(false);
        statusLabel.setText("Verifying...");
        statusLabel.setForeground(Color.YELLOW);

        new Thread(() -> {
            try {
                // C++ validates admin code using Admin::validateAdminCode().
                // Password/username here are ignored for code validation purposes in your C++ logic.
                String result = BackendConnector.runCommand(
                        "adminlogin " + code + " sohail sohail123"
                );

                System.out.println("[ADMIN CODE VERIFY] Result='" + result + "'");

                String r = (result == null) ? "" : result.trim();
                boolean ok = r.equalsIgnoreCase("ADMIN_SUCCESS") || r.toUpperCase().contains("ADMIN_SUCCESS");

                if (ok) {
                    SwingUtilities.invokeLater(() -> {
                        new AdminLoginFrame(code);
                        dispose();
                    });
                } else if (r.equalsIgnoreCase("INVALID_ADMIN_CODE")) {
                    updateStatus("Invalid admin code!", Color.RED);
                } else {
                    // For wrong admin username/password the backend may return other tokens.
                    // For our UI, treat ONLY ADMIN_SUCCESS as pass-through.
                    updateStatus("Verification failed!", Color.RED);
                }
            } catch (Exception e) {
                updateStatus("Error: " + e.getMessage(), Color.RED);
            } finally {
                verifyButton.setEnabled(true);
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

