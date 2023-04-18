package com.nirmal.banking.dao;

import com.nirmal.banking.utils.TransactionType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class TransactionDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String uid;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Long totalAmount;

    public TransactionDetails(Integer amount, TransactionType transactionType) {
        this.amount = amount;
        this.transactionType = transactionType;
    }
}