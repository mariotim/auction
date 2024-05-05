package de.dbauction.auction.bid;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Bid {
    @Id
    private String id;
    private String bidderId;
    private String productId;
    private BigDecimal bidAmount;
    private long bidTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBidderId() {
        return bidderId;
    }

    public void setBidderId(String bidderId) {
        this.bidderId = bidderId;
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