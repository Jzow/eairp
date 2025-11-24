package com.wansenai.dto.basic;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class UpdateSupplierStatusDTO {

    private List<Long> ids;

    private Integer status;

    public UpdateSupplierStatusDTO(List<Long> ids, Integer status) {
        this.ids = ids;
        this.status = status;
    }
}