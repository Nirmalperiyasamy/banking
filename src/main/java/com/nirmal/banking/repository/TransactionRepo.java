package com.nirmal.banking.repository;

import com.nirmal.banking.dao.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionDetails, Integer> {

    boolean existsByUid(String uid);

    @Query("SELECT new com.nirmal.banking.dao.TransactionDetails( custom.amount, custom.transactionType) " +
            "FROM TransactionDetails custom WHERE custom.uid = :uid")
    List<TransactionDetails> totalAmount(@Param("uid") String uid);
}
