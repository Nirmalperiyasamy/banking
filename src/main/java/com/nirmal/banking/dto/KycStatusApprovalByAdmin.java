package com.nirmal.banking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class KycStatusApprovalByAdmin {

    String uid;

    String status;
}
