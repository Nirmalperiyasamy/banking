package com.nirmal.banking.repository;

import com.nirmal.banking.dao.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends JpaRepository<UserRole,Integer> {
}
