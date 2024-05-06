package de.dbauction.auction.product;

import de.dbauction.auction.bid.Bid;
import de.dbauction.auction.bid.BidService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final BidService bidService;

    public ProductController(ProductService productService, BidService bidService) {
        this.productService = productService;
        this.bidService = bidService;
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<Product>> registerProduct(@RequestBody Product product, Authentication authentication) {
        return productService.save(product, authentication)
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "/end-auction/{productId}", produces = "application/json")
    public Mono<ResponseEntity<Bid>> endAuction(@PathVariable Long productId) {
        return bidService.endAuction(productId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

}
