package de.dbauction.auction.product;

import de.dbauction.auction.AuthenticationService;
import de.dbauction.auction.bid.Bid;
import de.dbauction.auction.exception.AuthenticationException;
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
        long ownerId;
        try {
            if (authentication == null || authentication.getCredentials() == null) {
                throw new AuthenticationException("Authentication failed");
            }
            ownerId = Long.parseLong(authenticationService.extractUserId(authentication.getCredentials().toString()));
            product.setOwnerId(ownerId);
            return productRepository.save(product);
        } catch (NumberFormatException | AuthenticationException e) {
            return Mono.error(e);
        }

    }
}
