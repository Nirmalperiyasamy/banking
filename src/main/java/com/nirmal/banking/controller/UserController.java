package com.nirmal.banking.controller;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.exception.CustomException;
import com.nirmal.banking.interceptor.JwtUtil;
import com.nirmal.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.io.IOException;

import static com.nirmal.banking.common.Const.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(USER)
public class UserController {

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @PostMapping(ADD_USER)
    private ResponseEntity<?> addUser(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        return ResponseEntity.ok(userService.addUser(userDetailsDto));
    }

    @PostMapping(TOKEN)
    private ResponseEntity<?> token(@RequestBody UserDetailsDto userDetailsDto) {
        String uid = userService.findByName(userDetailsDto.getUsername());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(uid, userDetailsDto.getPassword()));
        } catch (Exception e) {
            throw new CustomException(ErrorMessages.INVALID);
        }
        return ResponseEntity.ok(jwtUtil.generateToken(uid));
    }

    @PostMapping(UPLOAD_IMAGES)
    private ResponseEntity<?> uploadImages(@RequestBody MultipartFile[] file, HttpServletRequest request) throws IOException {
        return ResponseEntity.ok(userService.uploadImage(file, request));
    }
}
