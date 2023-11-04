package de.bredex.javaproject.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.bredex.javaproject.objects.invoice.Invoice;
import de.bredex.javaproject.objects.product.Product;
import de.bredex.javaproject.objects.user.User;
import de.bredex.javaproject.objects.user.administrator.Administrator;
import de.bredex.javaproject.objects.user.customer.Customer;

public class MailOrderBusinessUtils {

    private static final String PATH_ALL_CUSTOMERS_CSV = "src/main/resources/static/AllCustomers.csv";
    private static final String PATH_ALL_PRODUCTS_CSV = "src/main/resources/static/AllProducts.csv";
    private static final String FOLDER_INVOICES_CSV = "src/main/resources/static/";
    private static final String FILENAME_INVOICES_CSV = "%d_%d.csv";

    private MailOrderBusinessUtils() {

    }

    public static int generateRandomNumber(int numberOfDigits) {
        Random r = new Random();
        int low = 10000;
        int high = (int) Math.pow(10, numberOfDigits);
        int result = r.nextInt(high - low) + low;
        return result;
    }

    /**
     * Method to show the login screen for the customer in the console.
     */
    public static void showLoginScreen() {
        System.out.println("    ==========                                                       ");
        System.out.println("    ==========                                                       ");
        System.out.println("       =========       _______________                               ");
        System.out.println("   =============      |  ___________  |                             ");
        System.out.println("    ==========        | | === == == | |                                 ");
        System.out.println("    ==========        | |  Versand  | |                               ");
        System.out.println("       ==========     | |    73!    | |                             ");
        System.out.println("   ===========        | |___________| |      ((;)                     ");
        System.out.println("|\"\"\"\"============\"\"\"\"\"|____________oo_|\"\")\"\"\"\";"
                + "(\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"|");
        System.out.println("|  ===========            ___)___(___,o  (   .---._                    |");
        System.out.println("|     ===========        |___________| 8  \\  |TEA|_)    .+-------+.    |");
        System.out.println("|  ===========                     o8o8    ) |___|    .' |_______| `.  |");
        System.out.println("|    =============      __________8___    (          /  /         \\  \\ |");
        System.out.println("| |\\`==========='/|   .'= --------- --`.   `.       |\\ /           \\ /||");
        System.out.println("| |\\`==========='/|   .'= --------- --`.   `.       |\\ /           \\ /||");
        System.out.println("| | \"-----------\" |  / ooooooooooooo  oo\\   _\\_     | \"-------------\" ||");
        System.out.println("| |______I_N______| /  oooooooooooo[] ooo\\  |=|     |_______OUT_______||");
        System.out.println("|                  / O O =========  O OO  \\ \\\"-\"  .-------,           |");
        System.out.println(
                "|                  `\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"\"'      /~~~~~~~/             |");
        System.out.println("|_______________________________________________/_   ~~~/______________|");
        System.out.println("..............................................\\/_______/..desk at 17:30..");
    }

    /**
     * Creates example Customers and adds them to the mail order business.
     */
    @NotForProduction(reason = "Only a backup and should never be used in a productive enviorment!")
    public static List<User> createExampleCustomers() {
        List<User> allUsers = new ArrayList<>();

        Customer max = new Customer(11111, "Max", "Mustermann", "Musterstrasse", 123, 12345, "Musterstadt", 0);
        Customer erika = new Customer("Erika", "Musterfrau", "Musterstrasse", 123, 12345, "Musterstadt");
        Customer reiner = new Customer("Reiner", "Zufall", "Robert-Bosch-Breite", 11, 37079, "Goettingen");
        Administrator alfred = new Administrator(22222, "Alfred", "Admin", "Antwort auf alles Strasse", 42, 1337,
                "Root", "abc123");

        allUsers.add(max);
        allUsers.add(erika);
        allUsers.add(reiner);
        allUsers.add(alfred);

        return allUsers;
    }

    @NotForProduction(reason = "Only a backup and should never be used in a productive enviorment!")
    public static List<Product> createExampleProducts() {
        List<Product> allProducts = new ArrayList<>();

        Product car = new Product(12345, "ID. 4", 46335.00,
                "Der ID.4 ist der Erste seiner Art – und das Beste aus zwei Welten - Als erster Volkswagen verbindet "
                        + "er die Stärke eines SUV mit dem nachhaltigen Fahrerlebnis eines ID. Erleben Sie ein völlig neues "
                        + "Raumkonzept, das Ihnen Freiheit schenkt.",
                0.19, Category.CAR);
        Product iceScraper = new Product(54321, "Eiskratzer", 9.99,
                "Eiskratzer mit Messingklinge - Made in Germany - langlebiger und stabiler Eiskratzer für das Auto "
                        + "aus Messing. Sehr stabil!",
                0.19, Category.ACCESSORIES);
        Product washerFluid = new Product(11111, "Scheibenwischwasser", 39.99,
                "20L (4 x 5 Liter) Premium Scheibenfrostschutz -30°C Wischwasser. Fertiggemixt für die Auto "
                        + "Scheibenwaschanlage. Mit Citrus Duft!",
                0.19, Category.ACCESSORIES);

        allProducts.add(car);
        allProducts.add(iceScraper);
        allProducts.add(washerFluid);

        return allProducts;
    }

    public static void saveUserData(List<User> users) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(PATH_ALL_CUSTOMERS_CSV), StandardCharsets.UTF_8)) {
            // Need to create the parent directory here, as I have broken it down into
            // weekly packages
            Files.createDirectories(Paths.get(PATH_ALL_CUSTOMERS_CSV).getParent());
            for (User user : users) {
                StringBuilder customerAsCSV = new StringBuilder();
                String[] splittedCustomer = user.toString().replace(":", "\n").split("\n");
                for (int i = 1; i < splittedCustomer.length; i += 2) {
                    customerAsCSV.append(splittedCustomer[i] + ";");
                }
                if (user instanceof Administrator) {
                    customerAsCSV.append(true);
                } else {
                    customerAsCSV.append(false);
                }
                bw.write(customerAsCSV.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveProductData(List<Product> products) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(PATH_ALL_PRODUCTS_CSV), StandardCharsets.UTF_8)) {
            for (Product product : products) {
                StringBuilder productAsCSV = new StringBuilder();
                String[] splittedProduct = product.toString().replace(":", "\n").split("\n");
                for (int i = 1; i < splittedProduct.length; i += 2) {
                    productAsCSV.append(splittedProduct[i] + ";");
                }
                bw.write(productAsCSV.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveInvoices(Customer customer) {
        for (Invoice invoice : customer.getInvoices()) {
            String pathInvoices = FOLDER_INVOICES_CSV
                    + String.format(FILENAME_INVOICES_CSV, customer.getId(), invoice.getId());
            Path path = Paths.get(pathInvoices);
            if (!Files.exists(path)) {
                try {
                    Files.createDirectories(path.getParent());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                StringBuilder invoiceAsCSV = new StringBuilder();
                String[] splittedInvoice = invoice.toString().replace(":", "\n").split("\n");
                for (int i = 1; i < splittedInvoice.length; i += 2) {
                    invoiceAsCSV.append(splittedInvoice[i] + ";");
                }
                bw.write(invoiceAsCSV.toString() + "\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getPathAllCustomersCsv() {
        return PATH_ALL_CUSTOMERS_CSV;
    }

    public static String getPathAllProductsCsv() {
        return PATH_ALL_PRODUCTS_CSV;
    }

    public static String getFolderInvoicesCsv() {
        return FOLDER_INVOICES_CSV;
    }

    public static String getFilenameInvoicesCsv() {
        return FILENAME_INVOICES_CSV;
    }

}
