package warehouse.utilities;

import java.util.List;

/**
 * @author Gerogi Iliev
 *
 */
public class OrderProcessor {

    /**
     * @param order
     * @return
     */
    public static Order getProcessedOrder(Order order) {

	switch (order.getQuantity()) {

	case OrdersAndDiscounts.SMALL_ORDER:
	    order = buildOrder(order, OrdersAndDiscounts.DISCOUNT_20_UNITS, OrdersAndDiscounts.PRICEDROP_RATIO_SMALL);
	    break;
	case OrdersAndDiscounts.MEDIUM_ORDER:
	    order = buildOrder(order, OrdersAndDiscounts.DISCOUNT_40_UNITS, OrdersAndDiscounts.PRICEDROP_RATIO_MEDIUM);
	    break;
	case OrdersAndDiscounts.LARGE_ORDER:
	    order = buildOrder(order, OrdersAndDiscounts.DISCOUNT_60_UNITS, OrdersAndDiscounts.PRICEDROP_RATIO_LARGE);
	    break;
	case OrdersAndDiscounts.SUPER_ORDER:
	    order = buildOrder(order, OrdersAndDiscounts.DISCOUNT_80_UNITS, OrdersAndDiscounts.PRICEDROP_RATIO_SUPER);
	    break;
	default:
	    order = buildOrder(order, OrdersAndDiscounts.NO_DISCOUNT, OrdersAndDiscounts.NO_PRICEDROP);
	    break;
	}

	return order;
    }

    private static Order buildOrder(Order order, int discount, double priceDropRatio) {

	List<Float> prices = order.getPrices();

	order.setDiscount(discount);
	order.setPriceDrop((int) priceDropRatio * 100);

	double reducedPrice = (double) prices.get(order.getProductId() - 1) - priceDropRatio
		* prices.get(order.getProductId() - 1);
	reducedPrice = Math.round(reducedPrice * 100) / 100;

	double totalPrice = Math.round((reducedPrice * order.getQuantity()) * 100) / 100;
	order.setReducedPrice(reducedPrice);
	order.setTotalPrice(totalPrice);

	return order;
    }

}
