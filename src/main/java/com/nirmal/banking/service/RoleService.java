package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dao.UserRole;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.repository.RoleRepo;
import com.nirmal.banking.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepo roleRepo;

    public UserRole getRole(Role role) {
        return roleRepo.findByRole(role).orElseThrow(() -> new CustomException(ErrorMessages.ROLE_NOT_FOUND));
    }
}
