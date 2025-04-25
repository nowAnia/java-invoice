package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class ExciseProducts extends OtherProduct {
    private final BigDecimal excise = new BigDecimal("5.56");

    public ExciseProducts(String name, BigDecimal price) {
        super(name, price);
    }

    public BigDecimal getPriceWithTax() {
        BigDecimal priceWithTax = super.getPriceWithTax();
        return priceWithTax.add(excise);
    }

    public BigDecimal getExcise() {
        return excise;
    }

}
