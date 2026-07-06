import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * PaymentDialog.java - Enhanced with Discounts, Shipping, and Promo Codes
 * - Tier-based discounts based on cart amount
 * - Dynamic shipping fees
 * - Promo code system
 * - COD: confirm payment on delivery
 * - Online: validate mobile and exact amount
 */
public class PaymentDialog extends JDialog {
    private String selectedMethod = null;
    private double charges = 0.0;
    private double discount = 0.0;
    private double shippingFee = 0.0;
    private boolean freeShipping = false;
    private boolean confirmed = false;

    private final double subtotal;
    private double finalAmount;

    // Online payment fields
    private JTextField mobileField;
    private JTextField exactPaymentField;
    private JTextField promoCodeField;

    private JPanel onlineFieldsPanel;
    private JLabel exactPayableHint;
    private JLabel discountLabel;
    private JLabel shippingLabel;
    private JLabel promoStatusLabel;
    private JLabel finalAmountLabel;

    // Keep for consumers
    private String mobileNumber = null;
    private String appliedPromoCode = null;
    private PromoCodeManager promoManager;

    public PaymentDialog(JFrame parent, double subtotal) {
        super(parent, "Select Payment Method", true);
        this.subtotal = subtotal;
        this.promoManager = new PromoCodeManager();
        this.finalAmount = subtotal;
        calculateInitialPricing();
        initializeUI();
    }

    private void calculateInitialPricing() {
        // Calculate default discount based on amount
        discount = PricingSystem.calculateDiscount(subtotal);
        shippingFee = PricingSystem.getShippingFee(subtotal);
        freeShipping = false;
        charges = 0; // Will be set when payment method is selected
    }

    private void initializeUI() {
        setSize(600, 700);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(UITheme.BACKGROUND);
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Checkout", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Options Panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(UITheme.BACKGROUND);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Promo Code Section
        optionsPanel.add(createPromoCodePanel());
        optionsPanel.add(Box.createVerticalStrut(15));

        // Price Summary Section
        optionsPanel.add(createPriceSummaryPanel());
        optionsPanel.add(Box.createVerticalStrut(20));

        // Payment Options
        JLabel paymentLabel = new JLabel("Select Payment Method:");
        paymentLabel.setForeground(Color.WHITE);
        paymentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        optionsPanel.add(paymentLabel);
        optionsPanel.add(Box.createVerticalStrut(10));

        // COD Option
        JPanel codPanel = createPaymentOptionPanel(
                "💳 Cash on Delivery (COD)",
                "Pay when you receive the product\nExtra Charges: Rs. 100",
                "COD",
                100.0
        );
        optionsPanel.add(codPanel);
        optionsPanel.add(Box.createVerticalStrut(15));

        // Online Option (with fields panel)
        JPanel onlinePanel = new JPanel();
        onlinePanel.setBackground(UITheme.BACKGROUND);
        onlinePanel.setLayout(new BorderLayout(10, 10));

        JPanel onlineHeader = createPaymentOptionPanel(
                "💰 Online Payment",
                "Pay securely using card/mobile banking\nNo extra charges",
                "ONLINE",
                0.0
        );
        onlinePanel.add(onlineHeader, BorderLayout.NORTH);

        onlineFieldsPanel = createOnlinePaymentFieldsPanel();
        onlineFieldsPanel.setVisible(false);
        onlinePanel.add(onlineFieldsPanel, BorderLayout.CENTER);

        optionsPanel.add(onlinePanel);

        JScrollPane scrollPane = new JScrollPane(optionsPanel);
        scrollPane.setBackground(UITheme.BACKGROUND);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(UITheme.BACKGROUND);

        JButton confirmBtn = new JButton("Confirm");
        confirmBtn.setBackground(UITheme.BUTTON);
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFont(UITheme.BUTTON_FONT);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setPreferredSize(new Dimension(120, 40));

        confirmBtn.addActionListener(e -> handleConfirm());

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(new Color(200, 50, 50));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(UITheme.BUTTON_FONT);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        buttonPanel.add(confirmBtn);
        buttonPanel.add(cancelBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        SwingEnterKeyBinder.bindEnterToButton(getRootPane(), confirmBtn);
    }

    private JPanel createPromoCodePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Apply Promo Code (Optional):");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));

        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(UITheme.PANEL);

        promoCodeField = new JTextField();
        promoCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        promoCodeField.setBackground(new Color(40, 40, 45));
        promoCodeField.setForeground(Color.WHITE);
        promoCodeField.setCaretColor(Color.WHITE);
        inputPanel.add(promoCodeField, BorderLayout.CENTER);

        JButton applyBtn = new JButton("Apply");
        applyBtn.setBackground(UITheme.BUTTON);
        applyBtn.setForeground(Color.WHITE);
        applyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        applyBtn.setFocusPainted(false);
        applyBtn.setBorderPainted(false);
        applyBtn.setPreferredSize(new Dimension(80, 32));
        applyBtn.addActionListener(e -> applyPromoCode());
        inputPanel.add(applyBtn, BorderLayout.EAST);

        panel.add(inputPanel);
        panel.add(Box.createVerticalStrut(8));

        promoStatusLabel = new JLabel(" ");
        promoStatusLabel.setForeground(new Color(100, 200, 100));
        promoStatusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        promoStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(promoStatusLabel);

        JTextArea infoArea = new JTextArea(2, 30);
        infoArea.setText(promoManager.getActivePromoCodesInfo());
        infoArea.setEditable(false);
        infoArea.setForeground(new Color(100, 200, 100));
        infoArea.setBackground(UITheme.BACKGROUND);
        infoArea.setFont(new Font("Consolas", Font.PLAIN, 10));
        infoArea.setLineWrap(true);
        panel.add(infoArea);

        return panel;
    }

