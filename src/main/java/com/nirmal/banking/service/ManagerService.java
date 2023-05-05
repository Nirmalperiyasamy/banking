package com.nirmal.banking.service;

import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.repository.UserDetailsRepo;
import com.nirmal.banking.response.CustomResponse;
import com.nirmal.banking.utils.CustomStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final UserDetailsRepo userDetailsRepo;

    public CustomResponse<List<CustomUserDetails>> allUser() {
        return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, userDetailsRepo.findAll());
    }
}
