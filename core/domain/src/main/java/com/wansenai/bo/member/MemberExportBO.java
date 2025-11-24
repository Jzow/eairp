package com.wansenai.bo.member;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MemberExportBO {

    private Long id;

    @ExcelExport(value = "会原卡号", sort = 1)
    private String memberNumber;

    @ExcelExport(value = "会员名称", sort = 2)
    private String memberName;

    @ExcelExport(value = "手机号码", sort = 3)
    private String phoneNumber;

    @ExcelExport(value = "电子邮箱", sort = 4)
    private String email;

    @ExcelExport(value = "预付款", sort = 5)
    private BigDecimal advancePayment;

    @ExcelExport(value = "状态", kv = "0-启用;1-停用", sort = 6)
    private Integer status;

    @ExcelExport(value = "备注", sort = 7)
    private String remark;

    private Integer sort;

    private LocalDateTime createTime;
}