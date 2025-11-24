package com.wansenai.bo.financial;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdvanceChargeExportBO {

    private Long id;

    @ExcelExport(value = "会员")
    private String memberName;

    @ExcelExport(value = "单据编号")
    private String receiptNumber;

    @ExcelExport(value = "单据日期")
    private LocalDateTime receiptDate;

    @ExcelExport(value = "收款金额")
    private BigDecimal collectedAmount;

    @ExcelExport(value = "合计金额")
    private BigDecimal totalAmount;

    @ExcelExport(value = "财务人员")
    private String financialPersonnel;

    @ExcelExport(value = "操作员")
    private String operator;

    @ExcelExport(value = "备注")
    private String remark;

    @ExcelExport(value = "状态", kv = "0:未审核;1:已审核")
    private Integer status;
}
