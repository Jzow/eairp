package com.wansenai.bo.supplier;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SupplierExportBO {

    private Long id;

    @ExcelExport(value = "供应商名称*", sort = 1)
    private String supplierName;

    @ExcelExport(value = "联系人*", sort = 2)
    private String contact;

    @ExcelExport(value = "联系电话", sort = 4)
    private String contactNumber;

    @ExcelExport(value = "手机号码*", sort = 3)
    private String phoneNumber;

    @ExcelExport(value = "地址", sort = 22)
    private String address;

    @ExcelExport(value = "电子邮箱", sort = 5)
    private String email;

    @ExcelExport(value = "状态", kv = "0-启用;1-停用", sort = 7)
    private Integer status;

    @ExcelExport(value = "一季度付款", sort = 12)
    private BigDecimal firstQuarterAccountPayment;

    @ExcelExport(value = "二季度付款", sort = 13)
    private BigDecimal secondQuarterAccountPayment;

    @ExcelExport(value = "三季度付款", sort = 14)
    private BigDecimal thirdQuarterAccountPayment;

    @ExcelExport(value = "四季度付款", sort = 15)
    private BigDecimal fourthQuarterAccountPayment;

    @ExcelExport(value = "累计应付账款", sort = 16)
    private BigDecimal totalAccountPayment;

    @ExcelExport(value = "传真", sort = 6)
    private String fax;

    @ExcelExport(value = "纳税人识别号", sort = 17)
    private String taxNumber;

    @ExcelExport(value = "开户行", sort = 19)
    private String bankName;

    @ExcelExport(value = "账号", sort = 20)
    private String accountNumber;

    @ExcelExport(value = "税率(%)", sort = 18)
    private BigDecimal taxRate;

    private Integer sort;

    @ExcelExport(value = "备注", sort = 23)
    private String remark;

    private LocalDateTime createTime;
}