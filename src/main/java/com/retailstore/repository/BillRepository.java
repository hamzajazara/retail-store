package com.retailstore.repository;

import com.retailstore.entity.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BillRepository extends MongoRepository<Bill, Long> {

    Optional<Bill> findByIdAndUserId(long id, long userId);
}
