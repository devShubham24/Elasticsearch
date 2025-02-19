package com.sb.main.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import com.sb.main.Entity.Product;

@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, String> {
}
