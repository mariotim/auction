package de.dbauction.auction.bid;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.TimeZone;
import java.util.UUID;

@Service
public class BidService {
    private final BidRepository bidRepository;

    public BidService(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public Mono<Bid> placeBid(Bid bid) {
        bid.setId(UUID.randomUUID().toString());
        bid.setBidTime(System.currentTimeMillis());
        return bidRepository.save(bid);
    }

    public Flux<Bid> getHighestBid(String productId) {
        return bidRepository.findAllByProductIdOrderByBidAmountDesc(productId);
    }
}
