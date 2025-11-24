package com.wansenai.dto.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryWarehouseDTO {

    private String warehouseName;

    private String remark;

    private Long page;

    private Long pageSize;

    private String startDate;

    private String endDate;
}