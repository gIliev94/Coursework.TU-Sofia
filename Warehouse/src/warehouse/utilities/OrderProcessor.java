package warehouse.utilities;

import java.util.List;

/**
 * Processor implementation for the orders.
 * 
 * @author Gerogi Iliev
 *
 */
public class OrderProcessor {

    /**
     * Processes the order, identifiying the order type, discount and pricedrop
     * ratios.
     * 
     * @param order
     *            - the order to be processed.
     * @return An order that is ready to be submitted.
     */
    public static Order getProcessedOrder(Order order) {

	switch (order.getQuantity()) {

	case OrdersAndDiscountsConstants.SMALL_ORDER:
	    order = buildOrder(order, OrdersAndDiscountsConstants.DISCOUNT_20_UNITS,
		    OrdersAndDiscountsConstants.PRICEDROP_RATIO_SMALL);
	    break;
	case OrdersAndDiscountsConstants.MEDIUM_ORDER:
	    order = buildOrder(order, OrdersAndDiscountsConstants.DISCOUNT_40_UNITS,
		    OrdersAndDiscountsConstants.PRICEDROP_RATIO_MEDIUM);
	    break;
	case OrdersAndDiscountsConstants.LARGE_ORDER:
	    order = buildOrder(order, OrdersAndDiscountsConstants.DISCOUNT_60_UNITS,
		    OrdersAndDiscountsConstants.PRICEDROP_RATIO_LARGE);
	    break;
	case OrdersAndDiscountsConstants.SUPER_ORDER:
	    order = buildOrder(order, OrdersAndDiscountsConstants.DISCOUNT_80_UNITS,
		    OrdersAndDiscountsConstants.PRICEDROP_RATIO_SUPER);
	    break;
	default:
	    order = buildOrder(order, OrdersAndDiscountsConstants.NO_DISCOUNT, OrdersAndDiscountsConstants.NO_PRICEDROP);
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
