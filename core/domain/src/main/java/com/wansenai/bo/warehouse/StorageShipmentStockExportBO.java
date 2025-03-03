/*
 * Copyright 2023-2025 EAIRP Team, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://opensource.wansenai.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.wansenai.bo.warehouse;

import com.wansenai.utils.excel.ExcelExport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 其他入库/出库导出BO (StorageShipmentStockExportBO) 两个BO类的字段一样，只是一个用于导出，一个用于导入
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageShipmentStockExportBO {

    @ExcelExport(value = "单据编号")
    private String receiptNumber;

    @ExcelExport(value = "关联人员类型")
    private String relatedPersonType;

    @ExcelExport(value = "关联人员")
    private String relatedPerson;

    @ExcelExport(value = "仓库名称")
    private String warehouseName;

    @ExcelExport(value = "条码")
    private String barCode;

    @ExcelExport(value = "商品名称")
    private String productName;

    @ExcelExport(value = "规格")
    private String productStandard;

    @ExcelExport(value = "型号")
    private String productModel;

    @ExcelExport(value = "扩展信息")
    private String productExtendInfo;

    @ExcelExport(value = "库存")
    private Integer stock;

    @ExcelExport(value = "单位")
    private String productUnit;

    @ExcelExport(value = "数量")
    private Integer productNumber;

    @ExcelExport(value = "单价")
    private BigDecimal unitPrice;

    @ExcelExport(value = "金额")
    private BigDecimal amount;

    @ExcelExport(value = "备注")
    private String remark;
}