    private JPanel createPriceSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Price Breakdown:");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));

        // Subtotal
        JLabel subtotalLabel = new JLabel();
        subtotalLabel.setForeground(new Color(200, 200, 200));
        subtotalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtotalLabel.setText(String.format("Subtotal: Rs. %.2f", subtotal));
        subtotalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subtotalLabel);

        // Discount
        discountLabel = new JLabel();
        discountLabel.setForeground(new Color(100, 200, 100));
        discountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        updateDiscountLabel();
        discountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(discountLabel);

        // Shipping
        shippingLabel = new JLabel();
        shippingLabel.setForeground(new Color(200, 200, 200));
        shippingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        updateShippingLabel();
        shippingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(shippingLabel);

        panel.add(Box.createVerticalStrut(8));

        // Total
        finalAmountLabel = new JLabel();
        finalAmountLabel.setForeground(new Color(255, 200, 100));
        finalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        updateFinalAmountLabel();
        finalAmountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(finalAmountLabel);

        return panel;
    }

    private void updateDiscountLabel() {
        double discountPercent = PricingSystem.getDiscountPercentage(subtotal);
        if (discount > 0) {
            discountLabel.setText(String.format("Discount (%.0f%%): - Rs. %.2f", discountPercent, discount));
        } else {
            discountLabel.setText("Discount: No discount available");
        }
    }

    private void updateShippingLabel() {
        if (freeShipping) {
            shippingLabel.setText("Shipping: FREE (Promo Code Applied)");
            shippingLabel.setForeground(new Color(100, 200, 100));
        } else {
            shippingLabel.setText(String.format("Shipping: Rs. %.2f", shippingFee));
            shippingLabel.setForeground(new Color(200, 200, 200));
        }
    }

    private void updateFinalAmountLabel() {
        double afterDiscount = subtotal - discount;
        double shipping = freeShipping ? 0 : shippingFee;
        finalAmount = afterDiscount + shipping + charges; // charges added when payment method selected
        finalAmountLabel.setText(String.format("Total: Rs. %.2f (+ Rs. %.2f for %s)", 
            finalAmount, charges, selectedMethod != null ? selectedMethod : "COD"));
    }

    private void applyPromoCode() {
        String code = promoCodeField.getText().trim();
        if (code.isEmpty()) {
            promoStatusLabel.setText("Please enter a promo code");
            promoStatusLabel.setForeground(new Color(200, 100, 100));
            return;
        }

        PromoCodeManager.PromoCodeResult result = promoManager.validatePromoCode(code);
        if (!result.valid) {
            promoStatusLabel.setText("❌ " + result.message);
            promoStatusLabel.setForeground(new Color(200, 100, 100));
            return;
        }

        // Apply promo code
        appliedPromoCode = code;
        promoManager.usePromoCode(code);

        if (result.isFreeShipping()) {
            freeShipping = true;
            promoStatusLabel.setText("✓ Free Shipping Activated!");
            promoStatusLabel.setForeground(new Color(100, 200, 100));
        } else {
            double discountPercent = result.getDiscountPercent();
            double promoDiscount = (subtotal * discountPercent) / 100.0;
            discount += promoDiscount; // Stack with tier discount
            promoStatusLabel.setText(String.format("✓ %d%% Extra Discount Applied!", (int)discountPercent));
            promoStatusLabel.setForeground(new Color(100, 200, 100));
        }

        promoCodeField.setEditable(false);
        updateDiscountLabel();
        updateShippingLabel();
        updateFinalAmountLabel();
        revalidate();
        repaint();
    }

    private double getExactPayableAmount() {
        double afterDiscount = subtotal - discount;
        double shipping = freeShipping ? 0 : shippingFee;
        return afterDiscount + shipping + charges;
    }

    private JPanel createOnlinePaymentFieldsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.PANEL);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        panel.add(Box.createVerticalStrut(5));

        exactPayableHint = new JLabel();
        exactPayableHint.setForeground(new Color(200, 200, 200));
        exactPayableHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exactPayableHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        exactPayableHint.setText("Exact payable amount: Rs. " + String.format("%.2f", getExactPayableAmount()));
        panel.add(exactPayableHint);
        panel.add(Box.createVerticalStrut(10));

        JLabel mobileLabel = new JLabel("Mobile Number (11 digits)");
        mobileLabel.setForeground(Color.WHITE);
        mobileLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        mobileLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(mobileLabel);

        mobileField = new JTextField();
        mobileField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        mobileField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mobileField.setBackground(new Color(40, 40, 45));
        mobileField.setForeground(Color.WHITE);
        mobileField.setCaretColor(Color.WHITE);
        panel.add(mobileField);
        panel.add(Box.createVerticalStrut(10));

        JLabel amountLabel = new JLabel("Exact Payment Amount (Rs.)");
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(amountLabel);

        exactPaymentField = new JTextField(String.format("%.2f", getExactPayableAmount()));
        exactPaymentField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        exactPaymentField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        exactPaymentField.setBackground(new Color(40, 40, 45));
        exactPaymentField.setForeground(Color.WHITE);
        exactPaymentField.setCaretColor(Color.WHITE);
        panel.add(exactPaymentField);
        panel.add(Box.createVerticalStrut(5));

        return panel;
    }

    private JPanel createPaymentOptionPanel(String title, String description, String method, double charge) {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.PANEL);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 70), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JRadioButton radioButton = new JRadioButton();
        radioButton.setBackground(UITheme.PANEL);
        radioButton.setForeground(UITheme.WARNING);
        radioButton.setFocusPainted(false);

        JPanel textPanel = new JPanel();
        textPanel.setBackground(UITheme.PANEL);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.LABEL_FONT.deriveFont(Font.BOLD, 14f));
        titleLabel.setForeground(Color.WHITE);

        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(200, 200, 200));

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);

        panel.add(radioButton, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        MouseAdapter clickListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radioButton.setSelected(true);
                onSelected(method, charge);
            }
        };
        panel.addMouseListener(clickListener);

        radioButton.addActionListener(e -> {
            if (radioButton.isSelected()) {
                onSelected(method, charge);
            }
        });

        return panel;
    }

    private void onSelected(String method, double charge) {
        selectedMethod = method;
        charges = charge;

        if ("ONLINE".equals(selectedMethod)) {
            onlineFieldsPanel.setVisible(true);
            double exactAmount = getExactPayableAmount();
            exactPayableHint.setText("Exact payable amount: Rs. " + String.format("%.2f", exactAmount));
            exactPaymentField.setText(String.format("%.2f", exactAmount));
        } else {
            onlineFieldsPanel.setVisible(false);
        }

        updateFinalAmountLabel();
        revalidate();
        repaint();
    }

    private void handleConfirm() {
        if (selectedMethod == null) {
            JOptionPane.showMessageDialog(this, "Please select a payment method", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("ONLINE".equals(selectedMethod)) {
            // Validate mobile number
            String m = mobileField.getText() == null ? "" : mobileField.getText().trim();
            if (!m.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(this, "Enter a valid Mobile Number (exactly 11 digits).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate exact amount
            String amtStr = exactPaymentField.getText() == null ? "" : exactPaymentField.getText().trim();
            double entered;
            try {
                entered = Double.parseDouble(amtStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid exact payment amount.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double expected = getExactPayableAmount();
            if (Math.abs(entered - expected) > 0.0001) {
                JOptionPane.showMessageDialog(
                        this,
                        "Wrong amount. Expected Rs. " + String.format("%.2f", expected),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            mobileNumber = m;
        }

        confirmed = true;
        dispose();
    }

    public String getSelectedMethod() {
        return selectedMethod;
    }

    public double getCharges() {
        return charges;
    }

    public double getDiscount() {
        return discount;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public boolean isFreeShipping() {
        return freeShipping;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getAppliedPromoCode() {
        return appliedPromoCode;
    }

    public double getFinalAmount() {
        return finalAmount;
    }
}

