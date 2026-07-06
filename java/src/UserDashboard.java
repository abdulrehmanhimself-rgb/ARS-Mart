import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class UserDashboard extends JFrame {

    private final String username;
    private FloatingChatButton floatingChatButton;  // ← ADD THIS FIELD

    public UserDashboard(String username) {
        this.username = username;

        setTitle("User Dashboard - " + username);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BACKGROUND);
        panel.setLayout(null);

        JLabel title = new JLabel("USER DASHBOARD", SwingConstants.CENTER);
        title.setBounds(0, 20, 700, 50);
        title.setForeground(Color.WHITE);
        title.setFont(UITheme.TITLE_FONT);
        panel.add(title);

        JLabel welcome = new JLabel("Welcome, " + username);
        welcome.setBounds(150, 70, 400, 30);
        welcome.setForeground(Color.LIGHT_GRAY);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(welcome);

        int y = 120;
        int buttonHeight = 50;
        int buttonSpacing = 60;

        String[][] buttons = {
                {"Browse Products", "browse"},
                {"Search Products", "search"},
                {"Shopping Cart", "cart"},
                {"View Orders", "orders"},
                {"My Profile", "profile"},
                {"Logout", "logout"}
        };

        for (String[] btn : buttons) {
            JButton button = createButton(btn[0], btn[1]);
            button.setBounds(150, y, 400, buttonHeight);
            panel.add(button);
            y += buttonSpacing;
        }

        add(panel);
        setVisible(true);
        
        // ← ADD THIS - Creates floating chat button after frame is visible
        SwingUtilities.invokeLater(() -> {
            floatingChatButton = new FloatingChatButton(this, username);
        });
    }

    private JButton createButton(String text, String action) {
        JButton button = new JButton(text);

        if (action.equals("logout") || action.equals("back") || action.equals("exit")) {
            button.setBackground(new Color(200, 50, 50));
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(UITheme.BUTTON);
            button.setForeground(Color.WHITE);
        }

        button.setFont(UITheme.BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (action.equals("logout") || action.equals("back") || action.equals("exit")) {
                    button.setBackground(new Color(220, 60, 60));
                } else {
                    button.setBackground(UITheme.BUTTON_HOVER);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (action.equals("logout") || action.equals("back") || action.equals("exit")) {
                    button.setBackground(new Color(200, 50, 50));
                } else {
                    button.setBackground(UITheme.BUTTON);
                }
            }
        });

        button.addActionListener(e -> handleAction(action));
        return button;
    }

    private void showInventory() {
        String result = BackendConnector.runCommand("inventory");
        if (result == null || result.isEmpty() || result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, "Could not load products.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        class Product {
            String name, category;
            int stock;
            double price;
        }

        java.util.List<Product> products = new java.util.ArrayList<>();
        for (String line : result.split("\n")) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("\\|");
            if (parts.length < 4) continue;
            Product p = new Product();
            p.name = parts[0];
            p.category = parts[1];
            p.stock = Integer.parseInt(parts[2]);
            p.price = Double.parseDouble(parts[3]);
            products.add(p);
        }

        JPanel vertical = new JPanel();
        vertical.setBackground(UITheme.BACKGROUND);
        vertical.setLayout(new BoxLayout(vertical, BoxLayout.Y_AXIS));

        java.util.Map<String, java.util.List<Product>> byCategory = new java.util.LinkedHashMap<>();
        for (Product prod : products) {
            byCategory.computeIfAbsent(prod.category, k -> new java.util.ArrayList<>()).add(prod);
        }

        for (java.util.Map.Entry<String, java.util.List<Product>> entry : byCategory.entrySet()) {
            String category = entry.getKey();

            JLabel catLabel = new JLabel(category, SwingConstants.LEFT);
            catLabel.setForeground(Color.WHITE);
            catLabel.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD, 18f));
            catLabel.setBorder(BorderFactory.createEmptyBorder(14, 10, 8, 10));
            vertical.add(catLabel);

            JPanel catGrid = new JPanel(new GridLayout(0, 2, 12, 12));
            catGrid.setBackground(UITheme.BACKGROUND);

            for (Product prod : entry.getValue()) {
                JPanel box = new JPanel();
                box.setBackground(UITheme.PANEL);
                box.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
                box.setLayout(new BorderLayout(6, 6));

                JLabel name = new JLabel("\uD83D\uDED2 " + prod.name, SwingConstants.CENTER);
                name.setForeground(Color.WHITE);
                name.setFont(UITheme.LABEL_FONT);

                JLabel meta = new JLabel("Stock: " + prod.stock, SwingConstants.CENTER);
                meta.setForeground(new Color(220, 220, 220));

                JLabel price = new JLabel("Rs. " + String.format("%.2f", prod.price), SwingConstants.CENTER);
                price.setForeground(UITheme.WARNING);
                price.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));

                JTextField qtyField = new JTextField("1");
                qtyField.setHorizontalAlignment(JTextField.CENTER);

                JButton addBtn = new JButton("Add to Cart");
                addBtn.setBackground(UITheme.BUTTON);
                addBtn.setForeground(Color.WHITE);
                addBtn.setFont(UITheme.BUTTON_FONT);
                addBtn.setFocusPainted(false);
                addBtn.setBorderPainted(false);

                addBtn.addActionListener(ev -> {
                    String qtyText = qtyField.getText().trim();
                    int qty;
                    try {
                        qty = Integer.parseInt(qtyText);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(UserDashboard.this, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (qty <= 0) {
                        JOptionPane.showMessageDialog(UserDashboard.this, "Quantity must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Cart localCart = new Cart(username);
                    localCart.addItem(prod.name, prod.category, qty, prod.price);
                    localCart.saveToFile();

                    String addResult = BackendConnector.runCommand("cart_add " + username + " " + prod.name + " " + qty);
                    
                    JOptionPane.showMessageDialog(UserDashboard.this, 
                            "✓ " + qty + " × " + prod.name + "\n\nSuccessfully added to cart!", 
                            "Added to Cart", 
                            JOptionPane.INFORMATION_MESSAGE);
                    qtyField.setText("1");
                });

                JPanel bottom = new JPanel(new BorderLayout(6, 6));
                bottom.setBackground(UITheme.PANEL);
                bottom.add(new JLabel("Qty:"), BorderLayout.WEST);
                bottom.add(qtyField, BorderLayout.CENTER);
                bottom.add(addBtn, BorderLayout.SOUTH);

                JPanel center = new JPanel(new GridLayout(3, 1, 4, 4));
                center.setBackground(UITheme.PANEL);
                center.add(name);
                center.add(meta);
                center.add(price);

                box.add(center, BorderLayout.NORTH);
                box.add(bottom, BorderLayout.SOUTH);

                catGrid.add(box);
            }

            vertical.add(catGrid);
        }

        JScrollPane scrollPane = new JScrollPane(vertical);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(700, 500));

        JDialog dialog = new JDialog(this, "Products", true);
        dialog.setSize(760, 560);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showCart() {
        CartUI cartUI = new CartUI(this, username);
        cartUI.setVisible(true);
    }
    
    private void showSearchProducts() {
        String keyword = JOptionPane.showInputDialog(this, "Enter product name or category to search:");
        if (keyword == null) return;
        
        keyword = keyword.trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Search keyword cannot be empty.", "Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String result = BackendConnector.runCommand("inventory");
        if (result == null || result.isEmpty() || result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, "Could not load products.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        class Product {
            String name, category;
            int stock;
            double price;
        }
        
        java.util.List<Product> matched = new java.util.ArrayList<>();
        String k = keyword.toLowerCase();
        
        for (String line : result.split("\n")) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split("\\|");
            if (parts.length < 4) continue;
            
            Product p = new Product();
            p.name = parts[0].trim();
            p.category = parts[1].trim();
            p.stock = Integer.parseInt(parts[2].trim());
            p.price = Double.parseDouble(parts[3].trim());
            
            if (p.name.toLowerCase().contains(k) || p.category.toLowerCase().contains(k)) {
                matched.add(p);
            }
        }
        
        if (matched.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products found for: " + keyword, "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JPanel vertical = new JPanel();
        vertical.setBackground(UITheme.BACKGROUND);
        vertical.setLayout(new BoxLayout(vertical, BoxLayout.Y_AXIS));
        
        JLabel header = new JLabel("Search results for: " + keyword);
        header.setForeground(Color.WHITE);
        header.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD, 18f));
        header.setBorder(BorderFactory.createEmptyBorder(14, 10, 8, 10));
        vertical.add(header);
        
        JPanel grid = new JPanel(new GridLayout(0, 2, 12, 12));
        grid.setBackground(UITheme.BACKGROUND);
        
        for (Product prod : matched) {
            JPanel box = new JPanel();
            box.setBackground(UITheme.PANEL);
            box.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
            box.setLayout(new BorderLayout(6, 6));
            
            JLabel name = new JLabel("\uD83D\uDED2 " + prod.name, SwingConstants.CENTER);
            name.setForeground(Color.WHITE);
            name.setFont(UITheme.LABEL_FONT);
            
            JLabel meta = new JLabel("Stock: " + prod.stock, SwingConstants.CENTER);
            meta.setForeground(new Color(220, 220, 220));
            
            JLabel price = new JLabel("Rs. " + String.format("%.2f", prod.price), SwingConstants.CENTER);
            price.setForeground(UITheme.WARNING);
            price.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD));
            
            JTextField qtyField = new JTextField("1");
            qtyField.setHorizontalAlignment(JTextField.CENTER);
            
            JButton addBtn = new JButton("Add to Cart");
            addBtn.setBackground(UITheme.BUTTON);
            addBtn.setForeground(Color.WHITE);
            addBtn.setFont(UITheme.BUTTON_FONT);
            addBtn.setFocusPainted(false);
            addBtn.setBorderPainted(false);
            
            addBtn.addActionListener(ev -> {
                String qtyText = qtyField.getText().trim();
                int qty;
                try {
                    qty = Integer.parseInt(qtyText);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(UserDashboard.this, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(UserDashboard.this, "Quantity must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Cart localCart = new Cart(username);
                localCart.addItem(prod.name, prod.category, qty, prod.price);
                localCart.saveToFile();
                
                BackendConnector.runCommand("cart_add " + username + " " + prod.name + " " + qty);
                
                JOptionPane.showMessageDialog(UserDashboard.this,
                        "✓ " + qty + " × " + prod.name + "\n\nSuccessfully added to cart!",
                        "Added to Cart",
                        JOptionPane.INFORMATION_MESSAGE);
                qtyField.setText("1");
            });
            
            JPanel bottom = new JPanel(new BorderLayout(6, 6));
            bottom.setBackground(UITheme.PANEL);
            bottom.add(new JLabel("Qty:"), BorderLayout.WEST);
            bottom.add(qtyField, BorderLayout.CENTER);
            bottom.add(addBtn, BorderLayout.SOUTH);
            
            JPanel center = new JPanel(new GridLayout(3, 1, 4, 4));
            center.setBackground(UITheme.PANEL);
            center.add(name);
            center.add(meta);
            center.add(price);
            
            box.add(center, BorderLayout.NORTH);
            box.add(bottom, BorderLayout.SOUTH);
            
            grid.add(box);
        }
        
        vertical.add(grid);
        
        JScrollPane scrollPane = new JScrollPane(vertical);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(700, 500));
        
        JDialog dialog = new JDialog(this, "Search Products", true);
        dialog.setSize(760, 560);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.BACKGROUND);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void handleAction(String action) {
        switch (action) {
            case "browse":
                showInventory();
                break;
            case "search":
                showSearchProducts();
                break;
            case "cart":
                showCart();
                break;
            case "orders":
                showOrders();
                break;
            case "profile":
                UserDashboardProfileUtil.showProfile(this, username);
                break;
            case "logout":
                new WelcomeFrame();
                dispose();
                break;
        }
    }

    private void showOrders() {
        String result = BackendConnector.runCommand("history_user " + username);
        if (result == null || result.isEmpty() || result.equalsIgnoreCase("EMPTY") || result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(this, "No orders found.", "Orders", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea area = new JTextArea(result);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBackground(new Color(20, 20, 24));
        area.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(650, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Your Orders", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // ← ADD THIS METHOD - Clean up when window closes
    @Override
    public void dispose() {
        if (floatingChatButton != null) {
            floatingChatButton.dispose();
        }
        super.dispose();
    }
}