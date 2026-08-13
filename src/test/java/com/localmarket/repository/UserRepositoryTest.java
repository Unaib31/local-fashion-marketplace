package com.localmarket.repository;

import com.localmarket.entity.Role;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save user and find by email")
    void shouldSaveAndFindByEmail() {
        User user = new User("Alice Sharma", "alice@example.com", "hashed_pwd_123", Role.CUSTOMER);
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertNotNull(savedUser.getCreatedAt());

        Optional<User> found = userRepository.findByEmail("alice@example.com");
        assertTrue(found.isPresent());
        assertEquals("Alice Sharma", found.get().getName());
        assertEquals(Role.CUSTOMER, found.get().getRole());
    }

    @Test
    @DisplayName("Should check existence by email")
    void shouldCheckExistenceByEmail() {
        User user = new User("Bob Verma", "bob@example.com", "hashed_pwd_123", Role.SHOP_OWNER);
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("bob@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should find users by Role")
    void shouldFindByRole() {
        userRepository.save(new User("Owner 1", "owner1@example.com", "pwd", Role.SHOP_OWNER));
        userRepository.save(new User("Owner 2", "owner2@example.com", "pwd", Role.SHOP_OWNER));
        userRepository.save(new User("Customer 1", "customer1@example.com", "pwd", Role.CUSTOMER));

        List<User> owners = userRepository.findByRole(Role.SHOP_OWNER);
        assertEquals(2, owners.size());

        List<User> customers = userRepository.findByRole(Role.CUSTOMER);
        assertEquals(1, customers.size());
    }

    @Test
    @DisplayName("Should enforce unique constraint on email")
    void shouldEnforceUniqueEmail() {
        User user1 = new User("User One", "unique@example.com", "pwd1", Role.CUSTOMER);
        userRepository.saveAndFlush(user1);

        User user2 = new User("User Two", "unique@example.com", "pwd2", Role.CUSTOMER);
        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(user2);
        });
    }
}
