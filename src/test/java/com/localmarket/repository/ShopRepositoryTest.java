package com.localmarket.repository;

import com.localmarket.entity.Role;
import com.localmarket.entity.Shop;
import com.localmarket.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ShopRepositoryTest {

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save shop with owner and verify default approved is false")
    void shouldSaveShopWithOwner() {
        User owner = userRepository.save(new User("Shopkeeper Dan", "dan@example.com", "pwd", Role.SHOP_OWNER));
        Shop shop = new Shop("Dan's Urban Styles", "Trendy streetwear", "123 Main Street, Bangalore", owner);

        Shop savedShop = shopRepository.save(shop);

        assertNotNull(savedShop.getId());
        assertNotNull(savedShop.getCreatedAt());
        assertFalse(savedShop.isApproved(), "Shop should default to approved = false");
        assertEquals(owner.getId(), savedShop.getOwner().getId());
    }

    @Test
    @DisplayName("Should find shop by owner and check owner existence")
    void shouldFindShopByOwner() {
        User owner = userRepository.save(new User("Shopkeeper Rita", "rita@example.com", "pwd", Role.SHOP_OWNER));
        Shop shop = shopRepository.save(new Shop("Rita Boutique", "Ethnic Wear", "456 Market Road, Mumbai", owner));

        Optional<Shop> found = shopRepository.findByOwner(owner);
        assertTrue(found.isPresent());
        assertEquals("Rita Boutique", found.get().getName());

        assertTrue(shopRepository.existsByOwnerId(owner.getId()));
        assertFalse(shopRepository.existsByOwnerId(999L));
    }

    @Test
    @DisplayName("Should filter approved and unapproved shops")
    void shouldFilterApprovedShops() {
        User owner1 = userRepository.save(new User("Owner 1", "o1@example.com", "pwd", Role.SHOP_OWNER));
        User owner2 = userRepository.save(new User("Owner 2", "o2@example.com", "pwd", Role.SHOP_OWNER));

        Shop shop1 = new Shop("Shop 1", "Desc 1", "Address 1", owner1);
        shop1.setApproved(true);
        shopRepository.save(shop1);

        Shop shop2 = new Shop("Shop 2", "Desc 2", "Address 2", owner2);
        shop2.setApproved(false);
        shopRepository.save(shop2);

        List<Shop> approvedShops = shopRepository.findByApprovedTrue();
        assertEquals(1, approvedShops.size());
        assertEquals("Shop 1", approvedShops.get(0).getName());

        List<Shop> pendingShops = shopRepository.findByApprovedFalse();
        assertEquals(1, pendingShops.size());
        assertEquals("Shop 2", pendingShops.get(0).getName());
    }

    @Test
    @DisplayName("Should enforce one shop per owner unique constraint")
    void shouldEnforceOneShopPerOwner() {
        User owner = userRepository.save(new User("Single Owner", "single@example.com", "pwd", Role.SHOP_OWNER));

        Shop shop1 = new Shop("First Shop", "Desc", "Address 1", owner);
        shopRepository.saveAndFlush(shop1);

        Shop shop2 = new Shop("Second Shop", "Desc", "Address 2", owner);
        assertThrows(DataIntegrityViolationException.class, () -> {
            shopRepository.saveAndFlush(shop2);
        });
    }
}
