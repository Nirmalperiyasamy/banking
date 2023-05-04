package com.nirmal.banking.dto;

import com.nirmal.banking.utils.TransactionStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class TransactionDecisionByAdmin {

    String transactionId;

    TransactionStatus decision;
}
