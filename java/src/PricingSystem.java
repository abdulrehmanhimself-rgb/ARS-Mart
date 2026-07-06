public class PricingSystem {

    // Discount tiers (amount range -> discount percentage)
    private static final double[][] DISCOUNT_TIERS = {
        {1500, 5},      // Up to 1500: 5% discount
        {2500, 10},     // Up to 2500: 10% discount
        {5000, 12},     // Up to 5000: 12% discount
        {10000, 15},    // Up to 10000: 15% discount
        {100000, 20}    // Up to 100000: 20% discount
    };

    // Shipping fees
    private static final double DEFAULT_SHIPPING = 200;
    private static final double BULK_SHIPPING = 500;
    private static final double BULK_THRESHOLD = 10000;

    /**
     * Calculate discount percentage based on subtotal
     */
    public static double getDiscountPercentage(double subtotal) {
        for (double[] tier : DISCOUNT_TIERS) {
            if (subtotal <= tier[0]) {
                return tier[1];
            }
        }
        return 0;
    }

    /**
     * Calculate discount amount
     */
    public static double calculateDiscount(double subtotal) {
        double discountPercent = getDiscountPercentage(subtotal);
        return (subtotal * discountPercent) / 100.0;
    }

    /**
     * Get shipping fee based on subtotal
     */
    public static double getShippingFee(double subtotal) {
        if (subtotal > BULK_THRESHOLD) {
            return BULK_SHIPPING;
        }
        return DEFAULT_SHIPPING;
    }

    /**
     * Calculate final amount with discount and shipping
     */
    public static double calculateFinalAmount(double subtotal, String paymentMethod, boolean freeShipping) {
        double discount = calculateDiscount(subtotal);
        double discountedAmount = subtotal - discount;
        
        double shipping = 0;
        if (!freeShipping) {
            shipping = getShippingFee(subtotal);
        }
        
        // COD has additional charges
        double charges = "COD".equals(paymentMethod) ? 100 : 0;
        
        return discountedAmount + shipping + charges;
    }

    /**
     * Get discount info as formatted string
     */
    public static String getDiscountInfo(double subtotal) {
        double discount = calculateDiscount(subtotal);
        if (discount <= 0) return "No discount available";
        return String.format("Discount (%d%%): Rs. %.2f", (int) getDiscountPercentage(subtotal), discount);
    }

    /**
     * Get shipping info as formatted string
     */
    public static String getShippingInfo(double subtotal, boolean freeShipping) {
        if (freeShipping) {
            return "Free Shipping (Promo Code Applied)";
        }
        double shipping = getShippingFee(subtotal);
        return String.format("Shipping: Rs. %.2f", shipping);
    }
}
