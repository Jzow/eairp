package com.wansenai.dto.product;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateProductCategoryDTO {

    private Long id;

    private String categoryName;

    private String categoryNumber;

    private Long parentId;

    private Integer sort;

    private String remark;
}