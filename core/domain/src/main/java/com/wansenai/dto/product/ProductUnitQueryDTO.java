package com.wansenai.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUnitQueryDTO {

    private String computeUnit;

    private Long page;

    private Long pageSize;
}