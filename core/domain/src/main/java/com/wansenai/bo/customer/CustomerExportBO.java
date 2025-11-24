package com.wansenai.bo.customer;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CustomerExportBO {

    private Long id;

    @ExcelExport(value = "客户", sort = 1)
    private String customerName;

    @ExcelExport(value = "联系人", sort = 2)
    private String contact;

    @ExcelExport(value = "手机号码", sort = 3)
    private String phoneNumber;

    @ExcelExport(value = "电子邮箱", sort = 4)
    private String email;

    @ExcelExport(value = "传真", sort = 5)
    private String fax;

    @ExcelExport(value = "一季度收款", sort = 8)
    private BigDecimal firstQuarterAccountReceivable;

    @ExcelExport(value = "二季度收款", sort = 9)
    private BigDecimal secondQuarterAccountReceivable;

    @ExcelExport(value = "三季度收款", sort = 10)
    private BigDecimal thirdQuarterAccountReceivable;

    @ExcelExport(value = "四季度收款", sort = 11)
    private BigDecimal fourthQuarterAccountReceivable;

    @ExcelExport(value = "累计应收账款", sort = 12)
    private BigDecimal totalAccountReceivable;

    @ExcelExport(value = "地址", sort = 6)
    private String address;

    @ExcelExport(value = "纳税人识别号", sort = 13)
    private String taxNumber;

    @ExcelExport(value = "开户行", sort = 15)
    private String bankName;

    @ExcelExport(value = "银行账户", sort = 16)
    private String accountNumber;

    @ExcelExport(value = "税率(%)", sort = 14)
    private BigDecimal taxRate;

    @ExcelExport(value = "状态", kv = "0-启用;1-停用", sort = 7)
    private Integer status;

    @ExcelExport(value = "备注", sort = 17)
    private String remark;

    private Integer sort;

    private LocalDateTime createTime;
}