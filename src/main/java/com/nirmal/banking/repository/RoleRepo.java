package com.nirmal.banking.repository;

import com.nirmal.banking.dao.UserRole;
import com.nirmal.banking.utils.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<UserRole,Integer> {
    Optional<UserRole> findByRole(Role role);
}
