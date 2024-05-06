# Auction

This solution can have improvements: better exception handling, separate db and transfer objects, integration tests, more unit tests and etc
This service provides functionality to create a product, make bids, and end an auction, receiving the highest bid



## Installation

### Clone the Repository

You can clone the repository using the following command:

```bash
git clone https://github.com/mariotim/auction.git
```

## Compile the Project

To compile the project, navigate to the project directory and run:

```bash
mvn clean package
```

## Usage

### Start Spring Boot Application

To start the Spring Boot application, navigate to the project directory and run:

```bash
java -jar target/auction-1.0-SNAPSHOT.jar
```

## API Endpoints
Each endpoint requires an Authorization header with Bearer token obtained from User Service (https://github.com/mariotim/auction_user.git)

### The service exposes an API endpoints to register a product .

##### Register Product

```
curl -X POST http://localhost:8080/api/product/register \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzE0OTg1MTU5LCJleHAiOjE3MTUwMjExNTl9.twhFYLwFaKs63QaYq9hM7heAfZ68ORtZqm9mivQIGMOVbR2SF8ZTNJpprR39gRTz2TA5XqvGhJkLEygdFZEyww" \
    -d '{
        "name": "Sample Product",
        "active": true,
        "minimumBid": 100.00
    }' \

```

Response example:
```
{
  "id": 2,
  "name": "test",
  "minimumBid": 123.32,
  "ownerId": 1,
  "active": false
}
```

##### Make a bid

```
curl -X POST http://localhost:8080/api/bid \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -H "Authorization: Bearer <TOKEN>" \
    -d '{
        "productId": 1,
        "bidAmount": 100.00
    }' \

```
Response example:
```
{
  "id": 1,
  "bidderId": 1,
  "productId": 1,
  "bidAmount": 222,
  "bidTime": 1715000241157
}
```

##### End an auction 

```
curl -X GET http://localhost:8080/api/product/end-auction/1 \
    -H "Content-Type: application/json" \
    -H "Accept: application/json" \
    -H "Authorization: Bearer <TOKEN>" \

```
Response Example:
```
{
  "id": 1,
  "bidderId": 1,
  "productId": 1,
  "bidAmount": 222,
  "bidTime": 1715000241157
}
```

## Testing

### Run Tests

To run tests, navigate to the project directory and run:

```bash
mvn test
```