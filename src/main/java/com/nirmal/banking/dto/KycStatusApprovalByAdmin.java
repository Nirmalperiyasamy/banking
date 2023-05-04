package com.nirmal.banking.dto;

import com.nirmal.banking.utils.KycStatus;
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

    KycStatus status;
}
