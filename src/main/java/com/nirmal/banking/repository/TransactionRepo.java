package com.nirmal.banking.repository;

import com.nirmal.banking.dao.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepo extends JpaRepository<TransactionDetails, Integer> {
    boolean existsByUid(String uid);

    @Query("SELECT SUM(p.amount) FROM TransactionDetails p WHERE p.uid = :uid")
    Long getTotalAmountByUid(@Param("uid")String uid);
}
