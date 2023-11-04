package de.bredex.javaproject.objects.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "first_name", length = 80)
    private String firstName;
    @Column(name = "last_name", length = 80)
    private String lastName;
    @Column(name = "street", length = 80)
    private String street;
    @Column(name = "house_no")
    private int houseNo;
    @Column(name = "zip_code")
    private int zipCode;
    @Column(name = "residence", length = 80)
    private String residence;

    public User() {

    }

    public User(String firstName, String lastName, String street, int houseNo, int zipCode, String residence) {
        this(-1, firstName, lastName, street, houseNo, zipCode, residence);
    }

    public User(int id, String firstName, String lastName, String street, int houseNo, int zipCode, String residence) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.street = street;
        this.houseNo = houseNo;
        this.zipCode = zipCode;
        this.residence = residence;
    }

    public int getId() {
        return this.id;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getStreet() {
        return this.street;
    }

    public int getHouseNo() {
        return this.houseNo;
    }

    public int getZipCode() {
        return this.zipCode;
    }

    public String getResidence() {
        return this.residence;
    }

    public void setId(int newUserId) {
        this.id = newUserId;
    }

    public void editUser(String firstName, String lastName, String street, int houseNo, int zipCode,
            String residence) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.houseNo = houseNo;
        this.zipCode = zipCode;
        this.residence = residence;
    }

    public abstract int getNumberOfInstances();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s:%s\n", "Kundenummer", this.getId()));
        sb.append(String.format("%-12s:%s\n", "Vorname", this.getFirstName()));
        sb.append(String.format("%-12s:%s\n", "Nachname", this.getLastName()));
        sb.append(String.format("%-12s:%s\n", "Strasse", this.getStreet()));
        sb.append(String.format("%-12s:%s\n", "Hausnummer", this.getHouseNo()));
        sb.append(String.format("%-12s:%s\n", "Postleitzahl", this.getZipCode()));
        sb.append(String.format("%-12s:%s\n", "Wohnort", this.getResidence()));

        return sb.toString();
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setHouseNo(int houseNo) {
        this.houseNo = houseNo;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

}
