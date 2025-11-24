package com.wansenai.service.product.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.dto.product.ProductUnitQueryDTO;
import com.wansenai.dto.product.AddOrUpdateProductUnitDTO;
import com.wansenai.dto.product.ProductUnitStatusDTO;
import com.wansenai.entities.product.ProductUnit;
import com.wansenai.mappers.product.ProductUnitMapper;
import com.wansenai.service.BaseService;
import com.wansenai.service.product.ProductUnitService;
import com.wansenai.utils.SnowflakeIdUtil;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.ProdcutCodeEnum;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.product.ProductUnitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProductUnitServiceImpl extends ServiceImpl<ProductUnitMapper, ProductUnit> implements ProductUnitService {

    private final ProductUnitMapper productUnitMapper;
    private final BaseService baseService;

    public ProductUnitServiceImpl(ProductUnitMapper productUnitMapper, BaseService baseService) {
        this.productUnitMapper = productUnitMapper;
        this.baseService = baseService;
    }

    @Override
    public Response<Page<ProductUnitVO>> productUnitList(ProductUnitQueryDTO productUnitQuery) {
        var page = new Page<ProductUnit>(
                Optional.ofNullable(productUnitQuery).map(ProductUnitQueryDTO::getPage).orElse(1L),
                Optional.ofNullable(productUnitQuery).map(ProductUnitQueryDTO::getPageSize).orElse(10L)
        );

        var wrapper = new LambdaQueryWrapper<ProductUnit>();
        if (productUnitQuery != null && productUnitQuery.getComputeUnit() != null) {
            wrapper.like(ProductUnit::getComputeUnit, productUnitQuery.getComputeUnit());
        }
        wrapper.eq(ProductUnit::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(ProductUnit::getCreateTime);

        var resultPage = productUnitMapper.selectPage(page, wrapper);
        var records = resultPage.getRecords();

        if (records.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
        }

        var voList = new ArrayList<ProductUnitVO>();
        for (var unit : records) {
            var vo = new ProductUnitVO();
            vo.setId(unit.getId());
            vo.setComputeUnit(unit.getComputeUnit());
            vo.setBasicUnit(unit.getBasicUnit());
            vo.setOtherUnit(unit.getOtherUnit());
            vo.setOtherUnitTwo(unit.getOtherUnitTwo());
            vo.setOtherUnitThree(unit.getOtherUnitThree());
            vo.setRatio(unit.getRatio());
            vo.setRatioTwo(unit.getRatioTwo());
            vo.setRatioThree(unit.getRatioThree());
            vo.setStatus(unit.getStatus());
            vo.setCreateTime(unit.getCreateTime());
            vo.setOtherComputeUnit(formatBigDecimal(unit.getRatio(), unit.getOtherUnit(), unit.getBasicUnit()));
            vo.setOtherComputeUnitTwo(formatBigDecimal(unit.getRatioTwo(), unit.getOtherUnitTwo(), unit.getBasicUnit()));
            vo.setOtherComputeUnitThree(formatBigDecimal(unit.getRatioThree(), unit.getOtherUnitThree(), unit.getBasicUnit()));
            voList.add(vo);
        }

        var resultVoPage = new Page<ProductUnitVO>();
        resultVoPage.setRecords(voList);
        resultVoPage.setTotal(resultPage.getTotal());
        resultVoPage.setPages(resultPage.getPages());
        resultVoPage.setSize(resultPage.getSize());

        return Response.responseData(resultVoPage);
    }

    private String formatBigDecimal(BigDecimal ratio, String otherUnit, String basicUnit) {
        if (ratio == null) {
            return null;
        }

        var scaledValue = ratio.setScale(3, RoundingMode.HALF_UP);
        var formattedValue = scaledValue.stripTrailingZeros().scale() <= 0
                ? scaledValue.toBigInteger().toString()
                : scaledValue.toString();

        return otherUnit + "=" + formattedValue + basicUnit;
    }

    @Override
    public Response<String> addOrUpdateProductUnit(AddOrUpdateProductUnitDTO productUnit) {
        if (productUnit == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var unitId = Optional.ofNullable(productUnit.getId()).orElse(SnowflakeIdUtil.nextId());
        var wrapper = new LambdaQueryWrapper<ProductUnit>();
        wrapper.eq(ProductUnit::getComputeUnit, buildComputeUnit(productUnit))
                .eq(ProductUnit::getDeleteFlag, CommonConstants.NOT_DELETED);

        if (productUnit.getId() != null) {
            wrapper.ne(ProductUnit::getId, productUnit.getId());
        }

        var unitExists = productUnitMapper.exists(wrapper);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (unitExists) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(ProdcutCodeEnum.PRODUCT_COMPUTE_UNIT_EXIST);
            }
            return Response.responseMsg(ProdcutCodeEnum.PRODUCT_COMPUTE_UNIT_EXIST_EN);
        }

        var unitEntity = buildProductUnit(unitId, productUnit);
        var result = saveOrUpdate(unitEntity);

        if (result) {
            if (productUnit.getId() == null) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_ADD_SUCCESS);
                } else {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_ADD_SUCCESS_EN);
                }
            } else {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_UPDATE_SUCCESS);
                } else {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_UPDATE_SUCCESS_EN);
                }
            }
        } else {
            if (productUnit.getId() == null) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_ADD_ERROR);
                } else {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_ADD_ERROR_EN);
                }
            } else {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_UPDATE_ERROR);
                } else {
                    return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_UPDATE_ERROR_EN);
                }
            }
        }
    }

    private ProductUnit buildProductUnit(Long id, AddOrUpdateProductUnitDTO unit) {
        var creator = baseService.getCurrentUserId();
        var productUnit = new ProductUnit();
        productUnit.setId(id);
        productUnit.setComputeUnit(buildComputeUnit(unit));
        productUnit.setBasicUnit(unit.getBasicUnit());
        productUnit.setOtherUnit(unit.getOtherUnit());
        productUnit.setOtherUnitTwo(unit.getOtherUnitTwo());
        productUnit.setOtherUnitThree(unit.getOtherUnitThree());
        productUnit.setRatio(unit.getRatio());
        productUnit.setRatioTwo(unit.getRatioTwo());
        productUnit.setRatioThree(unit.getRatioThree());
        productUnit.setStatus(unit.getStatus());

        if (unit.getId() == null) {
            productUnit.setCreateTime(LocalDateTime.now());
            productUnit.setCreateBy(creator);
        } else {
            productUnit.setUpdateTime(LocalDateTime.now());
            productUnit.setUpdateBy(creator);
        }

        return productUnit;
    }

    private String buildComputeUnit(AddOrUpdateProductUnitDTO productUnit) {
        var computeUnit = new StringBuilder();
        computeUnit.append(productUnit.getBasicUnit())
                .append("/(")
                .append(productUnit.getOtherUnit())
                .append("=")
                .append(productUnit.getRatio())
                .append(productUnit.getBasicUnit())
                .append(")");

        if (productUnit.getOtherUnitTwo() != null) {
            computeUnit.append("/(")
                    .append(productUnit.getOtherUnitTwo())
                    .append("=")
                    .append(productUnit.getRatioTwo())
                    .append(productUnit.getBasicUnit())
                    .append(")");
        }

        if (productUnit.getOtherUnitThree() != null) {
            computeUnit.append("/(")
                    .append(productUnit.getOtherUnitThree())
                    .append("=")
                    .append(productUnit.getRatioThree())
                    .append(productUnit.getBasicUnit())
                    .append(")");
        }

        return computeUnit.toString();
    }

    @Override
    public Response<String> deleteProductUnit(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var deleteResult = productUnitMapper.deleteBatchIds(ids);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (deleteResult == 0) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_DELETE_ERROR);
            }
            return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_DELETE_ERROR_EN);
        }

        if ("zh_CN".equals(systemLanguage)) {
            return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_DELETE_SUCCESS);
        }
        return Response.responseMsg(ProdcutCodeEnum.PRODUCT_UNIT_DELETE_SUCCESS_EN);
    }

    @Override
    public Response<String> updateUnitStatus(ProductUnitStatusDTO productUnitStatus) {
        if (productUnitStatus == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var unit = new ProductUnit();
        unit.setId(productUnitStatus.getId());
        unit.setStatus(productUnitStatus.getStatus());

        var updateResult = productUnitMapper.updateById(unit);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (updateResult == 0) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_UNIT_STATUS_ERROR);
            } else {
                return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_UNIT_STATUS_ERROR_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_UNIT_STATUS_SUCCESS);
            } else {
                return Response.responseMsg(ProdcutCodeEnum.UPDATE_PRODUCT_UNIT_STATUS_SUCCESS_EN);
            }
        }
    }
}