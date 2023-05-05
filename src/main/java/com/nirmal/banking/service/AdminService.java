package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dao.TransactionDetails;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.repository.TransactionRepo;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.response.CustomResponse;
import com.nirmal.banking.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
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


    public CustomResponse<CustomUserDetails> addManager(UserDetailsDto userDetailsDto) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanUtils.copyProperties(userDetailsDto, customUserDetails);
        customUserDetails.setPassword(passwordEncoder.encode(userDetailsDto.getPassword()));
        customUserDetails.setUserRole(roleService.getRole(Role.MANAGER));
        userDetailsRepo.save(customUserDetails);
        return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, customUserDetails);
    }

    public CustomResponse<String> approvedRejected(String uid, KycStatus kycStatus) {
        CustomUserDetails customUserDetails = userDetailsRepo.findByUid(uid);

        switch (kycStatus) {
            case APPROVED:
                customUserDetails.setKycStatus(KycStatus.APPROVED);
                userDetailsRepo.save(customUserDetails);
                return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, "User " + KycStatus.APPROVED);

            case REJECTED:
                customUserDetails.setKycStatus(KycStatus.REJECTED);
                userDetailsRepo.save(customUserDetails);
                return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, "User " + KycStatus.REJECTED);

            default:
                throw new CustomException(ErrorMessages.STATUS_ERROR);
        }

    }

    public CustomResponse<List<CustomUserDetails>> pendingKyc() {
        return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, userDetailsRepo.findAllByKycStatus(KycStatus.PENDING));
    }

    public CustomResponse<List<TransactionDetails>> transactionPending() {
        return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, transactionRepo.findAllByTransactionStatus(TransactionStatus.PENDING));
    }

    public CustomResponse<String> depositApprove(String transactionId, TransactionStatus transactionStatus) {
        TransactionDetails transactionDetails = transactionRepo.findByTransactionId(transactionId);
        switch (transactionStatus) {
            case APPROVED:
                transactionDetails.setTransactionStatus(TransactionStatus.APPROVED);
                transactionDetails.setTotalAmount(totalAmount(transactionDetails.getUid()));
                transactionRepo.save(transactionDetails);
                return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, "User Amount => " + TransactionType.DEPOSIT);

            case REJECTED:
                transactionDetails.setTransactionStatus(TransactionStatus.REJECTED);
                transactionRepo.save(transactionDetails);
                return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, "User Amount => " + TransactionStatus.REJECTED);

            default:
                throw new CustomException(ErrorMessages.STATUS_ERROR);
        }
    }

    Double totalAmount(String uid) {
        // TODO: Need to move summation to SQL
        return transactionRepo.findAllByUidAndTransactionStatusNot(uid, TransactionStatus.REJECTED).stream()
                .map(details -> {
                    if ((details.getTransactionType() == TransactionType.DEPOSIT) && (details.getTransactionStatus() == TransactionStatus.APPROVED)) {
                        return details.getAmount();
                    } else if (details.getTransactionType() == TransactionType.WITHDRAW) {
                        return -details.getAmount() - details.getWithdrawFee();
                    }
                    return 0.0;
                }).reduce(0.0, Double::sum);
    }

    public CustomResponse<String> withdrawApprove(String transactionId, TransactionStatus kycStatus) {
        TransactionDetails transactionDetails = transactionRepo.findByTransactionId(transactionId);
        switch (kycStatus) {
            case APPROVED:
                transactionDetails.setTransactionStatus(TransactionStatus.APPROVED);
                transactionRepo.save(transactionDetails);
                return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, "User Amount =>" + TransactionStatus.APPROVED);

            case REJECTED:
                transactionDetails.setTransactionStatus(TransactionStatus.REJECTED);
                transactionRepo.save(transactionDetails);
                return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, "User Amount =>" + TransactionStatus.REJECTED);


            default:
                throw new CustomException(ErrorMessages.STATUS_ERROR);
        }
    }
}
