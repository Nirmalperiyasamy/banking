package com.nirmal.banking.exception;

import java.util.function.Supplier;

public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }

}
