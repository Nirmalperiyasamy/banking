package com.nirmal.banking.dao;

import com.nirmal.banking.utils.Gender;
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

    private String userName;

    private String mobileNumber;

    private String email;

    private String password;

    private Integer age;

    private String aadhaar;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @OneToOne
    private UserRole userRole;
}