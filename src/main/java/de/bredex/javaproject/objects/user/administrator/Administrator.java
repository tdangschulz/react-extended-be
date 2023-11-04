package de.bredex.javaproject.objects.user.administrator;

import de.bredex.javaproject.objects.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "administrator")
public class Administrator extends User {

    @Transient
    private static int numberOfInstances = 0;

    @Column(name = "password", length = 30)
    private String password;

    public Administrator() {

    }

    public Administrator(int id, String firstName, String lastName, String street, int houseNo, int zipCode,
            String residence, String password) {
        super(id, firstName, lastName, street, houseNo, zipCode, residence);
        this.password = password;
        numberOfInstances++;
    }

    public boolean isPasswordCorrect(String password) {
        return this.password.equals(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public int getNumberOfInstances() {
        return numberOfInstances;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(String.format("%-12s:%s\n", "Passwort", this.password));

        return sb.toString();
    }

}
