package de.bredex.javaproject.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import de.bredex.javaproject.objects.user.administrator.Administrator;
import de.bredex.javaproject.objects.user.administrator.AdministratorRepository;
import de.bredex.javaproject.objects.user.customer.Customer;
import de.bredex.javaproject.objects.user.customer.CustomerRepository;

@Configuration
@EnableWebSecurity
public class AccessConfiguration {

    public static final String CUSTOMER_ROLE = "CUSTOMER";
    private static final String ADMIN_ROLE = "ADMIN";

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdministratorRepository adminRepository;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] adminUrls = new String[] { "profile/", "/profile/**", "/v3/**",
                "/swagger-ui/**", "/h2-console/**" };
        String[] adminAndCustomerUrls = new String[] { "/customers/", "/products/**",
                "/invoices/**" };
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(adminUrls).hasRole(ADMIN_ROLE)
                .requestMatchers(adminAndCustomerUrls).hasAnyRole(CUSTOMER_ROLE,
                        ADMIN_ROLE)
                .anyRequest().permitAll()
                .and().headers().frameOptions().disable()
                .and().cors().and()
                .httpBasic();
        return http.build();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService(PasswordEncoder passwordEncoder) {
        List<Customer> customers = this.customerRepository.findAll();
        List<Administrator> admins = this.adminRepository.findAll();

        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();

        for (Customer customer : customers) {
            System.out.println("user : " + customer.getId());
            UserDetails user = User.withUsername(String.valueOf(customer.getId()))
                    .password(passwordEncoder.encode(""))
                    .roles(CUSTOMER_ROLE)
                    .build();
            userDetailsManager.createUser(user);
        }

        for (Administrator admin : admins) {
            System.out.println("Admin: " + admin.getId() + ":" + admin.getPassword());

            UserDetails user = User.withUsername(String.valueOf(admin.getId()))
                    .password(passwordEncoder.encode(admin.getPassword()))
                    .roles(ADMIN_ROLE)
                    .build();
            userDetailsManager.createUser(user);
        }

        return userDetailsManager;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        var config = new CorsConfiguration();
        config.addAllowedMethod(HttpMethod.DELETE);
        config.addAllowedMethod(HttpMethod.POST);
        config.addAllowedMethod(HttpMethod.GET);
        config.addAllowedMethod(HttpMethod.OPTIONS);

        source.registerCorsConfiguration("/**", config.applyPermitDefaultValues());
        return source;
    }

}
