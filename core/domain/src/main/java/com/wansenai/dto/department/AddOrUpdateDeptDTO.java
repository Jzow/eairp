package com.wansenai.dto.department;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddOrUpdateDeptDTO {

    private Long id;

    private String deptName;

    private Long parentId;

    private String deptNumber;

    private String leader;

    private Integer status;

    private String remark;

    private String sort;
}