package com.wansenai.dto.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuerySupplierDTO {

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 联系人
     */
    private String contact;

    /**
     * 联系电话
     */
    private String contactNumber;

    /**
     * 手机号码
     */
    private String phoneNumber;

    /**
     * 分页信息
     */
    private Long page;

    private Long pageSize;

    private String startDate;

    private String endDate;
}