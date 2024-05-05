package de.dbauction.auction.product;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<Product>> registerProduct(@RequestBody Product product, Authentication authentication) {
        return productService.save(product, authentication)
                .map(ResponseEntity::ok);
    }
}
