package com.wansenai.service.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.dto.department.AddOrUpdateDeptDTO;
import com.wansenai.entities.SysDepartment;
import com.wansenai.entities.user.SysUserDeptRel;
import com.wansenai.mappers.system.SysDepartmentMapper;
import com.wansenai.mappers.user.SysUserDeptRelMapper;
import com.wansenai.service.BaseService;
import com.wansenai.service.system.SysDepartmentService;
import com.wansenai.service.user.ISysUserService;
import com.wansenai.utils.SnowflakeIdUtil;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.DeptCodeEnum;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.DeptListVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> implements SysDepartmentService {

    private final BaseService baseService;
    private final ISysUserService userService;
    private final SysUserDeptRelMapper userDeptRelMapper;

    public SysDepartmentServiceImpl(
            BaseService baseService,
            ISysUserService userService,
            SysUserDeptRelMapper userDeptRelMapper) {
        this.baseService = baseService;
        this.userService = userService;
        this.userDeptRelMapper = userDeptRelMapper;
    }

    @Override
    public Response<List<DeptListVO>> userDept() {
        var results = new ArrayList<DeptListVO>(10);

        var userRoleWrapper = new QueryWrapper<SysUserDeptRel>()
                .eq("user_id", userService.getCurrentUserId());
        var userDeptRelList = userDeptRelMapper.selectList(userRoleWrapper)
                .stream()
                .map(SysUserDeptRel::getDeptId)
                .collect(Collectors.toList());

        var departments = lambdaQuery()
                .in(SysDepartment::getId, userDeptRelList)
                .eq(SysDepartment::getDeleteFlag, CommonConstants.NOT_DELETED)
                .list();

        if (departments.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
        }

        return assemblePcNodesList(results, departments, null);
    }

    /**
     * P: Parent
     * C: Children
     * 组装部门树
     * @param results 返回结果
     * @param departments 部门列表
     * @param deptName 部门名称
     * @return Response<List<DeptListVO>>
     */
    private Response<List<DeptListVO>> assemblePcNodesList(
            ArrayList<DeptListVO> results,
            List<SysDepartment> departments,
            String deptName) {

        if (deptName != null) {
            for (var item : departments) {
                var parent = lambdaQuery()
                        .eq(SysDepartment::getId, item.getParentId())
                        .eq(SysDepartment::getDeleteFlag, CommonConstants.NOT_DELETED)
                        .one();

                if (parent == null) {
                    continue;
                }

                var children = new ArrayList<DeptListVO>(3);
                var childrenVo = DeptListVO.builder()
                        .id(item.getId())
                        .parentId(item.getParentId())
                        .deptNumber(item.getNumber())
                        .deptName(item.getName())
                        .status(item.getStatus())
                        .leader(item.getLeader())
                        .remark(item.getRemark())
                        .sort(item.getSort())
                        .createTime(item.getCreateTime())
                        .build();
                children.add(childrenVo);

                var deptChildrenVO = DeptListVO.builder()
                        .id(parent.getId())
                        .parentId(parent.getParentId())
                        .deptNumber(parent.getNumber())
                        .deptName(parent.getName())
                        .status(parent.getStatus())
                        .leader(parent.getLeader())
                        .remark(parent.getRemark())
                        .sort(parent.getSort())
                        .createTime(parent.getCreateTime())
                        .children(children)
                        .build();

                results.add(deptChildrenVO);
            }
        } else {
            for (var item : departments) {
                var deptListVO = DeptListVO.builder()
                        .id(item.getId())
                        .parentId(item.getParentId())
                        .deptNumber(item.getNumber())
                        .deptName(item.getName())
                        .status(item.getStatus())
                        .leader(item.getLeader())
                        .remark(item.getRemark())
                        .sort(item.getSort())
                        .createTime(item.getCreateTime())
                        .build();

                if (item.getParentId() == null) {
                    var children = new ArrayList<DeptListVO>(10);
                    var childrenList = lambdaQuery()
                            .eq(SysDepartment::getParentId, item.getId())
                            .eq(SysDepartment::getDeleteFlag, CommonConstants.NOT_DELETED)
                            .list();

                    for (var childrenItem : childrenList) {
                        var deptChildrenVO = DeptListVO.builder()
                                .id(childrenItem.getId())
                                .parentId(childrenItem.getParentId())
                                .deptNumber(childrenItem.getNumber())
                                .deptName(childrenItem.getName())
                                .status(childrenItem.getStatus())
                                .leader(childrenItem.getLeader())
                                .remark(childrenItem.getRemark())
                                .sort(childrenItem.getSort())
                                .createTime(childrenItem.getCreateTime())
                                .build();
                        children.add(deptChildrenVO);
                    }
                    deptListVO.setChildren(children);
                    results.add(deptListVO);
                }
            }
        }

        return Response.responseData(results);
    }

    @Override
    public Response<List<DeptListVO>> getDeptList(String deptName) {
        var results = new ArrayList<DeptListVO>(10);
        var tenantId = userService.getCurrentTenantId();

        var queryWrapper = lambdaQuery()
                .in(SysDepartment::getTenantId, tenantId)
                .eq(SysDepartment::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(SysDepartment::getCreateTime);

        if (StringUtils.hasText(deptName)) {
            queryWrapper.eq(SysDepartment::getName, deptName);
        }

        var departments = queryWrapper.list();

        return assemblePcNodesList(results, departments, deptName);
    }

    @Override
    public Response<String> addOrSaveDept(AddOrUpdateDeptDTO addOrUpdateDeptDTO) {
        if (addOrUpdateDeptDTO == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (addOrUpdateDeptDTO.getId() == null) {
            var userId = userService.getCurrentTenantId();
            var dept = SysDepartment.builder()
                    .id(SnowflakeIdUtil.nextId())
                    .tenantId(userId)
                    .parentId(addOrUpdateDeptDTO.getParentId())
                    .name(addOrUpdateDeptDTO.getDeptName())
                    .number(addOrUpdateDeptDTO.getDeptName()) // Note: using deptName as number
                    .status(addOrUpdateDeptDTO.getStatus())
                    .leader(addOrUpdateDeptDTO.getLeader())
                    .remark(addOrUpdateDeptDTO.getRemark())
                    .sort(addOrUpdateDeptDTO.getSort())
                    .createTime(LocalDateTime.now())
                    .build();

            var saveResult = save(dept);

            // add user_dept_rel
            var userDeptRel = SysUserDeptRel.builder()
                    .id(SnowflakeIdUtil.nextId())
                    .tenantId(userId)
                    .userId(userId)
                    .deptId(dept.getId())
                    .createTime(LocalDateTime.now())
                    .build();
            userDeptRelMapper.insert(userDeptRel);

            if (!saveResult) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(DeptCodeEnum.ADD_DEPARTMENT_ERROR);
                }
                return Response.responseMsg(DeptCodeEnum.ADD_DEPARTMENT_ERROR_EN);
            } else {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(DeptCodeEnum.ADD_DEPARTMENT_SUCCESS);
                }
                return Response.responseMsg(DeptCodeEnum.ADD_DEPARTMENT_SUCCESS_EN);
            }
        } else {
            var updateWrapper = lambdaUpdate()
                    .eq(SysDepartment::getId, addOrUpdateDeptDTO.getId());

            if (StringUtils.hasText(addOrUpdateDeptDTO.getDeptName())) {
                updateWrapper.set(SysDepartment::getName, addOrUpdateDeptDTO.getDeptName());
            }
            if (StringUtils.hasText(addOrUpdateDeptDTO.getDeptNumber())) {
                updateWrapper.set(SysDepartment::getNumber, addOrUpdateDeptDTO.getDeptNumber());
            }
            if (addOrUpdateDeptDTO.getParentId() != null) {
                updateWrapper.set(SysDepartment::getParentId, addOrUpdateDeptDTO.getParentId());
            }
            if (addOrUpdateDeptDTO.getStatus() != null) {
                updateWrapper.set(SysDepartment::getStatus, addOrUpdateDeptDTO.getStatus());
            }
            if (StringUtils.hasText(addOrUpdateDeptDTO.getLeader())) {
                updateWrapper.set(SysDepartment::getLeader, addOrUpdateDeptDTO.getLeader());
            }
            if (StringUtils.hasText(addOrUpdateDeptDTO.getRemark())) {
                updateWrapper.set(SysDepartment::getRemark, addOrUpdateDeptDTO.getRemark());
            }
            if (addOrUpdateDeptDTO.getSort() != null) {
                updateWrapper.set(SysDepartment::getSort, addOrUpdateDeptDTO.getSort());
            }

            updateWrapper.set(SysDepartment::getUpdateTime, LocalDateTime.now());

            var saveResult = updateWrapper.update();

            if (!saveResult) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(DeptCodeEnum.UPDATE_DEPARTMENT_ERROR);
                }
                return Response.responseMsg(DeptCodeEnum.UPDATE_DEPARTMENT_ERROR_EN);
            } else {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(DeptCodeEnum.UPDATE_DEPARTMENT_SUCCESS);
                }
                return Response.responseMsg(DeptCodeEnum.UPDATE_DEPARTMENT_SUCCESS_EN);
            }
        }
    }

    @Override
    public Response<String> deleteDept(String id) {
        if (id == null || id.isBlank()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var deleteResult = lambdaUpdate()
                .eq(SysDepartment::getId, id)
                .set(SysDepartment::getDeleteFlag, CommonConstants.DELETED)
                .update();

        var systemLanguage = baseService.getCurrentUserSystemLanguage();
        if (!deleteResult) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(DeptCodeEnum.DELETE_DEPARTMENT_ERROR);
            }
            return Response.responseMsg(DeptCodeEnum.DELETE_DEPARTMENT_ERROR_EN);
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(DeptCodeEnum.DELETE_DEPARTMENT_SUCCESS);
            }
            return Response.responseMsg(DeptCodeEnum.DELETE_DEPARTMENT_SUCCESS_EN);
        }
    }
}