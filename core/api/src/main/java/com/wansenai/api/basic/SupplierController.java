package com.wansenai.api.basic;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wansenai.service.basic.SupplierService;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.basic.SupplierVO;
import com.wansenai.dto.basic.QuerySupplierDTO;
import com.wansenai.dto.basic.AddSupplierDTO;
import com.wansenai.dto.basic.UpdateSupplierDTO;
import com.wansenai.dto.basic.UpdateSupplierStatusDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/basic/supplier")
public class SupplierController {

    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping("/pageList")
    public Response<Page<SupplierVO>> supplierPageList(@RequestBody QuerySupplierDTO querySupplierDTO) {
        return supplierService.getSupplierPageList(querySupplierDTO);
    }

    @GetMapping("/list")
    public Response<List<SupplierVO>> supplierList() {
        return supplierService.getSupplierList(null);
    }

    @PostMapping("/add")
    public Response<String> addSupplier(@RequestBody AddSupplierDTO addSupplierDTO) {
        return supplierService.addSupplier(addSupplierDTO);
    }

    @PostMapping("/update")
    public Response<String> updateSupplier(@RequestBody UpdateSupplierDTO updateSupplierDTO) {
        return supplierService.updateSupplier(updateSupplierDTO);
    }

    @DeleteMapping("/deleteBatch")
    public Response<String> deleteSupplier(@RequestParam List<Long> ids) {
        return supplierService.deleteSupplier(ids);
    }

    @PostMapping("/updateStatus")
    public Response<String> updateSupplierStatus(@RequestBody UpdateSupplierStatusDTO updateSupplierStatusDTO) {
        return supplierService.updateSupplierStatus(updateSupplierStatusDTO);
    }

    @GetMapping("/export")
    public void export(@ModelAttribute QuerySupplierDTO querySupplierDTO, HttpServletResponse response) {
        supplierService.exportSupplierData(querySupplierDTO, response);
    }
}