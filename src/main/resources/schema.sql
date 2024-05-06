CREATE TABLE PRODUCT
(
    id          BIGINT         NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(50)    NOT NULL,
    owner_id    BIGINT         NOT NULL,
    minimum_bid DECIMAL(10, 2) NOT NULL, -- Assuming bid amounts can have cents
    active      bool
);

CREATE TABLE BID
(
    id         BIGINT         NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bidder_id  BIGINT         NOT NULL,
    product_id BIGINT         NOT NULL,
    bid_amount DECIMAL(10, 2) NOT NULL, -- Assuming bid amounts can have cents
    bid_time   BIGINT   NOT NULL
);