package com.nirmal.banking.pojo;

import com.nirmal.banking.dao.TransactionDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationDetails {

    private List<TransactionDetails> transactionDetailsList;
    private int currentPage;
    private long totalItems;
    private int totalPages;

    public PaginationDetails(List<TransactionDetails> transactionDetailsList, int number, long totalElements, int totalPages) {
        this.transactionDetailsList = transactionDetailsList;
        this.currentPage = number;
        this.totalItems = totalElements;
        this.totalPages = totalPages;
    }
}
