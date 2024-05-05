package de.dbauction.auction.bid;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BidRepository extends ReactiveCrudRepository<Bid, String> {
    Flux<Bid> findAllByProductIdOrderByBidAmountDesc(String productId);
}