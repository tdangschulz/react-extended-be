package de.bredex.javaproject.mailorderbusiness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import de.bredex.javaproject.database.DatabaseUtils;
import de.bredex.javaproject.objects.invoice.Invoice;
import de.bredex.javaproject.objects.invoice.InvoiceRepository;
import de.bredex.javaproject.objects.product.Product;
import de.bredex.javaproject.objects.product.ProductRepository;
import de.bredex.javaproject.objects.user.User;
import de.bredex.javaproject.objects.user.administrator.Administrator;
import de.bredex.javaproject.objects.user.administrator.AdministratorRepository;
import de.bredex.javaproject.objects.user.customer.Customer;
import de.bredex.javaproject.objects.user.customer.CustomerRepository;
import de.bredex.javaproject.utils.Category;
import de.bredex.javaproject.utils.MailOrderBusinessUtils;

public class MailOrderBusiness {

    private static final String SELECTION_NOT_EXISTS = "Diese Option existiert nicht. Bitte die Eingabe korrigieren!";
    private static final String ERROR_INPUT = "Falscheingabe. Bitte die Eingabe korrigieren!";
    private static final String PLEASE_CHOOSE = "Bitte waehlen Sie aus folgenden Optionen:";

    private Scanner sc = new Scanner(System.in);
    private List<User> allUsers = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();
    private User activeUser;

    private CustomerRepository customerRepository;
    private AdministratorRepository adminRepository;
    private ProductRepository productRepository;
    private InvoiceRepository invoiceRepository;

    private DatabaseUtils dbUtils;

    public MailOrderBusiness(CustomerRepository customerRepository, AdministratorRepository adminRepository,
            ProductRepository productRepository, InvoiceRepository invoiceRepository, DatabaseUtils dbUtils) {
        this.customerRepository = customerRepository;
        this.adminRepository = adminRepository;
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
        this.dbUtils = dbUtils;
    }

    /**
     * Starts the mail order business
     */
    public void start() {
        MailOrderBusinessUtils.showLoginScreen();
        this.sc.nextLine();

        this.dbUtils.initDb();
        this.allProducts.addAll(this.productRepository.findAll());
        this.allUsers.addAll(this.adminRepository.findAll());
        this.allUsers.addAll(this.customerRepository.findAll());

        loginUser();
    }

    public void addProductsToMailOrderBusiness(List<Product> products) {
        this.allProducts.addAll(products);
    }

    private void printCustomerMenu() {
        while (true) {
            try {
                System.out.println(PLEASE_CHOOSE);
                System.out.println("1. Produktkatalog anzeigen");
                System.out.println("2. Produkt kaufen");
                System.out.println("3. Daten bearbeiten");
                System.out.println("4. Ausloggen");

                int userChoice = Integer.parseInt(this.sc.nextLine());
                if (userChoice == 1) {
                    showProductCatalogue();
                } else if (userChoice == 2) {
                    buyProduct();
                } else if (userChoice == 3) {
                    editUserData();
                } else if (userChoice == 4) {
                    loginUser();
                } else {
                    System.out.println(SELECTION_NOT_EXISTS);
                    continue;
                }
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }
        }
    }

    private void buyProduct() {
        while (true) {
            try {
                System.out.println("Bitte geben Sie die Produktnummer des zu erwerbenden Produktes ein: ");
                int articleNo = Integer.parseInt(this.sc.nextLine());
                Product productToBuy = getProductBy(articleNo);

                if (productToBuy != null) {
                    System.out.println("Folgendes Produkt wurde ausgewaehlt: ");
                    System.out.println(productToBuy);
                    System.out.println("Wie viele Produkte moechten Sie kaufen? (0 eingeben zum abbrechen)");
                    int quantity = readQuantity();
                    if (quantity != 0) {
                        printInvoice(productToBuy, quantity);
                    }
                } else {
                    System.out.println("Die ausgewaehlte Produktnummer befindet sich nicht in unserem Sortiment!");
                }
                break;
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }
        }
    }

    private void showProductCatalogue() {
        System.out.println("Folgende Produkte sind im Produktkatalog enthalten:");

        for (int i = 0; i < this.allProducts.size(); i++) {
            System.out.println(this.allProducts.get(i));
        }
    }

