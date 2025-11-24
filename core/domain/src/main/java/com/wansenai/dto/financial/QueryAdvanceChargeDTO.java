package com.wansenai.dto.financial;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryAdvanceChargeDTO {

    private String receiptNumber;

    private Long memberId;

    private Long operatorId;

    private Long financialPersonnelId;

    private Integer status;

    private String remark;

    private Long page;

    private Long pageSize;

    private String startDate;

    private String endDate;

    private Boolean isExportDetail;
}