package com.wansenai.service.product;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.entities.product.ProductAttribute;
import com.wansenai.utils.response.Response;
import com.wansenai.dto.product.ProductAttributeQueryDTO;
import com.wansenai.dto.product.AddOrUpdateProductAttributeDTO;
import com.wansenai.vo.product.ProductAttributeVO;
import com.wansenai.vo.product.ProductAttributeNameVO;
import java.util.List;

public interface ProductAttributeService extends IService<ProductAttribute> {

    Response<Page<ProductAttributeVO>> productAttributeList(ProductAttributeQueryDTO productAttributeQuery);

    Response<String> addOrUpdateProductAttribute(AddOrUpdateProductAttributeDTO productAttributeAddOrUpdate);

    Response<String> batchDeleteProductAttribute(List<Long> ids);

    List<ProductAttributeNameVO> getAttributeValuesById(Long id);
}