    private void editUserData() {
        int userChoice;
        while (true) {
            try {
                System.out.println("Folgende Daten sind derzeit in Ihrem Konto gespeichert:");
                System.out.println(this.activeUser);
                System.out.println(PLEASE_CHOOSE);
                System.out.println("1. Daten bearbeiten");
                System.out.println("2. Abbrechen");

                userChoice = Integer.parseInt(this.sc.nextLine());
                if (userChoice == 1) {
                    System.out.println("Bitte geben Sie die Daten zu Ihrem Konto neu ein.");

                    String[] newValues = readNewValues();
                    this.activeUser.editUser(newValues[0], newValues[1], newValues[2], Integer.parseInt(newValues[3]),
                            Integer.parseInt(newValues[4]), newValues[5]);
                    if (this.activeUser instanceof Administrator) {
                        System.out.println("Bitte geben Sie Ihr neues Passwort ein: ");
                        ((Administrator) this.activeUser).setPassword(this.sc.nextLine());
                    }

                    if (this.activeUser instanceof Customer) {
                        this.customerRepository.save((Customer) this.activeUser);
                    } else {
                        this.adminRepository.save((Administrator) this.activeUser);
                    }
                    System.out.println("Die Daten wurden erfolgreich gespeichert!");
                } else if (userChoice == 2) {
                    break;
                } else {
                    System.out.println(SELECTION_NOT_EXISTS);
                    continue;
                }
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }
        }
    }

    /**
     * Prints the invoice for the purchased product
     *
     * @param productId          the product ID of the purchased product
     * @param productName        the product name of the purchased product
     * @param productPrice       the product price of the purchased product
     * @param productDescription the product description of the purchased product
     * @param quantity           the quantity the product was purchased
     * @param totalPrice         the total price the customer has to pay
     */
    private void printInvoice(Product product, int quantity) {
        Invoice invoice = new Invoice(product, quantity, isPremiumCustomer(), (Customer) this.activeUser);
        invoice.print(this.activeUser);
        ((Customer) this.activeUser).addInvoice(invoice);
        ((Customer) this.activeUser).addPurchase(invoice.getTotalPrice());
        this.invoiceRepository.save(invoice);
        this.customerRepository.save((Customer) this.activeUser);

        printGoodByeMessage(invoice.getTotalPrice());
    }

    private boolean isPremiumCustomer() {
        return ((Customer) this.activeUser).getTurnoverSoFar() > 10000;
    }

    /**
     * Prints the good bye message for the user
     *
     * @param totalPrice the total price for which the customer has purchased
     */
    private void printGoodByeMessage(double totalPrice) {
        System.out.println("Vielen Dank " + this.activeUser.getFirstName() + " fuer deinen Einkauf im Wert von "
                + String.format("%,.2f", totalPrice) + "€.");
        System.out.println("Bis zum naechsten Mal!\n");
    }

    /**
     * Gets the user input for the quantity that he wants to buy
     *
     * @param sc the scanner to read the user input
     * @return the quantity the user entered
     */
    private int readQuantity() {
        int quantity = 0;
        while (true) {
            try {
                System.out.println("Bitte Menge eingeben: ");
                quantity = Integer.parseInt(this.sc.nextLine());
                break;
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }
        }

        return quantity;
    }

