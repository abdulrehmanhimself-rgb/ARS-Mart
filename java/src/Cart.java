import java.io.*;
import java.util.*;

/**
 * Cart.java - Local shopping cart management for the user
 * Stores cart data in memory during session and can persist to file
 */
public class Cart {
    private static class CartItem {
        String name;
        String category;
        int quantity;
        double price;
        double subtotal;

        CartItem(String name, String category, int quantity, double price) {
            this.name = name;
            this.category = category;
            this.quantity = quantity;
            this.price = price;
            this.subtotal = quantity * price;
        }

        void updateQuantity(int newQuantity) {
            this.quantity = newQuantity;
            this.subtotal = newQuantity * price;
        }

        void addQuantity(int added) {
            this.quantity += added;
            this.subtotal = this.quantity * price;
        }

        @Override
        public String toString() {
            return String.format("%s|%s|%d|%.2f|%.2f", name, category, quantity, price, subtotal);
        }
    }

    private String username;
    private Map<String, CartItem> items; // Key: product name
    private double subtotal;
    private double paymentCharges; // Extra charges (100 for COD, 0 for online)
    private String paymentMethod; // "COD" or "ONLINE"

    public Cart(String username) {
        this.username = username;
        this.items = new LinkedHashMap<>();
        this.subtotal = 0.0;
        this.paymentCharges = 0.0;
        this.paymentMethod = "COD"; // Default to COD
        loadFromFile();
    }

    /**
     * Add an item to cart
     */
    public void addItem(String name, String category, int quantity, double price) {
        if (items.containsKey(name)) {
            items.get(name).addQuantity(quantity);
        } else {
            items.put(name, new CartItem(name, category, quantity, price));
        }
        recalculateTotal();
    }

    /**
     * Update item quantity
     */
    public void updateItemQuantity(String name, int newQuantity) {
        if (items.containsKey(name)) {
            if (newQuantity <= 0) {
                items.remove(name);
            } else {
                items.get(name).updateQuantity(newQuantity);
            }
            recalculateTotal();
        }
    }

    /**
     * Remove item from cart
     */
    public void removeItem(String name) {
        items.remove(name);
        recalculateTotal();
    }

    /**
     * Clear all items from cart
     */
    public void clear() {
        items.clear();
        recalculateTotal();
    }

    /**
     * Get all items as a list
     */
    public List<String[]> getItems() {
        List<String[]> itemList = new ArrayList<>();
        for (CartItem item : items.values()) {
            itemList.add(new String[]{
                    item.name,
                    item.category,
                    String.valueOf(item.quantity),
                    String.valueOf(item.price),
                    String.valueOf(item.subtotal)
            });
        }
        return itemList;
    }

    /**
     * Get cart size
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Recalculate subtotal
     */
    private void recalculateTotal() {
        this.subtotal = 0.0;
        for (CartItem item : items.values()) {
            this.subtotal += item.subtotal;
        }
    }

    /**
     * Set payment method and charges
     */
    public void setPaymentMethod(String method, double charges) {
        this.paymentMethod = method;
        this.paymentCharges = charges;
    }

    /**
     * Get final bill amount
     */
    public double getFinalBill() {
        return subtotal + paymentCharges;
    }

    /**
     * Get subtotal without charges
     */
    public double getSubtotal() {
        return subtotal;
    }

    /**
     * Get payment charges
     */
    public double getPaymentCharges() {
        return paymentCharges;
    }

    /**
     * Get payment method
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Save cart to file
     */
    public void saveToFile() {
        try {
            String filename = username + "_cart.txt";
            FileWriter fw = new FileWriter(filename);
            fw.write(paymentMethod + "\n");
            fw.write(String.valueOf(paymentCharges) + "\n");
            for (CartItem item : items.values()) {
                fw.write(item.toString() + "\n");
            }
            fw.close();
        } catch (IOException e) {
            System.err.println("Error saving cart: " + e.getMessage());
        }
    }

    /**
     * Load cart from file
     */
    private void loadFromFile() {
        try {
            String filename = username + "_cart.txt";
            File file = new File(filename);
            if (!file.exists()) {
                return; // No saved cart
            }

            Scanner scanner = new Scanner(file);
            if (scanner.hasNextLine()) {
                this.paymentMethod = scanner.nextLine().trim();
            }
            if (scanner.hasNextLine()) {
                this.paymentCharges = Double.parseDouble(scanner.nextLine().trim());
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length >= 5) {
                    String name = parts[0];
                    String category = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    double price = Double.parseDouble(parts[3]);

                    items.put(name, new CartItem(name, category, quantity, price));
                }
            }
            scanner.close();
            recalculateTotal();
        } catch (FileNotFoundException e) {
            // File doesn't exist, start with empty cart
        } catch (Exception e) {
            System.err.println("Error loading cart: " + e.getMessage());
        }
    }

    /**
     * Get total quantity of items
     */
    public int getTotalQuantity() {
        int total = 0;
        for (CartItem item : items.values()) {
            total += item.quantity;
        }
        return total;
    }

    /**
     * Export cart data as formatted string for display
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append("SHOPPING CART\n");
        sb.append("═══════════════════════════════════════════════════\n");
        sb.append(String.format("%-25s | Qty | Price | Subtotal\n", "Product"));
        sb.append("───────────────────────────────────────────────────\n");

        if (items.isEmpty()) {
            sb.append("Your cart is empty!\n");
        } else {
            for (CartItem item : items.values()) {
                sb.append(String.format("%-25s | %3d | %6.2f | %8.2f\n",
                        item.name, item.quantity, item.price, item.subtotal));
            }
        }

        sb.append("═══════════════════════════════════════════════════\n");
        sb.append(String.format("Subtotal:       Rs. %.2f\n", subtotal));
        if (paymentCharges > 0) {
            sb.append(String.format("Payment Charges: Rs. %.2f (%s)\n", paymentCharges, paymentMethod));
        }
        sb.append(String.format("TOTAL:          Rs. %.2f\n", getFinalBill()));
        sb.append("═══════════════════════════════════════════════════\n");

        return sb.toString();
    }
}
