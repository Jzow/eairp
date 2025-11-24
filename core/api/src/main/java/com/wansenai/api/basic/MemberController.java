package com.wansenai.api.basic;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wansenai.service.basic.MemberService;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.basic.MemberVO;
import com.wansenai.dto.basic.QueryMemberDTO;
import com.wansenai.dto.basic.AddOrUpdateMemberDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/basic/member")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/pageList")
    public Response<Page<MemberVO>> getMemberPageList(@RequestBody QueryMemberDTO memberDTO) {
        return memberService.getMemberPageList(memberDTO);
    }

    @PostMapping("/addOrUpdate")
    public Response<String> addOrUpdateMember(@RequestBody AddOrUpdateMemberDTO memberDTO) {
        return memberService.addOrUpdateMember(memberDTO);
    }

    @DeleteMapping("/deleteBatch")
    public Response<String> deleteBatchMembers(@RequestParam List<Long> ids) {
        return memberService.deleteBatchMember(ids);
    }

    @PostMapping("/updateStatus")
    public Response<String> updateMemberStatus(
            @RequestParam("ids") List<Long> ids,
            @RequestParam("status") Integer status) {
        return memberService.updateMemberStatus(ids, status);
    }

    @GetMapping("/list")
    public Response<List<MemberVO>> getMemberList() {
        return memberService.getMemberList(null);
    }

    @GetMapping("/export")
    public void export(@ModelAttribute QueryMemberDTO queryMemberDTO, HttpServletResponse response) {
        memberService.exportMemberData(queryMemberDTO, response);
    }
}