package com.wansenai.service.product.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.entities.product.ProductAttribute;
import com.wansenai.mappers.product.ProductAttributeMapper;
import com.wansenai.service.BaseService;
import com.wansenai.service.product.ProductAttributeService;
import com.wansenai.service.user.ISysUserService;
import com.wansenai.utils.SnowflakeIdUtil;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.ProdcutCodeEnum;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.product.ProductAttributeVO;
import com.wansenai.vo.product.ProductAttributeNameVO;
import com.wansenai.dto.product.ProductAttributeQueryDTO;
import com.wansenai.dto.product.AddOrUpdateProductAttributeDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductAttributeServiceImpl extends ServiceImpl<ProductAttributeMapper, ProductAttribute> implements ProductAttributeService {

    private final ProductAttributeMapper productAttributeMapper;
    private final ISysUserService userService;
    private final BaseService baseService;

    public ProductAttributeServiceImpl(
            ProductAttributeMapper productAttributeMapper,
            ISysUserService userService,
            BaseService baseService) {
        this.productAttributeMapper = productAttributeMapper;
        this.userService = userService;
        this.baseService = baseService;
    }

    @Override
    public Response<Page<ProductAttributeVO>> productAttributeList(ProductAttributeQueryDTO productAttributeQuery) {
        var page = new Page<ProductAttribute>(
                Optional.ofNullable(productAttributeQuery).map(dto -> dto.getPage()).orElse(1L),
                Optional.ofNullable(productAttributeQuery).map(dto -> dto.getPageSize()).orElse(10L)
        );

        var wrapper = new LambdaQueryWrapper<ProductAttribute>();
        if (productAttributeQuery != null && productAttributeQuery.getAttributeName() != null) {
            wrapper.like(ProductAttribute::getAttributeName, productAttributeQuery.getAttributeName());
        }
        wrapper.eq(ProductAttribute::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(ProductAttribute::getCreateTime);

        var resultPage = productAttributeMapper.selectPage(page, wrapper);
        var records = resultPage.getRecords();

        var voList = new ArrayList<ProductAttributeVO>();
        for (var attribute : records) {
            var vo = new ProductAttributeVO();
            BeanUtils.copyProperties(attribute, vo);
            voList.add(vo);
        }

        var resultVoPage = new Page<ProductAttributeVO>();
        resultVoPage.setRecords(voList);
        resultVoPage.setTotal(resultPage.getTotal());
        resultVoPage.setPages(resultPage.getPages());
        resultVoPage.setSize(resultPage.getSize());

        return Response.responseData(resultVoPage);
    }

    @Override
    public Response<String> addOrUpdateProductAttribute(AddOrUpdateProductAttributeDTO productAttributeAddOrUpdate) {
        if (productAttributeAddOrUpdate == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var attribute = new ProductAttribute();
        BeanUtils.copyProperties(productAttributeAddOrUpdate, attribute);

        var systemLanguage = baseService.getCurrentUserSystemLanguage();
        var userId = userService.getCurrentUserId();

        if (attribute.getId() == null) {
            var wrapper = createWrapper(attribute);
            var count = getCount(wrapper);
            if (count > 0) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_ATTRIBUTE_NAME_EXIST);
                }
                return Response.responseMsg(ProdcutCodeEnum.PRODUCT_ATTRIBUTE_NAME_EXIST_EN);
            }

            attribute.setId(SnowflakeIdUtil.nextId());
            attribute.setCreateTime(LocalDateTime.now());
            attribute.setCreateBy(userId);
            var saveResult = saveAttribute(attribute);
            if (saveResult == 0) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_ATTRIBUTE_ERROR);
                }
                return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_ATTRIBUTE_ERROR_EN);
            } else {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_ATTRIBUTE_SUCCESS);
                }
                return Response.responseMsg(ProdcutCodeEnum.ADD_PRODUCT_ATTRIBUTE_SUCCESS_EN);
            }
        } else {
            var wrapper = createWrapper(attribute).ne(ProductAttribute::getId, attribute.getId());
            var count = getCount(wrapper);
            if (count > 0) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_ATTRIBUTE_NAME_EXIST);
                }
                return Response.responseMsg(ProdcutCodeEnum.PRODUCT_ATTRIBUTE_NAME_EXIST_EN);
            }

            attribute.setUpdateBy(userId);
            attribute.setUpdateTime(LocalDateTime.now());
            var updateResult = updateAttribute(attribute);
            if (updateResult == 0) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_ATTRIBUTE_ERROR);
                }
                return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_ATTRIBUTE_ERROR_EN);
            } else {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_ATTRIBUTE_SUCCESS);
                }
                return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_ATTRIBUTE_SUCCESS_EN);
            }
        }
    }

    private LambdaQueryWrapper<ProductAttribute> createWrapper(ProductAttribute attribute) {
        var wrapper = new LambdaQueryWrapper<ProductAttribute>();
        wrapper.eq(ProductAttribute::getAttributeName, attribute.getAttributeName())
                .eq(ProductAttribute::getDeleteFlag, CommonConstants.NOT_DELETED);
        return wrapper;
    }

    private Long getCount(LambdaQueryWrapper<ProductAttribute> wrapper) {
        return productAttributeMapper.selectCount(wrapper);
    }

    private int saveAttribute(ProductAttribute attribute) {
        return productAttributeMapper.insert(attribute);
    }

    private int updateAttribute(ProductAttribute attribute) {
        return productAttributeMapper.updateById(attribute);
    }

    @Override
    public Response<String> batchDeleteProductAttribute(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var deleteResult = productAttributeMapper.deleteBatchIds(ids);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (deleteResult == 0) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_ATTRIBUTE_ERROR);
            }
            return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_ATTRIBUTE_ERROR_EN);
        }

        if ("zh_CN".equals(systemLanguage)) {
            return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_ATTRIBUTE_SUCCESS);
        }
        return Response.responseMsg(ProdcutCodeEnum.DELETE_PRODUCT_ATTRIBUTE_SUCCESS_EN);
    }

    @Override
    public List<ProductAttributeNameVO> getAttributeValuesById(Long id) {
        if (id == null) {
            return new ArrayList<>();
        }

        var attribute = productAttributeMapper.selectById(id);
        if (attribute == null) {
            return new ArrayList<>();
        }

        if (attribute.getAttributeValue() == null || attribute.getAttributeValue().isEmpty()) {
            return new ArrayList<>();
        }

        var values = Arrays.asList(attribute.getAttributeValue().split("\\|"));
        return values.stream().map(value -> {
            var vo = new ProductAttributeNameVO();
            vo.setName(attribute.getAttributeName());
            vo.setValue(value);
            return vo;
        }).collect(Collectors.toList());
    }
}