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

    List<TransactionDetails> findAllByTransactionStatus(TransactionStatus transactionStatus);

    List<TransactionDetails> findAllByUidAndTransactionStatusNot(String uid, TransactionStatus status);

    TransactionDetails findByTransactionId(String transactionId);

    List<TransactionDetails> findAllByUidAndInitiatedAtBetweenAndTransactionStatusNot(String uid, long oneDayBefore
            , long currentTime, TransactionStatus status);

    List<TransactionDetails> findAllByUidAndInitiatedAtGreaterThanEqual(String uid, Long userSpecifiedDate);
}
