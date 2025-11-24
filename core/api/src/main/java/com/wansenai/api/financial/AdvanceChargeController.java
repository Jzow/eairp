package com.wansenai.api.financial;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wansenai.service.financial.AdvanceChargeService;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.financial.AdvanceChargeVO;
import com.wansenai.vo.financial.AdvanceChargeDetailVO;
import com.wansenai.dto.financial.AddOrUpdateAdvanceChargeDTO;
import com.wansenai.dto.financial.QueryAdvanceChargeDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/financial/advance-charge")
public class AdvanceChargeController {

    private final AdvanceChargeService advanceChargeService;

    @Autowired
    public AdvanceChargeController(AdvanceChargeService advanceChargeService) {
        this.advanceChargeService = advanceChargeService;
    }

    @PostMapping("/addOrUpdate")
    public Response<String> addOrUpdateAdvanceCharge(@RequestBody AddOrUpdateAdvanceChargeDTO advanceChargeDTO) {
        return advanceChargeService.addOrUpdateAdvanceCharge(advanceChargeDTO);
    }

    @PostMapping("/pageList")
    public Response<Page<AdvanceChargeVO>> getAdvanceChargePageList(@RequestBody QueryAdvanceChargeDTO advanceChargeDTO) {
        return advanceChargeService.getAdvanceChargePageList(advanceChargeDTO);
    }

    @GetMapping("/getDetailById/{id}")
    public Response<AdvanceChargeDetailVO> getDetailById(@PathVariable Long id) {
        return advanceChargeService.getAdvanceChargeDetailById(id);
    }

    @PutMapping("/deleteByIds")
    public Response<String> deleteByIds(@RequestParam("ids") List<Long> ids) {
        return advanceChargeService.deleteAdvanceChargeById(ids);
    }

    @PutMapping("/updateStatusByIds")
    public Response<String> updateStatus(
            @RequestParam("ids") List<Long> ids,
            @RequestParam("status") Integer status) {
        return advanceChargeService.updateAdvanceChargeStatusById(ids, status);
    }

    @GetMapping("/export")
    public void export(@ModelAttribute QueryAdvanceChargeDTO advanceChargeDTO, HttpServletResponse response) {
        advanceChargeService.exportAdvanceCharge(advanceChargeDTO, response);
    }

    @GetMapping("/exportDetail/{receiptNumber}")
    public void exportDetail(@PathVariable String receiptNumber, HttpServletResponse response) {
        advanceChargeService.exportAdvanceChargeDetail(receiptNumber, response);
    }
}