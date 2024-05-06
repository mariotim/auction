package de.dbauction.auction.bid;

import de.dbauction.auction.AuthenticationService;
import de.dbauction.auction.exception.AuctionClientErrorException;
import de.dbauction.auction.product.ProductRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class BidService {
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final AuthenticationService authenticationService;

    private final static String QUERY = "SELECT * FROM bid WHERE product_id = :productId ORDER BY bid_amount DESC LIMIT 1";

    public BidService(BidRepository bidRepository, ProductRepository productRepository, R2dbcEntityTemplate r2dbcEntityTemplate, AuthenticationService authenticationService) {
        this.bidRepository = bidRepository;
        this.productRepository = productRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.authenticationService = authenticationService;
    }

    public Mono<Bid> placeBid(Bid bid, Authentication authentication) {
        Long bidderId = Long.valueOf(authenticationService.extractUserId(authentication.getCredentials().toString()));
        bid.setBidderId(bidderId);
        bid.setBidTime(System.currentTimeMillis());
        return productRepository.findById(bid.getProductId())
                .switchIfEmpty(Mono.error(new AuctionClientErrorException("Product doesn't exist")))
                .flatMap(foundProduct -> {
                    if (foundProduct.getMinimumBid().compareTo(bid.getBidAmount()) >= 0) {
                        return Mono.error(new AuctionClientErrorException("Bid is too low"));
                    }
                    if (!foundProduct.isActive()) {
                        return Mono.error(new AuctionClientErrorException("Auction is over"));
                    } else {
                        return bidRepository.save(bid);
                    }
                });
    }

    public Mono<Bid> endAuction(Long productId) {
        return r2dbcEntityTemplate.getDatabaseClient()
                .sql(QUERY)
                .bind("productId", productId)
                .map((row, meta) -> new Bid(
                        row.get("id", Long.class),
                        row.get("bidder_id", Long.class),
                        row.get("product_id", Long.class),
                        row.get("bid_amount", BigDecimal.class),
                        row.get("bid_time", Long.class)
                ))
                .one().switchIfEmpty(Mono.error(new AuctionClientErrorException("No bid found")));
    }
}
