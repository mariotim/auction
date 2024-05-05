package de.dbauction.auction.product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class Product {
    @Id
    private String id;
    private String name;
    private BigDecimal minimumBid;

    private String ownerId;

    private boolean active;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String owerId) {
        this.ownerId = owerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
