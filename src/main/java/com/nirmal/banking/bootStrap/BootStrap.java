package com.nirmal.banking.bootStrap;

import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.repository.RoleRepo;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.utils.Gender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class BootStrap {

    private final RoleRepo roleRepo;

    private final UserDetailsRepo userDetailsRepo;

    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    private void postConstructor() {
        if (!isUsernameExist()) {

            UserDetailsDto userDetailsDto = UserDetailsDto.builder()
                    .userName("Nirmal")
                    .age(22)
                    .mobileNumber("8015277132")
                    .email("nirmal@gmail.com")
                    .password(passwordEncoder.encode("1234"))
                    .gender(Gender.MALE)
                    .aadhaar("12345678983")
                    .userRole(roleRepo.findById(1).get())
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails();
            BeanUtils.copyProperties(userDetailsDto, customUserDetails);
            userDetailsRepo.save(customUserDetails);

        }
    }

    private boolean isUsernameExist() {
        return userDetailsRepo.existsByuserName("Nirmal");
    }
}
