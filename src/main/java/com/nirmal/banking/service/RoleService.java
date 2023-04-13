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

    public UserRole getRole(Role manager) {

        switch (manager) {
            case ADMIN:
                return roleRepo.findById(1).get();

            case MANAGER:
                return roleRepo.findById(2).get();

            case USER:
                return roleRepo.findById(3).get();

            default:
                throw new CustomException(ErrorMessages.ROLE_NOT_FOUND);
        }
    }
}
