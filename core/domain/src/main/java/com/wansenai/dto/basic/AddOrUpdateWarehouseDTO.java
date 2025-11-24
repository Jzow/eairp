package com.wansenai.dto.basic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddOrUpdateWarehouseDTO {

    private Long id;

    private Long warehouseManager;

    private String warehouseName;

    private String address;

    private BigDecimal price;

    private BigDecimal truckage;

    private Integer type;

    private Integer status;

    private String remark;

    private Integer sort;

    private Integer isDefault;
}