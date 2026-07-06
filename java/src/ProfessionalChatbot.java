import java.util.*;

public class ProfessionalChatbot {
    
    private String username;
    private List<String[]> inventoryCache;
    private Random random;
    
    // Category to keywords mapping
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new HashMap<>();
    
    static {
        CATEGORY_KEYWORDS.put("electronics", Arrays.asList("laptop", "computer", "mobile", "phone", "tablet", "tv", "television", "headphone", "speaker"));
        CATEGORY_KEYWORDS.put("clothing", Arrays.asList("shirt", "pant", "jeans", "dress", "jacket", "coat", "sweater", "t-shirt"));
        CATEGORY_KEYWORDS.put("furniture", Arrays.asList("chair", "table", "desk", "sofa", "bed", "cabinet", "shelf"));
        CATEGORY_KEYWORDS.put("books", Arrays.asList("book", "novel", "textbook", "magazine", "notebook"));
        CATEGORY_KEYWORDS.put("shoes", Arrays.asList("shoe", "sneaker", "boot", "sandal", "loafer"));
    }
    
    public ProfessionalChatbot(String username) {
        this.username = username;
        this.random = new Random();
        loadInventory();
    }
    
    private void loadInventory() {
        inventoryCache = new ArrayList<>();
        String result = BackendConnector.runCommand("inventory");
        if (result != null && !result.isEmpty() && !result.startsWith("ERROR")) {
            String[] lines = result.split("\n");
            for (String line : lines) {
                String[] parts = line.split("\\|");
                if (parts.length >= 4) {
                    inventoryCache.add(new String[]{
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim()
                    });
                }
            }
        }
    }
    
    public String getResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        // Order related queries
        if (containsAny(lowerMessage, "how to order", "place order", "buy product", "purchase", "ordering process")) {
            return getOrderGuide();
        }
        
        // Product price queries
        if (containsAny(lowerMessage, "price", "cost", "how much", "rate")) {
            return getProductPrice(userMessage);
        }
        
        // Product availability
        if (containsAny(lowerMessage, "available", "in stock", "have you got", "is there")) {
            return getProductAvailability(userMessage);
        }
        
        // Delivery queries
        if (containsAny(lowerMessage, "delivery", "shipping", "deliver", "ship", "shipping time")) {
            return getDeliveryInfo();
        }
        
        // Payment queries
        if (containsAny(lowerMessage, "payment", "pay", "method", "cod", "card", "online payment")) {
            return getPaymentInfo();
        }
        
        // Return policy
        if (containsAny(lowerMessage, "return", "refund", "exchange", "cancel order")) {
            return getReturnPolicy();
        }
        
        // Cart queries
        if (containsAny(lowerMessage, "cart", "my cart", "shopping cart")) {
            return getCartInfo();
        }
        
        // Order history
        if (containsAny(lowerMessage, "my order", "order history", "previous order", "past order")) {
            return getOrderHistory();
        }
        
        // Track order
        if (containsAny(lowerMessage, "track", "where is my order", "order status")) {
            return getTrackOrderInfo();
        }
        
        // Product listing
        if (containsAny(lowerMessage, "all products", "show products", "list products", "what do you have")) {
            return getAllProducts();
        }
        
        // Recommendation
        if (containsAny(lowerMessage, "recommend", "suggest", "what should i buy", "gift")) {
            return getRecommendation(userMessage);
        }
        
        // Discount/Offers
        if (containsAny(lowerMessage, "discount", "offer", "sale", "deal")) {
            return getDiscountInfo();
        }
        
        // Contact support
        if (containsAny(lowerMessage, "contact", "support", "help", "customer service")) {
            return getContactInfo();
        }
        
