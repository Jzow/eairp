package com.wansenai.bo.financial;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdvanceChargeExportEnBO {

    private Long id;

    @ExcelExport(value = "Member")
    private String memberName;

    @ExcelExport(value = "Receipt Number")
    private String receiptNumber;

    @ExcelExport(value = "Receipt Date")
    private LocalDateTime receiptDate;

    @ExcelExport(value = "Collected Amount")
    private BigDecimal collectedAmount;

    @ExcelExport(value = "Total Amount")
    private BigDecimal totalAmount;

    @ExcelExport(value = "Financial Personnel")
    private String financialPersonnel;

    @ExcelExport(value = "Operator")
    private String operator;

    @ExcelExport(value = "Remark")
    private String remark;

    @ExcelExport(value = "Status", kv = "0-Unaudited;1-Audited;")
    private Integer status;
}