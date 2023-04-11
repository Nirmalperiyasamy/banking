package com.nirmal.banking.service;

import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.repository.UserDetailsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final UserDetailsRepo userDetailsRepo;

    public List<CustomUserDetails> allUser() {
        return userDetailsRepo.findAll();
    }
}
