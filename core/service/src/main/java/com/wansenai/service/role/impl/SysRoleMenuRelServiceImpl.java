package com.wansenai.service.role.impl;

import com.wansenai.mappers.role.SysRoleMenuRelMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.entities.role.SysRoleMenuRel;
import com.wansenai.service.role.SysRoleMenuRelService;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.List;

@Service
public class SysRoleMenuRelServiceImpl extends ServiceImpl<SysRoleMenuRelMapper, SysRoleMenuRel> implements SysRoleMenuRelService {

    @Override
    public List<SysRoleMenuRel> listByRoleId(Long roleId) {
        Objects.requireNonNull(roleId, "roleId must not be null");

        return lambdaQuery()
                .eq(SysRoleMenuRel::getRoleId, roleId)
                .list();
    }
}