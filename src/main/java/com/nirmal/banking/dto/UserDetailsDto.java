package com.nirmal.banking.dto;

import com.nirmal.banking.common.ErrorMessages;
import com.nirmal.banking.dao.UserRole;
import com.nirmal.banking.utils.Gender;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
@Builder
public class UserDetailsDto {

    private int id;

    @NotNull(message = ErrorMessages.USER_NAME)
    private String userName;

    @NotNull(message = ErrorMessages.MOBILE_NUMBER)
    @Pattern(regexp = "(^$|[0-9]{10})", message = ErrorMessages.MOBILE_NUMBER)
    private String mobileNumber;

    @NotNull(message = ErrorMessages.EMAIL)
    @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}",
            flags = Pattern.Flag.CASE_INSENSITIVE, message = ErrorMessages.EMAIL)
    private String email;

    @NotNull(message = ErrorMessages.PASSWORD)
    @Size(min = 4, message = ErrorMessages.PASSWORD)
    private String password;

    @NotNull(message = ErrorMessages.AGE)
    private Integer age;

    @NotNull(message = ErrorMessages.AADHAAR)
    @Pattern(regexp = "(^$|[0-9]{12})",message = ErrorMessages.AADHAAR)
    private String aadhaar;

    @NotNull(message = ErrorMessages.GENDER)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private UserRole userRole;
}
