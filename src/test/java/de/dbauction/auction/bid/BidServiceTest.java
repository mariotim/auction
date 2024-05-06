package de.dbauction.auction.bid;

import de.dbauction.auction.AuthenticationService;
import de.dbauction.auction.exception.AuctionClientErrorException;
import de.dbauction.auction.product.Product;
import de.dbauction.auction.product.ProductRepository;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BidServiceTest {
    private final BidRepository bidRepository = mock(BidRepository.class);
    private final ProductRepository productRepository = mock(ProductRepository.class);
    private final R2dbcEntityTemplate r2dbcEntityTemplate = mock(R2dbcEntityTemplate.class);
    private final AuthenticationService authenticationService = mock(AuthenticationService.class);
    private final BidService bidService = new BidService(bidRepository, productRepository, r2dbcEntityTemplate, authenticationService);

    @Test
    void placeBid_ProductExists_ReturnsSavedBid() {
        //given
        Bid bid = new Bid(1L, 1L, 1L, new BigDecimal("100.00"), System.currentTimeMillis());
        Product product = new Product(1L, "product", new BigDecimal("20.00"), 1L, true);
        Authentication authentication = mock(Authentication.class);

        when(productRepository.findById(any(Long.class))).thenReturn(Mono.just(product));
        when(bidRepository.save(any(Bid.class))).thenReturn(Mono.just(bid));
        when(authentication.getCredentials()).thenReturn("credentials");
        when(authenticationService.extractUserId(any())).thenReturn("1");

        //when
        Mono<Bid> result = bidService.placeBid(bid, authentication);

        //then
        StepVerifier.create(result)
                .expectNextMatches(returnedBid -> returnedBid.getBidAmount().compareTo(new BigDecimal("100.00")) == 0)
                .verifyComplete();
    }

    @Test
    void placeBid_ProductDoesNotExist_ThrowsException() {
        //given
        Bid bid = new Bid(1L, 1L, 1L, new BigDecimal("100.00"), System.currentTimeMillis());
        Authentication authentication = mock(Authentication.class);

        when(authentication.getCredentials()).thenReturn("credentials");
        when(authenticationService.extractUserId(any())).thenReturn("1");
        when(productRepository.findById(any(Long.class))).thenReturn(Mono.empty());

        //then
        StepVerifier.create(bidService.placeBid(bid, authentication))
                .expectError(AuctionClientErrorException.class)
                .verify();
    }

    @Test
    void placeBid_TooLowBid_ThrowsException() {
        //given
        Bid bid = new Bid(1L, 1L, 1L, new BigDecimal("100.00"), System.currentTimeMillis());
        Product product = new Product(1L, "", new BigDecimal("200.00"), 1L, true);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getCredentials()).thenReturn("credentials");
        when(authenticationService.extractUserId(any())).thenReturn("1");
        when(productRepository.findById(any(Long.class))).thenReturn(Mono.just(product));

        //then
        StepVerifier.create(bidService.placeBid(bid, authentication))
                .expectErrorMatches(exception -> exception instanceof AuctionClientErrorException && exception.getMessage().equals("Bid is too low"))
                .verify();
    }

    @Test
    void placeBid_AuctionOver_ThrowsException() {
        //given
        Bid bid = new Bid(1L, 1L, 1L, new BigDecimal("100.00"), System.currentTimeMillis());
        Product product = new Product(1L, "", new BigDecimal("20.00"), 1L, false);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getCredentials()).thenReturn("credentials");
        when(authenticationService.extractUserId(any())).thenReturn("1");
        when(productRepository.findById(any(Long.class))).thenReturn(Mono.just(product));

        //then
        StepVerifier.create(bidService.placeBid(bid, authentication))
                .expectErrorMatches(exception -> exception instanceof AuctionClientErrorException && exception.getMessage().equals("Auction is over"))
                .verify();
    }

    @Test
    void getHighestBid_ReturnsHighestBid() {
        //given
        Long productId = 1L;
        Bid expectedBid = new Bid(1L, 2L, productId, new BigDecimal("300.00"), System.currentTimeMillis());

        DatabaseClient.GenericExecuteSpec executeSpec = mock(DatabaseClient.GenericExecuteSpec.class);
        when(r2dbcEntityTemplate.getDatabaseClient()).thenReturn(mock(DatabaseClient.class));
        when(r2dbcEntityTemplate.getDatabaseClient().sql(any(String.class))).thenReturn(executeSpec);
        when(executeSpec.bind("productId", productId)).thenReturn(executeSpec);
        RowsFetchSpec<Bid> fetchSpec = mock(RowsFetchSpec.class);

        when(executeSpec.map(Mockito.<BiFunction<Row, RowMetadata, Bid>>any())).thenAnswer(invocation -> {
            Row row = mock(Row.class);

            when(row.get("id", Long.class)).thenReturn(1L);
            when(row.get("bidder_id", Long.class)).thenReturn(2L);
            when(row.get("product_id", Long.class)).thenReturn(1L);
            when(row.get("bid_amount", BigDecimal.class)).thenReturn(new BigDecimal("300.00"));
            when(row.get("bid_time", Long.class)).thenReturn(System.currentTimeMillis());

            return fetchSpec;
        });
        when(fetchSpec.one()).thenReturn(Mono.just(expectedBid));

        //when
        Mono<Bid> result = bidService.endAuction(productId);

        //then
        StepVerifier.create(result)
                .expectNextMatches(bid -> bid.getId().equals(expectedBid.getId())
                        && bid.getBidAmount().compareTo(expectedBid.getBidAmount()) == 0)
                .verifyComplete();
    }

    @Test
    void getHighestBid_NoBidFound_ThrowsException() {
        //given
        Long productId = 1L;
        DatabaseClient client = Mockito.mock(DatabaseClient.class);
        DatabaseClient.GenericExecuteSpec executeSpec = Mockito.mock(DatabaseClient.GenericExecuteSpec.class);
        RowsFetchSpec fetchSpec = mock(RowsFetchSpec.class);

        when(r2dbcEntityTemplate.getDatabaseClient()).thenReturn(client);
        when(client.sql(any(String.class))).thenReturn(executeSpec);
        when(executeSpec.bind("productId", productId)).thenReturn(executeSpec);
        when(r2dbcEntityTemplate.getDatabaseClient()).thenReturn(mock(DatabaseClient.class));
        when(r2dbcEntityTemplate.getDatabaseClient().sql(any(String.class))).thenReturn(executeSpec);
        when(executeSpec.bind("productId", productId)).thenReturn(executeSpec);
        when(executeSpec.map(Mockito.<BiFunction<Row, RowMetadata, Bid>>any())).thenAnswer(invocation -> {
            Row row = mock(Row.class);

            when(row.get("id", Long.class)).thenReturn(1L);
            when(row.get("bidder_id", Long.class)).thenReturn(2L);
            when(row.get("product_id", Long.class)).thenReturn(1L);
            when(row.get("bid_amount", BigDecimal.class)).thenReturn(new BigDecimal("300.00"));
            when(row.get("bid_time", Long.class)).thenReturn(System.currentTimeMillis());
            return fetchSpec;
        });
        when(fetchSpec.one()).thenReturn(Mono.empty());

        //when
        Mono<Bid> result = bidService.endAuction(productId);

        //then
        StepVerifier.create(result)
                .expectErrorMatches(exception -> exception instanceof AuctionClientErrorException && exception.getMessage().equals("No bid found"))
                .verify();
    }


}
