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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.wansenai.dto.product.AddOrUpdateProductAttributeDTO
import com.wansenai.dto.product.ProductAttributeQueryDTO
import com.wansenai.entities.product.ProductAttribute
import com.wansenai.mappers.product.ProductAttributeMapper
import com.wansenai.service.BaseService
import com.wansenai.service.product.ProductAttributeService
import com.wansenai.service.user.ISysUserService
import com.wansenai.utils.SnowflakeIdUtil
import com.wansenai.utils.constants.CommonConstants
import com.wansenai.utils.enums.BaseCodeEnum
import com.wansenai.utils.enums.ProdcutCodeEnum
import com.wansenai.utils.response.Response
import com.wansenai.vo.product.ProductAttributeNameVO
import com.wansenai.vo.product.ProductAttributeVO
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
open class ProductAttributeServiceImpl(
    private val productAttributeMapper: ProductAttributeMapper,
    private val userService: ISysUserService,
    private val baseService: BaseService,
):ServiceImpl<ProductAttributeMapper, ProductAttribute>(), ProductAttributeService {

    override fun productAttributeList(productAttributeQuery : ProductAttributeQueryDTO?): Response<Page<ProductAttributeVO>> {
        val page = productAttributeQuery?.run { Page<ProductAttribute>(page ?: 1, pageSize ?: 10) }
        val wrapper = LambdaQueryWrapper<ProductAttribute>().apply() {
            productAttributeQuery?.attributeName?.let { like(ProductAttribute::getAttributeName, it) }
            eq(ProductAttribute::getDeleteFlag, CommonConstants.NOT_DELETED)
            orderByDesc(ProductAttribute::getCreateTime)
        }

        val result = page?.run {
            productAttributeMapper.selectPage(this, wrapper)
            val listVo = records.map { attribute ->
                ProductAttributeVO().apply {
                    BeanUtils.copyProperties(attribute, this)
                }
            }
            Page<ProductAttributeVO>().apply {
                records = listVo
                total = this@run.total
                pages = this@run.pages
                size = this@run.size
            }
        } ?: Page<ProductAttributeVO>()

        return Response.responseData(result)
    }

    override fun addOrUpdateProductAttribute(productAttributeAddOrUpdate: AddOrUpdateProductAttributeDTO?): Response<String> {
        productAttributeAddOrUpdate?.run {
            val attribute = ProductAttribute().apply {
                BeanUtils.copyProperties(this@run, this)
            }
            val systemLanguage = baseService.currentUserSystemLanguage
            val userId = userService.currentUserId
            when (attribute.id) {
                null -> {
                    val wrapper = createWrapper(attribute)
                    val count = getCount(wrapper)
                    if (count > 0) {
                        if (systemLanguage == "zh_CN") {
                            return Response.responseMsg(ProdcutCodeEnum.PRODUCT_ATTRIBUTE_NAME_EXIST)
                        }
                        return Response.responseMsg(ProdcutCodeEnum.PRODUCT_ATTRIBUTE_NAME_EXIST_EN)
                    }
                    attribute.id = SnowflakeIdUtil.nextId()
                    attribute.createTime = LocalDateTime.now()
                    attribute.createBy = userId
                    val saveResult = saveAttribute(attribute)
                    if (saveResult == 0) {
                        if (systemLanguage == "zh_CN") {
                            return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_ATTRIBUTE_ERROR)
                        }
                        return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_ATTRIBUTE_ERROR_EN)
                    } else {
                        if (systemLanguage == "zh_CN") {
                            return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_ATTRIBUTE_SUCCESS)
                        }
                        return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_ATTRIBUTE_SUCCESS_EN)
                    }
                }
                else -> {
                    val wrapper = createWrapper(attribute).ne(ProductAttribute::getId, attribute.id)
                    val count = getCount(wrapper)
                    if (count > 0) {
                        if (systemLanguage == "zh_CN") {
                            return Response.responseMsg(ProdcutCodeEnum.PRODUCT_ATTRIBUTE_NAME_EXIST)
                        }
                        return Response.responseMsg(ProdcutCodeEnum.PRODUCT_ATTRIBUTE_NAME_EXIST_EN)
                    }
                    attribute.updateBy = userId
                    attribute.updateTime = LocalDateTime.now()
                    val updateResult = updateAttribute(attribute)
                    if (updateResult == 0) {
                        if (systemLanguage == "zh_CN") {
                            return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_ATTRIBUTE_ERROR)
                        }
                        return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_ATTRIBUTE_ERROR_EN)
                    } else {
                        if (systemLanguage == "zh_CN") {
                            return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_ATTRIBUTE_SUCCESS)
                        }
                        return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_ATTRIBUTE_SUCCESS_EN)
                    }
                }
            }
        } ?: return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL)
    }

    private fun createWrapper(attribute: ProductAttribute) = LambdaQueryWrapper<ProductAttribute>().apply {
        eq(ProductAttribute::getAttributeName, attribute.attributeName)
        eq(ProductAttribute::getDeleteFlag, CommonConstants.NOT_DELETED)
    }

    private fun getCount(wrapper: LambdaQueryWrapper<ProductAttribute>) = productAttributeMapper.selectCount(wrapper)

    private fun saveAttribute(attribute: ProductAttribute) = productAttributeMapper.insert(attribute)

    private fun updateAttribute(attribute: ProductAttribute) = productAttributeMapper.updateById(attribute)

    override fun batchDeleteProductAttribute(ids: List<Long>?): Response<String> {
        // Change the status from unmodified to physically deleted data
        ids?.let {
            val deleteResult = productAttributeMapper.deleteBatchIds(ids)
            val systemLanguage = baseService.currentUserSystemLanguage
            if(deleteResult == 0) {
                if (systemLanguage == "zh_CN") {
                    return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_ATTRIBUTE_ERROR)
                }
                return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_ATTRIBUTE_ERROR_EN)
            }
            if (systemLanguage == "zh_CN") {
                return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_ATTRIBUTE_SUCCESS)
            }
            return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_ATTRIBUTE_SUCCESS_EN)
        }?: return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL)
    }

    override fun getAttributeValuesById(id: Long?): List<ProductAttributeNameVO> {
        return id?.let {
            val attribute = productAttributeMapper.selectById(id)
            attribute?.run {
                val values = attribute.attributeValue?.split("|")
                values?.map { value ->
                    ProductAttributeNameVO().apply {
                        name = attribute.attributeName
                        this.value = value
                    }
                } ?: emptyList()
            } ?: emptyList()
        } ?: emptyList()
    }

}