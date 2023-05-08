package com.nirmal.banking.controller;

import com.nirmal.banking.dto.TransactionDetailsDto;
import com.nirmal.banking.dto.UserBankDetailsDto;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.interceptor.JwtUtil;
import com.nirmal.banking.response.CustomResponse;
import com.nirmal.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;

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
    private CustomResponse<?> ePassbook(@RequestParam("dateFrom") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
                                        @RequestParam("dateTo") @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
                                        @RequestParam(value = "pageSize", defaultValue = "3") int pageSize,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        HttpServletRequest request) {
        return CustomResponse.success(userService.ePassbook(dateFrom, dateTo, pageSize, pageNum, extractUid(request)));
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
