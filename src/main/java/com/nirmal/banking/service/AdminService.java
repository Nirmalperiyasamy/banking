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
import com.nirmal.banking.utils.TransactionStatus;
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
        return transactionRepo.findAllByTransactionStatus(TransactionStatus.PENDING);
    }

    public List<TransactionDetails> withdrawPending() {
        return transactionRepo.findAllByTransactionStatus(TransactionStatus.PENDING);
    }

    public String depositApprove(String transactionId, String decision) {
        KycStatus kycstatus = KycStatus.valueOf(decision.toUpperCase());
        TransactionDetails transactionDetails = transactionRepo.findByTransactionId(transactionId);
        switch (kycstatus) {
            case APPROVED:
                transactionDetails.setTransactionStatus(TransactionStatus.APPROVED);
                transactionDetails.setTotalAmount(totalAmount(transactionDetails.getUid()) + transactionDetails.getAmount());
                transactionRepo.save(transactionDetails);
                return "User Amount => " + TransactionType.DEPOSIT;

            case REJECTED:
                transactionDetails.setTransactionStatus(TransactionStatus.REJECTED);
                transactionRepo.save(transactionDetails);
                return "User Amount => " + TransactionStatus.REJECTED;

            default:
                throw new CustomException(ErrorMessages.STATUS_ERROR);
        }
    }

    Double totalAmount(String uid) {
        // TODO: Need to move summation to SQL
        return transactionRepo.findAllByUidAndTransactionStatusNot(uid, TransactionStatus.REJECTED).stream()
                .filter(details -> (details.getTransactionType() == TransactionType.DEPOSIT) && (details.getTransactionStatus() == TransactionStatus.APPROVED))
                .mapToDouble(TransactionDetails::getAmount).sum() -
                transactionRepo.findAllByUidAndTransactionStatusNot(uid, TransactionStatus.REJECTED).stream()
                        .filter(details -> details.getTransactionType() == TransactionType.WITHDRAW)
                        .mapToDouble(details -> details.getAmount() + details.getWithdrawFee()).sum();
    }

    public String withdrawApprove(String transactionId, String decision) {
        KycStatus kycstatus = KycStatus.valueOf(decision.toUpperCase());
        TransactionDetails transactionDetails = transactionRepo.findByTransactionId(transactionId);
        switch (kycstatus) {
            case APPROVED:
                transactionDetails.setTransactionStatus(TransactionStatus.APPROVED);
                transactionDetails.setTotalAmount(totalAmount(transactionDetails.getUid()) - transactionDetails.getAmount() -
                        transactionDetails.getWithdrawFee());
                transactionRepo.save(transactionDetails);
                return "User Amount =>" + TransactionType.WITHDRAW;

            case REJECTED:
                transactionDetails.setTransactionStatus(TransactionStatus.REJECTED);
                transactionRepo.save(transactionDetails);
                return "User Amount =>" + TransactionStatus.REJECTED;

            default:
                throw new CustomException(ErrorMessages.STATUS_ERROR);
        }
    }
}
