package de.dbauction.auction.bid;

import de.dbauction.auction.product.ProductRepository;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Service
public class BidService {
    private final BidRepository bidRepository;
    private final ProductRepository productRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    private final static String QUERY = "SELECT * FROM bid WHERE product_id = :productId ORDER BY bid_amount DESC LIMIT 1";

    public BidService(BidRepository bidRepository, ProductRepository productRepository, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.bidRepository = bidRepository;
        this.productRepository = productRepository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    public Mono<Bid> placeBid(Bid bid) {
        bid.setBidTime(System.currentTimeMillis());
        return productRepository.findById(bid.getProductId())
                .switchIfEmpty(Mono.error(new IllegalStateException("Product doesn't exist")))
                .flatMap(product -> bidRepository.save(bid));
    }

    public Mono<Bid> getHighestBid(Long productId) {
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
                .one();
    }
}
