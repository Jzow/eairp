package com.wansenai.dto.financial;

import com.wansenai.bo.FileDataBO;
import com.wansenai.bo.financial.AdvanceChargeDataBO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateAdvanceChargeDTO {

    private Long id;

    private Long memberId;

    private String receiptDate;

    private String receiptNumber;

    private Long financialPersonnelId;

    private List<AdvanceChargeDataBO> tableData;

    private BigDecimal totalAmount;

    private BigDecimal collectedAmount;

    private String remark;

    private List<FileDataBO> files;

    private Integer review;
}