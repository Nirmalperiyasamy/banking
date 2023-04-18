package com.nirmal.banking.controller;

import com.nirmal.banking.dto.TransactionDetailsDto;
import com.nirmal.banking.dto.UserBankDetailsDto;
import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping(ADD_USER)
    private ResponseEntity<?> addUser(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        return ResponseEntity.ok(userService.addUser(userDetailsDto));
    }

    @PostMapping(DEPOSIT)
    private ResponseEntity<?> deposit(@RequestBody TransactionDetailsDto transactionDetailsDto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.depositAmount(request, transactionDetailsDto.getAmount()));
    }

    @PostMapping(ADD_BANK_DETAILS)
    private ResponseEntity<?> addBank(@Valid @RequestBody UserBankDetailsDto userBankDetailsDto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.addBankDetails(userBankDetailsDto,request));
    }

    @PostMapping(WITHDRAW)
    private ResponseEntity<?> debit(@RequestBody TransactionDetailsDto transactionDetailsDto, HttpServletRequest request) {
        return ResponseEntity.ok(userService.withdrawAmount(request, transactionDetailsDto.getAmount()));
    }

    @GetMapping(BALANCE)
    private ResponseEntity<?> amountBalance(HttpServletRequest request) {
        return ResponseEntity.ok(userService.amountBalance(request));
    }

    @PostMapping(UPLOAD_IMAGES)
    private ResponseEntity<?> uploadImages(@RequestBody MultipartFile[] files, HttpServletRequest request) throws IOException {
        return ResponseEntity.ok(userService.uploadImage(files, request));
    }
}
