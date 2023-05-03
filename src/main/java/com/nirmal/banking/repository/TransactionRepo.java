package com.nirmal.banking.repository;

import com.nirmal.banking.dao.TransactionDetails;
import com.nirmal.banking.utils.TransactionStatus;
import com.nirmal.banking.utils.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionDetails, Integer> {

    boolean existsByUid(String uid);

    @Query("SELECT new com.nirmal.banking.dao.TransactionDetails( custom.amount, custom.transactionType,custom.transactionStatus) " +
            "FROM TransactionDetails custom WHERE custom.uid = :uid AND custom.transactionStatus != 'REJECTED'")
    List<TransactionDetails> totalAmount(@Param("uid") String uid);

    List<TransactionDetails> findAllByTransactionStatus(TransactionStatus transactionStatus);

    // TODO : Need to move summation to SQL
//    @Query("SELECT sum(custom.amount) FROM TransactionDetails custom WHERE custom.uid = :uid AND (custom.transactionType = :deposit) AND (custom.transactionStatus = :transactionStatus)")
//    Double depositTotalAmount(@Param("uid") String uid, @Param("deposit") TransactionType transactionType, @Param("transactionStatus") TransactionStatus transactionStatus);
//
//    @Query(value = "SELECT sum(CASE WHEN txn.amount IS NOT NULL THEN txn.amount ELSE 0 END) FROM transaction_details txn WHERE txn.uid = :uid AND txn.transaction_type = :txnType AND txn.transaction_status = :approved OR txn.transaction_status = :pending", nativeQuery = true)
//    Double withdrawTotalAmount(@Param("uid") String uid, @Param("txnType") TransactionType txnType, @Param("approved") TransactionStatus approved, @Param("pending") TransactionStatus pending);

    TransactionDetails findByTransactionId(String transactionId);
}
