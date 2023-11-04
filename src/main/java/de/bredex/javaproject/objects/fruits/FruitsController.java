package de.bredex.javaproject.objects.fruits;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("")
public class FruitsController {

    @GetMapping("fruits")
    public List<Fruit> getAllFruits() {
        Fruit a = new Fruit(1, "Apfel");
        Fruit b = new Fruit(2, "Birne");
        return Arrays.asList(a, b);
    }
}