    /**
     * Shows the login screen. Here its possible to login or register in the mail
     * order business
     *
     * @param sc the scanner to read the user input
     * @return the ID the user logged in with
     */
    private void loginUser() {
        int loginOrRegister;
        while (true) {
            try {
                System.out.println(PLEASE_CHOOSE);
                System.out.println("1. Login mit Kundennummer");
                System.out.println("2. Registrieren");
                System.out.println("3. Beenden");
                loginOrRegister = Integer.parseInt(this.sc.nextLine());

                if (loginOrRegister == 1) {
                    int userId = readUserId();
                    if (!checkIfUserExists(userId)) {
                        System.out.println("Diese Kundennummer ist nicht vorhanden. Bitte versuchen Sie es erneut "
                                + "oder registrieren Sie sich!");
                        continue;
                    } else {
                        for (User user : this.allUsers) {
                            if (user.getId() == userId) {
                                this.activeUser = user;
                            }
                        }
                    }
                    break;
                } else if (loginOrRegister == 2) {
                    registerCustomer();
                    continue;
                } else if (loginOrRegister == 3) {
                    System.exit(0);
                    continue;
                } else {
                    System.out.println(SELECTION_NOT_EXISTS);
                    continue;
                }
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }
        }

        System.out.println("\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"");
        System.out.println("|Herzlich Willkommen " + this.activeUser.getFirstName() + "!");
        System.out.println("\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"");

        if (this.activeUser instanceof Administrator) {
            System.out.println("Bitte Passwort eingeben: ");
            String password = this.sc.nextLine();
            if (((Administrator) this.activeUser).getPassword().equals(password)) {
                printAdminMenu();
            } else {
                System.out.println("Passwort stimmt nicht mit Konto ueberein!");
                loginUser();
            }
        } else {
            printCustomerMenu();
        }
    }

    private void printAdminMenu() {
        while (true) {
            try {
                System.out.println(PLEASE_CHOOSE);
                System.out.println("1. Benutzer loeschen");
                System.out.println("2. Produktdaten bearbeiten");
                System.out.println("3. Daten bearbeiten");
                System.out.println("4. Rechnungen anzeigen/speichern");
                System.out.println("5. Ausloggen");

                int userChoice = Integer.parseInt(this.sc.nextLine());
                if (userChoice == 1) {
                    deleteUser();
                } else if (userChoice == 2) {
                    editProduct();
                } else if (userChoice == 3) {
                    editUserData();
                } else if (userChoice == 4) {
                    showAndSaveInvoices();
                } else if (userChoice == 5) {
                    loginUser();
                } else {
                    System.out.println(SELECTION_NOT_EXISTS);
                    continue;
                }
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }
        }
    }

    private void editProduct() {
        while (true) {
            try {
                System.out.println("Bitte geben Sie die Produktnummer des zu editierenden Produktes ein:");

                int articleNo = Integer.parseInt(this.sc.nextLine());
                Product productToEdit = getProductBy(articleNo);
                if (productToEdit != null) {
                    System.out.println("Folgendes Produkt wurde ausgewaehlt: ");
                    System.out.println(productToEdit);
                    productToEdit.editProduct(createProduct());
                    this.productRepository.save(productToEdit);
                    System.out.println("Die Daten wurden erfolgreich gespeichert!");
                    return;
                }

                System.out.println("Die ausgewaehlte Produktnummer befindet sich nicht in unserem Sortiment!");
                break;
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }
        }
    }

    /**
     * Method to show all invoices of a customer
     */
    private void showAndSaveInvoices() {
        int userId = -1;
        while (true) {
            try {
                System.out.println("Bitte geben Sie eine Kundennummer ein: ");
                userId = Integer.parseInt(this.sc.nextLine());
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }

            if (!checkIfUserExists(userId)) {
                System.out.println(ERROR_INPUT);
            } else {
                break;
            }
        }

        Customer customerOfInvoices = null;
        for (User user : this.allUsers) {
            if (user.getId() == userId) {
                customerOfInvoices = (Customer) user;
                break;
            }
        }
        if (customerOfInvoices.getInvoices().isEmpty()) {
            System.out.println("Keine Rechnungen für den Kunden vorhanden!");
            return;
        }
        customerOfInvoices.printAllInvoices();

        System.out.println();
        System.out.println("Möchten Sie die Rechnungen als CSV-Datei exportieren?");
        System.out.println("1. Ja");
        System.out.println("2. Nein");

        try {
            int doSave = Integer.parseInt(this.sc.nextLine());
            if (doSave == 1) {
                MailOrderBusinessUtils.saveInvoices(customerOfInvoices);
                System.out.println("Export abgeschlossen\n");
            }
        } catch (NumberFormatException nfe) {
            System.out.println(ERROR_INPUT);
        }

    }

    /**
     * Method to register a new customer at the mail order business
     */
    private void registerCustomer() {
        System.out.println("Herzlich Willkommen in der Kundenregistrierung");
        Customer newUser = createCustomer();
        addUserToMailOrderBusiness(Arrays.asList(newUser));
        this.customerRepository.save(newUser);
    }

    private Customer createCustomer() {
        String[] newValues = readNewValues();
        Customer newCustomer = new Customer(newValues[0], newValues[1], newValues[2], Integer.parseInt(newValues[3]),
                Integer.parseInt(newValues[4]), newValues[5]);
        return newCustomer;
    }

    private String[] readNewValues() {
        String[] newValues = new String[6];
        System.out.println("Bitte geben Sie Ihren Vorname ein: ");
        newValues[0] = this.sc.nextLine();
        System.out.println("Bitte geben Sie Ihren Nachnamen ein: ");
        newValues[1] = this.sc.nextLine();
        System.out.println("Bitte geben Sie Ihre Strasse ein: ");
        newValues[2] = this.sc.nextLine();
        System.out.println("Bitte geben Sie Ihre Hausnummer ein: ");
        newValues[3] = this.sc.nextLine();
        System.out.println("Bitte geben Sie Ihre Postleitzahl ein: ");
        newValues[4] = this.sc.nextLine();
        System.out.println("Bitte geben Sie Ihren Wohnort ein: ");
        newValues[5] = this.sc.nextLine();

        return newValues;
    }

    private Product createProduct() {
        System.out.println("Bitte geben Sie die Produktnummer ein: ");
        int id = Integer.parseInt(this.sc.nextLine());
        System.out.println("Bitte geben Sie den Produktnamen ein: ");
        String name = this.sc.nextLine();
        System.out.println("Bitte geben Sie den Produktpreis ein: ");
        double price = Double.parseDouble(this.sc.nextLine());
        System.out.println("Bitte geben Sie die Produktbeschreibung ein: ");
        String description = this.sc.nextLine();
        System.out.println("Bitte geben Sie den Mehrwertsteuersatz ein: ");
        double vatRate = Double.parseDouble(this.sc.nextLine());
        System.out.println("Bitte geben Sie die Produktkategorie ein: ");
        Category category = Category.getCategoryOfString(this.sc.nextLine());

        return new Product(id, name, price, description, vatRate, category);
    }

    /**
     * Checks if the user ID exists in the mail order business
     *
     * @param allUsers all users of the mail order business
     * @param userId   the ID that needs to be checked
     * @return if the ID exists returns true, else false
     */
    private boolean checkIfUserExists(int userId) {
        boolean idExists = false;
        for (User user : this.allUsers) {
            if (user.getId() == userId) {
                idExists = true;
            }
        }

        return idExists;
    }

    /**
     * This method asks the user for the ID.
     *
     * @param sc the scanner to read the user input
     * @return the entered user ID
     */
    private int readUserId() {
        int userId;
        while (true) {
            try {
                System.out.println("Bitte Kundennummer für den Login eingeben: ");
                userId = Integer.parseInt(this.sc.nextLine());
                break;
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }
        }

        return userId;
    }

    /**
     * Adds a user to the mail order business
     *
     * @param The user to be added
     */
    public void addUserToMailOrderBusiness(List<User> users) {
        for (User user : users) {
            if (user.getId() == -1) {
                user.setId(createNewUserId());
                printNewUserId(user);
            }
            this.allUsers.add(user);
        }
    }

    /**
     * Creates a random 5-Digit-Number and checks if number already exists within
     * the MailOrderBusiness and retries if so
     *
     * @return a 5-Digit-Number that can be used within the MailOrderBusiness as an
     *         User Identifier
     */
    private int createNewUserId() {
        int newUserId = MailOrderBusinessUtils.generateRandomNumber(5);

        for (User user : this.allUsers) {
            if (user.getId() == newUserId) {
                return createNewUserId();
            }
        }

        return newUserId;
    }

    /**
     * Shows the user the new ID.
     *
     * @param newUser the new ID of the user
     */
    private void printNewUserId(User newUser) {
        System.out.println("|Hallo " + newUser.getFirstName() + " deine neue Kundennummer lautet: " + newUser.getId()
                + "|");
        System.out.println();
    }

    public void removeUserFromMailOrderBusiness(User... users) {
        for (User user : users) {
            if (this.allUsers.contains(user)) {
                this.allUsers.remove(user);
            }
        }
    }

    private void deleteUser() {
        while (true) {
            try {
                System.out.println("Bitte geben Sie die Kundennummer des zu loeschenden Kontos an:");
                int userId = Integer.parseInt(this.sc.nextLine());
                System.out.println("Bitte geben Sie den Vornamen des zu loeschenden Kontos an: ");
                String userFirstName = this.sc.nextLine();
                System.out.println("Bitte geben Sie den Nachnamen des zu loeschenden Kontos an: ");
                String userLastName = this.sc.nextLine();

                for (User user : this.allUsers) {
                    if (user.getId() == userId && user.getFirstName().equals(userFirstName)
                            && user.getLastName().equals(userLastName)) {
                        this.allUsers.remove(user);
                        if (user instanceof Customer) {
                            this.customerRepository.delete((Customer) user);
                        } else {
                            this.adminRepository.delete((Administrator) user);
                        }

                        System.out.println("Das Konto mit der Kundennummer: " + userId
                                + " wurde erfolgreich geloescht!");
                        return;
                    }
                }

                System.out.println("Das Konto mit der Kundennummer: " + userId + " konnte nicht geloescht werden.");
                break;
            } catch (NumberFormatException nfe) {
                System.out.println(ERROR_INPUT);
            }
        }
    }

    public List<User> getAllUsers() {
        return this.allUsers;
    }

    public List<Product> getAllProducts() {
        return this.allProducts;
    }

    /**
     * Checks if a product exists or not
     *
     * @param articleNo the article number to check
     * @return the existing product or null if the products does not exist
     */
    public Product getProductBy(int articleNo) {
        for (int i = 0; i < this.allProducts.size(); i++) {
            if (this.allProducts.get(i).getId() == articleNo) {
                return this.allProducts.get(i);
            }
        }

        return null;
    }

}
