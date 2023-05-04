package com.nirmal.banking.dao;

import com.nirmal.banking.utils.TransactionStatus;
import com.nirmal.banking.utils.TransactionType;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String uid;

    private String transactionId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private Double totalAmount;

    private Long initiatedAt;

    private Double withdrawFeePercentage;

    private Double withdrawFee;

    public TransactionDetails(Double amount, TransactionType transactionType, TransactionStatus transactionStatus) {
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionStatus = transactionStatus;
    }
}
