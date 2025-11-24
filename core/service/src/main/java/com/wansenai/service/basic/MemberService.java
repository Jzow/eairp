package com.wansenai.service.basic;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.entities.basic.Member;
import com.wansenai.utils.response.Response;
import com.wansenai.dto.basic.QueryMemberDTO;
import com.wansenai.dto.basic.AddOrUpdateMemberDTO;
import com.wansenai.vo.basic.MemberVO;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;

public interface MemberService extends IService<Member> {

    Response<Page<MemberVO>> getMemberPageList(QueryMemberDTO memberDTO);

    Response<String> addOrUpdateMember(AddOrUpdateMemberDTO memberDTO);

    Response<String> deleteBatchMember(List<Long> ids);

    Response<String> updateMemberStatus(List<Long> ids, Integer status);

    Boolean batchAddMember(List<Member> members);

    Response<List<MemberVO>> getMemberList(QueryMemberDTO memberDTO);

    Boolean updateAdvanceChargeAmount(Long memberId, BigDecimal amount);

    Member getMemberById(Long memberId);

    void exportMemberData(QueryMemberDTO memberDTO, HttpServletResponse response);
}