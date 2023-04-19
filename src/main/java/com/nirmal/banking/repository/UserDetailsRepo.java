package com.nirmal.banking.repository;

import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.utils.KycStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDetailsRepo extends JpaRepository<CustomUserDetails, Integer> {

    boolean existsByUsername(String username);

    CustomUserDetails findByUsername(String username);

    CustomUserDetails findByUid(String uid);

    List<CustomUserDetails> findAllByKycStatus(KycStatus pending);
}
