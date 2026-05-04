package com.renewable.ai.controller;

import com.renewable.ai.entity.Product;
import com.renewable.ai.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mall")
@CrossOrigin(origins = "*")
public class MallController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productRepository.findByStatus(1));
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }

    @GetMapping("/station/{stationId}/products")
    public ResponseEntity<List<Product>> getStationProducts(@PathVariable Long stationId) {
        return ResponseEntity.ok(productRepository.findByStationId(stationId));
    }
}
