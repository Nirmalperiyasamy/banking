package com.nirmal.banking.repository;

import com.nirmal.banking.dao.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepo extends JpaRepository<CustomUserDetails, Integer> {

    boolean existsByuserName(String username);

    CustomUserDetails findByuserName(String username);

}
