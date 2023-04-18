package com.nirmal.banking.controller;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.interceptor.JwtUtil;
import com.nirmal.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

import static com.nirmal.banking.common.Const.LOGIN;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @PostMapping(LOGIN)
    private ResponseEntity<?> token(@RequestBody UserDetailsDto userDetailsDto, HttpServletResponse response) {
        try {
            String uid = userService.findByName(userDetailsDto.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(uid, userDetailsDto.getPassword()));
            String token = jwtUtil.generateToken(uid);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            throw new CustomException(ErrorMessages.INVALID);
        }
    }
}
