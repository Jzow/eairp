package com.wansenai.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateProductAttributeDTO {

    private Long id;

    private String attributeName;

    private String attributeValue;

    private String remark;

    private String sort;
}