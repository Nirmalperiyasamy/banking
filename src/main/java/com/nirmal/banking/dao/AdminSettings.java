package com.nirmal.banking.dao;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Getter
public class AdminSettings {

    @Id
    private Integer id;

    @Setter
    private Double withdrawFeePercentage;
}
