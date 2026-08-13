package com.localmarket.repository;

import com.localmarket.entity.Product;
import com.localmarket.entity.Role;
import com.localmarket.entity.Shop;
import com.localmarket.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    private Shop approvedShop;
    private Shop unapprovedShop;

    @BeforeEach
    void setUp() {
        User owner1 = userRepository.save(new User("Owner 1", "owner1@shop.com", "pwd", Role.SHOP_OWNER));
        approvedShop = new Shop("Approved Fashion Hub", "Great clothes", "789 High Street", owner1);
        approvedShop.setApproved(true);
        approvedShop = shopRepository.save(approvedShop);

        User owner2 = userRepository.save(new User("Owner 2", "owner2@shop.com", "pwd", Role.SHOP_OWNER));
        unapprovedShop = new Shop("Pending Fashion Hub", "Pending clothes", "101 Low Street", owner2);
        unapprovedShop.setApproved(false);
        unapprovedShop = shopRepository.save(unapprovedShop);
    }

    @Test
    @DisplayName("Should save product with shop and verify automatic createdAt")
    void shouldSaveProductWithShop() {
        Product product = new Product(
                "Embroidered Silk Kurta",
                "Pure silk festive wear",
                new BigDecimal("2499.00"),
                "Ethnic Wear",
                "L",
                25,
                "https://images.example.com/kurta.jpg",
                approvedShop
        );

        Product saved = productRepository.save(product);

        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals("Embroidered Silk Kurta", saved.getName());
        assertEquals(new BigDecimal("2499.00"), saved.getPrice());
        assertEquals(approvedShop.getId(), saved.getShop().getId());
    }

    @Test
    @DisplayName("Should query products by shop and category")
    void shouldQueryByShopAndCategory() {
        productRepository.save(new Product("Denim Jacket", "Winter jacket", new BigDecimal("1999.00"), "Men", "M", 10, null, approvedShop));
        productRepository.save(new Product("Chinos", "Casual trousers", new BigDecimal("1299.00"), "Men", "32", 15, null, approvedShop));
        productRepository.save(new Product("Floral Dress", "Summer dress", new BigDecimal("1499.00"), "Women", "S", 8, null, approvedShop));

        List<Product> shopProducts = productRepository.findByShopId(approvedShop.getId());
        assertEquals(3, shopProducts.size());

        List<Product> menProducts = productRepository.findByCategory("Men");
        assertEquals(2, menProducts.size());

        List<Product> shopWomenProducts = productRepository.findByShopIdAndCategory(approvedShop.getId(), "Women");
        assertEquals(1, shopWomenProducts.size());
        assertEquals("Floral Dress", shopWomenProducts.get(0).getName());
    }

    @Test
    @DisplayName("Should search products by name keyword case-insensitively")
    void shouldSearchByNameContainingIgnoreCase() {
        productRepository.save(new Product("Classic Leather Boots", "Boots", new BigDecimal("3499.00"), "Footwear", "42", 5, null, approvedShop));
        productRepository.save(new Product("Running Shoes", "Sneakers", new BigDecimal("2199.00"), "Footwear", "41", 12, null, approvedShop));

        List<Product> results = productRepository.findByNameContainingIgnoreCase("leather");
        assertEquals(1, results.size());
        assertEquals("Classic Leather Boots", results.get(0).getName());

        List<Product> shoes = productRepository.findByNameContainingIgnoreCase("SHOES");
        assertEquals(1, shoes.size());
        assertEquals("Running Shoes", shoes.get(0).getName());
    }

    @Test
    @DisplayName("Should find products only from approved shops")
    void shouldFindProductsFromApprovedShops() {
        productRepository.save(new Product("Approved Shirt", "Shirt", new BigDecimal("799.00"), "Men", "L", 20, null, approvedShop));
        productRepository.save(new Product("Pending Shirt", "Shirt", new BigDecimal("699.00"), "Men", "L", 20, null, unapprovedShop));

        List<Product> approvedProducts = productRepository.findByShopApprovedTrue();
        assertEquals(1, approvedProducts.size());
        assertEquals("Approved Shirt", approvedProducts.get(0).getName());
    }
}
