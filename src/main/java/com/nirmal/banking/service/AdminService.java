package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.repository.RoleRepo;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.utils.KycStatus;
import com.nirmal.banking.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserDetailsRepo userDetailsRepo;

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

    public String approvedRejected(String username, String status) {

        KycStatus kycstatus = KycStatus.valueOf(status.toUpperCase());
        CustomUserDetails customUserDetails = userDetailsRepo.findByusername(username);

        switch (kycstatus) {
            case APPROVED:
                customUserDetails.setKycStatus(KycStatus.APPROVED);
                userDetailsRepo.save(customUserDetails);
                return "User " + username + KycStatus.APPROVED;

            case REJECTED:
                customUserDetails.setKycStatus(KycStatus.REJECTED);
                userDetailsRepo.save(customUserDetails);
                return "User " + username + KycStatus.REJECTED;

            default:
                throw new CustomException(ErrorMessages.STATUS_ERROR);
        }

    }
}
