package com.wansenai.service.basic;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.dto.basic.AddOrUpdateCustomerDTO;
import com.wansenai.dto.basic.QueryCustomerDTO;
import com.wansenai.entities.basic.Customer;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.basic.CustomerVO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface CustomerService extends IService<Customer> {

    Response<Page<CustomerVO>> getCustomerPageList(QueryCustomerDTO queryCustomerDTO);

    Response<List<CustomerVO>> getCustomerList(QueryCustomerDTO queryCustomerDTO);

    Response<String> addOrUpdateCustomer(AddOrUpdateCustomerDTO addOrUpdateCustomerDTO);

    Response<String> deleteCustomer(List<Long> ids);

    Response<String> updateCustomerStatus(List<Long> ids, Integer status);

    Boolean batchAddCustomer(List<Customer> customers);

    void exportCustomerData(QueryCustomerDTO queryCustomerDTO, HttpServletResponse response);
}
