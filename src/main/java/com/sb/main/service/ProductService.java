package com.sb.main.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sb.main.Entity.Product;
import com.sb.main.ResponseAudit.AuditLogResponseDTO;
import com.sb.main.dtoresponse.StudentSearchResponseDTO;
import com.sb.main.Dao.ProductDAO;
import com.sb.main.repository.ProductRepository;
import com.sb.main.uniqueDTO.auditResponseDto;


import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDAO productDAO;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public Map<String, Object> getAggregatedPriceData() {
        return productDAO.getAggregatedPriceData();
    }


    public StudentSearchResponseDTO searchStudent() {
        return productDAO.searchStudents();  // Calling DAO method to fetch students
    }
    

    public StudentSearchResponseDTO getStudentsWithMarksGreaterThan1(int marks) {
        return productDAO.getStudentsWithMarksGreaterThan(marks);
    }
    public AuditLogResponseDTO getAllAuditLogs() {
        return productDAO.fetchAllAuditLogs();
    }

    public auditResponseDto getUniqueAuditTypes() {
        return productDAO.getUniqueAuditTypes();
    }
    public AuditLogResponseDTO getFilteredAuditLogs(String firstName, String lastName, String email, String auditType, String dateRange) {
        return productDAO.fetchFilteredAuditLogs(firstName, lastName, email, auditType, dateRange);
    }
}
