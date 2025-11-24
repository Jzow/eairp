package com.wansenai.service.role;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.entities.role.SysRole;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.RoleVO;
import com.wansenai.dto.role.RoleListDTO;
import com.wansenai.dto.role.AddOrUpdateRoleDTO;
import com.wansenai.dto.role.RolePermissionDTO;
import java.util.List;

public interface SysRoleService extends IService<SysRole> {

    Response<List<RoleVO>> roleList();

    Response<Page<RoleVO>> rolePageList(RoleListDTO roleListDTO);

    Response<String> updateStatus(String id, Integer status);

    Response<String> addOrUpdateRole(AddOrUpdateRoleDTO addOrUpdateRoleDTO);

    Response<String> deleteRole(String id);

    Response<String> rolePermission(RolePermissionDTO rolePermissionDTO);
}