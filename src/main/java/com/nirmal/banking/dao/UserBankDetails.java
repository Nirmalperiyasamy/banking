package com.nirmal.banking.dao;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
public class UserBankDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String uid;

    private String accountNumber;

    private String ifscCode;

    private Long initiatedAt;
}
