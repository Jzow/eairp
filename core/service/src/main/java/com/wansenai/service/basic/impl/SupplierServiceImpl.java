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
package com.wansenai.service.basic.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.bo.supplier.SupplierExportBO;
import com.wansenai.bo.supplier.SupplierExportEnBO;
import com.wansenai.entities.basic.Supplier;
import com.wansenai.mappers.basic.SystemSupplierMapper;
import com.wansenai.service.BaseService;
import com.wansenai.service.basic.SupplierService;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.SupplierCodeEnum;
import com.wansenai.utils.excel.ExcelUtils;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.basic.SupplierVO;
import com.wansenai.dto.basic.AddSupplierDTO;
import com.wansenai.dto.basic.QuerySupplierDTO;
import com.wansenai.dto.basic.UpdateSupplierDTO;
import com.wansenai.dto.basic.UpdateSupplierStatusDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class SupplierServiceImpl extends ServiceImpl<SystemSupplierMapper, Supplier> implements SupplierService {

    private final BaseService baseService;
    private final SystemSupplierMapper supplierMapper;

    public SupplierServiceImpl(BaseService baseService, SystemSupplierMapper supplierMapper) {
        this.baseService = baseService;
        this.supplierMapper = supplierMapper;
    }

    @Override
    public Response<Page<SupplierVO>> getSupplierPageList(QuerySupplierDTO supplier) {
        var page = new Page<Supplier>(
                Optional.ofNullable(supplier).map(dto -> dto.getPage()).orElse(1L),
                Optional.ofNullable(supplier).map(dto -> dto.getPageSize()).orElse(10L)
        );

        var wrapper = new LambdaQueryWrapper<Supplier>();
        if (supplier != null) {
            if (supplier.getSupplierName() != null) {
                wrapper.like(Supplier::getSupplierName, supplier.getSupplierName());
            }
            if (supplier.getContact() != null) {
                wrapper.like(Supplier::getContact, supplier.getContact());
            }
            if (supplier.getContactNumber() != null) {
                wrapper.like(Supplier::getContactNumber, supplier.getContactNumber());
            }
            if (supplier.getPhoneNumber() != null) {
                wrapper.like(Supplier::getPhoneNumber, supplier.getPhoneNumber());
            }
            if (supplier.getStartDate() != null) {
                wrapper.ge(Supplier::getCreateTime, supplier.getStartDate());
            }
            if (supplier.getEndDate() != null) {
                wrapper.le(Supplier::getCreateTime, supplier.getEndDate());
            }
        }
        wrapper.eq(Supplier::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(Supplier::getCreateTime);

        var resultPage = supplierMapper.selectPage(page, wrapper);
        var records = resultPage.getRecords();

        if (records.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
        }

        var voList = new ArrayList<SupplierVO>();
        for (var supplierEntity : records) {
            var vo = new SupplierVO();
            vo.setId(supplierEntity.getId());
            vo.setSupplierName(supplierEntity.getSupplierName());
            vo.setContact(supplierEntity.getContact());
            vo.setContactNumber(supplierEntity.getContactNumber());
            vo.setPhoneNumber(supplierEntity.getPhoneNumber());
            vo.setAddress(supplierEntity.getAddress());
            vo.setEmail(supplierEntity.getEmail());
            vo.setStatus(supplierEntity.getStatus());
            vo.setFirstQuarterAccountPayment(supplierEntity.getFirstQuarterAccountPayment());
            vo.setSecondQuarterAccountPayment(supplierEntity.getSecondQuarterAccountPayment());
            vo.setThirdQuarterAccountPayment(supplierEntity.getThirdQuarterAccountPayment());
            vo.setFourthQuarterAccountPayment(supplierEntity.getFourthQuarterAccountPayment());
            vo.setTotalAccountPayment(supplierEntity.getTotalAccountPayment());
            vo.setFax(supplierEntity.getFax());
            vo.setTaxNumber(supplierEntity.getTaxNumber());
            vo.setBankName(supplierEntity.getBankName());
            vo.setAccountNumber(supplierEntity.getAccountNumber());
            vo.setTaxRate(supplierEntity.getTaxRate());
            vo.setSort(supplierEntity.getSort());
            vo.setRemark(supplierEntity.getRemark());
            vo.setCreateTime(supplierEntity.getCreateTime());
            voList.add(vo);
        }

        var resultVoPage = new Page<SupplierVO>();
        resultVoPage.setRecords(voList);
        resultVoPage.setTotal(resultPage.getTotal());
        resultVoPage.setPages(resultPage.getPages());
        resultVoPage.setSize(resultPage.getSize());

        return Response.responseData(resultVoPage);
    }

    public BigDecimal calculateTotalAccount(List<BigDecimal> list) {
        return list.stream()
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(3, RoundingMode.HALF_UP);
    }

    @Override
    public Response<String> addSupplier(AddSupplierDTO supplier) {
        var systemLanguage = baseService.getCurrentUserSystemLanguage();
        var supplierEntity = new Supplier();

        if (supplier != null) {
            BeanUtils.copyProperties(supplier, supplierEntity);
            supplierEntity.setFirstQuarterAccountPayment(convertToBigDecimal(supplier.getFirstQuarterAccountPayment()));
            supplierEntity.setSecondQuarterAccountPayment(convertToBigDecimal(supplier.getSecondQuarterAccountPayment()));
            supplierEntity.setThirdQuarterAccountPayment(convertToBigDecimal(supplier.getThirdQuarterAccountPayment()));
            supplierEntity.setFourthQuarterAccountPayment(convertToBigDecimal(supplier.getFourthQuarterAccountPayment()));
            supplierEntity.setTaxRate(convertToBigDecimal(Double.valueOf(supplier.getTaxRate())));
        }

        var totalAccountPayment = calculateTotalAccount(List.of(
                supplierEntity.getFirstQuarterAccountPayment(),
                supplierEntity.getSecondQuarterAccountPayment(),
                supplierEntity.getThirdQuarterAccountPayment(),
                supplierEntity.getFourthQuarterAccountPayment()
        ));

        supplierEntity.setTotalAccountPayment(totalAccountPayment);
        supplierEntity.setCreateTime(LocalDateTime.now());
        supplierEntity.setCreateBy(baseService.getCurrentUserId());

        var saveResult = save(supplierEntity);

        if (saveResult) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(SupplierCodeEnum.ADD_SUPPLIER_SUCCESS);
            } else {
                return Response.responseMsg(SupplierCodeEnum.ADD_SUPPLIER_SUCCESS_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(SupplierCodeEnum.ADD_SUPPLIER_ERROR);
            } else {
                return Response.responseMsg(SupplierCodeEnum.ADD_SUPPLIER_ERROR_EN);
            }
        }
    }

    @Override
    public Response<List<SupplierVO>> getSupplierList(QuerySupplierDTO supplier) {
        var wrapper = new LambdaQueryWrapper<Supplier>();
        if (supplier != null) {
            if (supplier.getSupplierName() != null) {
                wrapper.like(Supplier::getSupplierName, supplier.getSupplierName());
            }
            if (supplier.getContact() != null) {
                wrapper.like(Supplier::getContact, supplier.getContact());
            }
            if (supplier.getContactNumber() != null) {
                wrapper.like(Supplier::getContactNumber, supplier.getContactNumber());
            }
            if (supplier.getPhoneNumber() != null) {
                wrapper.like(Supplier::getPhoneNumber, supplier.getPhoneNumber());
            }
            if (supplier.getStartDate() != null) {
                wrapper.ge(Supplier::getCreateTime, supplier.getStartDate());
            }
            if (supplier.getEndDate() != null) {
                wrapper.le(Supplier::getCreateTime, supplier.getEndDate());
            }
        }
        wrapper.eq(Supplier::getStatus, CommonConstants.STATUS_NORMAL)
                .eq(Supplier::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByAsc(Supplier::getSort);

        var list = supplierMapper.selectList(wrapper);
        var voList = new ArrayList<SupplierVO>();

        for (var supplierEntity : list) {
            var vo = new SupplierVO();
            vo.setId(supplierEntity.getId());
            vo.setSupplierName(supplierEntity.getSupplierName());
            vo.setContact(supplierEntity.getContact());
            vo.setContactNumber(supplierEntity.getContactNumber());
            vo.setPhoneNumber(supplierEntity.getPhoneNumber());
            vo.setAddress(supplierEntity.getAddress());
            vo.setEmail(supplierEntity.getEmail());
            vo.setStatus(supplierEntity.getStatus());
            vo.setFirstQuarterAccountPayment(supplierEntity.getFirstQuarterAccountPayment());
            vo.setSecondQuarterAccountPayment(supplierEntity.getSecondQuarterAccountPayment());
            vo.setThirdQuarterAccountPayment(supplierEntity.getThirdQuarterAccountPayment());
            vo.setFourthQuarterAccountPayment(supplierEntity.getFourthQuarterAccountPayment());
            vo.setTotalAccountPayment(supplierEntity.getTotalAccountPayment());
            vo.setFax(supplierEntity.getFax());
            vo.setTaxNumber(supplierEntity.getTaxNumber());
            vo.setBankName(supplierEntity.getBankName());
            vo.setAccountNumber(supplierEntity.getAccountNumber());
            vo.setTaxRate(supplierEntity.getTaxRate());
            vo.setSort(supplierEntity.getSort());
            vo.setRemark(supplierEntity.getRemark());
            vo.setCreateTime(supplierEntity.getCreateTime());
            voList.add(vo);
        }

        return Response.responseData(voList);
    }

    @Transactional
    @Override
    public Boolean batchAddSupplier(List<Supplier> suppliers) {
        var supplierEntities = new ArrayList<Supplier>();
        var existingSuppliers = new HashSet<String>(); // 使用字符串组合作为键

        if (suppliers != null) {
            for (var supplier : suppliers) {
                var supplierKey = supplier.getSupplierName() + "|" + supplier.getContact();
                if (!existingSuppliers.contains(supplierKey)) {
                    var supplierEntity = supplierMapper.selectOne(
                            new LambdaQueryWrapper<Supplier>()
                                    .eq(Supplier::getSupplierName, supplier.getSupplierName())
                                    .eq(Supplier::getContact, supplier.getContact())
                    );
                    if (supplierEntity == null) {
                        var newSupplierEntity = new Supplier();
                        BeanUtils.copyProperties(supplier, newSupplierEntity);
                        newSupplierEntity.setFirstQuarterAccountPayment(supplier.getFirstQuarterAccountPayment());
                        newSupplierEntity.setSecondQuarterAccountPayment(supplier.getSecondQuarterAccountPayment());
                        newSupplierEntity.setThirdQuarterAccountPayment(supplier.getThirdQuarterAccountPayment());
                        newSupplierEntity.setFourthQuarterAccountPayment(supplier.getFourthQuarterAccountPayment());
                        newSupplierEntity.setTaxRate(supplier.getTaxRate());

                        var total = calculateTotalAccount(List.of(
                                supplier.getFirstQuarterAccountPayment(),
                                supplier.getSecondQuarterAccountPayment(),
                                supplier.getThirdQuarterAccountPayment(),
                                supplier.getFourthQuarterAccountPayment()
                        ));
                        newSupplierEntity.setTotalAccountPayment(total);

                        newSupplierEntity.setCreateTime(LocalDateTime.now());
                        newSupplierEntity.setCreateBy(baseService.getCurrentUserId());

                        supplierEntities.add(newSupplierEntity);
                        existingSuppliers.add(supplierKey);
                    }
                }
            }
        }
        return saveBatch(supplierEntities);
    }

    @Override
    public Response<String> updateSupplier(UpdateSupplierDTO supplier) {
        var supplierEntity = new Supplier();

        if (supplier != null) {
            BeanUtils.copyProperties(supplier, supplierEntity);
            supplierEntity.setFirstQuarterAccountPayment(convertToBigDecimal(supplier.getFirstQuarterAccountPayment()));
            supplierEntity.setSecondQuarterAccountPayment(convertToBigDecimal(supplier.getSecondQuarterAccountPayment()));
            supplierEntity.setThirdQuarterAccountPayment(convertToBigDecimal(supplier.getThirdQuarterAccountPayment()));
            supplierEntity.setFourthQuarterAccountPayment(convertToBigDecimal(supplier.getFourthQuarterAccountPayment()));
            supplierEntity.setTaxRate(convertToBigDecimal(Double.valueOf(supplier.getTaxRate())));
        }

        var totalAccountPayment = calculateTotalAccount(List.of(
                supplierEntity.getFirstQuarterAccountPayment(),
                supplierEntity.getSecondQuarterAccountPayment(),
                supplierEntity.getThirdQuarterAccountPayment(),
                supplierEntity.getFourthQuarterAccountPayment()
        ));

        supplierEntity.setTotalAccountPayment(totalAccountPayment);
        supplierEntity.setUpdateTime(LocalDateTime.now());
        supplierEntity.setUpdateBy(baseService.getCurrentUserId());

        var systemLanguage = baseService.getCurrentUserSystemLanguage();
        var updateResult = updateById(supplierEntity);

        if (updateResult) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(SupplierCodeEnum.UPDATE_SUPPLIER_SUCCESS);
            } else {
                return Response.responseMsg(SupplierCodeEnum.UPDATE_SUPPLIER_SUCCESS_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(SupplierCodeEnum.UPDATE_SUPPLIER_ERROR);
            } else {
                return Response.responseMsg(SupplierCodeEnum.UPDATE_SUPPLIER_ERROR_EN);
            }
        }
    }

    @Override
    public Response<String> deleteSupplier(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var deleteResult = supplierMapper.deleteBatchIds(ids);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (deleteResult == 0) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(SupplierCodeEnum.DELETE_SUPPLIER_ERROR);
            } else {
                return Response.responseMsg(SupplierCodeEnum.DELETE_SUPPLIER_ERROR_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(SupplierCodeEnum.DELETE_SUPPLIER_SUCCESS);
            } else {
                return Response.responseMsg(SupplierCodeEnum.DELETE_SUPPLIER_SUCCESS_EN);
            }
        }
    }

    @Override
    public Response<String> updateSupplierStatus(UpdateSupplierStatusDTO updateSupplierStatusDTO) {
        if (updateSupplierStatusDTO == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var supplier = new Supplier();
        supplier.setStatus(updateSupplierStatusDTO.getStatus());
        supplier.setUpdateTime(LocalDateTime.now());
        supplier.setUpdateBy(baseService.getCurrentUserId());

        var updateResult = supplierMapper.update(
                supplier,
                new LambdaQueryWrapper<Supplier>().in(Supplier::getId, updateSupplierStatusDTO.getIds())
        );

        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (updateResult == 0) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(SupplierCodeEnum.UPDATE_SUPPLIER_STATUS_ERROR);
            } else {
                return Response.responseMsg(SupplierCodeEnum.UPDATE_SUPPLIER_STATUS_ERROR_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(SupplierCodeEnum.UPDATE_SUPPLIER_STATUS_SUCCESS);
            } else {
                return Response.responseMsg(SupplierCodeEnum.UPDATE_SUPPLIER_STATUS_SUCCESS_EN);
            }
        }
    }

    @Override
    public void exportSupplierData(QuerySupplierDTO querySupplierDTO, HttpServletResponse response) {
        var suppliers = getSupplierList(querySupplierDTO);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (suppliers.getData() != null && !suppliers.getData().isEmpty()) {
            if ("zh_CN".equals(systemLanguage)) {
                var exportData = new ArrayList<SupplierExportBO>();
                for (var supplier : suppliers.getData()) {
                    var supplierExportBO = new SupplierExportBO();
                    supplierExportBO.setId(supplier.getId());
                    supplierExportBO.setSupplierName(supplier.getSupplierName());
                    supplierExportBO.setContact(supplier.getContact());
                    supplierExportBO.setContactNumber(supplier.getContactNumber());
                    supplierExportBO.setPhoneNumber(supplier.getPhoneNumber());
                    supplierExportBO.setAddress(supplier.getAddress());
                    supplierExportBO.setEmail(supplier.getEmail());
                    supplierExportBO.setStatus(supplier.getStatus());
                    supplierExportBO.setFirstQuarterAccountPayment(supplier.getFirstQuarterAccountPayment());
                    supplierExportBO.setSecondQuarterAccountPayment(supplier.getSecondQuarterAccountPayment());
                    supplierExportBO.setThirdQuarterAccountPayment(supplier.getThirdQuarterAccountPayment());
                    supplierExportBO.setFourthQuarterAccountPayment(supplier.getFourthQuarterAccountPayment());
                    supplierExportBO.setTotalAccountPayment(supplier.getTotalAccountPayment());
                    supplierExportBO.setFax(supplier.getFax());
                    supplierExportBO.setTaxNumber(supplier.getTaxNumber());
                    supplierExportBO.setBankName(supplier.getBankName());
                    supplierExportBO.setAccountNumber(supplier.getAccountNumber());
                    supplierExportBO.setTaxRate(supplier.getTaxRate());
                    supplierExportBO.setSort(supplier.getSort());
                    supplierExportBO.setRemark(supplier.getRemark());
                    supplierExportBO.setCreateTime(supplier.getCreateTime());
                    exportData.add(supplierExportBO);
                }
                ExcelUtils.export(response, "供应商信息", ExcelUtils.getSheetData(exportData));
            } else {
                var exportData = new ArrayList<SupplierExportEnBO>();
                for (var supplier : suppliers.getData()) {
                    var supplierExportEnBO = new SupplierExportEnBO();
                    supplierExportEnBO.setId(supplier.getId());
                    supplierExportEnBO.setSupplierName(supplier.getSupplierName());
                    supplierExportEnBO.setContact(supplier.getContact());
                    supplierExportEnBO.setContactNumber(supplier.getContactNumber());
                    supplierExportEnBO.setPhoneNumber(supplier.getPhoneNumber());
                    supplierExportEnBO.setAddress(supplier.getAddress());
                    supplierExportEnBO.setEmail(supplier.getEmail());
                    supplierExportEnBO.setStatus(supplier.getStatus());
                    supplierExportEnBO.setFirstQuarterAccountPayment(supplier.getFirstQuarterAccountPayment());
                    supplierExportEnBO.setSecondQuarterAccountPayment(supplier.getSecondQuarterAccountPayment());
                    supplierExportEnBO.setThirdQuarterAccountPayment(supplier.getThirdQuarterAccountPayment());
                    supplierExportEnBO.setFourthQuarterAccountPayment(supplier.getFourthQuarterAccountPayment());
                    supplierExportEnBO.setTotalAccountPayment(supplier.getTotalAccountPayment());
                    supplierExportEnBO.setFax(supplier.getFax());
                    supplierExportEnBO.setTaxNumber(supplier.getTaxNumber());
                    supplierExportEnBO.setBankName(supplier.getBankName());
                    supplierExportEnBO.setAccountNumber(supplier.getAccountNumber());
                    supplierExportEnBO.setTaxRate(supplier.getTaxRate());
                    supplierExportEnBO.setSort(supplier.getSort());
                    supplierExportEnBO.setRemark(supplier.getRemark());
                    supplierExportEnBO.setCreateTime(supplier.getCreateTime());
                    exportData.add(supplierExportEnBO);
                }
                ExcelUtils.export(response, "Supplier Info", ExcelUtils.getSheetData(exportData));
            }
        }
    }

    private BigDecimal convertToBigDecimal(Double value) {
        return value != null ? BigDecimal.valueOf(value) : null;
    }
}