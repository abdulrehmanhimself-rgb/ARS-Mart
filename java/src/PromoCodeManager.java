import java.io.*;
import java.util.*;

public class PromoCodeManager {

    private static class PromoCode {
        String code;
        String type;           // "DISCOUNT" or "FREE_SHIPPING"
        double discountPercent; // For DISCOUNT type
        int usageLimit;
        int usageCount;
        boolean active;

        PromoCode(String code, String type, double discountPercent, int usageLimit) {
            this.code = code;
            this.type = type;
            this.discountPercent = discountPercent;
            this.usageLimit = usageLimit;
            this.usageCount = 0;
            this.active = true;
        }

        @Override
        public String toString() {
            return String.format("%s|%s|%.1f|%d|%d|%s", code, type, discountPercent, usageLimit, usageCount, active);
        }
    }

    private Map<String, PromoCode> promoCodes;
    private static final String PROMO_FILE = "promo_codes.txt";

    public PromoCodeManager() {
        this.promoCodes = new HashMap<>();
        loadPromoCodes();
        initializeDefaultCodes();
    }

    /**
     * Initialize default promo codes
     */
    private void initializeDefaultCodes() {
        if (promoCodes.isEmpty()) {
            // Add some default promotional codes
            addPromoCode("SUMMER20", "DISCOUNT", 20, 100);      // 20% discount
            addPromoCode("FREESHIP", "FREE_SHIPPING", 0, 50);   // Free shipping
            addPromoCode("WELCOME10", "DISCOUNT", 10, 200);     // Welcome code: 10% discount
            addPromoCode("SAVE15", "DISCOUNT", 15, 100);        // 15% discount
            
            savePromoCodes();
        }
    }

    /**
     * Add a new promo code
     */
    public void addPromoCode(String code, String type, double discountPercent, int usageLimit) {
        promoCodes.put(code.toUpperCase(), new PromoCode(code.toUpperCase(), type, discountPercent, usageLimit));
    }

    /**
     * Validate and apply promo code
     */
    public PromoCodeResult validatePromoCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return new PromoCodeResult(false, "No promo code entered", null);
        }

        String codeUpper = code.toUpperCase().trim();
        if (!promoCodes.containsKey(codeUpper)) {
            return new PromoCodeResult(false, "Invalid promo code: " + code, null);
        }

        PromoCode promo = promoCodes.get(codeUpper);
        
        if (!promo.active) {
            return new PromoCodeResult(false, "Promo code is inactive", null);
        }

        if (promo.usageCount >= promo.usageLimit) {
            return new PromoCodeResult(false, "Promo code usage limit reached", null);
        }

        return new PromoCodeResult(true, "Promo code applied successfully", promo);
    }

    /**
     * Use a promo code (increment usage count)
     */
    public void usePromoCode(String code) {
        String codeUpper = code.toUpperCase().trim();
        if (promoCodes.containsKey(codeUpper)) {
            PromoCode promo = promoCodes.get(codeUpper);
            promo.usageCount++;
            savePromoCodes();
        }
    }

    /**
     * Save promo codes to file
     */
    private void savePromoCodes() {
        try (FileWriter fw = new FileWriter(PROMO_FILE)) {
            for (PromoCode promo : promoCodes.values()) {
                fw.write(promo.toString() + "\n");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save promo codes: " + e.getMessage());
        }
    }

    /**
     * Load promo codes from file
     */
    private void loadPromoCodes() {
        File file = new File(PROMO_FILE);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    try {
                        String code = parts[0];
                        String type = parts[1];
                        double discountPercent = Double.parseDouble(parts[2]);
                        int usageLimit = Integer.parseInt(parts[3]);
                        int usageCount = Integer.parseInt(parts[4]);
                        boolean active = Boolean.parseBoolean(parts[5]);

                        PromoCode promo = new PromoCode(code, type, discountPercent, usageLimit);
                        promo.usageCount = usageCount;
                        promo.active = active;
                        
                        promoCodes.put(code, promo);
                    } catch (NumberFormatException e) {
                        System.err.println("[ERROR] Invalid promo code format: " + line);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("[ERROR] Failed to load promo codes: " + e.getMessage());
        }
    }

    /**
     * Get list of active promo codes for display
     */
    public String getActivePromoCodesInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══ AVAILABLE PROMO CODES ═══\n");

        for (PromoCode promo : promoCodes.values()) {
            if (promo.active && promo.usageCount < promo.usageLimit) {
                if ("DISCOUNT".equals(promo.type)) {
                    sb.append(String.format("• %s - Get %d%% discount\n", promo.code, (int)promo.discountPercent));
                } else if ("FREE_SHIPPING".equals(promo.type)) {
                    sb.append(String.format("• %s - Get Free Shipping\n", promo.code));
                }
            }
        }

        return sb.toString();
    }

    /**
     * Result class for promo code validation
     */
    public static class PromoCodeResult {
        public boolean valid;
        public String message;
        public PromoCode promoCode;

        PromoCodeResult(boolean valid, String message, PromoCode promoCode) {
            this.valid = valid;
            this.message = message;
            this.promoCode = promoCode;
        }

        public boolean isFreeShipping() {
            return valid && promoCode != null && "FREE_SHIPPING".equals(promoCode.type);
        }

        public double getDiscountPercent() {
            return valid && promoCode != null && "DISCOUNT".equals(promoCode.type) 
                ? promoCode.discountPercent : 0;
        }
    }
}
