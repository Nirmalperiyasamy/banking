package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dao.AdminSettings;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.repository.AdminSettingsRepo;
import com.nirmal.banking.response.CustomResponse;
import com.nirmal.banking.utils.CustomStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminSettingsService {

    private final AdminSettingsRepo adminSettingsRepo;

    public CustomResponse<AdminSettings> withdrawInterestPercentage(Double interest) {
        if (!adminSettingsRepo.existsById(1)) {
            AdminSettings adminSettings = new AdminSettings();
            adminSettings.setWithdrawFeePercentage(interest);
            adminSettingsRepo.save(adminSettings);
            return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, adminSettings);
        } else {
            Optional<AdminSettings> adminSettings = adminSettingsRepo.findById(1);
            if (adminSettings.isEmpty()) throw new CustomException(ErrorMessages.WITHDRAW_FEE_ERROR);
            adminSettings.get().setWithdrawFeePercentage(interest);
            adminSettingsRepo.save(adminSettings.get());
            return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, adminSettings.get());
        }
    }

    public CustomResponse<AdminSettings> withdrawLimit(Double withdrawLimit) {
        Optional<AdminSettings> adminSettings = adminSettingsRepo.findById(1);
        if (adminSettings.isEmpty()) throw new CustomException(ErrorMessages.WITHDRAW_FEE_ERROR);
        adminSettings.get().setWithdrawLimit(withdrawLimit);
        return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, adminSettings.get());
    }
}
