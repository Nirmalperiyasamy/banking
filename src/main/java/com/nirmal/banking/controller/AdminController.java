package com.nirmal.banking.controller;

import com.nirmal.banking.dto.UserDetailsDto;
import com.nirmal.banking.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import static com.nirmal.banking.common.Const.*;
import static com.nirmal.banking.common.ErrorMessages.STATUS_ERROR;

@RestController
@RequiredArgsConstructor
@RequestMapping(ADMIN)
public class AdminController {

    private final AdminService adminService;

    @PostMapping(ADD_MANAGER)
    private ResponseEntity<?> addManager(@Valid @RequestBody UserDetailsDto userDetailsDto) {
        return ResponseEntity.ok(adminService.addManager(userDetailsDto));
    }

    @GetMapping(DOWNLOAD_IMAGES)
    private ResponseEntity<?> downloadImages(@PathVariable String username) {
        byte[] imageData = adminService.downloadImages(username);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(imageData);
    }

    @GetMapping(APPROVED_REJECTED)
    private ResponseEntity<?> approved(@PathVariable("username") String username,
                                       @PathVariable("decision") @Size(min = 1, max = 1, message = STATUS_ERROR) String status) {

        return ResponseEntity.ok(adminService.approvedRejected(username, status));
    }
}
