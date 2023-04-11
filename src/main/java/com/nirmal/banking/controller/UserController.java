package com.nirmal.banking.controller;

import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.nirmal.banking.common.Const.ADD_USER;
import static com.nirmal.banking.common.Const.USER;

@RestController
@RequiredArgsConstructor
@RequestMapping(USER)
public class UserController {

    private final UserService userService;


    @PostMapping(ADD_USER)
    private ResponseEntity<?> addUser(@Valid @RequestBody UserDetailsDto userDetailsDto){
        return ResponseEntity.ok(userService.addUser(userDetailsDto));
    }
}
