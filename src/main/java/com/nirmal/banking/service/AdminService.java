package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dao.TransactionDetails;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.repository.TransactionRepo;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.utils.KycStatus;
import com.nirmal.banking.utils.Role;
import com.nirmal.banking.utils.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserDetailsRepo userDetailsRepo;

    private final TransactionRepo transactionRepo;

    private final PasswordEncoder passwordEncoder;

    private final RoleService roleService;


    public CustomUserDetails addManager(UserDetailsDto userDetailsDto) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanUtils.copyProperties(userDetailsDto, customUserDetails);
        customUserDetails.setPassword(passwordEncoder.encode(userDetailsDto.getPassword()));
        customUserDetails.setUserRole(roleService.getRole(Role.MANAGER));
        userDetailsRepo.save(customUserDetails);
        return customUserDetails;
    }

    public String approvedRejected(String uid, String status) {
        KycStatus kycstatus = KycStatus.valueOf(status.toUpperCase());
        CustomUserDetails customUserDetails = userDetailsRepo.findByUid(uid);

        switch (kycstatus) {
            case APPROVED:
                customUserDetails.setKycStatus(KycStatus.APPROVED);
                userDetailsRepo.save(customUserDetails);
                return "User " + KycStatus.APPROVED;

            case REJECTED:
                customUserDetails.setKycStatus(KycStatus.REJECTED);
                userDetailsRepo.save(customUserDetails);
                return "User " + KycStatus.REJECTED;

            default:
                throw new CustomException(ErrorMessages.STATUS_ERROR);
        }

    }

    public List<CustomUserDetails> pendingKyc() {
        return userDetailsRepo.findAllByKycStatus(KycStatus.PENDING);
    }

    public List<TransactionDetails> depositPending() {
        return transactionRepo.findAllByTransactionType(TransactionType.DEPOSIT_PENDING);
    }

    public List<TransactionDetails> withdrawPending() {
        return transactionRepo.findAllByTransactionType(TransactionType.WITHDRAW_PENDING);
    }

    public String depositApprove(String transactionId, String decision) {
        KycStatus kycstatus = KycStatus.valueOf(decision.toUpperCase());
        TransactionDetails transactionDetails = transactionRepo.findByTransactionId(transactionId);
        switch (kycstatus) {
            case APPROVED:
                transactionDetails.setTransactionType(TransactionType.DEPOSIT);
                transactionDetails.setTotalAmount(totalAmount(transactionDetails.getUid()) + transactionDetails.getAmount());
                transactionRepo.save(transactionDetails);
                return "User " + TransactionType.DEPOSIT;

            case REJECTED:
                transactionDetails.setTransactionType(TransactionType.REJECTED);
                transactionRepo.save(transactionDetails);
                return "User " + TransactionType.REJECTED;

            default:
                throw new CustomException(ErrorMessages.STATUS_ERROR);
        }
    }

    Double totalAmount(String uid) {
        return transactionRepo.totalAmount(uid).stream()
                .filter(details -> details.getTransactionType() == TransactionType.DEPOSIT)
                .mapToDouble(TransactionDetails::getAmount).sum() -
                transactionRepo.totalAmount(uid).stream()
                        .filter(details -> details.getTransactionType() == TransactionType.WITHDRAW)
                        .mapToDouble(TransactionDetails::getAmount).sum();
    }

    public String withdrawApprove(String transactionId, String decision) {
        KycStatus kycstatus = KycStatus.valueOf(decision.toUpperCase());
        TransactionDetails transactionDetails = transactionRepo.findByTransactionId(transactionId);
        switch (kycstatus) {
            case APPROVED:
                transactionDetails.setTransactionType(TransactionType.WITHDRAW);
                transactionDetails.setTotalAmount(totalAmount(transactionDetails.getUid()) - transactionDetails.getAmount() -
                        transactionDetails.getWithdrawInterestAmount());
                transactionRepo.save(transactionDetails);
                return "User " + TransactionType.WITHDRAW;

            case REJECTED:
                transactionDetails.setTransactionType(TransactionType.REJECTED);
                transactionRepo.save(transactionDetails);
                return "User " + TransactionType.REJECTED;

            default:
                throw new CustomException(ErrorMessages.STATUS_ERROR);
        }
    }
}
