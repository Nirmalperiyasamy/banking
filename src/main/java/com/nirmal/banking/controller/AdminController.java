package com.nirmal.banking.controller;

import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.service.AdminService;
import com.nirmal.banking.utils.KycStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.nirmal.banking.common.Const.*;
import static com.nirmal.banking.common.ErrorMessages.STATUS_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN)
public class AdminController {

    private final AdminService adminService;

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
    private ResponseEntity<?> depositApprove(@PathVariable("transaction-id") String transactionId,
                                             @PathVariable("decision") String decision) {
        try {
            KycStatus kycStatus = KycStatus.valueOf(decision.toUpperCase());
        } catch (Exception exception) {
            throw new CustomException(STATUS_ERROR);
        }
        return ResponseEntity.ok(adminService.depositApprove(transactionId, decision));
    }

    @PutMapping(WITHDRAW_DECISION)
    private ResponseEntity<?> withdrawApprove(@PathVariable("transaction-id") String transactionId,
                                              @PathVariable("decision") String decision) {
        try {
            KycStatus kycStatus = KycStatus.valueOf(decision.toUpperCase());
        } catch (Exception exception) {
            throw new CustomException(STATUS_ERROR);
        }
        return ResponseEntity.ok(adminService.withdrawApprove(transactionId, decision));
    }

    @PutMapping(VALIDATE_KYC)
    private ResponseEntity<?> approved(@PathVariable("uid") String uid,
                                       @PathVariable("decision") String status) {
        try {
            KycStatus kycStatus = KycStatus.valueOf(status.toUpperCase());
        } catch (Exception exception) {
            throw new CustomException(STATUS_ERROR);
        }
        return ResponseEntity.ok(adminService.approvedRejected(uid, status));
    }

    @PostMapping(WITHDRAW_INTEREST_PERCENTAGE)
    private ResponseEntity<?> withdrawInterestPercentage(@RequestBody String interest) {
        return ResponseEntity.ok(adminService.withdrawInterestPercentage(Integer.parseInt(interest)));
    }
}
