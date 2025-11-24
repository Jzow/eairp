package com.wansenai.bo.financial;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AdvanceChargeDataExportEnBO {

    @ExcelExport(value = "Member")
    private String memberName;

    @ExcelExport(value = "Receipt Number")
    private String receiptNumber;

    @ExcelExport(value = "Account")
    private String accountName;

    @ExcelExport(value = "Amount")
    private BigDecimal amount;

    @ExcelExport(value = "Remark")
    private String remark;
}
