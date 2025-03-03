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
package com.wansenai.vo.financial

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.wansenai.NoArg
import com.wansenai.bo.BigDecimalSerializerBO
import lombok.Data
import java.math.BigDecimal
import java.time.LocalDateTime

@NoArg
@Data
data class AdvanceChargeVO (

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    var id: Long? = null,

    var memberName: String,

    var receiptNumber: String,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    var receiptDate: LocalDateTime,

    @JsonSerialize(using = BigDecimalSerializerBO::class)
    var collectedAmount : BigDecimal,

    @JsonSerialize(using = BigDecimalSerializerBO::class)
    var totalAmount : BigDecimal,

    var financialPersonnel: String,

    var operator: String,

    var remark: String? = null,

    var status: Int,
)
