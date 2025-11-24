package com.wansenai.dto.role;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionDTO {

    private Long id;

    private List<Integer> menuIds = new ArrayList<>();

    // 用于只有id的情况
    public RolePermissionDTO(Long id) {
        this.id = id;
        this.menuIds = new ArrayList<>();
    }

    // 用于只有menuIds的情况
    public RolePermissionDTO(List<Integer> menuIds) {
        this.id = null;
        this.menuIds = menuIds != null ? new ArrayList<>(menuIds) : new ArrayList<>();
    }
}