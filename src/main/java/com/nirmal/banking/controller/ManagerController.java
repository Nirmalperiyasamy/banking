package com.nirmal.banking.controller;

import com.nirmal.banking.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.nirmal.banking.common.Const.ALL_USER;
import static com.nirmal.banking.common.Const.MANAGER;

@RestController
@RequiredArgsConstructor
@RequestMapping(MANAGER)
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping(ALL_USER)
    public ResponseEntity<?> allUser() {
        return ResponseEntity.ok(managerService.allUser());
    }
}