        // Default response for unknown queries
        return getHelpResponse();
    }
    
    private String getOrderGuide() {
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "🛍️ **HOW TO PLACE AN ORDER**\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
               "📋 **Step-by-Step Guide:**\n\n" +
               "┌─────────────────────────────────────────┐\n" +
               "│ 1️⃣ **Browse Products**                  │\n" +
               "│    • Click 'Browse Products' from      │\n" +
               "│      your dashboard                     │\n" +
               "│    • Browse through categories          │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 2️⃣ **Add to Cart**                      │\n" +
               "│    • Enter quantity                     │\n" +
               "│    • Click 'Add to Cart' button         │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 3️⃣ **Review Cart**                      │\n" +
               "│    • Click 'Shopping Cart'              │\n" +
               "│    • Update quantities if needed        │\n" +
               "│    • Remove items if needed             │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 4️⃣ **Proceed to Checkout**              │\n" +
               "│    • Select payment method              │\n" +
               "│    • For COD: Extra Rs. 100             │\n" +
               "│    • For Online: No extra charges       │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 5️⃣ **Confirm Order**                    │\n" +
               "│    • Review order summary               │\n" +
               "│    • Click 'Confirm'                    │\n" +
               "│    • Receipt will be generated          │\n" +
               "└─────────────────────────────────────────┘\n\n" +
               "✅ **After ordering:**\n" +
               "• You'll receive a receipt file\n" +
               "• Order will be delivered in 3-5 business days\n" +
               "• Track order from 'View Orders' section\n\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "💡 Would you like me to help you find products to order?";
    }
    
    private String getProductPrice(String query) {
        String productName = extractProductName(query);
        
        if (productName.isEmpty()) {
            return "❓ Please specify the product name. Example: **\"Price of laptop\"** or **\"How much are headphones?\"**";
        }
        
        for (String[] product : inventoryCache) {
            if (product[0].toLowerCase().contains(productName.toLowerCase())) {
                double price = Double.parseDouble(product[3]);
                int stock = Integer.parseInt(product[2]);
                
                return String.format(
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "💰 **PRICE DETAILS**\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "┌─────────────────────────────────────────┐\n" +
                    "│ 📦 Product: %s\n" +
                    "│ 📂 Category: %s\n" +
                    "│ 💵 Price: Rs. %.2f\n" +
                    "│ 📊 Stock Available: %s units\n" +
                    "└─────────────────────────────────────────┘\n\n" +
                    "%s\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "💡 Type **\"add %s 1\"** to add to your cart!",
                    product[0], product[1], price, product[2],
                    stock < 10 ? "⚠️ **Limited stock!** Only " + stock + " units left." : "✅ Product is in stock and ready to order.",
                    product[0]
                );
            }
        }
        
        return "❌ Product **\"" + productName + "\"** not found in our inventory.\n\n" +
               "🔍 Try checking the spelling or type **\"all products\"** to see what's available.";
    }
    
    private String getProductAvailability(String query) {
        String productName = extractProductName(query);
        
        if (productName.isEmpty()) {
            return "❓ Which product would you like to check availability for?";
        }
        
        for (String[] product : inventoryCache) {
            if (product[0].toLowerCase().contains(productName.toLowerCase())) {
                int stock = Integer.parseInt(product[2]);
                if (stock > 0) {
                    return String.format(
                        "✅ **%s** is available!\n\n" +
                        "📊 **Stock:** %s units remaining\n" +
                        "💰 **Price:** Rs. %s\n\n" +
                        "🛒 Would you like to add it to your cart?",
                        product[0], product[2], product[3]
                    );
                } else {
                    return String.format(
                        "❌ **%s** is currently out of stock.\n\n" +
                        "🔄 Would you like me to recommend similar products?",
                        product[0]
                    );
                }
            }
        }
        
        return "❌ Product **\"" + productName + "\"** not found in our inventory.";
    }
    
    private String getDeliveryInfo() {
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "🚚 **DELIVERY INFORMATION**\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
               "┌─────────────────────────────────────────┐\n" +
               "│ 📍 **Standard Delivery**                 │\n" +
               "│    • Time: 3-5 business days           │\n" +
               "│    • Cost: Rs. 150                     │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ ⚡ **Express Delivery**                  │\n" +
               "│    • Time: 1-2 business days           │\n" +
               "│    • Cost: Rs. 350                     │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 🎉 **Free Shipping**                    │\n" +
               "│    • On orders above Rs. 2000          │\n" +
               "│    • Standard delivery only            │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 📦 **Cash on Delivery**                 │\n" +
               "│    • Extra Rs. 100 fee                 │\n" +
               "│    • Pay when you receive              │\n" +
               "└─────────────────────────────────────────┘\n\n" +
               "📍 **Delivery Areas:** Nationwide delivery across Pakistan\n\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    }
    
    private String getPaymentInfo() {
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "💳 **PAYMENT METHODS**\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
               "┌─────────────────────────────────────────┐\n" +
               "│ 💵 **Cash on Delivery (COD)**           │\n" +
               "│    • Extra charge: Rs. 100             │\n" +
               "│    • Pay cash at delivery              │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 💳 **Credit/Debit Card**                │\n" +
               "│    • No extra charges                  │\n" +
               "│    • Visa, Mastercard, Amex accepted   │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 📱 **Mobile Wallet**                    │\n" +
               "│    • JazzCash, Easypaisa               │\n" +
               "│    • No extra charges                  │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 🏦 **Online Banking**                   │\n" +
               "│    • All major banks supported         │\n" +
               "│    • Secure payment gateway            │\n" +
               "└─────────────────────────────────────────┘\n\n" +
               "🔒 All payments are secure and encrypted.\n\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    }
    
    private String getReturnPolicy() {
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "🔄 **RETURN & REFUND POLICY**\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
               "┌─────────────────────────────────────────┐\n" +
               "│ ✅ **Return Window**                     │\n" +
               "│    • 7 days from delivery date         │\n" +
               "│    • Item must be unused               │\n" +
               "│    • Original packaging required       │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 💰 **Refund Process**                   │\n" +
               "│    • 5-7 business days for processing  │\n" +
               "│    • Refund to original payment method │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ ❌ **Non-returnable items**             │\n" +
               "│    • Perishable goods                  │\n" +
               "│    • Personalized items                │\n" +
               "│    • Clearance sale items              │\n" +
               "└─────────────────────────────────────────┘\n\n" +
               "📞 To initiate a return, contact support@arsmart.com\n\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    }
    
    private String getCartInfo() {
        Cart cart = new Cart(username);
        if (cart.isEmpty()) {
            return "🛒 **Your cart is empty**\n\n" +
                   "📋 To add items:\n" +
                   "• Go to 'Browse Products' from dashboard\n" +
                   "• Select quantity and click 'Add to Cart'\n\n" +
                   "💡 Type **\"recommend\"** for product suggestions!";
        } else {
            return String.format(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "🛒 **YOUR SHOPPING CART**\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "┌─────────────────────────────────────────┐\n" +
                "│ 📦 Total Items: %d                       │\n" +
                "│ 💰 Subtotal: Rs. %.2f                   │\n" +
                "│ 💳 Payment Charges: Rs. %.2f            │\n" +
                "├─────────────────────────────────────────┤\n" +
                "│ 💵 **Total Amount: Rs. %.2f**            │\n" +
                "└─────────────────────────────────────────┘\n\n" +
                "✅ **Next Steps:**\n" +
                "1. Click 'Shopping Cart' from dashboard\n" +
                "2. Review your items\n" +
                "3. Select payment method\n" +
                "4. Confirm order\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                cart.getItemCount(), cart.getSubtotal(), cart.getPaymentCharges(), cart.getFinalBill()
            );
        }
    }
    
    private String getOrderHistory() {
        String history = BackendConnector.runCommand("history_user " + username);
        if (history == null || history.isEmpty() || history.equals("EMPTY")) {
            return "📭 **No orders found**\n\n" +
                   "You haven't placed any orders yet.\n\n" +
                   "🛍️ Ready to make your first purchase? Type **\"how to order\"** to get started!";
        } else {
            return "📜 **You have previous orders!**\n\n" +
                   "To view your complete order history:\n" +
                   "• Go to your **User Dashboard**\n" +
                   "• Click on **\"View Orders\"** button\n" +
                   "• See all your past purchases\n\n" +
                   "💡 Want to reorder something? Let me know what you liked!";
        }
    }
    
    private String getTrackOrderInfo() {
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "📍 **TRACK YOUR ORDER**\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
               "📋 **How to track your order:**\n\n" +
               "┌─────────────────────────────────────────┐\n" +
               "│ 1️⃣ Go to **User Dashboard**              │\n" +
               "│ 2️⃣ Click **\"View Orders\"**               │\n" +
               "│ 3️⃣ Find your order by Order ID          │\n" +
               "│ 4️⃣ Check order status                   │\n" +
               "└─────────────────────────────────────────┘\n\n" +
               "📞 **Need help?** Contact support with your Order ID\n" +
               "   Email: support@arsmart.com\n\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    }
    
    private String getAllProducts() {
        if (inventoryCache.isEmpty()) {
            return "📭 No products found in inventory.";
        }
        
        Map<String, List<String[]>> categorized = new LinkedHashMap<>();
        for (String[] product : inventoryCache) {
            categorized.computeIfAbsent(product[1], k -> new ArrayList<>()).add(product);
        }
        
        StringBuilder response = new StringBuilder();
        response.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        response.append("📋 **COMPLETE PRODUCT CATALOG**\n");
        response.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        for (Map.Entry<String, List<String[]>> entry : categorized.entrySet()) {
            response.append("┌─── **").append(entry.getKey().toUpperCase()).append("** ───┐\n");
            for (String[] product : entry.getValue()) {
                response.append(String.format("│ • %s - Rs. %s\n", product[0], product[3]));
            }
            response.append("└─────────────────────────────────────────┘\n\n");
        }
        
        response.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        response.append("💡 Type **\"price of [product]\"** to check specific product.\n");
        
        return response.toString();
    }
    
    private String getRecommendation(String query) {
        String budget = extractBudget(query);
        String category = extractCategory(query);
        
        List<String[]> recommendations = new ArrayList<>(inventoryCache);
        
        // Filter by budget
        if (budget != null) {
            try {
                double budgetAmount = Double.parseDouble(budget);
                recommendations.removeIf(p -> Double.parseDouble(p[3]) > budgetAmount);
            } catch (NumberFormatException e) {}
        }
        
        // Filter by category
        if (category != null && !category.isEmpty()) {
            recommendations.removeIf(p -> !p[1].toLowerCase().contains(category.toLowerCase()));
        }
        
        if (recommendations.isEmpty()) {
            return "❌ No products found matching your criteria.\n\n" +
                   "💡 Try:\n" +
                   "• **\"recommend under 500\"** for budget items\n" +
                   "• **\"recommend electronics\"** for specific category";
        }
        
        // Take top 5
        recommendations = recommendations.subList(0, Math.min(5, recommendations.size()));
        
        StringBuilder response = new StringBuilder();
        response.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        response.append("🎯 **RECOMMENDED FOR YOU**\n");
        response.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        
        for (String[] product : recommendations) {
            response.append(String.format(
                "┌─────────────────────────────────────────┐\n" +
                "│ 📦 **%s**\n" +
                "│ 📂 %s\n" +
                "│ 💰 Rs. %s\n" +
                "│ 📊 Stock: %s\n" +
                "└─────────────────────────────────────────┘\n\n",
                product[0], product[1], product[3], product[2]
            ));
        }
        
        response.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        response.append("💡 Type **\"add [product name] [quantity]\"** to order!\n");
        
        return response.toString();
    }
    
    private String getDiscountInfo() {
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "🏷️ **CURRENT OFFERS & DISCOUNTS**\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
               "┌─────────────────────────────────────────┐\n" +
               "│ 🎉 **Free Shipping**                     │\n" +
               "│    On orders above Rs. 2000             │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 💳 **No Extra Charges**                  │\n" +
               "│    On all online payments               │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 🆕 **New User Offer**                    │\n" +
               "│    10% off on first order               │\n" +
               "│    Use code: WELCOME10                  │\n" +
               "└─────────────────────────────────────────┘\n\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "💡 Check back often for new deals!";
    }
    
    private String getContactInfo() {
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "📞 **CUSTOMER SUPPORT**\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
               "┌─────────────────────────────────────────┐\n" +
               "│ 📧 **Email**                             │\n" +
               "│    support@arsmart.com                  │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 📞 **Phone**                             │\n" +
               "│    +92 123 4567890                      │\n" +
               "│    Mon-Fri: 9AM - 6PM                   │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 💬 **Live Chat**                         │\n" +
               "│    Available 24/7 through this chat     │\n" +
               "└─────────────────────────────────────────┘\n\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    }
    
    private String getHelpResponse() {
        return "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "🤖 **ARS MART ASSISTANT - HELP**\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
               "┌─────────────────────────────────────────┐\n" +
               "│ 🛍️ **ORDERS**                            │\n" +
               "│    • \"How to order?\"                    │\n" +
               "│    • \"My orders\"                        │\n" +
               "│    • \"Track my order\"                   │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 💰 **PRODUCTS**                          │\n" +
               "│    • \"Price of [product]\"               │\n" +
               "│    • \"Is [product] available?\"          │\n" +
               "│    • \"All products\"                     │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 📦 **DELIVERY**                          │\n" +
               "│    • \"Delivery time\"                    │\n" +
               "│    • \"Shipping cost\"                    │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 💳 **PAYMENT**                           │\n" +
               "│    • \"Payment methods\"                  │\n" +
               "│    • \"COD charges\"                      │\n" +
               "├─────────────────────────────────────────┤\n" +
               "│ 🎯 **RECOMMENDATIONS**                   │\n" +
               "│    • \"Recommend products\"               │\n" +
               "│    • \"Under Rs.500\"                     │\n" +
               "└─────────────────────────────────────────┘\n\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "💡 What would you like help with?";
    }
    
    // Helper methods
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }
    
    private String extractProductName(String query) {
        String[] phrases = {"price of", "cost of", "tell me about", "is", "available", "have you got"};
        for (String phrase : phrases) {
            if (query.toLowerCase().contains(phrase)) {
                String after = query.toLowerCase().split(phrase)[1].trim();
                if (after.contains("?")) after = after.split("\\?")[0];
                String[] words = after.split(" ");
                for (String word : words) {
                    if (word.length() > 3) return word;
                }
                return after.split(" ")[0];
            }
        }
        
        String[] words = query.split(" ");
        for (String word : words) {
            if (word.length() > 4 && !word.matches(".*(how|what|where|when|why|which|can|you|please|tell|me).*")) {
                return word;
            }
        }
        return "";
    }
    
    private String extractBudget(String query) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(?:under|below|less than|Rs\\.?|PKR)\\s*(\\d+)").matcher(query);
        if (m.find()) return m.group(1);
        return null;
    }
    
    private String extractCategory(String query) {
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (query.toLowerCase().contains(keyword)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}