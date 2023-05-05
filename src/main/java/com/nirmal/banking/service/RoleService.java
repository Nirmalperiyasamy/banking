package com.nirmal.banking.service;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dao.UserRole;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.repository.RoleRepo;
import com.nirmal.banking.utils.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepo roleRepo;

    public UserRole getRole(Role manager) {

        switch (manager) {
            case ADMIN:
                return role(1);

            case MANAGER:
                return role(2);

            case USER:
                return role(3);

            default:
                throw new CustomException(ErrorMessages.ROLE_NOT_FOUND);
        }
    }

    public UserRole role(Integer number) {
        Optional<UserRole> userRole = roleRepo.findById(number);
        if (userRole.isEmpty()) throw new CustomException(ErrorMessages.ROLE_NOT_FOUND);
        return userRole.get();
    }
}
