package de.dbauction.auction.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<Product>> registerProduct(@RequestBody Product product) {
        return productRepository.save(product)
                .map(ResponseEntity::ok);
    }
}
