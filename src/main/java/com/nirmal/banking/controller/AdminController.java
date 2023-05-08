package com.nirmal.banking.controller;

import com.nirmal.banking.dto.KycStatusApprovalByAdmin;
import com.nirmal.banking.dto.TransactionDecisionByAdmin;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.service.AdminService;
import com.nirmal.banking.service.AdminSettingsService;
import lombok.RequiredArgsConstructor;
import com.nirmal.banking.response.CustomResponse;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.nirmal.banking.common.Routes.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN)
public class AdminController {

    private final AdminService adminService;

    private final AdminSettingsService adminSettingsService;

    @PostMapping(ADD_MANAGER)
    private CustomResponse<?> addManager(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        return CustomResponse.success(adminService.addManager(userDetailsDto));
    }

    @GetMapping(PENDING_KYC)
    private CustomResponse<?> pendingKyc() {
        return CustomResponse.success(adminService.pendingKyc());
    }

    @GetMapping(TRANSACTION_PENDING)
    private CustomResponse<?> depositPending() {
        return CustomResponse.success(adminService.transactionPending());
    }

    @PutMapping(DEPOSIT_DECISION)
    private CustomResponse<?> depositApprove(@RequestBody TransactionDecisionByAdmin transactionDecisionByAdmin) {
        return CustomResponse.success(adminService.depositApprove(transactionDecisionByAdmin.getTransactionId(),
                transactionDecisionByAdmin.getDecision()));
    }

    @PutMapping(WITHDRAW_DECISION)
    private CustomResponse<?> withdrawApprove(@RequestBody TransactionDecisionByAdmin transactionDecisionByAdmin) {
        return CustomResponse.success(adminService.withdrawApprove(transactionDecisionByAdmin.getTransactionId(),
                transactionDecisionByAdmin.getDecision()));
    }

    @PutMapping(VALIDATE_KYC)
    private CustomResponse<?> approved(@RequestBody KycStatusApprovalByAdmin kycStatusApprovalByAdmin) {
        return CustomResponse.success(adminService.approvedRejected(kycStatusApprovalByAdmin.getUid(), kycStatusApprovalByAdmin.getStatus()));
    }

    @PostMapping(WITHDRAW_FEE_PERCENTAGE)
    private CustomResponse<?> withdrawInterestPercentage(@RequestBody String interest) {
        return CustomResponse.success(adminSettingsService.withdrawInterestPercentage(Double.parseDouble(interest)));
    }

    @PutMapping(WITHDRAW_LIMIT)
    private CustomResponse<?> withdrawLimit(@RequestBody String limit) {
        return CustomResponse.success(adminSettingsService.withdrawLimit(Double.valueOf(limit)));
    }
}
