package de.deutschebank;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("bids")
public class Bid {
    @Id
    private Long id;
    private Long userId;
    private Long productId;
    private BigDecimal bidAmount;  // Bid amount using BigDecimal
    private LocalDateTime bidTime;  // Timestamp when the bid was placed

    // Constructors, Getters, and Setters
    public Bid() {}

    public Bid(Long userId, Long productId, BigDecimal bidAmount, LocalDateTime bidTime) {
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }
}