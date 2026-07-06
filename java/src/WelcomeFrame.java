import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class WelcomeFrame extends JFrame {

    public WelcomeFrame() {
        setTitle("ARS Mart - Inventory System");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(UITheme.BACKGROUND);

        JLabel title = new JLabel("ARS MART", SwingConstants.CENTER);
        title.setFont(UITheme.TITLE_FONT.deriveFont(Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBounds(0, 50, 700, 60);
        panel.add(title);

        JLabel subtitle = new JLabel("A Minimal Shopping Hub", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setBounds(0, 110, 700, 30);
        panel.add(subtitle);

        int y = 180;
        int buttonHeight = 55;
        int buttonWidth = 400;
        int x = (700 - buttonWidth) / 2;
        int spacing = 75;

        JButton adminBtn = createBlueButton("ADMIN LOGIN", false);
        adminBtn.setBounds(x, y, buttonWidth, buttonHeight);
        adminBtn.addActionListener(e -> {
            new AdminCodeFrame();
            dispose();
        });
        panel.add(adminBtn);
        y += spacing;

        JButton userBtn = createBlueButton("USER LOGIN", false);
        userBtn.setBounds(x, y, buttonWidth, buttonHeight);
        userBtn.addActionListener(e -> {
            new LoginFrame();
            dispose();
        });
        panel.add(userBtn);
        y += spacing;

        JButton registerBtn = createBlueButton("CREATE ACCOUNT", false);
        registerBtn.setBounds(x, y, buttonWidth, buttonHeight);
        registerBtn.addActionListener(e -> {
            new RegisterFrame();
            dispose();
        });
        panel.add(registerBtn);
        y += spacing;

        JButton exitBtn = createDangerButton("EXIT");
        exitBtn.setBounds(x, y, buttonWidth, buttonHeight);
        exitBtn.addActionListener(e -> System.exit(0));
        panel.add(exitBtn);

        add(panel);
        SwingEnterKeyBinder.bindEnterToButton(getRootPane(), adminBtn);
        setVisible(true);
    }

    private JButton createBlueButton(String text, boolean unused) {
        JButton button = new JButton(text);
        button.setBackground(UITheme.BUTTON);
        button.setForeground(Color.WHITE);
        button.setFont(UITheme.BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UITheme.BUTTON_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UITheme.BUTTON);
            }
        });
        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(UITheme.DANGER);
        button.setForeground(Color.WHITE);
        button.setFont(UITheme.BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(UITheme.DANGER_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(UITheme.DANGER);
            }
        });
        return button;
    }
}

