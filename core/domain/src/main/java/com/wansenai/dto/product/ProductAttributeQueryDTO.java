package com.wansenai.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeQueryDTO {

    private String attributeName;

    private Long page;

    private Long pageSize;
}