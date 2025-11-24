package com.wansenai.service.basic;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.entities.basic.Supplier;
import com.wansenai.utils.response.Response;
import com.wansenai.dto.basic.QuerySupplierDTO;
import com.wansenai.dto.basic.AddSupplierDTO;
import com.wansenai.dto.basic.UpdateSupplierDTO;
import com.wansenai.dto.basic.UpdateSupplierStatusDTO;
import com.wansenai.vo.basic.SupplierVO;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface SupplierService extends IService<Supplier> {

    Response<Page<SupplierVO>> getSupplierPageList(QuerySupplierDTO supplier);

    Response<String> addSupplier(AddSupplierDTO supplier);

    Response<List<SupplierVO>> getSupplierList(QuerySupplierDTO supplier);

    /**
     * 内部使用方法
     */
    Boolean batchAddSupplier(List<Supplier> suppliers);

    Response<String> updateSupplier(UpdateSupplierDTO supplier);

    Response<String> deleteSupplier(List<Long> ids);

    Response<String> updateSupplierStatus(UpdateSupplierStatusDTO updateSupplierStatusDTO);

    void exportSupplierData(QuerySupplierDTO querySupplierDTO, HttpServletResponse response);
}