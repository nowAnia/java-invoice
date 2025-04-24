package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {
   // private Collection<Product> products= new ArrayList<>();
    private Map<Product, Integer> quantityOfProducts = new LinkedHashMap<>();
    private static int number = 0;
    private int numberOfProducts = 0;

    public Invoice() {
        Invoice.number++;
    }

    public int getNumber() {
        return number;
    }

    public void addProduct(Product product) {
        numberOfProducts++;
        if (product == null) {
            throw new IllegalArgumentException("Product can not be null");
        }
        quantityOfProducts.merge(product,1, Integer::sum);
    }

    public void addProduct(Product product, Integer quantity) {
        numberOfProducts +=quantity;
        if (quantity == 0 || quantity < 0){
            throw new IllegalArgumentException("Quantity can not be empty or minus");
        }
        quantityOfProducts.merge(product,quantity,Integer::sum);
    }

    public BigDecimal getSubtotal() {
        BigDecimal getSubTotal = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> position : quantityOfProducts.entrySet()) {
            getSubTotal = getSubTotal.add(position.getKey().getPrice().multiply(BigDecimal.valueOf(position.getValue())));
        }

        return getSubTotal;
    }

    public BigDecimal getTax() {
        BigDecimal tax = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> position : quantityOfProducts.entrySet()) {
            tax = tax.add(position.getKey().getTaxPercent().multiply(position.getKey().getPrice()).multiply(BigDecimal.valueOf(position.getValue())));
        }

        return tax;
    }

    public BigDecimal getTotal() {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<Product, Integer> position : quantityOfProducts.entrySet()) {
            totalPrice = totalPrice.add(position.getKey().getPriceWithTax().multiply(BigDecimal.valueOf(position.getValue())));
        }

        return totalPrice;
    }

    public int getNumberOfProducts() {
        return numberOfProducts;
    }

    public String printListOfProducts() {
        StringBuilder message = new StringBuilder("Number: " + number + "\n");

        for (Map.Entry<Product, Integer> position : quantityOfProducts.entrySet()) {
            BigDecimal price = position.getKey().getPriceWithTax().multiply(BigDecimal.valueOf(position.getValue()));
            message.append(position.getKey().getName() +" "+ position.getValue() + " " + price + "\n");
        }
        message.append("Amount of products: "+ numberOfProducts + "\n");
        return message.toString();
    }


}
