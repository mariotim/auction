CREATE TABLE PRODUCT
(
    id          VARCHAR(50) NOT NULL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    owner_id    VARCHAR(50) NOT NULL,
    minimum_bid NUMERIC     NOT NULL,
    active      bool
);

CREATE TABLE BID
(
    id         VARCHAR(50) NOT NULL PRIMARY KEY,
    bidder_id  VARCHAR(50) NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    bid_amount VARCHAR(50) NOT NULL,
    bid_time   VARCHAR(50) NOT NULL
);