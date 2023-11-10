package de.bredex.javaproject.objects.invoice;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.bredex.javaproject.objects.product.Product;
import de.bredex.javaproject.objects.user.User;
import de.bredex.javaproject.objects.user.customer.Customer;
import de.bredex.javaproject.utils.MailOrderBusinessUtils;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
@Table(name = "invoice")
public class Invoice {

    @Id
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "quantity")
    private int quantity;
    @Column(name = "total_price")
    private double totalPrice;
    @Column(name = "price_without_vat")
    private double priceWithoutVat;
    @Column(name = "is_premium_customer")
    private boolean isPremiumCustomer;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;

    public Invoice() {

    }

    public Invoice(Product product, int quantity, boolean isPremiumCustomer, Customer customer) {
        this(MailOrderBusinessUtils.generateRandomNumber(5), product, quantity, isPremiumCustomer, customer);
    }

    public Invoice(int id, Product product, int quantity, boolean isPremiumCustomer, Customer customer) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.isPremiumCustomer = isPremiumCustomer;
        this.customer = customer;
        this.totalPrice = roundDouble(calculatePrice(product.getPrice(), quantity));
        this.priceWithoutVat = roundDouble(calculatePriceWithoutVAT(this.totalPrice, product.getVatRate()));
    }

    public Invoice(int id, Product product, int quantity, double totalPrice, double priceWithoutVat,
            boolean isPremiumCustomer, Customer customer) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.priceWithoutVat = priceWithoutVat;
        this.isPremiumCustomer = isPremiumCustomer;
        this.customer = customer;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public Product getProduct() {
        return this.product;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public double getTotalPrice() {
        return this.totalPrice;
    }

    public double getPriceWithoutVat() {
        return this.priceWithoutVat;
    }

    public boolean isPremiumCustomer() {
        return this.isPremiumCustomer;
    }

    public void print(User user) {
        System.out.println();
        System.out.println(user.getFirstName() + " " + user.getLastName());
        System.out.println(user.getStreet() + " " + user.getHouseNo());
        System.out.println(user.getZipCode() + " " + user.getResidence());
        System.out.println();
        System.out.println();
        System.out.println("Danke " + user.getFirstName() + " fuer deinen Einkauf!");

        System.out.println("Folgende Positionen stellen wir in Rechnung: ");
        System.out.println("Rechnungsnummer: " + this.id);
        System.out.println("_________________________________________________________________________________________");
        System.out.printf("%4s %-45s %5s %11s %18s \n", "Pos.", "Beschreibung", "Menge", "Preis", "Gesamtpreis");
        System.out.printf("%4d %-45s %5d %,10.2f€ %,16.2f€ \n", 001, this.product.getName(), this.quantity,
                this.product.getPrice(), this.totalPrice);
        String productDescription = this.product.getDescription();

        while (productDescription.length() > 0) {
            String temp = "";
            if (productDescription.length() > 83) {
                temp = productDescription.substring(0, 83);
                productDescription = productDescription.substring(83);
            } else {
                temp = productDescription;
                productDescription = "";
            }
            System.out.printf("%4s %-83s \n", "", temp);
        }

        System.out.println("_________________________________________________________________________________________");
        System.out.printf("%44s %19s %,20.2f€ \n", "", "Gesamt Netto", this.priceWithoutVat);

        if (this.isPremiumCustomer) {
            double premiumDiscount = roundDouble(this.priceWithoutVat * 0.03);
            this.priceWithoutVat -= premiumDiscount;
            this.totalPrice = roundDouble(this.priceWithoutVat * 1.19);
            System.out.printf("%44s %19s %,20.2f€ \n", "", "Premium Rabatt 3%", -premiumDiscount);
        }

        System.out.printf("%44s %19s %,20.2f€ \n", "", "MwSt. " + (int) (this.product.getVatRate() * 100) + "%",
                +(this.totalPrice - this.priceWithoutVat));
        System.out.printf("%51s%s\n", "", "___________________________________");
        System.out.printf("%44s %19s %,20.2f€ \n", "", "Gesamt Brutto", this.totalPrice);
    }

    /**
     * This method calculates the total price if a product price and a quantity is
     * given.
     *
     * @param productPrice the price of the product
     * @param quantity     the quantity the customer wants to buy
     * @return the result of the multiplication of productPrice and quantity
     */
    private double calculatePrice(double productPrice, int quantity) {
        return productPrice * quantity;
    }

    /**
     * Calculates the price without the including VAT
     *
     * @param totalPrice the total price with the including VAT
     * @param vatRate    the including VAT rate
     * @return the price without the including VAT
     */
    private double calculatePriceWithoutVAT(double totalPrice, double vatRate) {
        return totalPrice / (1 + vatRate);
    }

    /**
     * Round the double given to two decimal places
     *
     * @param doubleToRound the double to be round
     * @return the rounded double
     */
    private double roundDouble(double doubleToRound) {
        return Math.round(doubleToRound * 100.0) / 100.0;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-16s:%s\n", "Rechnungsnummer", this.id));
        sb.append(String.format("%-16s:%s\n", "Produktnummer", this.product.getId()));
        sb.append(String.format("%-16s:%s\n", "Menge", this.quantity));
        sb.append(String.format("%-16s:%s\n", "Gesamtpreis", this.totalPrice));
        sb.append(String.format("%-16s:%s\n", "Preis ohne Mwst.", this.priceWithoutVat));
        sb.append(String.format("%-16s:%s\n", "Premiumkunde", this.isPremiumCustomer));

        return sb.toString();
    }

}
