package com.wansenai.api.product;

import com.wansenai.service.product.ProductCategoryService;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.product.ProductCategoryVO;
import com.wansenai.dto.product.AddOrUpdateProductCategoryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@RestController
@RequestMapping("/product/category")
public class ProductCategoryController {

    private final ProductCategoryService productCategoryService;

    @Autowired
    public ProductCategoryController(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @GetMapping("/list")
    public Response<List<ProductCategoryVO>> productCategoryList() {
        return productCategoryService.productCategoryList();
    }

    @PostMapping("/addOrUpdate")
    public Response<String> addOrUpdateProductCategory(@RequestBody AddOrUpdateProductCategoryDTO productCategory) {
        return productCategoryService.addOrUpdateProductCategory(productCategory);
    }

    @PostMapping("/deleteBatch")
    public Response<String> deleteProductCategory(@RequestParam List<Long> ids) {
        return productCategoryService.deleteProductCategory(ids);
    }
}