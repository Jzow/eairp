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
package com.wansenai.service.product.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.wansenai.dto.product.AddOrUpdateProductCategoryDTO
import com.wansenai.entities.product.ProductCategory
import com.wansenai.mappers.product.ProductCategoryMapper
import com.wansenai.service.product.ProductCategoryService
import com.wansenai.service.user.ISysUserService
import com.wansenai.vo.product.ProductCategoryVO
import com.wansenai.utils.SnowflakeIdUtil
import com.wansenai.utils.constants.CommonConstants
import com.wansenai.utils.enums.BaseCodeEnum
import com.wansenai.utils.enums.ProdcutCodeEnum
import com.wansenai.utils.response.Response
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime

@Service
open class ProductCategoryServiceImpl(
    private val userService: ISysUserService
) : ServiceImpl<ProductCategoryMapper, ProductCategory>(), ProductCategoryService {

    override fun productCategoryList(): Response<List<ProductCategoryVO>> {
        val productCategoryVOs = mutableListOf<ProductCategoryVO>()
        val productCategories = lambdaQuery()
            .eq(ProductCategory::getDeleteFlag, CommonConstants.NOT_DELETED)
            .orderByDesc(ProductCategory::getCreateTime)
            .list()
        productCategories.forEach {
            val productCategoryVO = ProductCategoryVO()
            // 获取item的父级分类名称
            val parentId = it.parentId
            if (parentId != null) {
                val parentCategory = lambdaQuery()
                    .eq(ProductCategory::getId, parentId)
                    .one()
                productCategoryVO.parentName = parentCategory.categoryName
            }
            BeanUtils.copyProperties(it, productCategoryVO)
            productCategoryVOs.add(productCategoryVO)
        }

        return Response.responseData(productCategoryVOs)
    }

    override fun addOrUpdateProductCategory(productCategory: AddOrUpdateProductCategoryDTO): Response<String> {
        val systemLanguage = userService.getUserSystemLanguage(userService.currentUserId)
        productCategory.let { dto ->
            val userId = userService.getCurrentTenantId().toLong()
            if (dto.id == null) {
                // Add product category
                val savaResult = save(
                    ProductCategory.builder()
                        .id(SnowflakeIdUtil.nextId())
                        .tenantId(userId)
                        .categoryName(dto.categoryName)
                        .categoryNumber(dto.categoryNumber)
                        .parentId(dto.parentId)
                        .sort(dto.sort)
                        .remark(dto.remark)
                        .createTime(LocalDateTime.now())
                        .createBy(userId)
                        .build()
                )
                if (systemLanguage == "zh_CN") {
                    if (!savaResult) {
                        return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_CATEGORY_ERROR)
                    }
                    return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_CATEGORY_SUCCESS)
                } else {
                    if (!savaResult) {
                        return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_CATEGORY_ERROR_EN)
                    }
                    return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_CATEGORY_SUCCESS_EN)
                }

            } else {
                val updateResult = lambdaUpdate()
                    .eq(ProductCategory::getId, dto.id)
                    .apply {
                        set(StringUtils.hasText(dto.categoryName), ProductCategory::getCategoryName, dto.categoryName)
                        set(StringUtils.hasText(dto.categoryNumber), ProductCategory::getCategoryNumber, dto.categoryNumber)
                        set(dto.parentId != null, ProductCategory::getParentId, dto.parentId)
                        set(dto.sort != null, ProductCategory::getSort, dto.sort)
                        set(StringUtils.hasText(dto.remark), ProductCategory::getRemark, dto.remark)
                        set(ProductCategory::getUpdateTime, LocalDateTime.now())
                        set(ProductCategory::getUpdateBy, userId)
                    }
                    .update()

                if (systemLanguage == "zh_CN") {
                    if (!updateResult) {
                        return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_CATEGORY_ERROR)
                    }
                    return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_CATEGORY_SUCCESS)
                } else {
                    if (!updateResult) {
                        return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_CATEGORY_ERROR_EN)
                    }
                    return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_CATEGORY_SUCCESS_EN)
                }
            }
        }
    }

    override fun deleteProductCategory(ids: List<Long>?): Response<String> {
        // 如果id为空返回参数错误 否则进行逻辑删除产品分类id
        if(ids.isNullOrEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL)
        }
        val deleteResult = lambdaUpdate()
            .`in`(ProductCategory::getId, ids)
            .set(ProductCategory::getDeleteFlag, CommonConstants.DELETED)
            .update()

        val systemLanguage = userService.getUserSystemLanguage(userService.currentUserId)
        if (systemLanguage == "zh_CN") {
            if(!deleteResult){
                return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_CATEGORY_ERROR)
            }
            return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_CATEGORY_SUCCESS)
        } else {
            if(!deleteResult){
                return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_CATEGORY_ERROR_EN)
            }
            return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_CATEGORY_SUCCESS_EN)
        }
    }

    override fun getProductCategoryByName(name: String?): ProductCategory {
        // 如果name为空就返回空对象 否则根据name查询产品分类
        if(StringUtils.hasLength(name)){
            return ProductCategory()
        }
        return lambdaQuery()
            .eq(ProductCategory::getCategoryName, name)
            .one()
    }
}