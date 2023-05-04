package com.nirmal.banking.controller;

import com.nirmal.banking.dto.KycStatusApprovalByAdmin;
import com.nirmal.banking.dto.TransactionDecisionByAdmin;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.service.AdminService;
import com.nirmal.banking.service.AdminSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.nirmal.banking.common.Const.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN)
public class AdminController {

    private final AdminService adminService;

    private final AdminSettingsService adminSettingsService;

    @PostMapping(ADD_MANAGER)
    private ResponseEntity<?> addManager(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        return ResponseEntity.ok(adminService.addManager(userDetailsDto));
    }

    @GetMapping(PENDING_KYC)
    private ResponseEntity<?> pendingKyc() {
        return ResponseEntity.ok(adminService.pendingKyc());
    }

    @GetMapping(DEPOSIT_PENDING)
    private ResponseEntity<?> depositPending() {
        return ResponseEntity.ok(adminService.depositPending());
    }

    @GetMapping(WITHDRAW_PENDING)
    private ResponseEntity<?> withdrawPending() {
        return ResponseEntity.ok(adminService.withdrawPending());
    }

    @PutMapping(DEPOSIT_DECISION)
    private ResponseEntity<?> depositApprove(@RequestBody TransactionDecisionByAdmin transactionDecisionByAdmin) {
        return ResponseEntity.ok(adminService.depositApprove(transactionDecisionByAdmin.getTransactionId(),
                transactionDecisionByAdmin.getDecision()));
    }

    @PutMapping(WITHDRAW_DECISION)
    private ResponseEntity<?> withdrawApprove(@RequestBody TransactionDecisionByAdmin transactionDecisionByAdmin) {
        return ResponseEntity.ok(adminService.withdrawApprove(transactionDecisionByAdmin.getTransactionId(),
                transactionDecisionByAdmin.getDecision()));
    }

    @PutMapping(VALIDATE_KYC)
    private ResponseEntity<?> approved(@RequestBody KycStatusApprovalByAdmin kycStatusApprovalByAdmin) {
        return ResponseEntity.ok(adminService.approvedRejected(kycStatusApprovalByAdmin.getUid(), kycStatusApprovalByAdmin.getStatus()));
    }

    @PostMapping(WITHDRAW_FEE_PERCENTAGE)
    private ResponseEntity<?> withdrawInterestPercentage(@RequestBody String interest) {
        return ResponseEntity.ok(adminSettingsService.withdrawInterestPercentage(Double.parseDouble(interest)));
    }
}
