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
public class AddOrUpdateMemberDTO {

    private Long id;

    private String memberNumber;

    private String memberName;

    private String phoneNumber;

    private String email;

    private BigDecimal advancePayment;

    private Integer status;

    private String remark;

    private Integer sort;
}