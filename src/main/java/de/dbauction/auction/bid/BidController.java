package de.dbauction.auction.bid;

import de.dbauction.auction.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bid")
public class BidController {
    private final BidService bidService;
    private final AuthenticationService authenticationService;


    public BidController(BidService bidService, AuthenticationService authenticationService) {
        this.bidService = bidService;
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "", consumes = "application/json", produces = "application/json")
    public Mono<Bid> placeBid(@RequestBody Bid bid, Authentication authentication) {
        Long bidderId = Long.valueOf(authenticationService.extractUserId(authentication.getCredentials().toString()));
        bid.setBidderId(bidderId);
        return bidService.placeBid(bid);
    }
}

