package com.nirmal.banking.service;

import com.nirmal.banking.dao.AdminSettings;
import com.nirmal.banking.repository.AdminSettingsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminSettingsService {

    private final AdminSettingsRepo adminSettingsRepo;

    public AdminSettings withdrawInterestPercentage(Double interest) {
        if (!adminSettingsRepo.existsById(1)) {
            AdminSettings adminSettings = new AdminSettings();
            adminSettings.setId(1);
            adminSettings.setWithdrawInterestPercentage(interest);
            adminSettingsRepo.save(adminSettings);
            return adminSettings;
        } else {
            Optional<AdminSettings> adminSettings = adminSettingsRepo.findById(1);
            adminSettings.get().setWithdrawInterestPercentage(interest);
            adminSettingsRepo.save(adminSettings.get());
            return adminSettings.get();
        }
    }
}
