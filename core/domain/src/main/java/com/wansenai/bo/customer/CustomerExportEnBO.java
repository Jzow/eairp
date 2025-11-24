package com.wansenai.bo.customer;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CustomerExportEnBO {

    private Long id;

    @ExcelExport(value = "Customer", sort = 1)
    private String customerName;

    @ExcelExport(value = "Contact", sort = 2)
    private String contact;

    @ExcelExport(value = "Phone Number", sort = 3)
    private String phoneNumber;

    @ExcelExport(value = "Email", sort = 4)
    private String email;

    @ExcelExport(value = "Fax", sort = 5)
    private String fax;

    @ExcelExport(value = "First Quarter Collection", sort = 8)
    private BigDecimal firstQuarterAccountReceivable;

    @ExcelExport(value = "Second Quarter Collection", sort = 9)
    private BigDecimal secondQuarterAccountReceivable;

    @ExcelExport(value = "Third Quarter Collection", sort = 10)
    private BigDecimal thirdQuarterAccountReceivable;

    @ExcelExport(value = "Fourth Quarter Collection", sort = 11)
    private BigDecimal fourthQuarterAccountReceivable;

    @ExcelExport(value = "Total Collection", sort = 12)
    private BigDecimal totalAccountReceivable;

    @ExcelExport(value = "Address", sort = 6)
    private String address;

    @ExcelExport(value = "Tax Number", sort = 13)
    private String taxNumber;

    @ExcelExport(value = "Bank", sort = 15)
    private String bankName;

    @ExcelExport(value = "Bank Account Number", sort = 16)
    private String accountNumber;

    @ExcelExport(value = "Tax Rate(%)", sort = 14)
    private BigDecimal taxRate;

    @ExcelExport(value = "Status", kv = "0-Enable;1-Deactivate", sort = 7)
    private Integer status;

    @ExcelExport(value = "Remark", sort = 17)
    private String remark;

    private Integer sort;

    private LocalDateTime createTime;
}