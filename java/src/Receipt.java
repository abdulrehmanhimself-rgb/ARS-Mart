import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Receipt {
    private String username;
    private String orderId;
    private Date orderDate;
    private List<String[]> items; // Format: {name, category, quantity, price, subtotal}
    private double subtotal;
    private double discount;
    private double shippingFee;
    private double paymentCharges;
    private String paymentMethod;
    private double totalAmount;
    private String appliedPromoCode;

    public Receipt(String username, List<String[]> items, double subtotal,
                   double discount, double shippingFee, double paymentCharges, String paymentMethod) {
        this(username, items, subtotal, discount, shippingFee, paymentCharges, paymentMethod, null);
    }

    public Receipt(String username, List<String[]> items, double subtotal,
                   double discount, double shippingFee, double paymentCharges, String paymentMethod, String appliedPromoCode) {
        this.username = username;
        this.items = items;

        // Recompute subtotal from the provided `items` to guarantee itemization correctness.
        this.subtotal = calculateSubtotalFromItems(items, subtotal);
        this.discount = discount;
        this.shippingFee = shippingFee;
        this.paymentCharges = paymentCharges;
        this.paymentMethod = paymentMethod;
        this.appliedPromoCode = appliedPromoCode;
        this.totalAmount = this.subtotal - this.discount + this.shippingFee + paymentCharges;
        this.orderDate = new Date();
        this.orderId = generateOrderId();
    }

    private double calculateSubtotalFromItems(List<String[]> items, double fallbackSubtotal) {
        if (items == null || items.isEmpty()) return fallbackSubtotal;

        double sum = 0.0;
        boolean parsedAny = false;

        for (String[] item : items) {
            if (item == null || item.length < 5) continue;

            try {
                // item[4] is subtotal for that line
                double lineSubtotal = Double.parseDouble(item[4]);
                sum += lineSubtotal;
                parsedAny = true;
            } catch (Exception ignored) {
                // fallback below
            }
        }

        return parsedAny ? sum : fallbackSubtotal;
    }


    /**
     * Generate unique order ID
     */
    private String generateOrderId() {
        long timestamp = System.currentTimeMillis();
        return "ORD-" + timestamp;
    }

    /**
     * Get formatted receipt as string
     */
    public String getFormattedReceipt() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        StringBuilder receipt = new StringBuilder();

        receipt.append("\n╔════════════════════════════════════════════════════════════╗\n");
        receipt.append("║                    ═══ PURCHASE RECEIPT ═══                 ║\n");
        receipt.append("║                    Online Shopping Center                    ║\n");
        receipt.append("╚════════════════════════════════════════════════════════════╝\n\n");

        // Order Information
        receipt.append("┌─ ORDER INFORMATION ─────────────────────────────────────────┐\n");
        receipt.append(String.format("│ Order ID        : %-48s │\n", orderId));
        receipt.append(String.format("│ Customer Name   : %-48s │\n", username));
        receipt.append(String.format("│ Order Date      : %-48s │\n", dateFormat.format(orderDate)));
        receipt.append("└─────────────────────────────────────────────────────────────┘\n\n");

        // Items Table
        receipt.append("┌─ PURCHASED ITEMS ──────────────────────────────────────────┐\n");
        receipt.append(String.format("│ %-28s │ Qty │ Price  │ Total    │\n", "Product Name"));
        receipt.append("├─────────────────────────────────┼─────┼────────┼──────────┤\n");

        for (String[] item : items) {
            String name = item[0].length() > 28 ? item[0].substring(0, 25) + "..." : item[0];
            String qty = item[2];
            String price = item[3];
            String subtotalItem = item[4];

            receipt.append(String.format("│ %-28s │ %3s │ %6s │ %8s │\n",
                    name, qty, price, subtotalItem));
        }

        receipt.append("├─────────────────────────────────┼─────┼────────┼──────────┤\n");
        receipt.append(String.format("│ %-28s │     │        │ %8.2f │\n", "Subtotal", subtotal));

        // Discount section
        if (discount > 0) {
            receipt.append(String.format("│ %-28s │     │        │ -%7.2f │\n", "Discount", discount));
            if (appliedPromoCode != null) {
                receipt.append(String.format("│ (Promo: %s) │     │        │          │\n", appliedPromoCode));
            }
        }

        // Shipping section
        if (shippingFee > 0) {
            receipt.append(String.format("│ %-28s │     │        │ %8.2f │\n", "Shipping", shippingFee));
        } else if (shippingFee == 0 && appliedPromoCode != null) {
            receipt.append(String.format("│ %-28s │     │        │    FREE  │\n", "Shipping"));
        }

        // Payment charges section
        if (paymentCharges > 0) {
            receipt.append(String.format("│ Payment Charges (%s) │     │        │ %8.2f │\n",
                    paymentCharges == 100 ? "COD" : "ONLINE", paymentCharges));
        }

        receipt.append("└─────────────────────────────────┴─────┴────────┴──────────┘\n\n");

        // Payment Summary
        receipt.append("┌─ PAYMENT SUMMARY ───────────────────────────────────────────┐\n");
        receipt.append(String.format("│ Payment Method  : %-48s │\n", paymentMethod));

        if ("COD".equals(paymentMethod)) {
            receipt.append("│                   Status: PENDING (Pay on Delivery)         │\n");
        } else {
            receipt.append("│                   Status: COMPLETED                          │\n");
        }

        receipt.append("├─────────────────────────────────────────────────────────────┤\n");
        receipt.append(String.format("│ TOTAL AMOUNT    : Rs. %-43.2f │\n", totalAmount));
        receipt.append("└─────────────────────────────────────────────────────────────┘\n\n");

        // Footer
        receipt.append("╔════════════════════════════════════════════════════════════╗\n");
        receipt.append("║           Thank you for shopping with us!                  ║\n");
        receipt.append("║     We will deliver your order within 3-5 business days     ║\n");
        receipt.append("║          Track your order using Order ID above              ║\n");
        receipt.append("╚════════════════════════════════════════════════════════════╝\n");

        return receipt.toString();
    }

    /**
     * Save receipt to file
     */
    public void saveToFile() {
        try {
            String filename = "data/Receipt_" + orderId + ".txt";
            FileWriter fw = new FileWriter(filename);
            fw.write(getFormattedReceipt());
            fw.close();
            System.out.println("[INFO] Receipt saved to: " + filename);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save receipt: " + e.getMessage());
        }
    }

 
    public void saveToUserHistory() {
        try {
            String filename = username + "_purchases.txt";
            FileWriter fw = new FileWriter(filename, true); // Append mode
            fw.write("\n" + "=".repeat(60) + "\n");
            fw.write(getFormattedReceipt());
            fw.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save to user history: " + e.getMessage());
        }
    }

    /**
     * Get order ID
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Get total amount
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * Get order date
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * Print receipt to console (for debugging)
     */
    public void printToConsole() {
        System.out.println(getFormattedReceipt());
    }
}
