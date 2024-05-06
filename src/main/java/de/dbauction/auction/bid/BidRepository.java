package de.dbauction.auction.bid;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface BidRepository extends ReactiveCrudRepository<Bid, Long> {
}