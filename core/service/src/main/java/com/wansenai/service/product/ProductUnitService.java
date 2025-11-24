package com.wansenai.service.product;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.entities.product.ProductUnit;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.product.ProductUnitVO;
import com.wansenai.dto.product.ProductUnitQueryDTO;
import com.wansenai.dto.product.AddOrUpdateProductUnitDTO;
import com.wansenai.dto.product.ProductUnitStatusDTO;
import java.util.List;

public interface ProductUnitService extends IService<ProductUnit> {

    Response<Page<ProductUnitVO>> productUnitList(ProductUnitQueryDTO productUnitQuery);

    Response<String> addOrUpdateProductUnit(AddOrUpdateProductUnitDTO productUnit);

    Response<String> deleteProductUnit(List<Long> ids);

    Response<String> updateUnitStatus(ProductUnitStatusDTO productUnitStatus);
}