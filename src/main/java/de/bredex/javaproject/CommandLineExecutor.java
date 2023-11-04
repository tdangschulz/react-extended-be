package de.bredex.javaproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import de.bredex.javaproject.database.DatabaseUtils;
import de.bredex.javaproject.mailorderbusiness.MailOrderBusiness;
import de.bredex.javaproject.objects.invoice.InvoiceRepository;
import de.bredex.javaproject.objects.product.ProductRepository;
import de.bredex.javaproject.objects.user.administrator.AdministratorRepository;
import de.bredex.javaproject.objects.user.customer.CustomerRepository;

@Profile("!test")
@Component
public class CommandLineExecutor implements CommandLineRunner {

    @Autowired
    private DatabaseUtils dbUtils;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdministratorRepository administratorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public void run(String... args) throws Exception {
        MailOrderBusiness mOB = new MailOrderBusiness(this.customerRepository, this.administratorRepository,
                this.productRepository, this.invoiceRepository, this.dbUtils);
        mOB.start();
        dbUtils.initDb();
    }

}
