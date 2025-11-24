package com.wansenai.api.basic;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wansenai.service.basic.CustomerService;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.basic.CustomerVO;
import com.wansenai.dto.basic.QueryCustomerDTO;
import com.wansenai.dto.basic.AddOrUpdateCustomerDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/basic/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/pageList")
    public Response<Page<CustomerVO>> customerPageList(@RequestBody QueryCustomerDTO queryCustomerDTO) {
        return customerService.getCustomerPageList(queryCustomerDTO);
    }

    @GetMapping("/list")
    public Response<List<CustomerVO>> customerList() {
        return customerService.getCustomerList(null);
    }

    @PostMapping("/addOrUpdate")
    public Response<String> addOrUpdateCustomer(@RequestBody AddOrUpdateCustomerDTO addOrUpdateCustomerDTO) {
        return customerService.addOrUpdateCustomer(addOrUpdateCustomerDTO);
    }

    @DeleteMapping("/deleteBatch")
    public Response<String> deleteBatchCustomer(@RequestParam List<Long> ids) {
        return customerService.deleteCustomer(ids);
    }

    @PostMapping("/updateStatus")
    public Response<String> updateCustomerStatus(
            @RequestParam("ids") List<Long> ids,
            @RequestParam("status") Integer status) {
        return customerService.updateCustomerStatus(ids, status);
    }

    @GetMapping("/export")
    public void export(@ModelAttribute QueryCustomerDTO queryCustomerDTO, HttpServletResponse response) {
        customerService.exportCustomerData(queryCustomerDTO, response);
    }
}