package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.function.BinaryOperator;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.w3c.dom.ls.LSOutput;
import pl.edu.agh.mwo.invoice.product.*;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTax()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        Assert.assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        Assert.assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getTotal(), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTax()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }


    @Test
    public void testInvoiceNumber(){
        int number = invoice.getNumber();
        Assert.assertThat(number, Matchers.greaterThan(0));
    }

    @Test
    public void testInvoiceNumberHaveConsequentNumber() {
        int number1 = new Invoice().getNumber();
        int number2 = new Invoice().getNumber();
        Assert.assertThat(number1, Matchers.equalTo(number2 - 1));
    }

    @Test
    public void amountOfProductsIsGreaterThanZero() {
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(invoice.getNumberOfProducts(), Matchers.greaterThan(0));
    }

    @Test
    public void amountOfProductsIsEqualNumberOfProducts() {
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(invoice.getNumberOfProducts(), Matchers.equalTo(1005));
    }

    @Test
    public void shouldPrintInvoiceWithItems(){
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        String result = invoice.printListOfProducts();
        int invoiceNumber = invoice.getNumber();
        String expectedMessage = "Number: "+invoiceNumber+"\n"+
                """
                Kubek 2 10
                Kozi Serek 3 32.40
                Amount of products: 5
                """;
        Assert.assertEquals(expectedMessage, result);
    }

    @Test
    public void shouldPrintTemplateWithoutItems(){
        String result = invoice.printListOfProducts();
        int invoiceNumber = invoice.getNumber();
        String expectedMessage = "Number: "+invoiceNumber+"\n"+
                """
                Amount of products: 0
                """;
        Assert.assertEquals(expectedMessage, result);
    }

    @Test
    public void shouldDuplicateProductAfterAddingTheSameProduct(){
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")));
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")));
        String result = invoice.printListOfProducts();
        int invoiceNumber = invoice.getNumber();
        String expectedMessage = "Number: "+invoiceNumber+"\n"+
                """
                Kubek 2 10
                Amount of products: 2
                """;
        Assert.assertEquals(expectedMessage, result);
    }

    @Test
    public void shouldReturnProperPriceForBottleOfWine(){
        ExciseProducts resling = new BottleOfWine("Resling", new BigDecimal(5));
        Assert.assertThat(resling.getPrice(), Matchers.equalTo(new BigDecimal(5)));
    }

    @Test
    public void shouldReturnProperPriceWithTaxAndExciseForBottleOfWine(){
        ExciseProducts resling = new BottleOfWine("Carlo Rossi", new BigDecimal(25));
        Assert.assertThat(resling.getPriceWithTax(), Matchers.equalTo(new BigDecimal("36.31")));
    }

    @Test
    public void shouldReturnProperPriceWithTaxAndExciseForFuelCanister(){
        ExciseProducts fuel = new FuelCanister("Fuel", new BigDecimal(50));
        Assert.assertThat(fuel.getPriceWithTax(), Matchers.equalTo(new BigDecimal("67.06")));
    }

    @Test
    public void shouldPrintInvoiceWithVariousItemsIncludingExciseProducts(){
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        invoice.addProduct(new BottleOfWine("Wine", new BigDecimal("25")));
        String result = invoice.printListOfProducts();
        int invoiceNumber = invoice.getNumber();
        String expectedMessage = "Number: "+invoiceNumber+"\n"+
                """
                Kubek 2 10
                Kozi Serek 3 32.40
                Wine 1 36.31
                Amount of products: 6
                """;
        Assert.assertEquals(expectedMessage, result);
    }


}
