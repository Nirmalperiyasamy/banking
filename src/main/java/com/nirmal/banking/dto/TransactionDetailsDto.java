package com.nirmal.banking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class TransactionDetailsDto {

    private Integer id;

    private String uid;

    private Integer amount;

}
