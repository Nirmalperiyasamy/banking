package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.common.SuccessMessages;
import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dao.TransactionDetails;
import com.nirmal.banking.dto.TransactionDetailsDto;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.interceptor.JwtUtil;
import com.nirmal.banking.repository.RoleRepo;
import com.nirmal.banking.repository.TransactionRepo;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.utils.FileType;
import com.nirmal.banking.utils.KycStatus;
import com.nirmal.banking.utils.Role;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserDetailsRepo userDetailsRepo;

    private final TransactionRepo transactionRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final RoleService roleService;

    @Value("${fileStorage}")
    protected String fileStoragePath;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        CustomUserDetails details = userDetailsRepo.findByuid(uid);
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
        customUserDetails.setKycStatus(KycStatus.APPROVED);
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

        CustomUserDetails customUserDetails = userDetailsRepo.findByuid(uid);
        customUserDetails.setKycStatus(KycStatus.PENDING);
        userDetailsRepo.save(customUserDetails);
        return SuccessMessages.UPLOADED;
    }

    public String depositAmount(HttpServletRequest request, Integer depositAmount) {
        String uid = extractUid(request);
        CustomUserDetails customUserDetails = userDetailsRepo.findByuid(uid);
        if (!customUserDetails.getKycStatus().equals(KycStatus.APPROVED)) {
            throw new CustomException(ErrorMessages.KYC_NOT_APPROVED);
        }
        if (!transactionRepo.existsByUid(uid)) {
            if (depositAmount < 1000) {
                throw new CustomException(ErrorMessages.MINIMUM_DEPOSIT_AMOUNT);
            }
        }
        TransactionDetails transactionDetails = new TransactionDetails();
        TransactionDetailsDto transactionDetailsDto = TransactionDetailsDto.builder()
                .uid(uid)
                .amount(depositAmount)
                .build();
        BeanUtils.copyProperties(transactionDetailsDto, transactionDetails);
        transactionRepo.save(transactionDetails);
        transactionDetails.setTotalAmount(transactionRepo.getTotalAmountByUid(uid));
        transactionRepo.save(transactionDetails);
        return depositAmount.toString() + SuccessMessages.AMOUNT_CREDITED;
    }

    public String debitedAmount(HttpServletRequest request, Integer debitedAmount) {
        String uid = extractUid(request);
        CustomUserDetails customUserDetails = userDetailsRepo.findByuid(uid);
        if (!customUserDetails.getKycStatus().equals(KycStatus.APPROVED)) {
            throw new CustomException(ErrorMessages.KYC_NOT_APPROVED);
        }
        if (debitedAmount > transactionRepo.getTotalAmountByUid(uid)) {
            throw new CustomException(ErrorMessages.INSUFFICIENT_BALANCE);
        }
        TransactionDetails transactionDetails = new TransactionDetails();
        TransactionDetailsDto transactionDetailsDto = TransactionDetailsDto.builder()
                .uid(uid)
                .amount(-debitedAmount)
                .build();
        BeanUtils.copyProperties(transactionDetailsDto, transactionDetails);
        transactionRepo.save(transactionDetails);
        transactionDetails.setTotalAmount(transactionRepo.getTotalAmountByUid(uid));
        transactionRepo.save(transactionDetails);
        return debitedAmount.toString() + SuccessMessages.AMOUNT_DEBITED;
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
            CustomUserDetails customUserDetails = userDetailsRepo.findByusername(username);
            return customUserDetails.getUid();
        } else {
            throw new CustomException(ErrorMessages.USER_NOT_REGISTERED);
        }
    }

    boolean userExist(String username) {
        return userDetailsRepo.existsByusername(username);
    }

    public Long amountBalance(HttpServletRequest request) {
        String uid = extractUid(request);
        return transactionRepo.getTotalAmountByUid(uid);
    }
}