package com.wansenai.dto.role;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateRoleDTO {

    private Long id;

    private String roleName;

    private String type;

    private Integer priceLimit;

    private Integer status;

    private String description;

    private Long page;

    private Long pageSize;
}