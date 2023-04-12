package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.repository.RoleRepo;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.utils.ImageStatus;
import com.nirmal.banking.utils.ImageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserDetailsRepo userDetailsRepo;

    private final RoleRepo roleRepo;

    private final PasswordEncoder passwordEncoder;

    public CustomUserDetails addManager(UserDetailsDto userDetailsDto) {
        CustomUserDetails customUserDetails = new CustomUserDetails();
        BeanUtils.copyProperties(userDetailsDto, customUserDetails);
        customUserDetails.setPassword(passwordEncoder.encode(userDetailsDto.getPassword()));
        customUserDetails.setUserRole(roleRepo.findById(2).get());
        userDetailsRepo.save(customUserDetails);
        return customUserDetails;
    }

    public byte[] downloadImages(String username) {
        CustomUserDetails customUserDetails = userDetailsRepo.findByuserName(username);
        byte[] images = ImageUtil.decompressImage(customUserDetails.getImageData());
        customUserDetails.setImageStatus(ImageStatus.PENDING);
        userDetailsRepo.save(customUserDetails);
        return images;
    }

    public String approvedRejected(String username, String status) {

        if (status.equals("1")) {
            CustomUserDetails customUserDetails = userDetailsRepo.findByuserName(username);
            customUserDetails.setImageStatus(ImageStatus.APPROVED);
            userDetailsRepo.save(customUserDetails);
            return "User " + username + ImageStatus.APPROVED;
        } else if (status.equals("0")) {
            CustomUserDetails customUserDetails = userDetailsRepo.findByuserName(username);
            customUserDetails.setImageStatus(ImageStatus.REJECTED);
            userDetailsRepo.save(customUserDetails);
            return "User " + username + ImageStatus.REJECTED;
        } else {
            throw new CustomException(ErrorMessages.STATUS_ERROR);
        }
    }
}
