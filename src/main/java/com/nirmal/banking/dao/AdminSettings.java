package com.nirmal.banking.dao;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
@Setter
@Getter
public class AdminSettings {
    @Id
    private Integer id;

    private Double withdrawFeePercentage;
}
