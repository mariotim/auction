package de.dbauction.auction.bid;
import de.dbauction.auction.product.Product;
import de.dbauction.auction.product.ProductRepository;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
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
    private final BidService bidService = new BidService(bidRepository, productRepository, r2dbcEntityTemplate);

    @Test
    void placeBid_ProductExists_ReturnsSavedBid() {
        Bid bid = new Bid(1L, 1L, 1L, new BigDecimal("100.00"), System.currentTimeMillis());
        Product product = new Product();

        when(productRepository.findById(any(Long.class))).thenReturn(Mono.just(product));
        when(bidRepository.save(any(Bid.class))).thenReturn(Mono.just(bid));

        Mono<Bid> result = bidService.placeBid(bid);

        StepVerifier.create(result)
                .expectNextMatches(returnedBid -> returnedBid.getBidAmount().compareTo(new BigDecimal("100.00")) == 0)
                .verifyComplete();
    }

    @Test
    void placeBid_ProductDoesNotExist_ThrowsException() {
        Bid bid = new Bid(1L, 1L, 1L, new BigDecimal("100.00"), System.currentTimeMillis());

        when(productRepository.findById(any(Long.class))).thenReturn(Mono.empty());

        StepVerifier.create(bidService.placeBid(bid))
                .expectError(IllegalStateException.class)
                .verify();
    }
    @Test
    void getHighestBid_ReturnsHighestBid() {
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

        Mono<Bid> result = bidService.getHighestBid(productId);

        StepVerifier.create(result)
                .expectNextMatches(bid -> bid.getId().equals(expectedBid.getId())
                        && bid.getBidAmount().compareTo(expectedBid.getBidAmount()) == 0)
                .verifyComplete();
    }

}
