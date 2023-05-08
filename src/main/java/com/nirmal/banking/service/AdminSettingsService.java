package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dao.AdminSettings;
import com.nirmal.banking.exception.CustomException;
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
            adminSettings.setWithdrawFeePercentage(interest);
            adminSettingsRepo.save(adminSettings);
            return adminSettings;
        } else {
            Optional<AdminSettings> adminSettings = adminSettingsRepo.findById(1);
            if (adminSettings.isEmpty()) throw new CustomException(ErrorMessages.WITHDRAW_FEE_ERROR);
            adminSettings.get().setWithdrawFeePercentage(interest);
            adminSettingsRepo.save(adminSettings.get());
            return adminSettings.get();
        }
    }

    public AdminSettings withdrawLimit(Double withdrawLimit) {
        AdminSettings adminSettings = adminSettingsRepo.findById(1).orElseThrow(
                () -> new CustomException(ErrorMessages.WITHDRAW_FEE_ERROR));
        adminSettings.setWithdrawLimit(withdrawLimit);
        adminSettingsRepo.save(adminSettings);
        return adminSettings;
    }

    public AdminSettings adminSettingsDetails() {
        return adminSettingsRepo.findById(1).orElseThrow(() -> new CustomException(ErrorMessages.WITHDRAW_FEE_ERROR));
    }
}
