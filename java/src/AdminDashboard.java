import java.awt.*;
import javax.swing.*;

public class AdminDashboard extends JFrame {

    private final String username;

    public AdminDashboard(String username) {
        this.username = username;

        setTitle("Admin Dashboard - " + username);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BACKGROUND);
        panel.setLayout(null);

        // Title
        JLabel title = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        title.setBounds(0, 20, 700, 50);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.TITLE_FONT);
        panel.add(title);

        // Username Display
        JLabel userDisplay = new JLabel("Welcome, " + username);
        userDisplay.setBounds(150, 70, 400, 30);
        userDisplay.setForeground(Color.LIGHT_GRAY);
        userDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(userDisplay);

        int y = 120;
        int buttonHeight = 50;
        int buttonSpacing = 60;

        String[][] buttons = {
            {"Add Product", "addproduct"},
            {"View Inventory", "inventory"},
            {"Restock Product", "restock"},
            {"Update Price", "updateprice"},
            {"Sales Report", "salesreport"},
            {"View History", "history"},
            {"Logout", "logout"}
        };

        for (String[] btn : buttons) {
            JButton button = createButton(btn[0], btn[1]);
            // Exit/Back/Logout must be red everywhere
            if (btn[1].equals("logout") || btn[1].equals("back") || btn[1].equals("exit")) {
                button.setBackground(new Color(200, 50, 50));
                button.setForeground(Color.WHITE);
            }

            button.setBounds(150, y, 400, buttonHeight);
            panel.add(button);
            y += buttonSpacing;
        }


        add(panel);
        setVisible(true);
    }

    private JButton createButton(String text, String action) {
        JButton button = new JButton(text);
        button.setBackground(UITheme.BUTTON);
        button.setForeground(Color.WHITE);
        button.setFont(UITheme.BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);



        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(UITheme.BUTTON_HOVER);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(UITheme.BUTTON);
            }
        });

        button.addActionListener(e -> handleAction(action));
        return button;
    }

    private void showInventory() {
        String result = BackendConnector.runCommand("inventory");
        if (result == null || result.isEmpty() || result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, "Failed to load inventory.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextArea area = new JTextArea(result);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBackground(new Color(20, 20, 24));
        area.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(650, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Inventory", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSalesReport() {
        String result = BackendConnector.runCommand("salesreport");
        if (result == null || result.isEmpty() || result.equalsIgnoreCase("EMPTY") || result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, "No sales found.", "Sales Report", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea area = new JTextArea(result);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBackground(new Color(20, 20, 24));
        area.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(650, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Sales Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAdminHistory() {
        String result = BackendConnector.runCommand("history_all");
        if (result == null || result.isEmpty() || result.equalsIgnoreCase("EMPTY") || result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, "No history found.", "History", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea area = new JTextArea(result);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBackground(new Color(20, 20, 24));
        area.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(650, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "History", JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleAction(String action) {
        switch (action) {
            case "inventory":
                showInventory();
                break;
            case "restock":
                showRestockForm();
                break;
            case "updateprice":
                showUpdatePriceForm();
                break;
            case "addproduct":
                showAddProductForm();
                break;
            case "salesreport":
                showSalesReport();
                break;
            case "history":
                showAdminHistory();
                break;
            case "logout":
                new WelcomeFrame();
                dispose();
                break;

        }
    }

    private void showAddProductForm() {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBackground(UITheme.BACKGROUND);

        // Create text fields with placeholder functionality
        JTextField categoryField = createPlaceholderField("Category name");
        JTextField nameField = createPlaceholderField("Product Name");
        JTextField stockField = createPlaceholderField("Stock");
        JTextField priceField = createPlaceholderField("Product Price");

        form.add(new JLabel("Category:"));
        form.add(categoryField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Stock:"));
        form.add(stockField);
        form.add(new JLabel("Price:"));
        form.add(priceField);

        int res = JOptionPane.showConfirmDialog(this, form, "Add Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String category = categoryField.getText().trim();
        String name = nameField.getText().trim();
        String stockText = stockField.getText().trim();
        String priceText = priceField.getText().trim();

        if (category.isEmpty() || name.isEmpty() || stockText.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cmd = "admin_addproduct " + category + " " + name + " " + stockText + " " + priceText;
        String result = BackendConnector.runCommand(cmd);
        if (result == null || result.isEmpty() || result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this,
                    "Failed to add product. Backend said:\n" + result,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Product add response: " + result,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showRestockForm() {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBackground(UITheme.BACKGROUND);

        JTextField nameField = new JTextField();
        JTextField qtyField = new JTextField();

        form.add(new JLabel("Product Name:"));
        form.add(nameField);
        form.add(new JLabel("Add Quantity:"));
        form.add(qtyField);

        int res = JOptionPane.showConfirmDialog(this, form, "Restock Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String qtyText = qtyField.getText().trim();

        if (name.isEmpty() || qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cmd = "admin_restock " + name + " " + qtyText;
        String result = BackendConnector.runCommand(cmd);
        if (result == null || result.isEmpty() || result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, "Failed to restock product.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (result.startsWith("OK|")) {
            JOptionPane.showMessageDialog(this, "Restock successful: " + result, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Restock failed: " + result, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUpdatePriceForm() {
        JPanel form = new JPanel(new GridLayout(0, 2, 10, 8));
        form.setBackground(UITheme.BACKGROUND);

        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();

        form.add(new JLabel("Product Name:"));
        form.add(nameField);
        form.add(new JLabel("New Price:"));
        form.add(priceField);

        int res = JOptionPane.showConfirmDialog(this, form, "Update Price", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String cmd = "admin_updateprice " + name + " " + priceText;
        String result = BackendConnector.runCommand(cmd);
        if (result == null || result.isEmpty() || result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, "Failed to update price.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (result.startsWith("OK|")) {
            JOptionPane.showMessageDialog(this, "Price updated successfully: " + result, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Update price failed: " + result, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Create a text field with placeholder text that disappears on focus
     */
    private JTextField createPlaceholderField(String placeholder) {
        JTextField field = new JTextField() {
            private boolean isPlaceholder = true;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (isPlaceholder && getText().isEmpty()) {
                    g.setColor(new Color(150, 150, 150));
                    g.setFont(getFont());
                    g.drawString(placeholder, 5, getHeight() / 2 + 5);
                }
            }
        };

        field.setText(placeholder);
        field.setForeground(new Color(150, 150, 150));
        field.setBackground(new Color(40, 40, 45));
        field.setCaretColor(Color.WHITE);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 150, 150));
                }
            }
        });

        return field;
    }

}

