package com.wansenai.bo.supplier;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SupplierExportEnBO {

    private Long id;

    @ExcelExport(value = "Supplier*", sort = 1)
    private String supplierName;

    @ExcelExport(value = "Contact*", sort = 2)
    private String contact;

    @ExcelExport(value = "Contact Phone", sort = 4)
    private String contactNumber;

    @ExcelExport(value = "Phone Number*", sort = 3)
    private String phoneNumber;

    @ExcelExport(value = "Address", sort = 22)
    private String address;

    @ExcelExport(value = "Email", sort = 5)
    private String email;

    @ExcelExport(value = "Status", kv = "0-Enable;1-Deactivate", sort = 7)
    private Integer status;

    @ExcelExport(value = "First Quarter Payment", sort = 12)
    private BigDecimal firstQuarterAccountPayment;

    @ExcelExport(value = "Second Quarter Payment", sort = 13)
    private BigDecimal secondQuarterAccountPayment;

    @ExcelExport(value = "Third Quarter Payment", sort = 14)
    private BigDecimal thirdQuarterAccountPayment;

    @ExcelExport(value = "Fourth Quarter Payment", sort = 15)
    private BigDecimal fourthQuarterAccountPayment;

    @ExcelExport(value = "Total Payment", sort = 16)
    private BigDecimal totalAccountPayment;

    @ExcelExport(value = "Fax", sort = 6)
    private String fax;

    @ExcelExport(value = "Tax Number", sort = 17)
    private String taxNumber;

    @ExcelExport(value = "Bank", sort = 19)
    private String bankName;

    @ExcelExport(value = "Bank Account Number", sort = 20)
    private String accountNumber;

    @ExcelExport(value = "Tax Rate(%)", sort = 18)
    private BigDecimal taxRate;

    private Integer sort;

    @ExcelExport(value = "Remark", sort = 23)
    private String remark;

    private LocalDateTime createTime;
}