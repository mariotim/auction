package de.dbauction.auction.product;

import de.dbauction.auction.AuthenticationService;
import de.dbauction.auction.exception.AuctionClientErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    private final ProductRepository productRepository = mock();
    private final AuthenticationService authenticationService = mock();
    private final Authentication authentication = mock();

    private final ProductService productService = new ProductService(productRepository, authenticationService);

    @Test
    void save_ReturnsSavedProduct() {
        // given
        Product product = new Product();
        product.setName("Test Product");

        String userId = "12345";
        Long ownerId = Long.parseLong(userId);
        when(authentication.getCredentials()).thenReturn(userId);
        when(authenticationService.extractUserId(authentication.getCredentials().toString())).thenReturn(userId);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // when
        Mono<Product> result = productService.save(product, authentication);

        // then
        StepVerifier.create(result)
                .assertNext(savedProduct -> {
                    assertEquals(ownerId, savedProduct.getOwnerId());
                    assertEquals("Test Product", savedProduct.getName());
                })
                .verifyComplete();
    }

    @Test
    void save_InvalidUserId_ThrowsException() {
        // give
        Product product = new Product();
        when(authentication.getCredentials()).thenReturn("1");
        when(authenticationService.extractUserId(any())).thenThrow(new NumberFormatException("Invalid user ID"));

        // then
        Mono<Product> save = productService.save(product, authentication);
        StepVerifier.create(save)
                .expectErrorMatches(exception -> exception instanceof NumberFormatException
                        && exception.getMessage().equals("Invalid user ID"))
                .verify();
    }

    @Test
    void save_InvalidAuthentication_ThrowsException() {
        // give
        Product product = new Product();
        when(authentication.getCredentials()).thenReturn(null);

        // then
        Mono<Product> save = productService.save(product, authentication);
        StepVerifier.create(save)
                .expectErrorMatches(exception -> exception instanceof AuctionClientErrorException
                        && exception.getMessage().equals("Authentication failed"))
                .verify();
    }
}
