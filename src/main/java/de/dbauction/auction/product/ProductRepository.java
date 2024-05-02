package de.dbauction.auction.product;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
}