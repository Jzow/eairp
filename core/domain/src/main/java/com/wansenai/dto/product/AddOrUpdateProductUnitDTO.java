package com.wansenai.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateProductUnitDTO {

    private Long id;

    private String basicUnit;

    private String otherUnit;

    private String otherUnitTwo;

    private String otherUnitThree;

    private BigDecimal ratio;

    private BigDecimal ratioTwo;

    private BigDecimal ratioThree;

    private Integer status;
}