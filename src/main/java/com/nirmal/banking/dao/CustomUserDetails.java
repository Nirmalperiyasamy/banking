package com.nirmal.banking.dao;

import com.nirmal.banking.utils.Gender;
import com.nirmal.banking.utils.KycStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
public class CustomUserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String username;

    private String mobileNumber;

    private String email;

    private String password;

    private Integer age;

    private String aadhaar;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private KycStatus kycStatus;

    private String uid;

    @OneToOne
    private UserRole userRole;
}
