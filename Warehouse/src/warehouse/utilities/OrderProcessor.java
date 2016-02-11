package warehouse.utilities;

import java.math.BigDecimal;
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
	int orderedQuantity = order.getQuantity();

	switch (orderedQuantity) {
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
	List<Object> priceList = order.getPrices();

	double price = (double) priceList.get(order.getProductId() - 1);

	double reducedPrice = (double) price - priceDropRatio * price;
	Double preciseReducedPrice = new BigDecimal(reducedPrice).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

	double totalPrice = reducedPrice * order.getQuantity();
	Double preciseTotalPrice = new BigDecimal(totalPrice).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();

	order.setDiscount(discount);
	order.setPriceDrop((int) (priceDropRatio * 100));
	order.setReducedPrice(preciseReducedPrice);
	order.setTotalPrice(preciseTotalPrice);

	return order;
    }

}
