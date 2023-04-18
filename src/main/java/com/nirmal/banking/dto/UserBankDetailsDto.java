package com.nirmal.banking.dto;

import com.nirmal.banking.common.ErrorMessages;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Getter
@Setter
public class UserBankDetailsDto {

    @NotNull(message = ErrorMessages.BANK_ACCOUNT_NUMBER)
    @Pattern(regexp = "(^$|[0-9]{11,18})", message = ErrorMessages.BANK_ACCOUNT_NUMBER)
    private String accountNumber;

    @NotNull(message = ErrorMessages.IFSC_CODE)
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = ErrorMessages.IFSC_CODE)
    private String ifscCode;
}
