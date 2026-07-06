import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.*;


public class AISmartChatbot {
    
    private String username;
    private List<String[]> inventoryCache;
    private Map<String, Object> userContext;
    private List<String> conversationMemory;
    private Map<String, Integer> userInterests;
    private Random random;
    private String lastIntent;
    
    // Advanced intent detection with confidence scoring
    private static class Intent {
        String name;
        Pattern pattern;
        double confidence;
        
        Intent(String name, Pattern pattern) {
            this.name = name;
            this.pattern = pattern;
            this.confidence = 0;
        }
    }
    
    private List<Intent> intents;
    
    public AISmartChatbot(String username) {
        this.username = username;
        this.userContext = new HashMap<>();
        this.conversationMemory = new ArrayList<>();
        this.userInterests = new HashMap<>();
        this.random = new Random();
        this.lastIntent = "";
        
        initializeIntents();
        loadInventory();
        loadUserProfile();
    }
    
    private void initializeIntents() {
        intents = new ArrayList<>();
        intents.add(new Intent("GREETING", Pattern.compile("(?i).*(hi|hello|hey|greetings|sup|howdy|good morning|good afternoon|good evening).*")));
        intents.add(new Intent("RECOMMENDATION", Pattern.compile("(?i).*(recommend|suggest|what should I buy|looking for|need a|gift for|best|top rated|popular).*")));
        intents.add(new Intent("PRICE_CHECK", Pattern.compile("(?i).*(price|cost|how much|rate of).*")));
        intents.add(new Intent("AVAILABILITY", Pattern.compile("(?i).*(available|in stock|have you got|is there).*")));
        intents.add(new Intent("CART", Pattern.compile("(?i).*(cart|add to cart|my cart|shopping cart).*")));
        intents.add(new Intent("ORDER", Pattern.compile("(?i).*(order|purchase|bought|ordered).*")));
        intents.add(new Intent("DELIVERY", Pattern.compile("(?i).*(delivery|shipping|deliver|ship).*")));
        intents.add(new Intent("PAYMENT", Pattern.compile("(?i).*(payment|pay|method|cod|card).*")));
        intents.add(new Intent("RETURN", Pattern.compile("(?i).*(return|refund|exchange|cancel).*")));
        intents.add(new Intent("HELP", Pattern.compile("(?i).*(help|what can you do|features|capabilities).*")));
        intents.add(new Intent("PRODUCT_LIST", Pattern.compile("(?i).*(list|show me|display|all products|what do you have).*")));
        intents.add(new Intent("COMPLAINT", Pattern.compile("(?i).*(bad|worse|terrible|awful|disappointed|issue|problem).*")));
        intents.add(new Intent("THANKS", Pattern.compile("(?i).*(thanks|thank you|appreciate|grateful).*")));
        intents.add(new Intent("SMALL_TALK", Pattern.compile("(?i).*(how are you|what's up|how's it going|weather|nice day).*")));
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
    
    private void loadUserProfile() {
        try {
            Scanner scanner = new Scanner(new File(username + "_ai_profile.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    userInterests.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
            scanner.close();
        } catch (Exception e) {
            // New user
        }
    }
    
    private void saveUserProfile() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(username + "_ai_profile.txt"));
            for (Map.Entry<String, Integer> entry : userInterests.entrySet()) {
                writer.println(entry.getKey() + "=" + entry.getValue());
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getSmartResponse(String userMessage) {
        // Store in memory
        conversationMemory.add(userMessage);
        if (conversationMemory.size() > 20) {
            conversationMemory.remove(0);
        }
        
        // Detect intent with confidence
        Intent detectedIntent = detectIntentWithConfidence(userMessage);
        lastIntent = detectedIntent.name;
        
        // Add to user interests
        updateUserInterests(userMessage);
        
        // Simulate AI "thinking" (will be called with delay in UI)
        String response = generateResponse(detectedIntent, userMessage);
        
        // Post-processing - add personality
        response = addPersonality(response, detectedIntent.name);
        
        return response;
    }
    
    private Intent detectIntentWithConfidence(String message) {
        Intent bestMatch = intents.get(0);
        double highestConfidence = 0;
        
        for (Intent intent : intents) {
            Matcher matcher = intent.pattern.matcher(message);
            if (matcher.matches()) {
                // Calculate confidence based on match length and keyword relevance
                double confidence = 0.7 + (random.nextDouble() * 0.2);
                if (confidence > highestConfidence) {
                    highestConfidence = confidence;
                    bestMatch = intent;
                    bestMatch.confidence = confidence;
                }
            }
        }
        
        return bestMatch;
    }
    
    private String generateResponse(Intent intent, String userMessage) {
        switch (intent.name) {
            case "GREETING":
                return generateGreeting();
            case "RECOMMENDATION":
                return generateRecommendation(userMessage);
            case "PRICE_CHECK":
                return checkPrice(userMessage);
            case "AVAILABILITY":
                return checkAvailability(userMessage);
            case "CART":
                return handleCartQuery();
            case "ORDER":
                return handleOrderQuery();
            case "DELIVERY":
                return getDeliveryInfo();
            case "PAYMENT":
                return getPaymentInfo();
            case "RETURN":
                return getReturnInfo();
            case "HELP":
                return getHelpMenu();
            case "PRODUCT_LIST":
                return getAllProducts();
            case "COMPLAINT":
                return handleComplaint();
            case "THANKS":
                return handleThanks();
            case "SMALL_TALK":
                return handleSmallTalk();
            default:
                return getFallbackResponse(userMessage);
        }
    }
    
    private String generateGreeting() {
        String[] greetings = {
            "🌟 Hello " + username + "! Great to see you at ARS Mart! How can I brighten your shopping experience today?",
            "✨ Hi " + username + "! Ready to discover some amazing deals? I'm here to help!",
            "🎯 Hey " + username + "! Welcome back! What fantastic finds are we looking for today?",
            "💫 " + username + "! So glad you're here! Need help finding something special?",
            "⭐ Welcome to ARS Mart, " + username + "! I've been expecting you. What's on your shopping list?"
        };
        return greetings[random.nextInt(greetings.length)];
    }
    
    private String generateRecommendation(String query) {
        String category = extractCategory(query);
        String budget = extractBudget(query);
        
        List<String[]> candidates = new ArrayList<>();
        
        // Prioritize user interests
        for (Map.Entry<String, Integer> interest : userInterests.entrySet()) {
            for (String[] product : inventoryCache) {
                if (product[1].toLowerCase().contains(interest.getKey().toLowerCase())) {
                    candidates.add(product);
                }
            }
        }
        
        if (candidates.isEmpty()) {
            candidates = new ArrayList<>(inventoryCache);
        }
        
        // Apply budget filter
        if (budget != null) {
            try {
                double budgetAmount = Double.parseDouble(budget);
                candidates.removeIf(p -> Double.parseDouble(p[3]) > budgetAmount);
            } catch (NumberFormatException e) {}
        }
        
        if (candidates.isEmpty()) {
            return "🤔 Hmm, I couldn't find products matching your criteria. Could you tell me more about what you're looking for?";
        }
        
        // Shuffle and take top 3
        Collections.shuffle(candidates);
        List<String[]> recommendations = candidates.subList(0, Math.min(3, candidates.size()));
        
        StringBuilder response = new StringBuilder();
        response.append("🎯 **After analyzing your preferences, here's what I recommend:**\n\n");
        
        for (String[] product : recommendations) {
            double price = Double.parseDouble(product[3]);
            response.append(String.format("┌─────────────────────────────────┐\n"));
            response.append(String.format("│ 📦 **%s**\n", product[0]));
            response.append(String.format("│ 📂 Category: %s\n", product[1]));
            response.append(String.format("│ 💰 Price: Rs. %.2f\n", price));
            response.append(String.format("│ 📊 In stock: %s units\n", product[2]));
            response.append(String.format("└─────────────────────────────────┘\n\n"));
        }
        
        response.append("💡 **Pro tip:** Type **\"add [product name] [quantity]\"** to add any of these to your cart!\n\n");
        response.append("Would you like more personalized recommendations?");
        
        return response.toString();
    }
    
    private String checkPrice(String query) {
        String productName = extractProductName(query);
        
        if (productName.isEmpty()) {
            return "🤔 Which product's price would you like to know? Please tell me the name.";
        }
        
        for (String[] product : inventoryCache) {
            if (product[0].toLowerCase().contains(productName.toLowerCase())) {
                double price = Double.parseDouble(product[3]);
                int stock = Integer.parseInt(product[2]);
                
                String response = String.format(
                    "💰 **Price Analysis for %s**\n\n" +
                    "┌─────────────────────────────────┐\n" +
                    "│ 💵 Price: Rs. %.2f\n" +
                    "│ 📊 Stock: %s units available\n" +
                    "│ 📂 Category: %s\n" +
                    "└─────────────────────────────────┘\n\n",
                    product[0], price, product[2], product[1]);
                
                if (stock < 10) {
                    response += "⚠️ **Limited stock available!** Only " + stock + " units left.\n\n";
                }
                
                response += "💡 Type **\"add " + product[0] + " 1\"** to purchase now!";
                return response;
            }
        }
        
        return "❌ I searched my inventory but couldn't find \"" + productName + "\".\n\n🔍 Try checking the spelling or ask me to **\"list all products\"** to see what's available.";
    }
    
    private String checkAvailability(String query) {
        String productName = extractProductName(query);
        
        if (productName.isEmpty()) {
            return "Which product are you looking for? Let me check our inventory for you!";
        }
        
        for (String[] product : inventoryCache) {
            if (product[0].toLowerCase().contains(productName.toLowerCase())) {
                int stock = Integer.parseInt(product[2]);
                if (stock > 0) {
                    return String.format("✅ **Good news!** %s is available!\n\n📊 **Stock available:** %s units\n💰 **Price:** Rs. %s\n\n🛒 Want me to help you add it to your cart?", 
                        product[0], product[2], product[3]);
                } else {
                    return String.format("❌ **Out of stock!** %s is currently unavailable.\n\n🔄 Would you like me to recommend similar products?", product[0]);
                }
            }
        }
        
        return "🔍 I don't see \"" + productName + "\" in our inventory. Try one of these popular items:\n" + getPopularProducts();
    }
    
    private String handleCartQuery() {
        Cart cart = new Cart(username);
        if (cart.isEmpty()) {
            return "🛒 **Your cart is empty**\n\n💡 Let me help you find something great! Tell me what you're looking for or type **\"recommend\"** for personalized suggestions.";
        } else {
            return String.format("🛒 **Your Shopping Cart Summary**\n\n" +
                "┌─────────────────────────────────┐\n" +
                "│ 📦 Items: %d\n" +
                "│ 💰 Subtotal: Rs. %.2f\n" +
                "│ 💳 Payment charges: Rs. %.2f\n" +
                "│ ════════════════════════════════\n" +
                "│ 💵 Total: Rs. %.2f\n" +
                "└─────────────────────────────────┘\n\n" +
                "✅ Ready to checkout? Go to **Shopping Cart** from your dashboard!",
                cart.getItemCount(), cart.getSubtotal(), cart.getPaymentCharges(), cart.getFinalBill());
        }
    }
    
    private String handleOrderQuery() {
        String history = BackendConnector.runCommand("history_user " + username);
        if (history == null || history.isEmpty() || history.equals("EMPTY")) {
            return "📭 **No orders yet**\n\n🎯 Ready to make your first purchase? Let me help you find the perfect item! Type **\"recommend\"** to get started.";
        } else {
            return "📦 **Order History Available**\n\nYou have previous orders! To see all your order details:\n\n1️⃣ Go to your **User Dashboard**\n2️⃣ Click on **\"View Orders\"**\n3️⃣ See your complete purchase history\n\n💡 Want to reorder something? Let me know what you liked!";
        }
    }
    
    private String getDeliveryInfo() {
        return "🚚 **Delivery Information**\n\n" +
               "┌─────────────────────────────────┐\n" +
               "│ 📍 **Standard Delivery**         │\n" +
               "│    • Time: 3-5 business days    │\n" +
               "│    • Cost: Rs. 150              │\n" +
               "├─────────────────────────────────┤\n" +
               "│ ⚡ **Express Delivery**          │\n" +
               "│    • Time: 1-2 business days    │\n" +
               "│    • Cost: Rs. 350              │\n" +
               "├─────────────────────────────────┤\n" +
               "│ 🎉 **Free Shipping**             │\n" +
               "│    • On orders over Rs. 2000    │\n" +
               "│    • Standard delivery only     │\n" +
               "├─────────────────────────────────┤\n" +
               "│ 📦 **Cash on Delivery**          │\n" +
               "│    • Extra Rs. 100 fee          │\n" +
               "│    • Pay when you receive       │\n" +
               "└─────────────────────────────────┘\n\n" +
               "📍 We deliver nationwide! Need to track an order?";
    }
    
    private String getPaymentInfo() {
        return "💳 **Payment Methods**\n\n" +
               "┌─────────────────────────────────┐\n" +
               "│ 💵 **Cash on Delivery (COD)**    │\n" +
               "│    • Extra charge: Rs. 100      │\n" +
               "│    • Pay at your doorstep       │\n" +
               "├─────────────────────────────────┤\n" +
               "│ 💳 **Credit/Debit Card**         │\n" +
               "│    • No extra charges           │\n" +
               "│    • Visa, Mastercard, Amex     │\n" +
               "├─────────────────────────────────┤\n" +
               "│ 📱 **Mobile Wallet**             │\n" +
               "│    • JazzCash, Easypaisa        │\n" +
               "│    • Instant payment            │\n" +
               "├─────────────────────────────────┤\n" +
               "│ 🏦 **Online Banking**            │\n" +
               "│    • All major banks supported  │\n" +
               "│    • Secure payment gateway     │\n" +
               "└─────────────────────────────────┘\n\n" +
               "🔒 All payments are secure and encrypted!";
    }
    
    private String getReturnInfo() {
        return "🔄 **Return & Refund Policy**\n\n" +
               "┌─────────────────────────────────┐\n" +
               "│ ✅ **7-Day Return Window**       │\n" +
               "│    • Item must be unused        │\n" +
               "│    • Original packaging required│\n" +
               "├─────────────────────────────────┤\n" +
               "│ 💰 **Refund Process**            │\n" +
               "│    • 5-7 business days          │\n" +
               "│    • Refund to original method  │\n" +
               "├─────────────────────────────────┤\n" +
               "│ 📞 **Contact Support**           │\n" +
               "│    • Email: support@arsmart.com │\n" +
               "│    • Phone: +92 123 4567890    │\n" +
               "└─────────────────────────────────┘\n\n" +
               "Need to return something? I can help start the process!";
    }
    
    private String getHelpMenu() {
        return "🤖 **ARS Mart AI - Complete Help Guide**\n\n" +
               "┌─────────────────────────────────────────────────┐\n" +
               "│ 🎯 **Product Discovery**                         │\n" +
               "│    • \"Recommend a gift for dad\"                 │\n" +
               "│    • \"Products under Rs.500\"                    │\n" +
               "│    • \"Best selling items\"                       │\n" +
               "├─────────────────────────────────────────────────┤\n" +
               "│ 💰 **Pricing & Availability**                    │\n" +
               "│    • \"Price of headphones\"                      │\n" +
               "│    • \"Do you have laptops?\"                     │\n" +
               "│    • \"Show me all products\"                     │\n" +
               "├─────────────────────────────────────────────────┤\n" +
               "│ 🛒 **Shopping & Cart**                           │\n" +
               "│    • \"Add mouse 2 to cart\"                      │\n" +
               "│    • \"What's in my cart?\"                       │\n" +
               "├─────────────────────────────────────────────────┤\n" +
               "│ 📦 **Orders & Delivery**                         │\n" +
               "│    • \"Track my order\"                           │\n" +
               "│    • \"Delivery time\"                            │\n" +
               "│    • \"Payment methods\"                          │\n" +
               "├─────────────────────────────────────────────────┤\n" +
               "│ 🔄 **Returns & Support**                         │\n" +
               "│    • \"Return policy\"                            │\n" +
               "│    • \"Contact support\"                          │\n" +
               "└─────────────────────────────────────────────────┘\n\n" +
               "✨ **What would you like help with today?**";
    }
    
    private String getAllProducts() {
        Map<String, List<String[]>> categorized = new LinkedHashMap<>();
        
        for (String[] product : inventoryCache) {
            categorized.computeIfAbsent(product[1], k -> new ArrayList<>()).add(product);
        }
        
        StringBuilder response = new StringBuilder();
        response.append("📋 **Complete Product Catalog**\n\n");
        
        for (Map.Entry<String, List<String[]>> entry : categorized.entrySet()) {
            response.append("┌─── **").append(entry.getKey().toUpperCase()).append("** ───┐\n");
            for (String[] product : entry.getValue()) {
                response.append(String.format("│ • %s - Rs. %s\n", product[0], product[3]));
            }
            response.append("└─────────────────────────────────┘\n\n");
        }
        
        response.append("💡 Type **\"recommend\"** for personalized suggestions based on your interests!");
        return response.toString();
    }
    
    private String handleComplaint() {
        String[] responses = {
            "😔 I'm really sorry to hear that you're not satisfied. Could you tell me more about the issue? I want to help make it right.",
            "🙏 I apologize for any inconvenience. Please share the details so I can escalate this to our support team immediately.",
            "💔 I understand your frustration. At ARS Mart, customer satisfaction is our top priority. Let me help resolve this for you."
        };
        return responses[random.nextInt(responses.length)];
    }
    
    private String handleThanks() {
        String[] responses = {
            "😊 You're very welcome! Happy to help! Anything else I can assist you with?",
            "🙌 My pleasure! Shopping should be fun and easy. Need anything else?",
            "✨ Anytime! That's what I'm here for. Have a wonderful shopping experience at ARS Mart!"
        };
        return responses[random.nextInt(responses.length)];
    }
    
    private String handleSmallTalk() {
        String[] responses = {
            "🤖 I'm doing fantastic! Just here helping amazing shoppers like you find great products!",
            "✨ I'm powered by ARS Mart AI, always ready and excited to assist you!",
            "💫 I'm feeling great, thank you for asking! How can I make your shopping experience awesome today?"
        };
        return responses[random.nextInt(responses.length)];
    }
    
    private String getFallbackResponse(String query) {
        // Check if query contains any product name
        for (String[] product : inventoryCache) {
            if (query.toLowerCase().contains(product[0].toLowerCase())) {
                return String.format("🤔 I see you mentioned **%s**!\n\nWould you like to:\n• Check its price\n• See if it's available\n• Add it to your cart\n\nJust let me know!", product[0]);
            }
        }
        
        String[] fallbacks = {
            "🤔 Hmm, I'm still learning! Could you rephrase that? I'm best at helping with product recommendations, prices, and orders.",
            "😅 I didn't quite catch that. Try asking me about products, prices, delivery, or type **\"help\"** to see all my features!",
            "💭 Let me think... Could you tell me more about what you're looking for? I specialize in shopping assistance!",
            "🤖 I want to help! Try asking something like:\n• \"Recommend a product\"\n• \"Price of headphones\"\n• \"Delivery information\""
        };
        return fallbacks[random.nextInt(fallbacks.length)];
    }
    
    private void updateUserInterests(String message) {
        for (String[] product : inventoryCache) {
            if (message.toLowerCase().contains(product[0].toLowerCase()) ||
                message.toLowerCase().contains(product[1].toLowerCase())) {
                userInterests.put(product[1], userInterests.getOrDefault(product[1], 0) + 1);
            }
        }
        saveUserProfile();
    }
    
    private String addPersonality(String response, String intent) {
        String[] emojis = {"✨", "🎯", "💫", "⭐", "🌟", "💎", "🔮"};
        if (random.nextDouble() > 0.7 && !intent.equals("HELP")) {
            return emojis[random.nextInt(emojis.length)] + " " + response;
        }
        return response;
    }
    
    private String extractCategory(String query) {
        String[] categories = {"electronics", "clothing", "furniture", "books", "shoes", 
                               "accessories", "mobile", "phone", "laptop", "grocery", 
                               "food", "watch", "bag", "shirt", "pant", "gift"};
        for (String cat : categories) {
            if (query.toLowerCase().contains(cat)) {
                return cat;
            }
        }
        return "";
    }
    
    private String extractBudget(String query) {
        Matcher m = Pattern.compile("(?:under|below|less than|Rs\\.?|PKR|less than)\\s*(\\d+)").matcher(query);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
    
    private String extractProductName(String query) {
        String[] commonPhrases = {"price of", "cost of", "tell me about", "details of", 
                                   "show me", "do you have", "is there", "available", 
                                   "check", "what about", "tell me"};
        for (String phrase : commonPhrases) {
            if (query.toLowerCase().contains(phrase)) {
                String after = query.toLowerCase().split(phrase)[1].trim();
                if (after.contains("?")) after = after.split("\\?")[0];
                String[] words = after.split(" ");
                for (String word : words) {
                    if (word.length() > 3 && !word.matches(".*(and|or|the|a|an|for|from).*")) {
                        return word;
                    }
                }
                return after.split(" ")[0];
            }
        }
        
        // If no phrase, try to find product name
        String[] words = query.split(" ");
        for (String word : words) {
            if (word.length() > 3 && !word.matches(".*(how|what|where|when|why|which|can|you|please|tell|me).*")) {
                return word;
            }
        }
        return "";
    }
    
    private String getPopularProducts() {
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String[] product : inventoryCache) {
            if (count++ >= 3) break;
            sb.append("   • **").append(product[0]).append("** - Rs. ").append(product[3]).append("\n");
        }
        return sb.toString();
    }
    
    public boolean addToCart(String productName, int quantity) {
        for (String[] product : inventoryCache) {
            if (product[0].equalsIgnoreCase(productName)) {
                Cart cart = new Cart(username);
                double price = Double.parseDouble(product[3]);
                cart.addItem(product[0], product[1], quantity, price);
                cart.saveToFile();
                return true;
            }
        }
        return false;
    }
}