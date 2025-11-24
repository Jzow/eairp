package com.wansenai.service.financial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.dto.financial.AddOrUpdateAdvanceChargeDTO;
import com.wansenai.dto.financial.QueryAdvanceChargeDTO;
import com.wansenai.entities.financial.FinancialMain;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.financial.AdvanceChargeVO;
import com.wansenai.vo.financial.AdvanceChargeDetailVO;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface AdvanceChargeService extends IService<FinancialMain> {

    Response<String> addOrUpdateAdvanceCharge(AddOrUpdateAdvanceChargeDTO advanceChargeDTO);

    Response<Page<AdvanceChargeVO>> getAdvanceChargePageList(QueryAdvanceChargeDTO advanceChargeDTO);

    Response<AdvanceChargeDetailVO> getAdvanceChargeDetailById(Long id);

    Response<String> deleteAdvanceChargeById(List<Long> ids);

    Response<String> updateAdvanceChargeStatusById(List<Long> ids, Integer status);

    void exportAdvanceCharge(QueryAdvanceChargeDTO advanceChargeDTO, HttpServletResponse response);

    void exportAdvanceChargeDetail(String receiptNumber, HttpServletResponse response);
}