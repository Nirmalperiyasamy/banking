package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.common.SuccessMessages;
import com.nirmal.banking.dao.AdminSettings;
import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dao.TransactionDetails;
import com.nirmal.banking.dao.UserBankDetails;
import com.nirmal.banking.dto.UserBankDetailsDto;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.interceptor.JwtUtil;
import com.nirmal.banking.repository.AdminSettingsRepo;
import com.nirmal.banking.repository.TransactionRepo;
import com.nirmal.banking.repository.UserBankDetailsRepo;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserDetailsRepo userDetailsRepo;

    private final TransactionRepo transactionRepo;

    private final UserBankDetailsRepo userBankDetailsRepo;

    private final AdminSettingsRepo adminSettingsRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

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

    public String uploadImage(MultipartFile[] file, HttpServletRequest request) throws IOException {
        String uid = extractUid(request);
        String filePath = fileStoragePath + File.separator + uid;
        File folder = new File(filePath);
        folder.mkdir();

        convertMultipartFileToFile(file, filePath);

        CustomUserDetails customUserDetails = userDetailsRepo.findByUid(uid);
        customUserDetails.setKycStatus(KycStatus.PENDING);
        userDetailsRepo.save(customUserDetails);
        return SuccessMessages.UPLOADED;
    }

    public String depositAmount(HttpServletRequest request, Integer depositAmount) {
        String uid = extractUid(request);
        CustomUserDetails customUserDetails = userDetailsRepo.findByUid(uid);

        //Verifying the user is approved by The Admin.
        if (!customUserDetails.getKycStatus().equals(KycStatus.APPROVED))
            throw new CustomException(ErrorMessages.KYC_NOT_APPROVED);

        //Initial deposit amount.
        if (!transactionRepo.existsByUid(uid))
            if (depositAmount < 1000) throw new CustomException(ErrorMessages.MINIMUM_DEPOSIT_AMOUNT);

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setUid(uid);
        transactionDetails.setTransactionId(UUID.randomUUID().toString());
        transactionDetails.setAmount(depositAmount);
        transactionDetails.setTotalAmount(totalAmount(uid) + depositAmount);
        transactionDetails.setTransactionType(TransactionType.DEPOSIT);
        transactionDetails.setTransactionStatus(TransactionStatus.APPROVED);
        transactionDetails.setInitiatedAt(System.currentTimeMillis());
        transactionDetails.setWithdrawFeePercentage(0D);
        transactionDetails.setWithdrawFee(0D);
        transactionRepo.save(transactionDetails);
        return depositAmount + SuccessMessages.AMOUNT_CREDITED;
    }

    public String withdrawAmount(HttpServletRequest request, Integer debitedAmount) {
        String uid = extractUid(request);
        CustomUserDetails customUserDetails = userDetailsRepo.findByUid(uid);

        if (!customUserDetails.getKycStatus().equals(KycStatus.APPROVED))
            throw new CustomException(ErrorMessages.KYC_NOT_APPROVED);

        if (debitedAmount > totalAmount(uid))
            throw new CustomException(ErrorMessages.INSUFFICIENT_BALANCE);

        TransactionDetails transactionDetails = new TransactionDetails();
        transactionDetails.setUid(uid);
        transactionDetails.setTransactionId(UUID.randomUUID().toString());
        transactionDetails.setAmount(debitedAmount);
        transactionDetails.setTotalAmount(totalAmount(uid) - debitedAmount - WithdrawFeeAmount(debitedAmount));
        transactionDetails.setTransactionType(TransactionType.WITHDRAW);
        transactionDetails.setTransactionStatus(TransactionStatus.PENDING);
        transactionDetails.setInitiatedAt(System.currentTimeMillis());
        transactionDetails.setWithdrawFeePercentage(withdrawFeePercentage());
        transactionDetails.setWithdrawFee(WithdrawFeeAmount(debitedAmount));
        transactionRepo.save(transactionDetails);
        return debitedAmount - WithdrawFeeAmount(debitedAmount) + SuccessMessages.AMOUNT_DEBITED
                + withdrawFeePercentage() + SuccessMessages.WITHDRAW_FEE_PERCENTAGE
                + WithdrawFeeAmount(debitedAmount) + SuccessMessages.WITHDRAW_FEE_AMOUNT;
    }


    private Double withdrawFeePercentage() {
        Optional<AdminSettings> adminService = adminSettingsRepo.findById(1);
        return adminService.get().getWithdrawFeePercentage();
    }

    private Double WithdrawFeeAmount(Integer debitedAmount) {
        return (debitedAmount * (withdrawFeePercentage() / 100));
    }

    Double totalAmount(String uid) {
        // TODO: Need to move summation to SQL
        return transactionRepo.totalAmount(uid).stream()
                .mapToDouble(amountDetail -> amountDetail.getTransactionType() == TransactionType.DEPOSIT ?
                        amountDetail.getAmount() : -amountDetail.getAmount())
                .sum();
    }

    public Double amountBalance(HttpServletRequest request) {
        String uid = extractUid(request);
        return totalAmount(uid);
    }

    private String extractUid(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        authorizationHeader = authorizationHeader.replace("Bearer", "");
        return jwtUtil.extractUsername(authorizationHeader.trim());
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

    public String addBankDetails(UserBankDetailsDto userBankDetailsDto, HttpServletRequest request) {
        String uid = extractUid(request);
        UserBankDetails userBankDetails = new UserBankDetails();
        userBankDetails.setUid(uid);
        userBankDetails.setIfscCode(userBankDetailsDto.getIfscCode());
        userBankDetails.setAccountNumber(userBankDetailsDto.getAccountNumber());
        userBankDetails.setInitiatedAt(System.currentTimeMillis());
        userBankDetailsRepo.save(userBankDetails);
        return SuccessMessages.BANK_ACCOUNT_ADDED;
    }
}