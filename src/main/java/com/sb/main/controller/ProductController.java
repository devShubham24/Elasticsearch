package com.sb.main.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.sb.main.Entity.Product;
import com.sb.main.ResponseAudit.AuditLogResponseDTO;
import com.sb.main.dtoresponse.StudentSearchResponseDTO;
import com.sb.main.service.ProductService;
import com.sb.main.uniqueDTO.auditResponseDto;

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
    @GetMapping("/marks/{minMarks}")
    public StudentSearchResponseDTO getStudentsByMarks(@PathVariable int minMarks) {
        return productService.getStudentsWithMarksGreaterThan1(minMarks);
    }
    @GetMapping("/auditlogs")
    public AuditLogResponseDTO getAuditLogs() {
        return productService.getAllAuditLogs();
    }
    @GetMapping("/types")
    public auditResponseDto getUniqueAuditTypes() {
        return  productService.getUniqueAuditTypes();
    }
    @GetMapping("/filter")
    public AuditLogResponseDTO getFilteredAuditLogs(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String auditType,
            @RequestParam(required = false) String dateRange,
            @RequestParam(defaultValue = "0") int pageNumber,  // Default to first page
            @RequestParam(defaultValue = "50") int pageSize) { // Default page size to 10

        return productService.getFilteredAuditLogs(firstName, lastName, email, auditType, dateRange, pageNumber, pageSize);
    }

}
