package de.deutschebank.bid;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface BidRepository extends ReactiveCrudRepository<Bid, Long> {
    Flux<Bid> findByProductId(Long productId);
}