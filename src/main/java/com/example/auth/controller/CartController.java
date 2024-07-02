package com.example.auth.controller;

import com.example.auth.model.Cart;
import com.example.auth.model.Product;
import com.example.auth.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/add")
    public Cart addToCart(@RequestBody Cart cart) {
        Optional<Cart> existingCart = cartRepository.findByUsernameAndOrdered(cart.getUsername(), false);

        if (existingCart.isPresent()) {
            Cart updatedCart = existingCart.get();
            updatedCart.setProducts(cart.getProducts());
            return cartRepository.save(updatedCart);
        } else {
            return cartRepository.save(cart);
        }
    }

    @GetMapping("/{username}")
    public Cart getCart(@PathVariable String username) {
        return cartRepository.findByUsernameAndOrdered(username, false)
                .orElse(new Cart());
    }

    @PutMapping("/update")
    public Cart updateCart(@RequestBody Cart cart) {
        return cartRepository.save(cart);
    }

    @PostMapping("/order")
    public void orderCart(@RequestBody String username) {
        Optional<Cart> cartOptional = cartRepository.findByUsernameAndOrdered(username, false);

        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            cart.setOrdered(true);
            cartRepository.save(cart);
        } else {
            throw new RuntimeException("Cart not found for username: " + username);
        }
    }
}
