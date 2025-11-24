package com.wansenai.bo.member;

import com.wansenai.utils.excel.ExcelExport;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MemberExportEnBO {

    private Long id;

    @ExcelExport(value = "Member Number", sort = 1)
    private String memberNumber;

    @ExcelExport(value = "Member", sort = 2)
    private String memberName;

    @ExcelExport(value = "Phone Number", sort = 3)
    private String phoneNumber;

    @ExcelExport(value = "Email", sort = 4)
    private String email;

    @ExcelExport(value = "Advance Payment", sort = 5)
    private BigDecimal advancePayment;

    @ExcelExport(value = "Status", kv = "0-Enable;1-Deactivate", sort = 6)
    private Integer status;

    @ExcelExport(value = "Remark", sort = 7)
    private String remark;

    private Integer sort;

    private LocalDateTime createTime;
}