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
package com.wansenai.service.product.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.entities.product.ProductCategory;
import com.wansenai.mappers.product.ProductCategoryMapper;
import com.wansenai.service.product.ProductCategoryService;
import com.wansenai.service.user.ISysUserService;
import com.wansenai.utils.SnowflakeIdUtil;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.ProdcutCodeEnum;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.product.ProductCategoryVO;
import com.wansenai.dto.product.AddOrUpdateProductCategoryDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {

    private final ISysUserService userService;

    public ProductCategoryServiceImpl(ISysUserService userService) {
        this.userService = userService;
    }

    @Override
    public Response<List<ProductCategoryVO>> productCategoryList() {
        var productCategoryVOs = new ArrayList<ProductCategoryVO>();
        var productCategories = lambdaQuery()
                .eq(ProductCategory::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(ProductCategory::getCreateTime)
                .list();

        for (var category : productCategories) {
            var productCategoryVO = new ProductCategoryVO();
            // 获取item的父级分类名称
            var parentId = category.getParentId();
            if (parentId != null) {
                var parentCategory = lambdaQuery()
                        .eq(ProductCategory::getId, parentId)
                        .one();
                if (parentCategory != null) {
                    productCategoryVO.setParentName(parentCategory.getCategoryName());
                }
            }
            BeanUtils.copyProperties(category, productCategoryVO);
            productCategoryVOs.add(productCategoryVO);
        }

        return Response.responseData(productCategoryVOs);
    }

    @Override
    public Response<String> addOrUpdateProductCategory(AddOrUpdateProductCategoryDTO productCategory) {
        if (productCategory == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var systemLanguage = userService.getUserSystemLanguage(userService.getCurrentUserId());
        var userId = userService.getCurrentTenantId();

        if (productCategory.getId() == null) {
            // Add product category
            var newCategory = ProductCategory.builder()
                    .id(SnowflakeIdUtil.nextId())
                    .tenantId(userId)
                    .categoryName(productCategory.getCategoryName())
                    .categoryNumber(productCategory.getCategoryNumber())
                    .parentId(productCategory.getParentId())
                    .sort(Optional.ofNullable(productCategory.getSort()).orElse(0))
                    .remark(productCategory.getRemark())
                    .createTime(LocalDateTime.now())
                    .createBy(userId)
                    .build();

            var saveResult = save(newCategory);

            if ("zh_CN".equals(systemLanguage)) {
                if (!saveResult) {
                    return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_CATEGORY_ERROR);
                }
                return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_CATEGORY_SUCCESS);
            } else {
                if (!saveResult) {
                    return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_CATEGORY_ERROR_EN);
                }
                return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_CATEGORY_SUCCESS_EN);
            }
        } else {
            var updateWrapper = lambdaUpdate()
                    .eq(ProductCategory::getId, productCategory.getId());

            if (StringUtils.hasText(productCategory.getCategoryName())) {
                updateWrapper.set(ProductCategory::getCategoryName, productCategory.getCategoryName());
            }
            if (StringUtils.hasText(productCategory.getCategoryNumber())) {
                updateWrapper.set(ProductCategory::getCategoryNumber, productCategory.getCategoryNumber());
            }
            if (productCategory.getParentId() != null) {
                updateWrapper.set(ProductCategory::getParentId, productCategory.getParentId());
            }
            if (productCategory.getSort() != null) {
                updateWrapper.set(ProductCategory::getSort, productCategory.getSort());
            }
            if (StringUtils.hasText(productCategory.getRemark())) {
                updateWrapper.set(ProductCategory::getRemark, productCategory.getRemark());
            }

            updateWrapper.set(ProductCategory::getUpdateTime, LocalDateTime.now())
                    .set(ProductCategory::getUpdateBy, userId);

            var updateResult = updateWrapper.update();

            if ("zh_CN".equals(systemLanguage)) {
                if (!updateResult) {
                    return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_CATEGORY_ERROR);
                }
                return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_CATEGORY_SUCCESS);
            } else {
                if (!updateResult) {
                    return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_CATEGORY_ERROR_EN);
                }
                return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_CATEGORY_SUCCESS_EN);
            }
        }
    }

    @Override
    public Response<String> deleteProductCategory(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var deleteResult = lambdaUpdate()
                .in(ProductCategory::getId, ids)
                .set(ProductCategory::getDeleteFlag, CommonConstants.DELETED)
                .update();

        var systemLanguage = userService.getUserSystemLanguage(userService.getCurrentUserId());
        if ("zh_CN".equals(systemLanguage)) {
            if (!deleteResult) {
                return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_CATEGORY_ERROR);
            }
            return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_CATEGORY_SUCCESS);
        } else {
            if (!deleteResult) {
                return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_CATEGORY_ERROR_EN);
            }
            return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_CATEGORY_SUCCESS_EN);
        }
    }

    @Override
    public ProductCategory getProductCategoryByName(String name) {
        if (!StringUtils.hasLength(name)) {
            return new ProductCategory();
        }
        return lambdaQuery()
                .eq(ProductCategory::getCategoryName, name)
                .one();
    }
}