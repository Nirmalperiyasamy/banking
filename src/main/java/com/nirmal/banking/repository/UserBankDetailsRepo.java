package com.nirmal.banking.repository;

import com.nirmal.banking.dao.UserBankDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBankDetailsRepo extends JpaRepository<UserBankDetails, Integer> {
    UserBankDetails findByUid(String uid);
}
