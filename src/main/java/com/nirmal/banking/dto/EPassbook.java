package com.nirmal.banking.dto;

import com.nirmal.banking.common.ErrorMessages;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@Getter
@Setter
public class EPassbook {
    @NotNull
    private Long days;
}
