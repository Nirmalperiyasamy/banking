package com.nirmal.banking.controller;

import com.nirmal.banking.dto.EPassbook;
import com.nirmal.banking.dto.TransactionDetailsDto;
import com.nirmal.banking.dto.UserBankDetailsDto;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.interceptor.JwtUtil;
import com.nirmal.banking.response.CustomResponse;
import com.nirmal.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

import static com.nirmal.banking.common.Routes.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(USER)
public class UserController {

    private final UserService userService;

    private final JwtUtil jwtUtil;

    @PostMapping(ADD_USER)
    private CustomResponse<?> addUser(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        return CustomResponse.success(userService.addUser(userDetailsDto));
    }

    @PostMapping(DEPOSIT)
    private CustomResponse<?> deposit(@RequestBody TransactionDetailsDto transactionDetailsDto, HttpServletRequest request) {
        return CustomResponse.success(userService.depositAmount(extractUid(request), transactionDetailsDto.getAmount()));
    }

    @PostMapping(ADD_BANK_DETAILS)
    private CustomResponse<?> addBank(@Valid @RequestBody UserBankDetailsDto userBankDetailsDto, HttpServletRequest request) {
        return CustomResponse.success(userService.addBankDetails(userBankDetailsDto, extractUid(request)));
    }

    @PostMapping(WITHDRAW)
    private CustomResponse<?> debit(@RequestBody TransactionDetailsDto transactionDetailsDto, HttpServletRequest request) {
        return CustomResponse.success(userService.withdrawAmount(extractUid(request), transactionDetailsDto.getAmount()));
    }

    @GetMapping(BALANCE)
    private CustomResponse<?> amountBalance(HttpServletRequest request) {
        return CustomResponse.success(userService.amountBalance(extractUid(request)));
    }

    @GetMapping(E_PASSBOOK)
    private CustomResponse<?> ePassbook(@Valid @RequestBody EPassbook ePassbook, HttpServletRequest request) {
        return CustomResponse.success(userService.ePassbook(ePassbook, extractUid(request)));
    }

    @PostMapping(UPLOAD_IMAGES)
    private CustomResponse<?> uploadImages(@RequestBody MultipartFile[] files, HttpServletRequest request) throws IOException {
        return CustomResponse.success(userService.uploadImage(files, extractUid(request)));
    }

    private String extractUid(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        authorizationHeader = authorizationHeader.replace("Bearer", "");
        return jwtUtil.extractUsername(authorizationHeader.trim());
    }
}
