package de.dbauction.auction.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<Product>> registerProduct(@RequestBody Product product) {
        product.setId(UUID.randomUUID().toString());
        return productRepository.save(product)
                .map(ResponseEntity::ok);
    }
}
