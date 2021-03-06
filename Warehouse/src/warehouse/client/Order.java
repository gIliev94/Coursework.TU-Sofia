package warehouse.client;

import java.util.List;

/**
 * Implementation for order object.
 * 
 * @author Georgi Iliev
 *
 */
public class Order {

    private int priceDrop;
    private double reducedPrice;
    private double totalPrice;
    private int discount;
    private int client;
    private int productId;
    private int quantity;

    private List<Object> prices;

    public int getPriceDrop() {
	return priceDrop;
    }

    public void setPriceDrop(int priceDrop) {
	this.priceDrop = priceDrop;
    }

    public double getReducedPrice() {
	return reducedPrice;
    }

    public void setReducedPrice(double reducedPrice) {
	this.reducedPrice = reducedPrice;
    }

    public double getTotalPrice() {
	return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
	this.totalPrice = totalPrice;
    }

    public int getDiscount() {
	return discount;
    }

    public void setDiscount(int discount) {
	this.discount = discount;
    }

    public int getClient() {
	return client;
    }

    public void setClient(int client) {
	this.client = client;
    }

    public int getProductId() {
	return productId;
    }

    public void setProductId(int productId) {
	this.productId = productId;
    }

    public int getQuantity() {
	return quantity;
    }

    public void setQuantity(int quantity) {
	this.quantity = quantity;
    }

    public List<Object> getPrices() {
	return prices;
    }

    public void setPrices(List<Object> prices) {
	this.prices = prices;
    }

}
