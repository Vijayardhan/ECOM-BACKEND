package com.example.auth.repository;

import com.example.auth.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUsernameAndOrdered(String username, boolean ordered);
}
