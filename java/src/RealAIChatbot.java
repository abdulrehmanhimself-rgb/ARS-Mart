import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class RealAIChatbot {
    
    private String username;
    
    public RealAIChatbot(String username) {
        this.username = username;
        System.out.println("🤖 AI Chatbot initialized for: " + username);
        System.out.println("📡 API Status: " + (AIConfig.isConfigured() ? "CONNECTED" : "OFFLINE"));
    }
    
    public String getResponse(String userMessage) {
        // First try real API
        if (AIConfig.isConfigured()) {
            try {
                String apiResponse = callGeminiAPI(userMessage);
                if (apiResponse != null && !apiResponse.isEmpty()) {
                    return apiResponse;
                }
            } catch (Exception e) {
                System.err.println("API Error: " + e.getMessage());
                // Fall through to offline response
            }
        }
        
        // Fallback to smart offline responses
        return getSmartOfflineResponse(userMessage);
    }
    
    private String callGeminiAPI(String userMessage) throws Exception {
        // Build the prompt
        String prompt = "You are ARS Mart AI Assistant, a helpful shopping assistant for ARS Mart in Pakistan. " +
                       "Current user: " + username + ". " +
                       "Answer this question helpfully and concisely (max 3-4 sentences): " + userMessage;
        
        // Create JSON request body
        String jsonBody = String.format(
            "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
            escapeJson(prompt)
        );
        
        // Create connection
        URL url = new URL(AIConfig.GEMINI_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        
        // Send request
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
        
        // Check response
        int responseCode = conn.getResponseCode();
        
        if (responseCode == 200) {
            // Read success response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            
            // Parse the response
            return parseResponse(response.toString());
        } else {
            // Read error response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            StringBuilder error = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                error.append(line);
            }
            reader.close();
            System.err.println("API Error " + responseCode + ": " + error.toString());
            return null;
        }
    }
    
    private String parseResponse(String json) {
        try {
            // Find the text in the response
            String searchFor = "\"text\":\"";
            int start = json.indexOf(searchFor);
            if (start == -1) return null;
            
            start += searchFor.length();
            int end = start;
            
            while (end < json.length()) {
                char c = json.charAt(end);
                if (c == '"' && (end == 0 || json.charAt(end-1) != '\\')) {
                    break;
                }
                end++;
            }
            
            if (end > start) {
                String text = json.substring(start, end);
                return text.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
            }
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
        return null;
    }
    
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
    
    private String getSmartOfflineResponse(String message) {
        String msg = message.toLowerCase();
        
        // Order related
        if (msg.contains("order") || msg.contains("buy") || msg.contains("purchase")) {
            if (msg.contains("how") || msg.contains("place")) {
                return "🛍️ **How to Place an Order:**\n\n" +
                       "1️⃣ Go to 'Browse Products' from your dashboard\n" +
                       "2️⃣ Find the product you want\n" +
                       "3️⃣ Enter quantity and click 'Add to Cart'\n" +
                       "4️⃣ Go to 'Shopping Cart'\n" +
                       "5️⃣ Select payment method (COD +Rs.100 / Online free)\n" +
                       "6️⃣ Click 'Confirm Order'\n\n" +
                       "✅ That's it! Your order will arrive in 3-5 days.";
            }
            return "📦 To place an order, browse products, add to cart, then checkout from Shopping Cart page.";
        }
        
        // Product related
        if (msg.contains("product") || msg.contains("item") || msg.contains("stuff")) {
            if (msg.contains("what") || msg.contains("list") || msg.contains("show")) {
                return "📋 **Our Products:**\n\n" +
                       "We have various products in categories like:\n" +
                       "• Electronics (laptops, phones, headphones)\n" +
                       "• Clothing (shirts, pants, jackets)\n" +
                       "• Accessories (watches, bags)\n" +
                       "• And much more!\n\n" +
                       "Go to 'Browse Products' to see everything!";
            }
            return "🛒 You can browse all our products by clicking 'Browse Products' on your dashboard.";
        }
        
        // Price related
        if (msg.contains("price") || msg.contains("cost") || msg.contains("how much")) {
            return "💰 To check prices:\n\n" +
                   "• Go to 'Browse Products' from your dashboard\n" +
                   "• All products show their prices\n" +
                   "• You can also search for specific products\n\n" +
                   "Need help finding something specific?";
        }
        
        // Delivery related
        if (msg.contains("delivery") || msg.contains("shipping") || msg.contains("deliver")) {
            return "🚚 **Delivery Information:**\n\n" +
                   "┌─────────────────────────────────────────┐\n" +
                   "│ 📍 Standard Delivery: 3-5 days, Rs.150  │\n" +
                   "│ ⚡ Express Delivery: 1-2 days, Rs.350   │\n" +
                   "│ 🎉 Free Shipping: Orders over Rs.2000   │\n" +
                   "│ 📦 COD Fee: +Rs.100                     │\n" +
                   "└─────────────────────────────────────────┘\n\n" +
                   "📍 We deliver nationwide across Pakistan!";
        }
        
        // Payment related
        if (msg.contains("payment") || msg.contains("pay") || msg.contains("cod")) {
            return "💳 **Payment Methods:**\n\n" +
                   "• 💵 Cash on Delivery (COD): +Rs.100 fee\n" +
                   "• 💳 Credit/Debit Card: No extra charges\n" +
                   "• 📱 Mobile Wallet (JazzCash/Easypaisa)\n" +
                   "• 🏦 Online Banking\n\n" +
                   "All payments are secure and encrypted!";
        }
        
        // Return related
        if (msg.contains("return") || msg.contains("refund") || msg.contains("exchange")) {
            return "🔄 **Return & Refund Policy:**\n\n" +
                   "✅ 7-day return window from delivery date\n" +
                   "✅ Item must be unused with original packaging\n" +
                   "✅ Refund processed in 5-7 business days\n" +
                   "📞 Contact: support@arsmart.com\n\n" +
                   "Need to return something? I can help guide you!";
        }
        
        // Cart related
        if (msg.contains("cart")) {
            return "🛒 **Your Cart:**\n\n" +
                   "You can view your cart by clicking 'Shopping Cart' on your dashboard.\n\n" +
                   "From there you can:\n" +
                   "• Update quantities\n" +
                   "• Remove items\n" +
                   "• Proceed to checkout";
        }
        
        // Help
        if (msg.contains("help") || msg.contains("what can you do")) {
            return "🤖 **ARS Mart Assistant - Help Menu**\n\n" +
                   "┌─────────────────────────────────────────┐\n" +
                   "│ 💬 **Things you can ask me:**            │\n" +
                   "├─────────────────────────────────────────┤\n" +
                   "│ 🛍️ \"How to place an order?\"             │\n" +
                   "│ 💰 \"Check product prices\"                │\n" +
                   "│ 🚚 \"Delivery time and charges\"           │\n" +
                   "│ 💳 \"Payment methods\"                     │\n" +
                   "│ 🔄 \"Return policy\"                       │\n" +
                   "│ 🛒 \"View my cart\"                        │\n" +
                   "└─────────────────────────────────────────┘\n\n" +
                   "Just type your question and I'll help you!";
        }
        
        // Greeting
        if (msg.contains("hi") || msg.contains("hello") || msg.contains("hey")) {
            return "👋 Hello " + username + "! Welcome to ARS Mart!\n\n" +
                   "I'm your AI shopping assistant. How can I help you today?\n\n" +
                   "💡 Try asking: \"How to place an order?\" or \"Delivery information\"";
        }
        
        // Default
        return "🤔 I'm your ARS Mart assistant!\n\n" +
               "💡 **Try asking me:**\n" +
               "• \"How to place an order?\"\n" +
               "• \"Delivery time\"\n" +
               "• \"Payment methods\"\n" +
               "• \"Return policy\"\n" +
               "• \"Help\"\n\n" +
               "I'm here to help with all your shopping needs! 🛍️";
    }
}