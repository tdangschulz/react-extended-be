package de.bredex.javaproject.objects.user.customer;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.bredex.javaproject.objects.invoice.Invoice;
import de.bredex.javaproject.objects.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "customer")
public class Customer extends User {

    @Transient
    private static int numberOfInstances = 0;

    @Column(name = "turnover_so_far")
    private double turnoverSoFar = 0.00;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @OneToMany(mappedBy = "customer")
    private List<Invoice> invoices;

    public Customer() {

    }

    public Customer(String firstName, String lastName, String street, int houseNo, int zipCode, String residence) {
        this(-1, firstName, lastName, street, houseNo, zipCode, residence, 0);
    }

    public Customer(int id, String firstName, String lastName, String street, int houseNo, int zipCode,
            String residence, double turnoverSoFar) {
        super(id, firstName, lastName, street, houseNo, zipCode, residence);
        this.turnoverSoFar = turnoverSoFar;
        this.invoices = new ArrayList<>();
        numberOfInstances++;
    }

    public double getTurnoverSoFar() {
        return this.turnoverSoFar;
    }

    public void addPurchase(double purchaseValue) {
        this.turnoverSoFar += Math.round(purchaseValue * 100.0) / 100.0;
    }

    public List<Invoice> getInvoices() {
        return this.invoices;
    }

    public void addInvoice(Invoice invoice) {
        this.invoices.add(invoice);
    }

    public void editCustomer(String firstName, String lastName, String street, int houseNo, int zipCode,
            String residence, double turnoverSoFar) {
        super.editUser(firstName, lastName, street, houseNo, zipCode, residence);
        this.turnoverSoFar = turnoverSoFar;
    }

    public void printAllInvoices() {
        for (Invoice invoice : this.invoices) {
            invoice.print(this);
            System.out.println("\n----\n");
        }
    }

    @Override
    public int getNumberOfInstances() {
        return numberOfInstances;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(String.format("%-12s:%s\n", "Umsatz", this.turnoverSoFar));

        return sb.toString();
    }

    

}
