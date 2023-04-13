package com.nirmal.banking.bootStrap;

import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.service.RoleService;
import com.nirmal.banking.utils.KycStatus;
import com.nirmal.banking.utils.Gender;
import com.nirmal.banking.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BootStrap {

    private final RoleService roleService;

    private final UserDetailsRepo userDetailsRepo;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void postConstructor() {

        if (!isUsernameExist()) {
            UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                    .username("Nirmal")
                    .age(22)
                    .mobileNumber("8015277132")
                    .email("nirmal@gmail.com")
                    .password(passwordEncoder.encode("1234"))
                    .gender(Gender.MALE)
                    .aadhaar("12345678983")
                    .uid(String.valueOf(UUID.randomUUID()))
                    .userRole(roleService.getRole(Role.ADMIN))
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails();
            BeanUtils.copyProperties(userDetailsDto, customUserDetails);
            customUserDetails.setKycStatus(KycStatus.APPROVED);
            userDetailsRepo.save(customUserDetails);
        }
    }

    private boolean isUsernameExist() {
        return userDetailsRepo.existsByusername("Nirmal");
    }
}
