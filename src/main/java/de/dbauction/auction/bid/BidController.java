package de.dbauction.auction.bid;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bid")
public class BidController {
    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public Mono<Bid> placeBid(@RequestBody Bid bid, Authentication authentication) {
        return bidService.placeBid(bid,authentication);
    }
}

