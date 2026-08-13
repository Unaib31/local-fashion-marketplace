package com.localmarket.repository;

import com.localmarket.entity.Shop;
import com.localmarket.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByOwner(User owner);

    Optional<Shop> findByOwnerId(Long ownerId);

    boolean existsByOwnerId(Long ownerId);

    List<Shop> findByApprovedTrue();

    List<Shop> findByApprovedFalse();
}
