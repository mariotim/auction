package de.dbauction.auction.bid;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table
public class Bid {
    @Id
    private Long id;
    private Long bidderId;
    private Long productId;
    private BigDecimal bidAmount;
    private long bidTime;

    public Bid() {
    }

    public Bid(Long id, Long bidderId, Long productId, BigDecimal bidAmount, long bidTime) {
        this.id = id;
        this.bidderId = bidderId;
        this.productId = productId;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBidderId() {
        return bidderId;
    }

    public void setBidderId(Long bidderId) {
        this.bidderId = bidderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public long getBidTime() {
        return bidTime;
    }

    public void setBidTime(long bidTime) {
        this.bidTime = bidTime;
    }
}