package com.wansenai.dto.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateCustomerDTO {

    private Long id;

    private String customerName;

    private String contact;

    private String phoneNumber;

    private String email;

    private BigDecimal firstQuarterAccountReceivable;

    private BigDecimal secondQuarterAccountReceivable;

    private BigDecimal thirdQuarterAccountReceivable;

    private BigDecimal fourthQuarterAccountReceivable;

    private BigDecimal totalAccountReceivable;

    private String fax;

    private String address;

    private String taxNumber;

    private String bankName;

    private String accountNumber;

    private BigDecimal taxRate;

    private Integer status;

    private String remark;

    private Integer sort;
}