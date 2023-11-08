package de.bredex.javaproject.objects.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    @PostMapping("/products")
    public Product postProduct(@RequestBody Product body) {
        return this.productRepository.save(body);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable int id) {
        this.productRepository.deleteById(id);
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable int id) {
        return this.productRepository.findById(id).get();
    }
}
