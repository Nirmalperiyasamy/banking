package com.nirmal.banking.repository;

import com.nirmal.banking.dao.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminSettingsRepo extends JpaRepository<AdminSettings,Integer> {
}
