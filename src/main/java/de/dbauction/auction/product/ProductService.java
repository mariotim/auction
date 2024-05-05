package de.dbauction.auction.product;

import de.dbauction.auction.AuthenticationService;
import de.dbauction.auction.bid.Bid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AuthenticationService authenticationService;

    public ProductService(ProductRepository productRepository, AuthenticationService authenticationService) {
        this.productRepository = productRepository;
        this.authenticationService = authenticationService;
    }

    public Mono<Product> save(Product product, Authentication authentication) {
        product.setId(UUID.randomUUID().toString());
        String ownerId = authenticationService.extractUserId(authentication.getCredentials().toString());
        product.setOwnerId(ownerId);
        return productRepository.save(product);
    }

    public Mono<Bid> endAuction(String productId) {
        return null;
    }
}
