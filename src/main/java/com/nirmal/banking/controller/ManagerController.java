package com.nirmal.banking.controller;

import com.nirmal.banking.dao.CustomUserDetails;
import com.nirmal.banking.response.CustomResponse;
import com.nirmal.banking.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.nirmal.banking.common.Routes.ALL_USER;
import static com.nirmal.banking.common.Routes.MANAGER;
import static com.nirmal.banking.response.CustomResponse.success;

@RestController
@RequiredArgsConstructor
@RequestMapping(MANAGER)
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping(ALL_USER)
    public CustomResponse<List<CustomUserDetails>> allUser() {
        return success(managerService.allUser());
    }
}
