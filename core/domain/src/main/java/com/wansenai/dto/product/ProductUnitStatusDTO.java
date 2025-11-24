package com.wansenai.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUnitStatusDTO {

    private Long id;

    private Integer status;
}