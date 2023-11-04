package de.bredex.javaproject.objects.user.customer;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import de.bredex.javaproject.config.AccessConfiguration;

@RestController
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return this.customerRepository.findAll();
    }

    @PostMapping("/customers")
    public Customer register(@RequestBody Customer customer) {
        var customerDb = this.customerRepository.save(customer);

        UserDetails user = User.withUsername(String.valueOf(customer.getId()))
                .password(passwordEncoder.encode(""))
                .roles(AccessConfiguration.CUSTOMER_ROLE).build();

        this.inMemoryUserDetailsManager.createUser(user);
        return customerDb;
    }
}
