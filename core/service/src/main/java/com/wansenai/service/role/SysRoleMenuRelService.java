package com.wansenai.service.role;

import com.wansenai.entities.role.SysRoleMenuRel;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface SysRoleMenuRelService extends IService<SysRoleMenuRel> {

    List<SysRoleMenuRel> listByRoleId(Long roleId);
}