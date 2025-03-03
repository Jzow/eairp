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
package com.wansenai.dto.financial

import com.wansenai.bo.AdvanceChargeDataBO
import com.wansenai.bo.FileDataBO
import lombok.Data
import java.math.BigDecimal

@Data
data class AddOrUpdateAdvanceChargeDTO(

    val id : Long? = null,

    val memberId: Long? = null,

    val receiptDate: String,

    val receiptNumber: String? = null,

    val financialPersonnelId: Long? = null,

    val tableData: List<AdvanceChargeDataBO>,

    val totalAmount : BigDecimal,

    val collectedAmount : BigDecimal,

    val remark: String? = null,

    val files: List<FileDataBO>? = null,

    val review: Int? = null,
)
