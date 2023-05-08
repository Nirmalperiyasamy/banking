package com.nirmal.banking.response;

import com.nirmal.banking.utils.CustomStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomResponse<T> {

    private HttpStatus httpStatus;

    private CustomStatus customStatus;

    private T data;

    public CustomResponse() {

    }

    public static <T> CustomResponse<T> success(T data) {
        return new CustomResponse<>(HttpStatus.OK, CustomStatus.SUCCESS, data);
    }

    public static <T> CustomResponse<T> failure(T data) {
        return new CustomResponse<>(HttpStatus.BAD_REQUEST, CustomStatus.FAILURE, data);
    }

    public CustomResponse(HttpStatus httpStatus, CustomStatus customStatus, T data) {
        this.httpStatus = httpStatus;
        this.customStatus = customStatus;
        this.data = data;
    }
}
