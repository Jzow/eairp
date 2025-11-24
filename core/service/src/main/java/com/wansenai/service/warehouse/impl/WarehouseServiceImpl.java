package com.wansenai.service.warehouse.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.dto.basic.AddOrUpdateWarehouseDTO;
import com.wansenai.dto.basic.QueryWarehouseDTO;
import com.wansenai.entities.warehouse.Warehouse;
import com.wansenai.mappers.warehouse.WarehouseMapper;
import com.wansenai.service.BaseService;
import com.wansenai.service.warehouse.WarehouseService;
import com.wansenai.service.user.ISysUserService;
import com.wansenai.utils.SnowflakeIdUtil;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.WarehouseCodeEnum;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.warehouse.WarehouseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    private final WarehouseMapper warehouseMapper;
    private final BaseService baseService;
    private final ISysUserService userService;

    public WarehouseServiceImpl(
            WarehouseMapper warehouseMapper,
            BaseService baseService,
            ISysUserService userService) {
        this.warehouseMapper = warehouseMapper;
        this.baseService = baseService;
        this.userService = userService;
    }

    @Override
    public Response<Page<WarehouseVO>> getWarehousePageList(QueryWarehouseDTO warehouseDTO) {
        var page = new Page<Warehouse>(
                Optional.ofNullable(warehouseDTO).map(QueryWarehouseDTO::getPage).orElse(1L),
                Optional.ofNullable(warehouseDTO).map(QueryWarehouseDTO::getPageSize).orElse(10L)
        );

        var wrapper = new LambdaQueryWrapper<Warehouse>();
        if (warehouseDTO != null) {
            if (warehouseDTO.getWarehouseName() != null) {
                wrapper.like(Warehouse::getWarehouseName, warehouseDTO.getWarehouseName());
            }
            if (warehouseDTO.getRemark() != null) {
                wrapper.like(Warehouse::getRemark, warehouseDTO.getRemark());
            }
        }
        wrapper.eq(Warehouse::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(Warehouse::getCreateTime);

        var resultPage = warehouseMapper.selectPage(page, wrapper);
        var records = resultPage.getRecords();

        if (records.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
        }

        var listVo = new ArrayList<WarehouseVO>();
        for (var warehouse : records) {
            var user = userService.getById(warehouse.getWarehouseManager());
            var name = user != null ? user.getName() : null;

            var vo = new WarehouseVO();
            vo.setId(warehouse.getId());
            vo.setWarehouseName(warehouse.getWarehouseName());
            vo.setWarehouseManager(warehouse.getWarehouseManager());
            vo.setWarehouseManagerName(name);
            vo.setAddress(warehouse.getAddress());
            vo.setPrice(warehouse.getPrice());
            vo.setTruckage(warehouse.getTruckage());
            vo.setType(warehouse.getType());
            vo.setStatus(warehouse.getStatus());
            vo.setRemark(warehouse.getRemark());
            vo.setSort(warehouse.getSort());
            vo.setIsDefault(warehouse.getIsDefault());
            vo.setCreateTime(warehouse.getCreateTime());
            listVo.add(vo);
        }

        var resultVoPage = new Page<WarehouseVO>();
        resultVoPage.setRecords(listVo);
        resultVoPage.setTotal(resultPage.getTotal());
        resultVoPage.setPages(resultPage.getPages());
        resultVoPage.setSize(resultPage.getSize());

        return Response.responseData(resultVoPage);
    }

    private void updateDefaultAccount(Long id) {
        var defaultWarehouse = lambdaQuery()
                .eq(Warehouse::getIsDefault, CommonConstants.IS_DEFAULT)
                .eq(Warehouse::getDeleteFlag, CommonConstants.NOT_DELETED)
                .one();

        if (defaultWarehouse != null) {
            defaultWarehouse.setIsDefault(CommonConstants.NOT_DEFAULT);
            updateById(defaultWarehouse);
        }

        var warehouse = getById(id);
        if (warehouse != null) {
            warehouse.setIsDefault(CommonConstants.IS_DEFAULT);
            updateById(warehouse);
        }
    }

    @Transactional
    @Override
    public Response<String> addOrUpdateWarehouse(AddOrUpdateWarehouseDTO warehouseDTO) {
        if (warehouseDTO == null) {
            return Response.fail();
        }

        var userId = baseService.getCurrentUserId();
        var isAdd = warehouseDTO.getId() == null;
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        var warehouse = new Warehouse();
        warehouse.setId(warehouseDTO.getId() != null ? warehouseDTO.getId() : SnowflakeIdUtil.nextId());
        warehouse.setWarehouseName(warehouseDTO.getWarehouseName());
        warehouse.setWarehouseManager(warehouseDTO.getWarehouseManager());
        warehouse.setAddress(warehouseDTO.getAddress());
        warehouse.setPrice(Optional.ofNullable(warehouseDTO.getPrice()).orElse(BigDecimal.ZERO));
        warehouse.setTruckage(Optional.ofNullable(warehouseDTO.getTruckage()).orElse(BigDecimal.ZERO));
        warehouse.setType(warehouseDTO.getType());
        warehouse.setStatus(warehouseDTO.getStatus());
        warehouse.setRemark(warehouseDTO.getRemark());
        warehouse.setSort(warehouseDTO.getSort());
        warehouse.setIsDefault(warehouseDTO.getIsDefault());

        if (isAdd) {
            warehouse.setCreateTime(LocalDateTime.now());
            warehouse.setCreateBy(userId);
        } else {
            warehouse.setUpdateTime(LocalDateTime.now());
            warehouse.setUpdateBy(userId);
        }

        if (warehouse.getIsDefault() == CommonConstants.IS_DEFAULT) {
            updateDefaultAccount(warehouse.getId());
        }

        var saveResult = saveOrUpdate(warehouse);

        if ("zh_CN".equals(systemLanguage)) {
            if (saveResult && isAdd) {
                return Response.responseMsg(WarehouseCodeEnum.ADD_WAREHOUSE_SUCCESS);
            } else if (saveResult) {
                return Response.responseMsg(WarehouseCodeEnum.UPDATE_WAREHOUSE_INFO_SUCCESS);
            } else {
                return Response.fail();
            }
        } else {
            if (saveResult && isAdd) {
                return Response.responseMsg(WarehouseCodeEnum.ADD_WAREHOUSE_SUCCESS_EN);
            } else if (saveResult) {
                return Response.responseMsg(WarehouseCodeEnum.UPDATE_WAREHOUSE_INFO_SUCCESS_EN);
            } else {
                return Response.fail();
            }
        }
    }

    @Override
    public Response<String> deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var updateResult = warehouseMapper.deleteBatchIds(ids);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (updateResult > 0) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(WarehouseCodeEnum.DELETE_WAREHOUSE_SUCCESS);
            } else {
                return Response.responseMsg(WarehouseCodeEnum.DELETE_WAREHOUSE_SUCCESS_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(WarehouseCodeEnum.DELETE_WAREHOUSE_ERROR);
            } else {
                return Response.responseMsg(WarehouseCodeEnum.DELETE_WAREHOUSE_ERROR_EN);
            }
        }
    }

    @Override
    public Response<List<WarehouseVO>> getWarehouse() {
        var warehouseList = new ArrayList<WarehouseVO>();
        var warehouses = list();

        for (var warehouse : warehouses) {
            var vo = new WarehouseVO();
            BeanUtils.copyProperties(warehouse, vo);
            warehouseList.add(vo);
        }

        return Response.responseData(warehouseList);
    }

    @Override
    public Response<String> updateBatchStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty() || status == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var updateResult = lambdaUpdate()
                .in(Warehouse::getId, ids)
                .set(Warehouse::getStatus, status)
                .update();

        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (!updateResult) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(WarehouseCodeEnum.UPDATE_WAREHOUSE_STATUS_ERROR);
            } else {
                return Response.responseMsg(WarehouseCodeEnum.UPDATE_WAREHOUSE_STATUS_ERROR_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(WarehouseCodeEnum.UPDATE_WAREHOUSE_STATUS_SUCCESS);
            } else {
                return Response.responseMsg(WarehouseCodeEnum.UPDATE_WAREHOUSE_STATUS_SUCCESS_EN);
            }
        }
    }

    @Override
    public Warehouse getWarehouseByName(String name) {
        if (name == null || name.isEmpty()) {
            return new Warehouse();
        }

        var warehouse = lambdaQuery()
                .eq(Warehouse::getWarehouseName, name)
                .eq(Warehouse::getDeleteFlag, CommonConstants.NOT_DELETED)
                .one();

        return warehouse != null ? warehouse : new Warehouse();
    }

    @Override
    public Response<List<WarehouseVO>> getWarehouseList() {
        var warehouseList = new ArrayList<WarehouseVO>();
        var warehouses = list();

        for (var warehouse : warehouses) {
            var vo = new WarehouseVO();
            vo.setId(warehouse.getId());
            vo.setWarehouseName(warehouse.getWarehouseName());
            vo.setWarehouseManager(warehouse.getWarehouseManager());
            vo.setAddress(warehouse.getAddress());
            vo.setPrice(warehouse.getPrice());
            vo.setTruckage(warehouse.getTruckage());
            vo.setType(warehouse.getType());
            vo.setStatus(warehouse.getStatus());
            vo.setRemark(warehouse.getRemark());
            vo.setSort(warehouse.getSort());
            vo.setIsDefault(warehouse.getIsDefault());
            vo.setCreateTime(warehouse.getCreateTime());
            warehouseList.add(vo);
        }

        return Response.responseData(warehouseList);
    }

    @Override
    public Response<WarehouseVO> getDefaultWarehouse() {
        var warehouse = lambdaQuery()
                .eq(Warehouse::getIsDefault, CommonConstants.IS_DEFAULT)
                .eq(Warehouse::getDeleteFlag, CommonConstants.NOT_DELETED)
                .one();

        if (warehouse == null) {
            return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
        }

        var warehouseVO = new WarehouseVO();
        warehouseVO.setId(warehouse.getId());
        warehouseVO.setWarehouseName(warehouse.getWarehouseName());
        warehouseVO.setWarehouseManager(warehouse.getWarehouseManager());
        warehouseVO.setAddress(warehouse.getAddress());
        warehouseVO.setPrice(warehouse.getPrice());
        warehouseVO.setTruckage(warehouse.getTruckage());
        warehouseVO.setType(warehouse.getType());
        warehouseVO.setStatus(warehouse.getStatus());
        warehouseVO.setRemark(warehouse.getRemark());
        warehouseVO.setSort(warehouse.getSort());
        warehouseVO.setIsDefault(warehouse.getIsDefault());
        warehouseVO.setCreateTime(warehouse.getCreateTime());

        return Response.responseData(warehouseVO);
    }
}