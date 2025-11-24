package com.wansenai.api.product;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wansenai.service.product.ProductUnitService;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.product.ProductUnitVO;
import com.wansenai.dto.product.ProductUnitQueryDTO;
import com.wansenai.dto.product.AddOrUpdateProductUnitDTO;
import com.wansenai.dto.product.ProductUnitStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@RestController
@RequestMapping("/product/unit")
public class ProductUnitController {

    private final ProductUnitService productUnitService;

    @Autowired
    public ProductUnitController(ProductUnitService productUnitService) {
        this.productUnitService = productUnitService;
    }

    @PostMapping("/list")
    public Response<Page<ProductUnitVO>> productUnitList(@RequestBody ProductUnitQueryDTO productUnitQuery) {
        return productUnitService.productUnitList(productUnitQuery);
    }

    @PostMapping("/addOrUpdate")
    public Response<String> addOrUpdateProductUnit(@RequestBody AddOrUpdateProductUnitDTO productUnit) {
        return productUnitService.addOrUpdateProductUnit(productUnit);
    }

    @DeleteMapping("/deleteBatch")
    public Response<String> deleteProductUnit(@RequestParam List<Long> ids) {
        return productUnitService.deleteProductUnit(ids);
    }

    @PostMapping("/updateUnitStatus")
    public Response<String> updateUnitStatus(@RequestBody ProductUnitStatusDTO productUnitStatus) {
        return productUnitService.updateUnitStatus(productUnitStatus);
    }
}