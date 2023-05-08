package com.nirmal.banking.recipt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawReceipt {

    private double amountDebited;

    private double feePercentage;

    private double feeAmount;

    public WithdrawReceipt(double amountDebited, double feePercentage, double feeAmount) {
        this.amountDebited = amountDebited;
        this.feePercentage = feePercentage;
        this.feeAmount = feeAmount;
    }
}
