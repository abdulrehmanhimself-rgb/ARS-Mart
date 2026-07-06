import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CartUI extends JDialog {
    private String username;
    private Cart cart;
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private JLabel subtotalLabel;
    private JLabel chargesLabel;
    private JLabel totalLabel;
    private JButton updateBtn;
    private JButton removeBtn;

    public CartUI(JFrame parent, String username) {
        super(parent, "Shopping Cart - " + username, true);
        this.username = username;
        this.cart = new Cart(username);
        
        initializeUI();
    }

    private void initializeUI() {
        setSize(900, 650);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(UITheme.BACKGROUND);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Title
        JLabel titleLabel = new JLabel("SHOPPING CART", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Center: Items Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(UITheme.BACKGROUND);

        centerPanel.add(createItemsTable(), BorderLayout.CENTER);
        centerPanel.add(createControlPanel(), BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // South: Summary and Buttons
        mainPanel.add(createSummaryAndButtons(), BorderLayout.SOUTH);

        add(mainPanel);
        refreshCartDisplay();

        // ENTER should update quantity (auto-next)
        SwingEnterKeyBinder.bindEnterToButton(getRootPane(), updateBtn);
    }


    private JPanel createItemsTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.BACKGROUND);

        // Create table model
        tableModel = new DefaultTableModel(new String[]{"Product", "Category", "Qty", "Price", "Subtotal"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only quantity column is editable
            }
        };

        itemsTable = new JTable(tableModel);
        itemsTable.setBackground(new Color(30, 30, 35));
        itemsTable.setForeground(Color.WHITE);
        itemsTable.setGridColor(new Color(60, 60, 70));
        itemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itemsTable.setRowHeight(25);

        // Header styling
        itemsTable.getTableHeader().setBackground(UITheme.BUTTON);
        itemsTable.getTableHeader().setForeground(Color.WHITE);
        itemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Populate table with cart items
        List<String[]> items = cart.getItems();
        for (String[] item : items) {
            tableModel.addRow(new Object[]{
                    item[0],                                    // Product
                    item[1],                                    // Category
                    Integer.parseInt(item[2]),                 // Qty
                    String.format("Rs. %.2f", Double.parseDouble(item[3])),  // Price
                    String.format("Rs. %.2f", Double.parseDouble(item[4]))   // Subtotal
            });
        }

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(UITheme.BACKGROUND);

        updateBtn = new JButton("Update Qty");
        updateBtn.setBackground(UITheme.BUTTON);
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFont(UITheme.BUTTON_FONT);
        updateBtn.setFocusPainted(false);
        updateBtn.setBorderPainted(false);
        updateBtn.addActionListener(e -> updateSelectedItem());

        removeBtn = new JButton("Remove Item");
        removeBtn.setBackground(new Color(200, 100, 50));
        removeBtn.setForeground(Color.WHITE);
        removeBtn.setFont(UITheme.BUTTON_FONT);
        removeBtn.setFocusPainted(false);
        removeBtn.setBorderPainted(false);
        removeBtn.addActionListener(e -> removeSelectedItem());

        panel.add(updateBtn);
        panel.add(removeBtn);

        return panel;
    }

    private JPanel createSummaryAndButtons() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.BACKGROUND);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Summary Panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setBackground(UITheme.PANEL);
        summaryPanel.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 70), 1));
        summaryPanel.setLayout(new GridLayout(3, 2, 15, 10));
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel subtotalTitle = new JLabel("Subtotal:", SwingConstants.LEFT);
        subtotalTitle.setForeground(Color.WHITE);
        subtotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));

        subtotalLabel = new JLabel("Rs. 0.00", SwingConstants.RIGHT);
        subtotalLabel.setForeground(UITheme.WARNING);
        subtotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel chargesTitle = new JLabel("Payment Charges:", SwingConstants.LEFT);
        chargesTitle.setForeground(Color.WHITE);
        chargesTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));

        chargesLabel = new JLabel("Rs. 0.00", SwingConstants.RIGHT);
        chargesLabel.setForeground(new Color(255, 165, 0));
        chargesLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel totalTitle = new JLabel("TOTAL:", SwingConstants.LEFT);
        totalTitle.setForeground(Color.WHITE);
        totalTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        totalLabel = new JLabel("Rs. 0.00", SwingConstants.RIGHT);
        totalLabel.setForeground(UITheme.WARNING);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        summaryPanel.add(subtotalTitle);
        summaryPanel.add(subtotalLabel);
        summaryPanel.add(chargesTitle);
        summaryPanel.add(chargesLabel);
        summaryPanel.add(totalTitle);
        summaryPanel.add(totalLabel);

        panel.add(summaryPanel, BorderLayout.WEST);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(UITheme.BACKGROUND);

        JButton continueShoppingBtn = new JButton("Continue Shopping");
        continueShoppingBtn.setBackground(new Color(100, 100, 150));
        continueShoppingBtn.setForeground(Color.WHITE);
        continueShoppingBtn.setFont(UITheme.BUTTON_FONT);
        continueShoppingBtn.setFocusPainted(false);
        continueShoppingBtn.setBorderPainted(false);
        continueShoppingBtn.addActionListener(e -> dispose());

        JButton clearCartBtn = new JButton("Clear Cart");
        clearCartBtn.setBackground(new Color(200, 100, 50));
        clearCartBtn.setForeground(Color.WHITE);
        clearCartBtn.setFont(UITheme.BUTTON_FONT);
        clearCartBtn.setFocusPainted(false);
        clearCartBtn.setBorderPainted(false);
        clearCartBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to clear the cart?",
                    "Clear Cart",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                cart.clear();
                refreshCartDisplay();
            }
        });

        JButton checkoutBtn = new JButton("Proceed to Checkout");
        checkoutBtn.setBackground(UITheme.BUTTON);
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(UITheme.BUTTON_FONT);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setBorderPainted(false);
        checkoutBtn.addActionListener(e -> proceedToCheckout());

        buttonsPanel.add(continueShoppingBtn);
        buttonsPanel.add(clearCartBtn);
        buttonsPanel.add(checkoutBtn);

        panel.add(buttonsPanel, BorderLayout.EAST);

        return panel;
    }

    private void updateSelectedItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String productName = (String) tableModel.getValueAt(selectedRow, 0);
        int newQty;

        try {
            String input = JOptionPane.showInputDialog(this, "Enter new quantity:", "1");
            if (input == null) return; // User cancelled
            newQty = Integer.parseInt(input);
            
            if (newQty <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            cart.updateItemQuantity(productName, newQty);
            refreshCartDisplay();
            JOptionPane.showMessageDialog(this, "Quantity updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity entered", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeSelectedItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to remove", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String productName = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + productName + " from cart?",
                "Remove Item",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            cart.removeItem(productName);
            refreshCartDisplay();
            JOptionPane.showMessageDialog(this, "Item removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void proceedToCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Use subtotal for payment dialog (not final bill which already includes charges)
        double subtotal = cart.getSubtotal();

        // Show payment dialog with new enhanced features
        PaymentDialog paymentDialog = new PaymentDialog((JFrame) getParent(), subtotal);
        paymentDialog.setVisible(true);

        if (!paymentDialog.isConfirmed() || paymentDialog.getSelectedMethod() == null) {
            return; // User cancelled
        }

        // Get all details from payment dialog
        String method = paymentDialog.getSelectedMethod();
        double charges = paymentDialog.getCharges();
        double discount = paymentDialog.getDiscount();
        double shippingFee = paymentDialog.isFreeShipping() ? 0 : paymentDialog.getShippingFee();
        String appliedPromoCode = paymentDialog.getAppliedPromoCode();

        // Calculate final amount for display
        double finalAmount = paymentDialog.getFinalAmount();

        // Show order summary
        showOrderSummary(method, charges, discount, shippingFee, appliedPromoCode, finalAmount);
    }

    private void showOrderSummary(String paymentMethod, double charges, double discount, double shippingFee, String promoCode, double finalAmount) {
        StringBuilder summary = new StringBuilder();
        summary.append("════════════════════════════════════════════════\n");
        summary.append("ORDER SUMMARY\n");
        summary.append("════════════════════════════════════════════════\n\n");

        List<String[]> items = cart.getItems();
        summary.append(String.format("%-30s | Qty | Price | Subtotal\n", "Product"));
        summary.append("───────────────────────────────────────────────\n");

        double subtotal = 0;
        for (String[] item : items) {
            summary.append(String.format("%-30s | %3s | %5s | %8s\n",
                    item[0].length() > 30 ? item[0].substring(0, 27) + "..." : item[0],
                    item[2], item[3], item[4]));
            subtotal += Double.parseDouble(item[4]);
        }

        summary.append("───────────────────────────────────────────────\n");
        summary.append(String.format("Subtotal:          Rs. %.2f\n", subtotal));
        
        if (discount > 0) {
            summary.append(String.format("Discount:          - Rs. %.2f\n", discount));
            if (promoCode != null) {
                summary.append(String.format("(Promo: %s)\n", promoCode));
            }
        }
        
        if (shippingFee > 0) {
            summary.append(String.format("Shipping:          Rs. %.2f\n", shippingFee));
        } else if (shippingFee == 0 && promoCode != null) {
            summary.append("Shipping:          FREE (Promo)\n");
        }
        
        summary.append(String.format("Payment Method:    %s\n", paymentMethod));
        if (charges > 0) {
            summary.append(String.format("Extra Charges:     Rs. %.2f\n", charges));
        }
        summary.append("───────────────────────────────────────────────\n");
        summary.append(String.format("TOTAL AMOUNT:      Rs. %.2f\n", finalAmount));
        summary.append("════════════════════════════════════════════════\n");

        int confirm = JOptionPane.showConfirmDialog(this,
                summary.toString(),
                "Confirm Order",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            processCheckout(paymentMethod, discount, shippingFee, charges, promoCode);
        }
    }

    private void processCheckout(String paymentMethod, double discount, double shippingFee, double charges, String promoCode) {
        try {
            // Create receipt for the entire cart
            List<String[]> items = cart.getItems();
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Cart is empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double subtotal = cart.getSubtotal();

            // Create receipt with new pricing details
            Receipt receipt = new Receipt(username, items, subtotal,
                    discount, shippingFee, charges, paymentMethod, promoCode);

            // Save receipt
            receipt.saveToFile();
            receipt.saveToUserHistory();
            
            // Log sale to sales report  
            for (String[] item : items) {
                String productName = item[0];
                int qty = Integer.parseInt(item[2]);
                double price = Double.parseDouble(item[3]);
                double itemTotal = Double.parseDouble(item[4]);
                
                String saleCommand = "record_sale " + productName + " " + qty + " " + itemTotal;
                BackendConnector.runCommand(saleCommand);
            }

            // Show receipt
            JTextArea receiptArea = new JTextArea(receipt.getFormattedReceipt());
            receiptArea.setEditable(false);
            receiptArea.setFont(new Font("Courier New", Font.PLAIN, 11));
            receiptArea.setBackground(new Color(20, 20, 24));
            receiptArea.setForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(receiptArea);
            scrollPane.setPreferredSize(new Dimension(700, 500));

            JOptionPane.showMessageDialog(this, scrollPane, "ORDER CONFIRMATION & RECEIPT", JOptionPane.INFORMATION_MESSAGE);

            // Clear cart
            cart.clear();
            cart.saveToFile();

            // Close dialog
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error processing order: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void refreshCartDisplay() {
        // Clear table
        tableModel.setRowCount(0);

        // Reload items
        List<String[]> items = cart.getItems();
        if (items.isEmpty()) {
            // Add empty row
            tableModel.addRow(new Object[]{"Your cart is empty", "", "", "", ""});
        } else {
            for (String[] item : items) {
                tableModel.addRow(new Object[]{
                        item[0],
                        item[1],
                        Integer.parseInt(item[2]),
                        String.format("Rs. %.2f", Double.parseDouble(item[3])),
                        String.format("Rs. %.2f", Double.parseDouble(item[4]))
                });
            }
        }

        // Update summary
        subtotalLabel.setText(String.format("Rs. %.2f", cart.getSubtotal()));
        chargesLabel.setText(String.format("Rs. %.2f", cart.getPaymentCharges()));
        totalLabel.setText(String.format("Rs. %.2f", cart.getFinalBill()));

        // Update button states
        updateBtn.setEnabled(!items.isEmpty());
        removeBtn.setEnabled(!items.isEmpty());
    }
}
