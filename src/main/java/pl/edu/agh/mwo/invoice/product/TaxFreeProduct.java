package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;
import java.util.Objects;

public class TaxFreeProduct extends Product {
    public TaxFreeProduct(String name, BigDecimal price) {
        super(name, price, BigDecimal.ZERO);

    }
}
