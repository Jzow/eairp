package com.wansenai.dto.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSupplierDTO {

    /**
     * 供应商ID
     */
    private Long id;

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
     * 地址
     */
    private String address;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 第一季度应付账款
     */
    private Double firstQuarterAccountPayment;

    /**
     * 第二季度应付账款
     */
    private Double secondQuarterAccountPayment;

    /**
     * 第三季度应付账款
     */
    private Double thirdQuarterAccountPayment;

    /**
     * 第四季度应付账款
     */
    private Double fourthQuarterAccountPayment;

    /**
     * 应付账款总额
     */
    private BigDecimal totalAccountPayment;

    /**
     * 传真
     */
    private String fax;

    /**
     * 税号
     */
    private String taxNumber;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 账号
     */
    private Long accountNumber;

    /**
     * 税率
     */
    private Integer taxRate;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
