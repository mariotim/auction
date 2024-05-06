package de.dbauction.auction.product;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table
public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal minimumBid;

    private Long ownerId;

    private boolean active;

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMinimumBid() {
        return minimumBid;
    }

    public void setMinimumBid(BigDecimal minimumBid) {
        this.minimumBid = minimumBid;
    }
}
