package com.wansenai.service.role.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.dto.role.RoleListDTO;
import com.wansenai.dto.role.AddOrUpdateRoleDTO;
import com.wansenai.dto.role.RolePermissionDTO;
import com.wansenai.entities.role.SysRole;
import com.wansenai.entities.role.SysRoleMenuRel;
import com.wansenai.mappers.role.SysRoleMapper;
import com.wansenai.mappers.role.SysRoleMenuRelMapper;
import com.wansenai.service.BaseService;
import com.wansenai.service.role.SysRoleService;
import com.wansenai.service.role.SysRoleMenuRelService;
import com.wansenai.utils.SnowflakeIdUtil;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.RoleCodeEnum;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.RoleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final BaseService baseService;
    private final SysRoleMapper roleMapper;
    private final SysRoleMenuRelMapper roleMenuRelMapper;
    private final SysRoleMenuRelService roleMenuRelService;

    public SysRoleServiceImpl(
            BaseService baseService,
            SysRoleMapper roleMapper,
            SysRoleMenuRelMapper roleMenuRelMapper,
            SysRoleMenuRelService roleMenuRelService) {
        this.baseService = baseService;
        this.roleMapper = roleMapper;
        this.roleMenuRelMapper = roleMenuRelMapper;
        this.roleMenuRelService = roleMenuRelService;
    }

    @Override
    public Response<List<RoleVO>> roleList() {
        var roles = new ArrayList<RoleVO>();

        var sysRoles = lambdaQuery()
                .eq(SysRole::getDeleteFlag, CommonConstants.NOT_DELETED)
                .list()
                .stream()
                .filter(item -> !item.getId().equals(0L)) // not return platform admin role
                .toList();

        for (var item : sysRoles) {
            var roleVo = new RoleVO();
            BeanUtils.copyProperties(item, roleVo);
            roles.add(roleVo);
        }

        return Response.responseData(roles);
    }

    @Override
    public Response<Page<RoleVO>> rolePageList(RoleListDTO roleListDTO) {
        var page = new Page<SysRole>(
                Optional.ofNullable(roleListDTO).map(RoleListDTO::getPage).orElse(1L),
                Optional.ofNullable(roleListDTO).map(RoleListDTO::getPageSize).orElse(10L)
        );

        var roleWrapper = new LambdaQueryWrapper<SysRole>();
        if (roleListDTO != null) {
            if (roleListDTO.getRoleName() != null) {
                roleWrapper.eq(SysRole::getRoleName, roleListDTO.getRoleName());
            }
            if (roleListDTO.getStatus() != null) {
                roleWrapper.eq(SysRole::getStatus, roleListDTO.getStatus());
            }
        }
        roleWrapper.eq(SysRole::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(SysRole::getCreateTime);

        var resultPage = roleMapper.selectPage(page, roleWrapper);
        var records = resultPage.getRecords();

        if (records.isEmpty()) {
            return Response.responseData(new Page<>());
        }

        var listVo = new ArrayList<RoleVO>();
        for (var role : records) {
            var vo = new RoleVO();
            BeanUtils.copyProperties(role, vo);
            listVo.add(vo);
        }

        // Process menu IDs for each role
        for (var roleVo : listVo) {
            var roleMenuRelList = roleMenuRelMapper.selectList(
                    new LambdaQueryWrapper<SysRoleMenuRel>()
                            .eq(SysRoleMenuRel::getRoleId, roleVo.getId())
            );

            var menuList = new ArrayList<Integer>();
            for (var roleMenuRel : roleMenuRelList) {
                var menuId = roleMenuRel.getMenuId();
                var pattern = Pattern.compile("\\d+");
                var matcher = pattern.matcher(menuId);

                while (matcher.find()) {
                    menuList.add(Integer.parseInt(matcher.group()));
                }
            }
            roleVo.setMenuIds(menuList);
        }

        var resultVoPage = new Page<RoleVO>();
        resultVoPage.setRecords(listVo);
        resultVoPage.setTotal(resultPage.getTotal());
        resultVoPage.setPages(resultPage.getPages());
        resultVoPage.setSize(resultPage.getSize());

        return Response.responseData(resultVoPage);
    }

    @Override
    public Response<String> updateStatus(String id, Integer status) {
        if (id == null || id.isBlank() || status == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var updateResult = lambdaUpdate()
                .eq(SysRole::getId, id)
                .set(SysRole::getStatus, status)
                .update();

        var systemLanguage = baseService.getCurrentUserSystemLanguage();
        if (updateResult) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(RoleCodeEnum.UPDATE_ROLE_STATUS_SUCCESS);
            } else {
                return Response.responseMsg(RoleCodeEnum.UPDATE_ROLE_STATUS_SUCCESS_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(RoleCodeEnum.UPDATE_ROLE_STATUS_ERROR);
            } else {
                return Response.responseMsg(RoleCodeEnum.UPDATE_ROLE_STATUS_ERROR_EN);
            }
        }
    }

    @Override
    public Response<String> addOrUpdateRole(AddOrUpdateRoleDTO addOrUpdateRoleDTO) {
        if (addOrUpdateRoleDTO == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var systemLanguage = baseService.getCurrentUserSystemLanguage();
        if (addOrUpdateRoleDTO.getId() == null) {
            var sysRole = new SysRole();
            sysRole.setId(SnowflakeIdUtil.nextId());
            sysRole.setRoleName(addOrUpdateRoleDTO.getRoleName());
            sysRole.setType(addOrUpdateRoleDTO.getType());
            sysRole.setPriceLimit(addOrUpdateRoleDTO.getPriceLimit());
            sysRole.setStatus(addOrUpdateRoleDTO.getStatus());
            sysRole.setDescription(addOrUpdateRoleDTO.getDescription());
            sysRole.setCreateTime(LocalDateTime.now());

            var saveResult = save(sysRole);
            if (!saveResult) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(RoleCodeEnum.ADD_ROLE_ERROR);
                } else {
                    return Response.responseMsg(RoleCodeEnum.ADD_ROLE_ERROR_EN);
                }
            } else {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(RoleCodeEnum.ADD_ROLE_SUCCESS);
                } else {
                    return Response.responseMsg(RoleCodeEnum.ADD_ROLE_SUCCESS_EN);
                }
            }
        } else {
            // update
            var updateWrapper = lambdaUpdate()
                    .eq(SysRole::getId, addOrUpdateRoleDTO.getId())
                    .set(SysRole::getRoleName, addOrUpdateRoleDTO.getRoleName())
                    .set(SysRole::getType, addOrUpdateRoleDTO.getType())
                    .set(SysRole::getStatus, addOrUpdateRoleDTO.getStatus())
                    .set(SysRole::getPriceLimit, addOrUpdateRoleDTO.getPriceLimit())
                    .set(SysRole::getDescription, addOrUpdateRoleDTO.getDescription())
                    .set(SysRole::getUpdateTime, LocalDateTime.now());

            var updateResult = updateWrapper.update();

            if (!updateResult) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(RoleCodeEnum.UPDATE_ROLE_ERROR);
                } else {
                    return Response.responseMsg(RoleCodeEnum.UPDATE_ROLE_ERROR_EN);
                }
            } else {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(RoleCodeEnum.UPDATE_ROLE_SUCCESS);
                } else {
                    return Response.responseMsg(RoleCodeEnum.UPDATE_ROLE_SUCCESS_EN);
                }
            }
        }
    }

    @Override
    public Response<String> deleteRole(String id) {
        if (id == null || id.isBlank()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var deleteResult = lambdaUpdate()
                .eq(SysRole::getId, id)
                .set(SysRole::getDeleteFlag, CommonConstants.DELETED)
                .update();

        var systemLanguage = baseService.getCurrentUserSystemLanguage();
        if (deleteResult) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(RoleCodeEnum.DELETE_ROLE_SUCCESS);
            } else {
                return Response.responseMsg(RoleCodeEnum.DELETE_ROLE_SUCCESS_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(RoleCodeEnum.DELETE_ROLE_ERROR);
            } else {
                return Response.responseMsg(RoleCodeEnum.DELETE_ROLE_ERROR_EN);
            }
        }
    }

    @Override
    public Response<String> rolePermission(RolePermissionDTO rolePermissionDTO) {
        var roleId = rolePermissionDTO.getId();
        var menuIds = rolePermissionDTO.getMenuIds();

        if (roleId == null || menuIds == null || menuIds.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        // 先删除原有的角色权限关系
        roleMenuRelService.lambdaUpdate()
                .eq(SysRoleMenuRel::getRoleId, roleId)
                .remove();

        var roleMenuRel = new SysRoleMenuRel();
        var menuIdStr = menuIds.stream()
                .map(id -> "[" + id + "]")
                .collect(Collectors.joining(""));
        roleMenuRel.setMenuId(menuIdStr);
        roleMenuRel.setRoleId(roleId);

        var saveBatchResult = roleMenuRelService.saveOrUpdate(roleMenuRel);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (saveBatchResult) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(RoleCodeEnum.ROLE_PERMISSION_MENU_SUCCESS);
            } else {
                return Response.responseMsg(RoleCodeEnum.ROLE_PERMISSION_MENU_SUCCESS_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(RoleCodeEnum.ROLE_PERMISSION_MENU_ERROR);
            } else {
                return Response.responseMsg(RoleCodeEnum.ROLE_PERMISSION_MENU_ERROR_EN);
            }
        }
    }
}