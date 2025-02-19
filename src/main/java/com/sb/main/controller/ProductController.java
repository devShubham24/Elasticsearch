package com.sb.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sb.main.Entity.Product;
import com.sb.main.dtoresponse.StudentSearchResponseDTO;
import com.sb.main.service.ProductService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public Product saveProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }

    @GetMapping("get/{id}")
    public Optional<Product> getProduct(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @GetMapping("/fetchAll")
    public Iterable<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/aggregatedPrice")
    public Map<String, Object> getAggregatedPriceData() {
        return productService.getAggregatedPriceData();
    }
    @GetMapping("/getStudent")
    public StudentSearchResponseDTO getStudentData() {
        return productService.searchStudent();  // Now returns the DTO
    }
}
