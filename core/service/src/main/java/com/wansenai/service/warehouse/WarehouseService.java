package com.wansenai.service.warehouse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.dto.basic.AddOrUpdateWarehouseDTO;
import com.wansenai.dto.basic.QueryWarehouseDTO;
import com.wansenai.entities.warehouse.Warehouse;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.warehouse.WarehouseVO;
import java.util.List;

public interface WarehouseService extends IService<Warehouse> {

    Response<Page<WarehouseVO>> getWarehousePageList(QueryWarehouseDTO warehouseDTO);

    Response<String> addOrUpdateWarehouse(AddOrUpdateWarehouseDTO warehouseDTO);

    Response<String> deleteBatch(List<Long> ids);

    Response<List<WarehouseVO>> getWarehouse();

    Response<String> updateBatchStatus(List<Long> ids, Integer status);

    Warehouse getWarehouseByName(String name);

    Response<List<WarehouseVO>> getWarehouseList();

    Response<WarehouseVO> getDefaultWarehouse();
}