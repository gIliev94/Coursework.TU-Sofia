package warehouse.utilities;

/**
 * Constants for identifying the preset order types, discounts and price drop
 * ratios.
 * 
 * @author Georgi Iliev
 *
 */
public class OrdersAndDiscountsConstants {

    private OrdersAndDiscountsConstants() {
    }

    public static final int SMALL_ORDER = 20;
    public static final int MEDIUM_ORDER = 40;
    public static final int LARGE_ORDER = 60;
    public static final int SUPER_ORDER = 80;

    public static final int NO_DISCOUNT = 0;
    public static final int DISCOUNT_20_UNITS = 1;
    public static final int DISCOUNT_40_UNITS = 2;
    public static final int DISCOUNT_60_UNITS = 3;
    public static final int DISCOUNT_80_UNITS = 4;

    public static final double NO_PRICEDROP = 0.00;
    public static final double PRICEDROP_RATIO_SMALL = 0.05;
    public static final double PRICEDROP_RATIO_MEDIUM = 10;
    public static final double PRICEDROP_RATIO_LARGE = 15;
    public static final double PRICEDROP_RATIO_SUPER = 20;

}
