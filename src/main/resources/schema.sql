CREATE TABLE PRODUCT
(
    id          VARCHAR(50) NOT NULL PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    owner_id        VARCHAR(50) NOT NULL,
    minimum_bid NUMERIC     NOT NULL,
    active      bool
);