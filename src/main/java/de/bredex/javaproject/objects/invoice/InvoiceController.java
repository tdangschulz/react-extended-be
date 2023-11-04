package de.bredex.javaproject.objects.invoice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.bredex.javaproject.objects.user.customer.Customer;
import de.bredex.javaproject.objects.user.customer.CustomerRepository;
import de.bredex.javaproject.utils.MailOrderBusinessUtils;

@RestController
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @PostMapping("/invoices")
    public Invoice saveInvoice(@RequestBody Invoice invoice) {
        if (invoice.getId() == 0) {
            var invoceId = MailOrderBusinessUtils.generateRandomNumber(5);
            invoice.setId(invoceId);
        }

        var invoiceDb = this.invoiceRepository.save(invoice);
        Customer customer = invoiceDb.getCustomer();
        customer.addPurchase(invoiceDb.getTotalPrice());

        this.customerRepository.save(customer);
        return invoiceDb;
    }
}
