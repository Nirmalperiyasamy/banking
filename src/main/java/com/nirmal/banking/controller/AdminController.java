package com.nirmal.banking.controller;

import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.nirmal.banking.common.Const.ADD_MANAGER;
import static com.nirmal.banking.common.Const.ADMIN;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN)
public class AdminController {

    private final AdminService adminService;

    @PostMapping(ADD_MANAGER)
    public ResponseEntity<?> addManager(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        return ResponseEntity.ok(adminService.addManager(userDetailsDto));
    }

}
