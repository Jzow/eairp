package com.wansenai.service.product;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.entities.product.ProductCategory;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.product.ProductCategoryVO;
import com.wansenai.dto.product.AddOrUpdateProductCategoryDTO;
import java.util.List;

public interface ProductCategoryService extends IService<ProductCategory> {

    Response<List<ProductCategoryVO>> productCategoryList();

    Response<String> addOrUpdateProductCategory(AddOrUpdateProductCategoryDTO productCategory);

    Response<String> deleteProductCategory(List<Long> ids);

    ProductCategory getProductCategoryByName(String name);
}