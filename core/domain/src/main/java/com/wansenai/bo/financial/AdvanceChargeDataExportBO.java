package com.wansenai.bo.financial;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class AdvanceChargeDataExportBO {

    @ExcelExport(value = "会员")
    private String memberName;

    @ExcelExport(value = "预付款单据编号")
    private String receiptNumber;

    @ExcelExport(value = "账户名称")
    private String accountName;

    @ExcelExport(value = "金额")
    private BigDecimal amount;

    @ExcelExport(value = "备注")
    private String remark;
}
