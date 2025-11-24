package com.wansenai.service.basic.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.bo.member.MemberExportBO;
import com.wansenai.bo.member.MemberExportEnBO;
import com.wansenai.entities.basic.Member;
import com.wansenai.mappers.basic.MemberMapper;
import com.wansenai.service.BaseService;
import com.wansenai.service.basic.MemberService;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.MemberCodeEnum;
import com.wansenai.utils.excel.ExcelUtils;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.basic.MemberVO;
import com.wansenai.dto.basic.AddOrUpdateMemberDTO;
import com.wansenai.dto.basic.QueryMemberDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {

    private final BaseService baseService;
    private final MemberMapper memberMapper;

    public MemberServiceImpl(BaseService baseService, MemberMapper memberMapper) {
        this.baseService = baseService;
        this.memberMapper = memberMapper;
    }

    @Override
    public Response<Page<MemberVO>> getMemberPageList(QueryMemberDTO memberDTO) {
        var page = new Page<Member>(
                Optional.ofNullable(memberDTO).map(QueryMemberDTO::getPage).orElse(1L),
                Optional.ofNullable(memberDTO).map(QueryMemberDTO::getPageSize).orElse(10L)
        );

        var wrapper = new LambdaQueryWrapper<Member>();
        if (memberDTO != null) {
            if (memberDTO.getMemberNumber() != null) {
                wrapper.like(Member::getMemberNumber, memberDTO.getMemberNumber());
            }
            if (memberDTO.getPhoneNumber() != null) {
                wrapper.like(Member::getPhoneNumber, memberDTO.getPhoneNumber());
            }
            if (memberDTO.getStartDate() != null) {
                wrapper.ge(Member::getCreateTime, memberDTO.getStartDate());
            }
            if (memberDTO.getEndDate() != null) {
                wrapper.le(Member::getCreateTime, memberDTO.getEndDate());
            }
        }
        wrapper.eq(Member::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(Member::getCreateTime);

        var resultPage = memberMapper.selectPage(page, wrapper);
        var records = resultPage.getRecords();

        if (records.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
        }

        var voList = new ArrayList<MemberVO>();
        for (var member : records) {
            var vo = new MemberVO();
            vo.setId(member.getId());
            vo.setMemberNumber(member.getMemberNumber());
            vo.setMemberName(member.getMemberName());
            vo.setPhoneNumber(member.getPhoneNumber());
            vo.setEmail(member.getEmail());
            vo.setAdvancePayment(member.getAdvancePayment());
            vo.setStatus(member.getStatus());
            vo.setRemark(member.getRemark());
            vo.setSort(member.getSort());
            vo.setCreateTime(member.getCreateTime());
            voList.add(vo);
        }

        var resultVoPage = new Page<MemberVO>();
        resultVoPage.setRecords(voList);
        resultVoPage.setTotal(resultPage.getTotal());
        resultVoPage.setPages(resultPage.getPages());
        resultVoPage.setSize(resultPage.getSize());

        return Response.responseData(resultVoPage);
    }

    @Transactional
    @Override
    public Response<String> addOrUpdateMember(AddOrUpdateMemberDTO memberDTO) {
        var userId = baseService.getCurrentUserId();
        var isAdd = memberDTO.getId() == null;
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        var member = new Member();
        member.setId(memberDTO.getId());
        member.setMemberNumber(memberDTO.getMemberNumber());
        member.setMemberName(memberDTO.getMemberName());
        member.setPhoneNumber(Optional.ofNullable(memberDTO.getPhoneNumber()).orElse(""));
        member.setEmail(Optional.ofNullable(memberDTO.getEmail()).orElse(""));
        member.setAdvancePayment(Optional.ofNullable(memberDTO.getAdvancePayment()).orElse(BigDecimal.ZERO));
        member.setStatus(memberDTO.getStatus());
        member.setRemark(Optional.ofNullable(memberDTO.getRemark()).orElse(""));
        member.setSort(Optional.ofNullable(memberDTO.getSort()).orElse(0));

        if (isAdd) {
            member.setCreateTime(LocalDateTime.now());
            member.setCreateBy(userId);
        } else {
            member.setUpdateTime(LocalDateTime.now());
            member.setUpdateBy(userId);
        }

        var saveResult = saveOrUpdate(member);

        if ("zh_CN".equals(systemLanguage)) {
            if (saveResult && isAdd) {
                return Response.responseMsg(MemberCodeEnum.ADD_MEMBER_SUCCESS);
            } else if (saveResult && !isAdd) {
                return Response.responseMsg(MemberCodeEnum.UPDATE_MEMBER_INFO_SUCCESS);
            } else {
                return Response.fail();
            }
        } else {
            if (saveResult && isAdd) {
                return Response.responseMsg(MemberCodeEnum.ADD_MEMBER_SUCCESS_EN);
            } else if (saveResult && !isAdd) {
                return Response.responseMsg(MemberCodeEnum.UPDATE_MEMBER_INFO_SUCCESS_EN);
            } else {
                return Response.fail();
            }
        }
    }

    @Override
    public Response<String> deleteBatchMember(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var updateResult = memberMapper.deleteBatchIds(ids);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (updateResult > 0) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(MemberCodeEnum.DELETE_MEMBER_SUCCESS);
            } else {
                return Response.responseMsg(MemberCodeEnum.DELETE_MEMBER_SUCCESS_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(MemberCodeEnum.DELETE_MEMBER_ERROR);
            } else {
                return Response.responseMsg(MemberCodeEnum.DELETE_MEMBER_ERROR_EN);
            }
        }
    }

    @Override
    public Response<String> updateMemberStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var updateResult = lambdaUpdate()
                .in(Member::getId, ids)
                .set(Member::getStatus, status)
                .update();

        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (!updateResult) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(MemberCodeEnum.UPDATE_MEMBER_STATUS_ERROR);
            } else {
                return Response.responseMsg(MemberCodeEnum.UPDATE_MEMBER_STATUS_ERROR_EN);
            }
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(MemberCodeEnum.UPDATE_MEMBER_STATUS_SUCCESS);
            } else {
                return Response.responseMsg(MemberCodeEnum.UPDATE_MEMBER_STATUS_SUCCESS_EN);
            }
        }
    }

    @Transactional
    @Override
    public Boolean batchAddMember(List<Member> members) {
        var memberEntities = new ArrayList<Member>();
        var existingMembers = new HashSet<String>(); // 使用字符串组合作为键

        if (members != null) {
            for (var member : members) {
                var memberKey = member.getMemberNumber() + "|" + member.getMemberName();
                if (!existingMembers.contains(memberKey)) {
                    var memberEntity = memberMapper.selectOne(
                            new LambdaQueryWrapper<Member>()
                                    .eq(Member::getMemberName, member.getMemberName())
                                    .eq(Member::getMemberNumber, member.getMemberNumber())
                    );
                    if (memberEntity == null) {
                        var newMemberEntity = new Member();
                        BeanUtils.copyProperties(member, newMemberEntity);
                        newMemberEntity.setCreateTime(LocalDateTime.now());
                        newMemberEntity.setCreateBy(baseService.getCurrentUserId());

                        memberEntities.add(newMemberEntity);
                        existingMembers.add(memberKey);
                    }
                }
            }
        }
        return saveBatch(memberEntities);
    }

    @Override
    public Response<List<MemberVO>> getMemberList(QueryMemberDTO memberDTO) {
        var wrapper = new LambdaQueryWrapper<Member>();
        if (memberDTO != null) {
            if (StringUtils.hasLength(memberDTO.getMemberNumber())) {
                wrapper.eq(Member::getMemberNumber, memberDTO.getMemberNumber());
            }
            if (StringUtils.hasLength(memberDTO.getPhoneNumber())) {
                wrapper.eq(Member::getPhoneNumber, memberDTO.getPhoneNumber());
            }
            if (StringUtils.hasLength(memberDTO.getStartDate())) {
                wrapper.ge(Member::getCreateTime, memberDTO.getStartDate());
            }
            if (StringUtils.hasLength(memberDTO.getEndDate())) {
                wrapper.le(Member::getCreateTime, memberDTO.getEndDate());
            }
        }
        wrapper.eq(Member::getStatus, CommonConstants.STATUS_NORMAL)
                .eq(Member::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByAsc(Member::getSort);

        var memberList = memberMapper.selectList(wrapper);
        var voList = new ArrayList<MemberVO>();

        for (var member : memberList) {
            var vo = new MemberVO();
            vo.setId(member.getId());
            vo.setMemberNumber(member.getMemberNumber());
            vo.setMemberName(member.getMemberName());
            vo.setPhoneNumber(member.getPhoneNumber());
            vo.setEmail(member.getEmail());
            vo.setAdvancePayment(member.getAdvancePayment());
            vo.setStatus(member.getStatus());
            vo.setRemark(member.getRemark());
            vo.setSort(member.getSort());
            vo.setCreateTime(member.getCreateTime());
            voList.add(vo);
        }

        return Response.responseData(voList);
    }

    @Override
    public Boolean updateAdvanceChargeAmount(Long memberId, BigDecimal amount) {
        if (memberId == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        var member = getById(memberId);
        if (member == null) {
            log.error("Member does not exist, memberId: " + memberId);
            return false;
        }

        return lambdaUpdate()
                .eq(Member::getId, memberId)
                .set(Member::getAdvancePayment,
                        Optional.ofNullable(member.getAdvancePayment()).orElse(BigDecimal.ZERO).add(amount))
                .update();
    }

    @Override
    public Member getMemberById(Long memberId) {
        if (memberId == null) {
            return null;
        }

        var member = getById(memberId);
        if (member == null) {
            log.error("Member does not exist, memberId: " + memberId);
            return null;
        }
        return member;
    }

    @Override
    public void exportMemberData(QueryMemberDTO memberDTO, HttpServletResponse response) {
        var members = getMemberList(memberDTO);
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if (members.getData() != null && !members.getData().isEmpty()) {
            if ("zh_CN".equals(systemLanguage)) {
                var exportData = new ArrayList<MemberExportBO>();
                for (var member : members.getData()) {
                    var memberExportBO = new MemberExportBO();
                    memberExportBO.setId(member.getId());
                    memberExportBO.setMemberNumber(member.getMemberNumber());
                    memberExportBO.setMemberName(member.getMemberName());
                    memberExportBO.setPhoneNumber(member.getPhoneNumber());
                    memberExportBO.setEmail(member.getEmail());
                    memberExportBO.setAdvancePayment(member.getAdvancePayment());
                    memberExportBO.setStatus(member.getStatus());
                    memberExportBO.setRemark(member.getRemark());
                    memberExportBO.setSort(member.getSort());
                    memberExportBO.setCreateTime(member.getCreateTime());
                    exportData.add(memberExportBO);
                }
                ExcelUtils.export(response, "会员信息", ExcelUtils.getSheetData(exportData));
            } else {
                var exportEnData = new ArrayList<MemberExportEnBO>();
                for (var member : members.getData()) {
                    var memberExportEnBO = new MemberExportEnBO();
                    memberExportEnBO.setId(member.getId());
                    memberExportEnBO.setMemberNumber(member.getMemberNumber());
                    memberExportEnBO.setMemberName(member.getMemberName());
                    memberExportEnBO.setPhoneNumber(member.getPhoneNumber());
                    memberExportEnBO.setEmail(member.getEmail());
                    memberExportEnBO.setAdvancePayment(member.getAdvancePayment());
                    memberExportEnBO.setStatus(member.getStatus());
                    memberExportEnBO.setRemark(member.getRemark());
                    memberExportEnBO.setSort(member.getSort());
                    memberExportEnBO.setCreateTime(member.getCreateTime());
                    exportEnData.add(memberExportEnBO);
                }
                ExcelUtils.export(response, "Member Info", ExcelUtils.getSheetData(exportEnData));
            }
        }
    }
}