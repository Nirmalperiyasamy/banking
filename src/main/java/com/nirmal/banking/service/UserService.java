package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.common.SuccessMessages;
import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dao.TransactionDetails;
import com.nirmal.banking.dao.UserBankDetails;
import com.nirmal.banking.dto.UserBankDetailsDto;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.pojo.PaginationDetails;
import com.nirmal.banking.recipt.WithdrawReceipt;
import com.nirmal.banking.repository.TransactionRepo;
import com.nirmal.banking.repository.UserBankDetailsRepo;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserDetailsRepo userDetailsRepo;

    private final TransactionRepo transactionRepo;

    private final UserBankDetailsRepo userBankDetailsRepo;

    private final PasswordEncoder passwordEncoder;

    private final AdminSettingsService adminSettingsService;

    private final RoleService roleService;

    @Value("${fileStorage}")
    protected String fileStoragePath;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        CustomUserDetails details = userDetailsRepo.findByUid(uid);
        List<GrantedAuthority> authorities = List.of((GrantedAuthority) () -> details.getUserRole().getRole().name());
        return new User(details.getUid(), details.getPassword(), authorities);
    }

    public UserDetailsDto addUser(UserDetailsDto userDetailsDto) {
        if (userExist(userDetailsDto.getUsername())) {
            throw new CustomException(ErrorMessages.USERNAME_EXIST);
        }

        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanUtils.copyProperties(userDetailsDto, customUserDetails);

        customUserDetails.setUserRole(roleService.getRole(Role.USER));
        customUserDetails.setPassword(passwordEncoder.encode(userDetailsDto.getPassword()));
        customUserDetails.setUid(UUID.randomUUID().toString());
        customUserDetails.setKycStatus(KycStatus.PENDING);
        customUserDetails.setInitiatedAt(System.currentTimeMillis());
        userDetailsRepo.save(customUserDetails);

        BeanUtils.copyProperties(customUserDetails, userDetailsDto);
        return userDetailsDto;
    }

    public String depositAmount(String uid, Double depositAmount) {
        CustomUserDetails customUserDetails = userDetailsRepo.findByUid(uid);

        //Verifying the user is approved by The Admin.
        if (!customUserDetails.getKycStatus().equals(KycStatus.APPROVED))
            throw new CustomException(ErrorMessages.KYC_NOT_APPROVED);

        //Initial deposit amount.
        if (!transactionRepo.existsByUid(uid))
            if (depositAmount < 1000) throw new CustomException(ErrorMessages.MINIMUM_DEPOSIT_AMOUNT);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .uid(uid)
                .amount(depositAmount)
                .transactionId(UUID.randomUUID().toString())
                .initiatedAt(System.currentTimeMillis())
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.PENDING)
                .totalAmount(totalAmount(uid))
                .withdrawFee(0D)
                .withdrawFeePercentage(0D)
                .build();
        transactionRepo.save(transactionDetails);
        return depositAmount + SuccessMessages.AMOUNT_CREDITED;
    }

    public WithdrawReceipt withdrawAmount(String uid, Double debitedAmount) {
        CustomUserDetails customUserDetails = userDetailsRepo.findByUid(uid);

        if (!customUserDetails.getKycStatus().equals(KycStatus.APPROVED))
            throw new CustomException(ErrorMessages.KYC_NOT_APPROVED);

        if (debitedAmount > adminSettingsService.adminSettingsDetails().getWithdrawLimit())
            throw new CustomException(ErrorMessages.WITHDRAW_LIMIT_REACHED);

        if (debitedAmount > totalAmount(uid))
            throw new CustomException(ErrorMessages.INSUFFICIENT_BALANCE);

        if (debitedAmount + withdrawLimitPerDay(uid) > adminSettingsService.adminSettingsDetails().getWithdrawLimitPerDay())
            throw new CustomException(ErrorMessages.WITHDRAW_LIMIT_PER_DAY);

        TransactionDetails transactionDetails = TransactionDetails.builder()
                .uid(uid)
                .amount(debitedAmount)
                .transactionId(UUID.randomUUID().toString())
                .initiatedAt(System.currentTimeMillis())
                .transactionType(TransactionType.WITHDRAW)
                .transactionStatus(TransactionStatus.PENDING)
                .totalAmount(totalAmount(uid) - debitedAmount - withdrawFeeAmount((debitedAmount)))
                .withdrawFee(withdrawFeeAmount(debitedAmount))
                .withdrawFeePercentage(withdrawFeePercentage())
                .build();
        transactionRepo.save(transactionDetails);
        return new WithdrawReceipt(
                debitedAmount - withdrawFeeAmount(debitedAmount),
                withdrawFeePercentage(),
                +withdrawFeeAmount(debitedAmount));
    }

    private Double withdrawFeePercentage() {
        return adminSettingsService.adminSettingsDetails().getWithdrawFeePercentage();
    }

    private Double withdrawFeeAmount(Double debitedAmount) {
        return percentageCalculator(debitedAmount, withdrawFeePercentage());
    }

    private Double percentageCalculator(Double debitedAmount, Double withdrawFeePercentage) {
        return debitedAmount * (withdrawFeePercentage / 100);

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

    Double withdrawLimitPerDay(String uid) {
        Long oneDay = 86400000L;
        return transactionRepo.findAllByUidAndInitiatedAtBetweenAndTransactionStatusNot(uid,
                        System.currentTimeMillis() - oneDay, System.currentTimeMillis(),
                        TransactionStatus.REJECTED)
                .stream()
                .mapToDouble(details -> details.getTransactionType() == TransactionType.WITHDRAW ?
                        details.getAmount() : 0)
                .sum();
    }

    public Double amountBalance(String uid) {
        return totalAmount(uid);
    }

    public PaginationDetails ePassbook(Date dateFrom, Date dateTo, Integer pageSize, Integer pageNo, String uid) {
        List<TransactionDetails> transactionDetailsList;
        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<TransactionDetails> transactionDetailsPage = transactionRepo.findAllByUidAndInitiatedAtBetween(uid,
                dateFrom.getTime(),
                dateTo.getTime(),
                paging);
        transactionDetailsList = transactionDetailsPage.getContent();

        return new PaginationDetails(transactionDetailsList, transactionDetailsPage.getNumber(),
                transactionDetailsPage.getTotalElements(),
                transactionDetailsPage.getTotalPages());
    }

    public String uploadImage(MultipartFile[] file, String uid) throws IOException {
        String filePath = fileStoragePath + File.separator + uid;
        File folder = new File(filePath);
        folder.mkdir();

        convertMultipartFileToFile(file, filePath);

        CustomUserDetails customUserDetails = userDetailsRepo.findByUid(uid);
        customUserDetails.setKycStatus(KycStatus.PENDING);
        userDetailsRepo.save(customUserDetails);
        return SuccessMessages.UPLOADED;
    }

    void convertMultipartFileToFile(MultipartFile[] multipartFiles, String filePath) throws IOException {
        File aadhaarFile = new File(filePath + File.separator + FileType.AADHAAR);
        multipartFiles[0].transferTo(aadhaarFile);

        File panFile = new File(filePath + File.separator + FileType.PAN);
        multipartFiles[1].transferTo(panFile);
    }

    public String findByName(String username) {
        if (userExist(username)) {
            CustomUserDetails customUserDetails = userDetailsRepo.findByUsername(username);
            return customUserDetails.getUid();
        } else {
            throw new CustomException(ErrorMessages.USER_NOT_REGISTERED);
        }
    }

    boolean userExist(String username) {
        return userDetailsRepo.existsByUsername(username);
    }

    public String addBankDetails(UserBankDetailsDto userBankDetailsDto, String uid) {
        UserBankDetails userBankDetails = UserBankDetails.builder()
                .uid(uid)
                .ifscCode(userBankDetailsDto.getIfscCode())
                .accountNumber(userBankDetailsDto.getAccountNumber())
                .initiatedAt(System.currentTimeMillis())
                .build();
        userBankDetailsRepo.save(userBankDetails);
        return SuccessMessages.BANK_ACCOUNT_ADDED;
    }
}
