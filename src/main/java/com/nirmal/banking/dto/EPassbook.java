package com.nirmal.banking.dto;

import com.nirmal.banking.common.ErrorMessages;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@Getter
@Setter
public class EPassbook {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateTo;

    private Integer pageNo;

    private Integer size;
}
