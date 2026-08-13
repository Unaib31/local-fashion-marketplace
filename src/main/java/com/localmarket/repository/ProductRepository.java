package com.localmarket.repository;

import com.localmarket.entity.Product;
import com.localmarket.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShop(Shop shop);

    List<Product> findByShopId(Long shopId);

    List<Product> findByCategory(String category);

    List<Product> findByShopIdAndCategory(Long shopId, String category);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByShopApprovedTrue();
}
