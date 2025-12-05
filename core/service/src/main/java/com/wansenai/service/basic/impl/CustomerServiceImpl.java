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
import com.wansenai.bo.customer.CustomerExportBO;
import com.wansenai.bo.customer.CustomerExportEnBO;
import com.wansenai.entities.basic.Customer;
import com.wansenai.mappers.basic.CustomerMapper;
import com.wansenai.service.BaseService;
import com.wansenai.service.basic.CustomerService;
import com.wansenai.utils.SnowflakeIdUtil;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.CustomerCodeEnum;
import com.wansenai.utils.excel.ExcelUtils;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.basic.CustomerVO;
import com.wansenai.dto.basic.AddOrUpdateCustomerDTO;
import com.wansenai.dto.basic.QueryCustomerDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    private final BaseService baseService;

    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(BaseService baseService, CustomerMapper customerMapper) {
        this.baseService = baseService;
        this.customerMapper = customerMapper;
    }

    @Override
    public Response<Page<CustomerVO>> getCustomerPageList(QueryCustomerDTO queryCustomerDTO) {
        var page = new Page<Customer>(
                Optional.ofNullable(queryCustomerDTO).map(dto -> dto.getPage()).orElse(1L),
                Optional.ofNullable(queryCustomerDTO).map(dto -> dto.getPageSize()).orElse(10L)
        );

        var wrapper = new LambdaQueryWrapper<Customer>();
        if (queryCustomerDTO != null) {
            if (queryCustomerDTO.getCustomerName() != null) {
                wrapper.like(Customer::getCustomerName, queryCustomerDTO.getCustomerName());
            }
            if (queryCustomerDTO.getContact() != null) {
                wrapper.like(Customer::getContact, queryCustomerDTO.getContact());
            }
            if (queryCustomerDTO.getPhoneNumber() != null) {
                wrapper.like(Customer::getPhoneNumber, queryCustomerDTO.getPhoneNumber());
            }
            if (queryCustomerDTO.getStartDate() != null) {
                wrapper.ge(Customer::getCreateTime, queryCustomerDTO.getStartDate());
            }
            if (queryCustomerDTO.getEndDate() != null) {
                wrapper.le(Customer::getCreateTime, queryCustomerDTO.getEndDate());
            }
        }
        wrapper.eq(Customer::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(Customer::getCreateTime);

        var resultPage = customerMapper.selectPage(page, wrapper);
        var records = resultPage.getRecords();

        if (records.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
        }

        var voList = new ArrayList<CustomerVO>();
        for (var customer : records) {
            var vo = new CustomerVO();
            vo.setId(customer.getId());
            vo.setCustomerName(customer.getCustomerName());
            vo.setContact(customer.getContact());
            vo.setPhoneNumber(customer.getPhoneNumber());
            vo.setEmail(customer.getEmail());
            vo.setFax(customer.getFax());
            vo.setFirstQuarterAccountReceivable(customer.getFirstQuarterAccountReceivable());
            vo.setSecondQuarterAccountReceivable(customer.getSecondQuarterAccountReceivable());
            vo.setThirdQuarterAccountivable(customer.getThirdQuarterAccountReceivable());
            vo.setFourthQuarterAccountReceivable(customer.getFourthQuarterAccountReceivable());
            vo.setTotalAccountReceivable(customer.getTotalAccountReceivable());
            vo.setAddress(customer.getAddress());
            vo.setTaxNumber(customer.getTaxNumber());
            vo.setBankName(customer.getBankName());
            vo.setAccountNumber(customer.getAccountNumber());
            vo.setTaxRate(customer.getTaxRate());
            vo.setStatus(customer.getStatus());
            vo.setRemark(customer.getRemark());
            vo.setSort(customer.getSort());
            vo.setCreateTime(customer.getCreateTime());
            voList.add(vo);
        }

        var resultVoPage = new Page<CustomerVO>();
        resultVoPage.setRecords(voList);
        resultVoPage.setTotal(resultPage.getTotal());
        resultVoPage.setPages(resultPage.getPages());
        resultVoPage.setSize(resultPage.getSize());

        return Response.responseData(resultVoPage);
    }

    @Override
    public Response<List<CustomerVO>> getCustomerList(QueryCustomerDTO queryCustomerDTO) {
        var wrapper = new LambdaQueryWrapper<Customer>();
        if (queryCustomerDTO != null) {
            if (queryCustomerDTO.getCustomerName() != null) {
                wrapper.like(Customer::getCustomerName, queryCustomerDTO.getCustomerName());
            }
            if (queryCustomerDTO.getContact() != null) {
                wrapper.like(Customer::getContact, queryCustomerDTO.getContact());
            }
            if (queryCustomerDTO.getPhoneNumber() != null) {
                wrapper.like(Customer::getPhoneNumber, queryCustomerDTO.getPhoneNumber());
            }
            if (queryCustomerDTO.getStartDate() != null) {
                wrapper.ge(Customer::getCreateTime, queryCustomerDTO.getStartDate());
            }
            if (queryCustomerDTO.getEndDate() != null) {
                wrapper.le(Customer::getCreateTime, queryCustomerDTO.getEndDate());
            }
        }
        wrapper.eq(Customer::getStatus, CommonConstants.STATUS_NORMAL)
                .eq(Customer::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByAsc(Customer::getSort);

        var list = customerMapper.selectList(wrapper);
        var voList = new ArrayList<CustomerVO>();

        for (var customer : list) {
            var vo = new CustomerVO();
            vo.setId(customer.getId());
            vo.setCustomerName(customer.getCustomerName());
            vo.setContact(customer.getContact());
            vo.setPhoneNumber(customer.getPhoneNumber());
            vo.setEmail(customer.getEmail());
            vo.setFax(customer.getFax());
            vo.setFirstQuarterAccountReceivable(customer.getFirstQuarterAccountReceivable());
            vo.setSecondQuarterAccountReceivable(customer.getSecondQuarterAccountReceivable());
            vo.setThirdQuarterAccountivable(customer.getThirdQuarterAccountReceivable());
            vo.setFourthQuarterAccountReceivable(customer.getFourthQuarterAccountReceivable());
            vo.setTotalAccountReceivable(customer.getTotalAccountReceivable());
            vo.setAddress(customer.getAddress());
            vo.setTaxNumber(customer.getTaxNumber());
            vo.setBankName(customer.getBankName());
            vo.setAccountNumber(customer.getAccountNumber());
            vo.setTaxRate(customer.getTaxRate());
            vo.setStatus(customer.getStatus());
            vo.setRemark(customer.getRemark());
            vo.setSort(customer.getSort());
            vo.setCreateTime(customer.getCreateTime());
            voList.add(vo);
        }

        return Response.responseData(voList);
    }

    public BigDecimal calculateTotalAccount(List<BigDecimal> list) {
        return list.stream()
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(3, RoundingMode.HALF_UP);
    }

    @Transactional
    @Override
    public Response<String> addOrUpdateCustomer(AddOrUpdateCustomerDTO addOrUpdateCustomerDTO) {
        var userId = baseService.getCurrentUserId();
        var isAdd = addOrUpdateCustomerDTO.getId() == null;
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        var customer = new Customer();
        customer.setId(isAdd ? SnowflakeIdUtil.nextId() : addOrUpdateCustomerDTO.getId());
        customer.setCustomerName(Optional.ofNullable(addOrUpdateCustomerDTO.getCustomerName()).orElse(""));
        customer.setContact(Optional.ofNullable(addOrUpdateCustomerDTO.getContact()).orElse(""));
        customer.setPhoneNumber(Optional.ofNullable(addOrUpdateCustomerDTO.getPhoneNumber()).orElse(""));
        customer.setEmail(Optional.ofNullable(addOrUpdateCustomerDTO.getEmail()).orElse(""));
        customer.setFirstQuarterAccountReceivable(Optional.ofNullable(addOrUpdateCustomerDTO.getFirstQuarterAccountReceivable()).orElse(BigDecimal.ZERO));
        customer.setSecondQuarterAccountReceivable(Optional.ofNullable(addOrUpdateCustomerDTO.getSecondQuarterAccountReceivable()).orElse(BigDecimal.ZERO));
        customer.setThirdQuarterAccountReceivable(Optional.ofNullable(addOrUpdateCustomerDTO.getThirdQuarterAccountReceivable()).orElse(BigDecimal.ZERO));
        customer.setFourthQuarterAccountReceivable(Optional.ofNullable(addOrUpdateCustomerDTO.getFourthQuarterAccountReceivable()).orElse(BigDecimal.ZERO));

        var total = calculateTotalAccount(Arrays.asList(
                Optional.ofNullable(addOrUpdateCustomerDTO.getFirstQuarterAccountReceivable()).orElse(BigDecimal.ZERO),
                Optional.ofNullable(addOrUpdateCustomerDTO.getSecondQuarterAccountReceivable()).orElse(BigDecimal.ZERO),
                Optional.ofNullable(addOrUpdateCustomerDTO.getThirdQuarterAccountReceivable()).orElse(BigDecimal.ZERO),
                Optional.ofNullable(addOrUpdateCustomerDTO.getFourthQuarterAccountReceivable()).orElse(BigDecimal.ZERO)
        ));
        customer.setTotalAccountReceivable(total);

        customer.setFax(Optional.ofNullable(addOrUpdateCustomerDTO.getFax()).orElse(""));
        customer.setAddress(Optional.ofNullable(addOrUpdateCustomerDTO.getAddress()).orElse(""));
        customer.setTaxNumber(Optional.ofNullable(addOrUpdateCustomerDTO.getTaxNumber()).orElse(""));
        customer.setBankName(Optional.ofNullable(addOrUpdateCustomerDTO.getBankName()).orElse(""));
        customer.setAccountNumber(Optional.ofNullable(addOrUpdateCustomerDTO.getAccountNumber()).orElse(""));
        customer.setTaxRate(Optional.ofNullable(addOrUpdateCustomerDTO.getTaxRate()).orElse(BigDecimal.ZERO));
        customer.setStatus(Optional.ofNullable(addOrUpdateCustomerDTO.getStatus()).orElse(0));
        customer.setRemark(Optional.ofNullable(addOrUpdateCustomerDTO.getRemark()).orElse(""));
        customer.setSort(Optional.ofNullable(addOrUpdateCustomerDTO.getSort()).orElse(0));

        if (isAdd) {
            customer.setCreateTime(LocalDateTime.now());
            customer.setCreateBy(userId);
        } else {
            customer.setUpdateTime(LocalDateTime.now());
            customer.setUpdateBy(userId);
        }

        var saveResult = saveOrUpdate(customer);
        if ("zh_CN".equals(systemLanguage)) {
            if (saveResult && isAdd) {
                return Response.responseMsg(CustomerCodeEnum.ADD_CUSTOMER_SUCCESS);
            } else if (saveResult) {
                return Response.responseMsg(CustomerCodeEnum.UPDATE_CUSTOMER_SUCCESS);
            } else {
                return Response.fail();
            }
        } else {
            if (saveResult && isAdd) {
                return Response.responseMsg(CustomerCodeEnum.ADD_CUSTOMER_SUCCESS_EN);
            } else if (saveResult) {
                return Response.responseMsg(CustomerCodeEnum.UPDATE_CUSTOMER_SUCCESS_EN);
            } else {
                return Response.fail();
            }
        }
    }

    @Override
    public Response<String> deleteCustomer(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var updateResult = customerMapper.deleteBatchIds(ids);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (updateResult > 0) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(CustomerCodeEnum.DELETE_CUSTOMER_SUCCESS);
            } else {
                return Response.responseMsg(CustomerCodeEnum.DELETE_CUSTOMER_SUCCESS_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(CustomerCodeEnum.DELETE_CUSTOMER_ERROR);
            } else {
                return Response.responseMsg(CustomerCodeEnum.DELETE_CUSTOMER_ERROR_EN);
            }
        }
    }

    @Override
    public Response<String> updateCustomerStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty() || status == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var updateResult = lambdaUpdate()
                .in(Customer::getId, ids)
                .set(Customer::getStatus, status)
                .update();

        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (!updateResult) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(CustomerCodeEnum.UPDATE_CUSTOMER_STATUS_ERROR);
            } else {
                return Response.responseMsg(CustomerCodeEnum.UPDATE_CUSTOMER_STATUS_ERROR_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(CustomerCodeEnum.UPDATE_CUSTOMER_STATUS_SUCCESS);
            } else {
                return Response.responseMsg(CustomerCodeEnum.UPDATE_CUSTOMER_STATUS_SUCCESS_EN);
            }
        }
    }

    @Transactional
    @Override
    public Boolean batchAddCustomer(List<Customer> customers) {
        var customerEntities = new ArrayList<Customer>();
        var existingCustomers = new HashSet<String>(); // 使用字符串组合作为键

        if (customers != null) {
            for (var customer : customers) {
                var customerKey = customer.getCustomerName() + "|" + customer.getContact();
                if (!existingCustomers.contains(customerKey)) {
                    var customerEntity = customerMapper.selectOne(
                            new LambdaQueryWrapper<Customer>()
                                    .eq(Customer::getCustomerName, customer.getCustomerName())
                                    .eq(Customer::getContact, customer.getContact())
                    );
                    if (customerEntity == null) {
                        var newCustomerEntity = new Customer();
                        BeanUtils.copyProperties(customer, newCustomerEntity);
                        newCustomerEntity.setFirstQuarterAccountReceivable(customer.getFirstQuarterAccountReceivable());
                        newCustomerEntity.setSecondQuarterAccountReceivable(customer.getSecondQuarterAccountReceivable());
                        newCustomerEntity.setThirdQuarterAccountReceivable(customer.getThirdQuarterAccountReceivable());
                        newCustomerEntity.setFourthQuarterAccountReceivable(customer.getFourthQuarterAccountReceivable());
                        newCustomerEntity.setTaxRate(customer.getTaxRate());

                        var total = calculateTotalAccount(List.of(
                                customer.getFirstQuarterAccountReceivable(),
                                customer.getSecondQuarterAccountReceivable(),
                                customer.getThirdQuarterAccountReceivable(),
                                customer.getFourthQuarterAccountReceivable()
                        ));
                        newCustomerEntity.setTotalAccountReceivable(total);

                        newCustomerEntity.setCreateTime(LocalDateTime.now());
                        newCustomerEntity.setCreateBy(baseService.getCurrentUserId());

                        customerEntities.add(newCustomerEntity);
                        existingCustomers.add(customerKey);
                    }
                }
            }
        }
        return saveBatch(customerEntities);
    }

    @Override
    public void exportCustomerData(QueryCustomerDTO queryCustomerDTO, HttpServletResponse response) {
        var customers = getCustomerList(queryCustomerDTO);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (customers.getData() != null && !customers.getData().isEmpty()) {
            if ("zh_CN".equals(systemLanguage)) {
                var exportData = new ArrayList<CustomerExportBO>();
                for (var customer : customers.getData()) {
                    var customerExportBO = new CustomerExportBO();
                    customerExportBO.setId(customer.getId());
                    customerExportBO.setCustomerName(customer.getCustomerName());
                    customerExportBO.setContact(customer.getContact());
                    customerExportBO.setPhoneNumber(customer.getPhoneNumber());
                    customerExportBO.setEmail(customer.getEmail());
                    customerExportBO.setFax(customer.getFax());
                    customerExportBO.setFirstQuarterAccountReceivable(customer.getFirstQuarterAccountReceivable());
                    customerExportBO.setSecondQuarterAccountReceivable(customer.getSecondQuarterAccountReceivable());
                    customerExportBO.setThirdQuarterAccountReceivable(customer.getThirdQuarterAccountivable());
                    customerExportBO.setFourthQuarterAccountReceivable(customer.getFourthQuarterAccountReceivable());
                    customerExportBO.setTotalAccountReceivable(customer.getTotalAccountReceivable());
                    customerExportBO.setAddress(customer.getAddress());
                    customerExportBO.setTaxNumber(customer.getTaxNumber());
                    customerExportBO.setBankName(customer.getBankName());
                    customerExportBO.setAccountNumber(customer.getAccountNumber());
                    customerExportBO.setTaxRate(customer.getTaxRate());
                    customerExportBO.setStatus(customer.getStatus());
                    customerExportBO.setRemark(customer.getRemark());
                    customerExportBO.setSort(customer.getSort());
                    customerExportBO.setCreateTime(customer.getCreateTime());
                    exportData.add(customerExportBO);
                }
                ExcelUtils.export(response, "客户信息", ExcelUtils.getSheetData(exportData));
            } else {
                var exportData = new ArrayList<CustomerExportEnBO>();
                for (var customer : customers.getData()) {
                    var customerExportEnBO = new CustomerExportEnBO();
                    customerExportEnBO.setId(customer.getId());
                    customerExportEnBO.setCustomerName(customer.getCustomerName());
                    customerExportEnBO.setContact(customer.getContact());
                    customerExportEnBO.setPhoneNumber(customer.getPhoneNumber());
                    customerExportEnBO.setEmail(customer.getEmail());
                    customerExportEnBO.setFax(customer.getFax());
                    customerExportEnBO.setFirstQuarterAccountReceivable(customer.getFirstQuarterAccountReceivable());
                    customerExportEnBO.setSecondQuarterAccountReceivable(customer.getSecondQuarterAccountReceivable());
                    customerExportEnBO.setThirdQuarterAccountReceivable(customer.getThirdQuarterAccountivable());
                    customerExportEnBO.setFourthQuarterAccountReceivable(customer.getFourthQuarterAccountReceivable());
                    customerExportEnBO.setTotalAccountReceivable(customer.getTotalAccountReceivable());
                    customerExportEnBO.setAddress(customer.getAddress());
                    customerExportEnBO.setTaxNumber(customer.getTaxNumber());
                    customerExportEnBO.setBankName(customer.getBankName());
                    customerExportEnBO.setAccountNumber(customer.getAccountNumber());
                    customerExportEnBO.setTaxRate(customer.getTaxRate());
                    customerExportEnBO.setStatus(customer.getStatus());
                    customerExportEnBO.setRemark(customer.getRemark());
                    customerExportEnBO.setSort(customer.getSort());
                    customerExportEnBO.setCreateTime(customer.getCreateTime());
                    exportData.add(customerExportEnBO);
                }
                ExcelUtils.export(response, "Customer Info", ExcelUtils.getSheetData(exportData));
            }
        }
    }
}