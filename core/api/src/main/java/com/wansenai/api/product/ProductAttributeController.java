package com.wansenai.api.product;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wansenai.service.product.ProductAttributeService;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.product.ProductAttributeVO;
import com.wansenai.vo.product.ProductAttributeNameVO;
import com.wansenai.dto.product.ProductAttributeQueryDTO;
import com.wansenai.dto.product.AddOrUpdateProductAttributeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;

@RestController
@RequestMapping("/product/attribute")
public class ProductAttributeController {

    private final ProductAttributeService productAttributeService;

    @Autowired
    public ProductAttributeController(ProductAttributeService productAttributeService) {
        this.productAttributeService = productAttributeService;
    }

    @PostMapping("/list")
    public Response<Page<ProductAttributeVO>> productAttributeList(@RequestBody ProductAttributeQueryDTO productAttributeQueryDTO) {
        return productAttributeService.productAttributeList(productAttributeQueryDTO);
    }

    @PostMapping("/addOrUpdate")
    public Response<String> addOrUpdateProductAttribute(@RequestBody AddOrUpdateProductAttributeDTO productAttributeDTO) {
        return productAttributeService.addOrUpdateProductAttribute(productAttributeDTO);
    }

    @DeleteMapping("/deleteBatch")
    public Response<String> deleteProductAttribute(@RequestParam List<Long> ids) {
        return productAttributeService.batchDeleteProductAttribute(ids);
    }

    @GetMapping("/getValuesById")
    public List<ProductAttributeNameVO> getAttributeValuesById(@RequestParam("id") Long id) {
        return productAttributeService.getAttributeValuesById(id);
    }
}