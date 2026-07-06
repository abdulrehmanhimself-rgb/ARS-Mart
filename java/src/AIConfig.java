public class AIConfig {
    
    // Your Google Gemini API Key (WORKING)
    public static final String GEMINI_API_KEY = "AIzaSyCIufcvChKmxEE3Rna8kyGftg1JYjZ_o5M";
    
    // Using gemini-2.0-flash (Fast, Free, and Working)
    public static final String GEMINI_MODEL = "gemini-2.0-flash";
    
    // API URL
    public static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL + ":generateContent?key=" + GEMINI_API_KEY;
    
    // Check if API is configured
    public static boolean isConfigured() {
        return GEMINI_API_KEY != null && !GEMINI_API_KEY.isEmpty() && GEMINI_API_KEY.length() > 10;
    }
}