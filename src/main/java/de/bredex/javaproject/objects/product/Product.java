package de.bredex.javaproject.objects.product;

import java.util.List;

import de.bredex.javaproject.objects.invoice.Invoice;
import de.bredex.javaproject.utils.Category;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name", length = 80)
    private String name;
    @Column(name = "price")
    private double price;
    @Column(name = "description", length = 250)
    private String description;
    @Column(name = "vat_rate")
    private double vatRate;
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Invoice> invoices;

    public Product() {

    }

    public Product(int id, String name, double price, String description, double vatRate, Category category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.vatRate = vatRate;
        this.category = category;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public double getPrice() {
        return this.price;
    }

    public String getDescription() {
        return this.description;
    }

    public double getVatRate() {
        return this.vatRate;
    }

    public Category getCategory() {
        return this.category;
    }

    public void editProduct(Product productToCopyFrom) {
        this.name = productToCopyFrom.getName();
        this.price = productToCopyFrom.getPrice();
        this.description = productToCopyFrom.getDescription();
        this.vatRate = productToCopyFrom.getVatRate();
        this.category = productToCopyFrom.getCategory();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-18s:%s\n", "Artikelnummer", this.id));
        sb.append(String.format("%-18s:%s\n", "Artikelname", this.name));
        sb.append(String.format("%-18s:%s\n", "Preis", this.price));
        sb.append(String.format("%-18s:%s\n", "Beschreibung", this.description));
        sb.append(String.format("%-18s:%s\n", "Mehrwertsteuersatz", this.vatRate));
        sb.append(String.format("%-18s:%s\n", "Kategorie", this.category));

        return sb.toString();
    }

}
