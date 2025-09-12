package com.erp_mes.mes.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.erp_mes.mes.stock.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
}
