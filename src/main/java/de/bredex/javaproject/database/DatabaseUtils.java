package de.bredex.javaproject.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component
public class DatabaseUtils {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    private DatabaseUtils() {

    }

    public void initDb() {
        if (this.customerRepository.count() == 0 || this.administratorRepository.count() == 0) {
            loadUserDataFromCsvIntoDb();
        }
        if (this.productRepository.count() == 0) {
            loadProductDataFromCsvIntoDb();
        }
        if (this.invoiceRepository.count() == 0) {
            loadInvoiceDataFromCsvIntoDb();
        }
    }

    private void loadUserDataFromCsvIntoDb() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(MailOrderBusinessUtils.getPathAllCustomersCsv()),
                StandardCharsets.UTF_8)) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                if (splittedLine[splittedLine.length - 1].equals("true")) {
                    Administrator readUser = new Administrator(Integer.parseInt(splittedLine[0]), splittedLine[1],
                            splittedLine[2], splittedLine[3], Integer.parseInt(splittedLine[4]),
                            Integer.parseInt(splittedLine[5]), splittedLine[6], splittedLine[7]);
                    this.administratorRepository.save(readUser);
                } else {
                    Customer readUser = new Customer(Integer.parseInt(splittedLine[0]), splittedLine[1],
                            splittedLine[2], splittedLine[3], Integer.parseInt(splittedLine[4]),
                            Integer.parseInt(splittedLine[5]), splittedLine[6], Double.parseDouble(splittedLine[7]));
                    this.customerRepository.save(readUser);
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "Die aktuelle Benutzerdatenbank konnte nicht geladen werden. Das Backup wurde verwendet.");
            List<User> allUsers = new ArrayList<>();
            allUsers.addAll(MailOrderBusinessUtils.createExampleCustomers());
            MailOrderBusinessUtils.saveUserData(allUsers);
        }
    }

    private void loadProductDataFromCsvIntoDb() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(MailOrderBusinessUtils.getPathAllProductsCsv()),
                StandardCharsets.UTF_8)) {
            String line = "";
            while ((line = br.readLine()) != null) {
                String[] splittedLine = line.split(";");
                Product readProduct = new Product(Integer.parseInt(splittedLine[0]), splittedLine[1],
                        Double.parseDouble(splittedLine[2]), splittedLine[3], Double.parseDouble(splittedLine[4]),
                        Category.getCategoryOfString(splittedLine[5]));
                this.productRepository.save(readProduct);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Der aktuelle Produktkatalog konnte nicht geladen werden. Das Backup wurde verwendet.");
            List<Product> allProducts = new ArrayList<>();
            allProducts.addAll(MailOrderBusinessUtils.createExampleProducts());
            MailOrderBusinessUtils.saveProductData(allProducts);
        }
    }

    private void loadInvoiceDataFromCsvIntoDb() {
        String folderInvoices = MailOrderBusinessUtils.getFolderInvoicesCsv();
        File folder = new File(folderInvoices);
        if (!folder.exists()) {
            return;
        }

        for (String file : folder.list()) {
            if (!file.matches("^[0-9]*_[0-9]*[.]csv$")) {
                continue;
            }

            String customerIdAsString = file.substring(0, file.indexOf('_'));

            try (BufferedReader br = Files.newBufferedReader(Paths.get(folderInvoices + file),
                    StandardCharsets.UTF_8)) {
                int customerId = Integer.parseInt(customerIdAsString);
                Optional<Customer> customer = this.customerRepository.findById(customerId);
                if (customer.isEmpty()) {
                    continue;
                }

                String line = "";
                while ((line = br.readLine()) != null) {
                    String[] splittedLine = line.split(";");
                    int productId = Integer.parseInt(splittedLine[1]);
                    Optional<Product> product = this.productRepository.findById(productId);
                    if (product.isEmpty()) {
                        break;
                    }

                    Invoice readInvoice = new Invoice(Integer.parseInt(splittedLine[0]), product.get(),
                            Integer.parseInt(splittedLine[2]), Boolean.parseBoolean(splittedLine[5]), customer.get());
                    this.invoiceRepository.save(readInvoice);
                }
            } catch (IOException | NumberFormatException e) {
                System.out.println("Die Rechnung '" + file + "' konnte nicht geladen werden.");
            }
        }
    }

}
