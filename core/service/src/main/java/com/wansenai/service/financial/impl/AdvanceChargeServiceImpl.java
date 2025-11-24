package com.wansenai.service.financial.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.bo.*;
import com.wansenai.bo.financial.*;
import com.wansenai.entities.financial.FinancialMain;
import com.wansenai.entities.financial.FinancialSub;
import com.wansenai.entities.basic.Member;
import com.wansenai.entities.basic.Operator;
import com.wansenai.entities.system.SysFile;
import com.wansenai.entities.user.SysUser;
import com.wansenai.mappers.financial.FinancialMainMapper;
import com.wansenai.mappers.system.SysFileMapper;
import com.wansenai.service.BaseService;
import com.wansenai.service.basic.IOperatorService;
import com.wansenai.service.basic.MemberService;
import com.wansenai.service.financial.FinancialSubService;
import com.wansenai.service.financial.IFinancialAccountService;
import com.wansenai.service.financial.AdvanceChargeService;
import com.wansenai.service.user.ISysUserService;
import com.wansenai.utils.SnowflakeIdUtil;
import com.wansenai.utils.TimeUtil;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.FinancialCodeEnum;
import com.wansenai.utils.excel.ExcelUtils;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.financial.AdvanceChargeVO;
import com.wansenai.vo.financial.AdvanceChargeDetailVO;
import com.wansenai.dto.financial.AddOrUpdateAdvanceChargeDTO;
import com.wansenai.dto.financial.QueryAdvanceChargeDTO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AdvanceChargeServiceImpl extends ServiceImpl<FinancialMainMapper, FinancialMain> implements AdvanceChargeService {

    private final BaseService baseService;
    private final FinancialSubService financialSubService;
    private final FinancialMainMapper financialMainMapper;
    private final MemberService memberService;
    private final IOperatorService operatorService;
    private final ISysUserService userService;
    private final IFinancialAccountService accountService;
    private final SysFileMapper fileMapper;

    public AdvanceChargeServiceImpl(
            BaseService baseService,
            FinancialSubService financialSubService,
            FinancialMainMapper financialMainMapper,
            MemberService memberService,
            IOperatorService operatorService,
            ISysUserService userService,
            IFinancialAccountService accountService,
            SysFileMapper fileMapper) {
        this.baseService = baseService;
        this.financialSubService = financialSubService;
        this.financialMainMapper = financialMainMapper;
        this.memberService = memberService;
        this.operatorService = operatorService;
        this.userService = userService;
        this.accountService = accountService;
        this.fileMapper = fileMapper;
    }

    @Transactional
    @Override
    public Response<String> addOrUpdateAdvanceCharge(AddOrUpdateAdvanceChargeDTO advanceChargeDTO) {
        if (advanceChargeDTO.getMemberId() == null || advanceChargeDTO.getReceiptDate() == null || advanceChargeDTO.getReceiptDate().isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var userId = baseService.getCurrentUserId();
        var systemLanguage = userService.getUserSystemLanguage(userId);
        var fileIdList = new ArrayList<Long>();

        if (advanceChargeDTO.getId() != null) {
            var financialSubList = financialSubService.lambdaQuery()
                    .eq(FinancialSub::getFinancialMainId, advanceChargeDTO.getId())
                    .list();

            if (!financialSubList.isEmpty()) {
                var financialSubIdList = financialSubList.stream().map(FinancialSub::getId).collect(Collectors.toList());
                var deleteFinancialSubResult = financialSubService.removeByIds(financialSubIdList);

                if (!deleteFinancialSubResult) {
                    if ("zh_CN".equals(systemLanguage)) {
                        return Response.responseMsg(FinancialCodeEnum.UPDATE_ADVANCE_ERROR);
                    }
                    return Response.responseMsg(FinancialCodeEnum.UPDATE_ADVANCE_ERROR_EN);
                }
            }

            if (advanceChargeDTO.getFiles() != null) {
                var financialMain = getById(advanceChargeDTO.getId());
                if (financialMain != null && financialMain.getFileId() != null && !financialMain.getFileId().isEmpty()) {
                    var ids = List.of(financialMain.getFileId().split(","))
                            .stream()
                            .map(Long::valueOf)
                            .collect(Collectors.toList());
                    fileMapper.deleteBatchIds(ids);
                }

                for (var file : advanceChargeDTO.getFiles()) {
                    var fileId = SnowflakeIdUtil.nextId();
                    var fileEntity = SysFile.builder()
                            .id(fileId)
                            .uid(file.getUid())
                            .fileName(file.getFileName())
                            .fileUrl(file.getFileUrl())
                            .fileType(file.getFileType())
                            .fileSize(file.getFileSize())
                            .build();
                    fileIdList.add(fileId);
                    fileMapper.insert(fileEntity);
                }
            }
        }

        var fileIds = fileIdList.stream().map(String::valueOf).collect(Collectors.joining(","));

        if (advanceChargeDTO.getTableData() != null && !advanceChargeDTO.getTableData().isEmpty()) {
            var financialMainId = advanceChargeDTO.getId() != null ? advanceChargeDTO.getId() : SnowflakeIdUtil.nextId();
            var financialMainBuilder = FinancialMain.builder()
                    .id(financialMainId)
                    .relatedPersonId(advanceChargeDTO.getMemberId())
                    .operatorId(advanceChargeDTO.getFinancialPersonnelId())
                    .type("收预付款")
                    .changeAmount(advanceChargeDTO.getTotalAmount())
                    .totalAmount(advanceChargeDTO.getTotalAmount())
                    .receiptNumber(advanceChargeDTO.getReceiptNumber())
                    .receiptSource(0)
                    .receiptDate(TimeUtil.parse(advanceChargeDTO.getReceiptDate()))
                    .fileId(fileIds)
                    .status(Optional.ofNullable(advanceChargeDTO.getReview()).orElse(CommonConstants.UNAUDITED))
                    .createBy(userId)
                    .remark(advanceChargeDTO.getRemark());

            if (advanceChargeDTO.getId() == null) {
                financialMainBuilder.createTime(LocalDateTime.now());
            } else {
                financialMainBuilder.updateBy(userId);
                financialMainBuilder.updateTime(LocalDateTime.now());
            }

            var financialMain = financialMainBuilder.build();
            var isFinancialMainAdded = saveOrUpdate(financialMain);

            var financialSubList = toFinancialSubList(advanceChargeDTO.getTableData(), financialMainId);
            var areFinancialSubsAdded = financialSubService.saveBatch(financialSubList);
            var isMemberUpdated = memberService.updateAdvanceChargeAmount(advanceChargeDTO.getMemberId(), advanceChargeDTO.getTotalAmount());

            if (isFinancialMainAdded && areFinancialSubsAdded && isMemberUpdated) {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(FinancialCodeEnum.ADD_ADVANCE_SUCCESS);
                }
                return Response.responseMsg(FinancialCodeEnum.ADD_ADVANCE_SUCCESS_EN);
            } else {
                if ("zh_CN".equals(systemLanguage)) {
                    return Response.responseMsg(FinancialCodeEnum.ADD_ADVANCE_ERROR);
                }
                return Response.responseMsg(FinancialCodeEnum.ADD_ADVANCE_ERROR_EN);
            }
        }
        return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
    }

    private List<FinancialSub> toFinancialSubList(List<AdvanceChargeDataBO> tableData, Long financialMainId) {
        return tableData.stream().map(item ->
                FinancialSub.builder()
                        .id(SnowflakeIdUtil.nextId())
                        .financialMainId(financialMainId)
                        .accountId(item.getAccountId())
                        .singleAmount(item.getAmount())
                        .remark(item.getRemark())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public Response<Page<AdvanceChargeVO>> getAdvanceChargePageList(QueryAdvanceChargeDTO advanceChargeDTO) {
        var page = new Page<FinancialMain>(
                Optional.ofNullable(advanceChargeDTO).map(QueryAdvanceChargeDTO::getPage).orElse(1L),
                Optional.ofNullable(advanceChargeDTO).map(QueryAdvanceChargeDTO::getPageSize).orElse(10L)
        );

        var wrapper = new LambdaQueryWrapper<FinancialMain>();
        if (advanceChargeDTO != null) {
            if (advanceChargeDTO.getFinancialPersonnelId() != null) {
                wrapper.eq(FinancialMain::getOperatorId, advanceChargeDTO.getFinancialPersonnelId());
            }
            if (advanceChargeDTO.getReceiptNumber() != null) {
                wrapper.eq(FinancialMain::getReceiptNumber, advanceChargeDTO.getReceiptNumber());
            }
            if (advanceChargeDTO.getStatus() != null) {
                wrapper.eq(FinancialMain::getStatus, advanceChargeDTO.getStatus());
            }
            if (advanceChargeDTO.getOperatorId() != null) {
                wrapper.eq(FinancialMain::getCreateBy, advanceChargeDTO.getOperatorId());
            }
            if (advanceChargeDTO.getRemark() != null) {
                wrapper.like(FinancialMain::getRemark, advanceChargeDTO.getRemark());
            }
            if (advanceChargeDTO.getStartDate() != null) {
                wrapper.ge(FinancialMain::getCreateTime, advanceChargeDTO.getStartDate());
            }
            if (advanceChargeDTO.getEndDate() != null) {
                wrapper.le(FinancialMain::getCreateTime, advanceChargeDTO.getEndDate());
            }
        }
        wrapper.eq(FinancialMain::getType, "收预付款")
                .eq(FinancialMain::getDeleteFlag, CommonConstants.NOT_DELETED)
                .orderByDesc(FinancialMain::getCreateTime);

        var resultPage = financialMainMapper.selectPage(page, wrapper);
        var records = resultPage.getRecords();

        if (records.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
        }

        var voList = records.stream().map(financialMain -> {
            var member = memberService.getMemberById(financialMain.getRelatedPersonId());
            var operator = userService.getById(financialMain.getCreateBy());
            var financialPerson = operatorService.getOperatorById(financialMain.getOperatorId());

            return toAdvanceChargeVO(financialMain, member, operator, financialPerson);
        }).collect(Collectors.toList());

        var resultVoPage = new Page<AdvanceChargeVO>();
        resultVoPage.setRecords(voList);
        resultVoPage.setTotal(resultPage.getTotal());
        resultVoPage.setPages(resultPage.getPages());
        resultVoPage.setSize(resultPage.getSize());

        return Response.responseData(resultVoPage);
    }

    private AdvanceChargeVO toAdvanceChargeVO(FinancialMain financialMain, Member member, SysUser operator, Operator financialPerson) {
        var vo = new AdvanceChargeVO();
        vo.setId(financialMain.getId());
        vo.setReceiptNumber(financialMain.getReceiptNumber());
        vo.setReceiptDate(financialMain.getReceiptDate());
        vo.setOperator(operator != null ? operator.getName() : "");
        vo.setFinancialPersonnel(financialPerson != null ? financialPerson.getName() : "");
        vo.setMemberName(member != null ? member.getMemberName() : "");
        vo.setTotalAmount(financialMain.getTotalAmount());
        vo.setCollectedAmount(Optional.ofNullable(financialMain.getChangeAmount()).orElse(BigDecimal.ZERO));
        vo.setStatus(financialMain.getStatus());
        vo.setRemark(financialMain.getRemark());
        return vo;
    }

    private AdvanceChargeExportBO toAdvanceChargeBO(FinancialMain financialMain, Member member, SysUser operator, Operator financialPerson) {
        var bo = new AdvanceChargeExportBO();
        bo.setId(financialMain.getId());
        bo.setReceiptNumber(financialMain.getReceiptNumber());
        bo.setReceiptDate(financialMain.getReceiptDate());
        bo.setOperator(operator != null ? operator.getName() : "");
        bo.setFinancialPersonnel(financialPerson != null ? financialPerson.getName() : "");
        bo.setMemberName(member != null ? member.getMemberName() : "");
        bo.setTotalAmount(financialMain.getTotalAmount());
        bo.setCollectedAmount(Optional.ofNullable(financialMain.getChangeAmount()).orElse(BigDecimal.ZERO));
        bo.setStatus(financialMain.getStatus());
        bo.setRemark(financialMain.getRemark());
        return bo;
    }

    private AdvanceChargeExportEnBO toAdvanceChargeEnBO(FinancialMain financialMain, Member member, SysUser operator, Operator financialPerson) {
        var bo = new AdvanceChargeExportEnBO();
        bo.setId(financialMain.getId());
        bo.setReceiptNumber(financialMain.getReceiptNumber());
        bo.setReceiptDate(financialMain.getReceiptDate());
        bo.setOperator(operator != null ? operator.getName() : "");
        bo.setFinancialPersonnel(financialPerson != null ? financialPerson.getName() : "");
        bo.setMemberName(member != null ? member.getMemberName() : "");
        bo.setTotalAmount(financialMain.getTotalAmount());
        bo.setCollectedAmount(Optional.ofNullable(financialMain.getChangeAmount()).orElse(BigDecimal.ZERO));
        bo.setStatus(financialMain.getStatus());
        bo.setRemark(financialMain.getRemark());
        return bo;
    }

    private List<AdvanceChargeExportBO> getAdvanceChargeList(QueryAdvanceChargeDTO advanceChargeDTO) {
        var wrapper = new LambdaQueryWrapper<FinancialMain>();
        if (advanceChargeDTO != null) {
            if (advanceChargeDTO.getFinancialPersonnelId() != null) {
                wrapper.eq(FinancialMain::getOperatorId, advanceChargeDTO.getFinancialPersonnelId());
            }
            if (advanceChargeDTO.getReceiptNumber() != null) {
                wrapper.eq(FinancialMain::getReceiptNumber, advanceChargeDTO.getReceiptNumber());
            }
            if (advanceChargeDTO.getStatus() != null) {
                wrapper.eq(FinancialMain::getStatus, advanceChargeDTO.getStatus());
            }
            if (advanceChargeDTO.getOperatorId() != null) {
                wrapper.eq(FinancialMain::getCreateBy, advanceChargeDTO.getOperatorId());
            }
            if (advanceChargeDTO.getRemark() != null) {
                wrapper.like(FinancialMain::getRemark, advanceChargeDTO.getRemark());
            }
            if (advanceChargeDTO.getStartDate() != null) {
                wrapper.ge(FinancialMain::getCreateTime, advanceChargeDTO.getStartDate());
            }
            if (advanceChargeDTO.getEndDate() != null) {
                wrapper.le(FinancialMain::getCreateTime, advanceChargeDTO.getEndDate());
            }
        }
        wrapper.eq(FinancialMain::getType, "收预付款")
                .eq(FinancialMain::getDeleteFlag, CommonConstants.NOT_DELETED);

        var result = financialMainMapper.selectList(wrapper);
        return result.stream().map(financialMain -> {
            var member = memberService.getMemberById(financialMain.getRelatedPersonId());
            var operator = userService.getById(financialMain.getCreateBy());
            var financialPerson = operatorService.getOperatorById(financialMain.getOperatorId());

            return toAdvanceChargeBO(financialMain, member, operator, financialPerson);
        }).collect(Collectors.toList());
    }

    private List<AdvanceChargeExportEnBO> getAdvanceChargeEnList(QueryAdvanceChargeDTO advanceChargeDTO) {
        var wrapper = new LambdaQueryWrapper<FinancialMain>();
        if (advanceChargeDTO != null) {
            if (advanceChargeDTO.getFinancialPersonnelId() != null) {
                wrapper.eq(FinancialMain::getOperatorId, advanceChargeDTO.getFinancialPersonnelId());
            }
            if (advanceChargeDTO.getReceiptNumber() != null) {
                wrapper.eq(FinancialMain::getReceiptNumber, advanceChargeDTO.getReceiptNumber());
            }
            if (advanceChargeDTO.getStatus() != null) {
                wrapper.eq(FinancialMain::getStatus, advanceChargeDTO.getStatus());
            }
            if (advanceChargeDTO.getOperatorId() != null) {
                wrapper.eq(FinancialMain::getCreateBy, advanceChargeDTO.getOperatorId());
            }
            if (advanceChargeDTO.getRemark() != null) {
                wrapper.like(FinancialMain::getRemark, advanceChargeDTO.getRemark());
            }
            if (advanceChargeDTO.getStartDate() != null) {
                wrapper.ge(FinancialMain::getCreateTime, advanceChargeDTO.getStartDate());
            }
            if (advanceChargeDTO.getEndDate() != null) {
                wrapper.le(FinancialMain::getCreateTime, advanceChargeDTO.getEndDate());
            }
        }
        wrapper.eq(FinancialMain::getType, "收预付款")
                .eq(FinancialMain::getDeleteFlag, CommonConstants.NOT_DELETED);

        var result = financialMainMapper.selectList(wrapper);
        return result.stream().map(financialMain -> {
            var member = memberService.getMemberById(financialMain.getRelatedPersonId());
            var operator = userService.getById(financialMain.getCreateBy());
            var financialPerson = operatorService.getOperatorById(financialMain.getOperatorId());

            return toAdvanceChargeEnBO(financialMain, member, operator, financialPerson);
        }).collect(Collectors.toList());
    }

    @Override
    public Response<AdvanceChargeDetailVO> getAdvanceChargeDetailById(Long id) {
        var financialMain = getById(id);
        if (financialMain != null) {
            var member = memberService.getMemberById(financialMain.getRelatedPersonId());
            var financialPerson = operatorService.getOperatorById(financialMain.getOperatorId());

            var subData = financialSubService.lambdaQuery()
                    .eq(FinancialSub::getFinancialMainId, id)
                    .eq(FinancialSub::getDeleteFlag, CommonConstants.NOT_DELETED)
                    .list();

            var tableData = new ArrayList<AdvanceChargeDataBO>();
            for (var financialSub : subData) {
                var account = accountService.getById(financialSub.getAccountId());
                var record = new AdvanceChargeDataBO();
                record.setAccountId(financialSub.getAccountId());
                record.setAccountName(account != null ? account.getAccountName() : "");
                record.setAmount(financialSub.getSingleAmount());
                record.setRemark(financialSub.getRemark());
                tableData.add(record);
            }

            var filesData = new ArrayList<FileDataBO>();
            if (financialMain.getFileId() != null && !financialMain.getFileId().isEmpty()) {
                var ids = List.of(financialMain.getFileId().split(","))
                        .stream()
                        .map(Long::valueOf)
                        .collect(Collectors.toList());
                var fileList = fileMapper.selectBatchIds(ids);
                for (var file : fileList) {
                    var fileBo = new FileDataBO();
                    fileBo.setId(file.getId());
                    fileBo.setFileName(file.getFileName());
                    fileBo.setFileUrl(file.getFileUrl());
                    fileBo.setFileType(file.getFileType());
                    fileBo.setFileSize(file.getFileSize());
                    filesData.add(fileBo);
                }
            }

            var resultVO = new AdvanceChargeDetailVO();
            resultVO.setMemberId(financialMain.getRelatedPersonId());
            resultVO.setMemberName(member != null ? member.getMemberName() : "");
            resultVO.setReceiptNumber(financialMain.getReceiptNumber());
            resultVO.setReceiptDate(financialMain.getReceiptDate());
            resultVO.setFinancialPersonnel(financialPerson != null ? financialPerson.getName() : "");
            resultVO.setFinancialPersonnelId(financialPerson != null ? financialPerson.getId() : null);
            resultVO.setTotalAmount(financialMain.getTotalAmount());
            resultVO.setCollectedAmount(financialMain.getChangeAmount());
            resultVO.setTableData(tableData);
            resultVO.setRemark(financialMain.getRemark());
            resultVO.setFiles(filesData);
            resultVO.setStatus(financialMain.getStatus());

            return Response.responseData(resultVO);
        }
        return Response.responseMsg(BaseCodeEnum.QUERY_DATA_EMPTY);
    }

    @Transactional
    @Override
    public Response<String> deleteAdvanceChargeById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var financialMainList = ids.stream().map(id ->
                FinancialMain.builder()
                        .id(id)
                        .deleteFlag(CommonConstants.DELETED)
                        .build()
        ).collect(Collectors.toList());

        var isDeleted = updateBatchById(financialMainList);

        var financialSubList = financialSubService.lambdaQuery()
                .in(FinancialSub::getFinancialMainId, ids)
                .list();

        var financialSubIdList = financialSubList.stream().map(FinancialSub::getId).collect(Collectors.toList());

        var financialSubUpdateList = financialSubIdList.stream().map(id ->
                FinancialSub.builder()
                        .id(id)
                        .deleteFlag(CommonConstants.DELETED)
                        .build()
        ).collect(Collectors.toList());

        var isFinancialSubDeleted = financialSubService.updateBatchById(financialSubUpdateList);

        var systemLanguage = userService.getUserSystemLanguage(userService.getCurrentUserId());

        if (isDeleted && isFinancialSubDeleted) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(FinancialCodeEnum.DELETE_ADVANCE_SUCCESS);
            }
            return Response.responseMsg(FinancialCodeEnum.DELETE_ADVANCE_SUCCESS_EN);
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(FinancialCodeEnum.DELETE_ADVANCE_ERROR);
            }
            return Response.responseMsg(FinancialCodeEnum.DELETE_ADVANCE_ERROR_EN);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Response<String> updateAdvanceChargeStatusById(List<Long> ids, Integer status) {
        var financialMainList = ids.stream().map(id ->
                FinancialMain.builder()
                        .id(id)
                        .status(status)
                        .build()
        ).collect(Collectors.toList());

        var isUpdated = updateBatchById(financialMainList);
        var systemLanguage = userService.getUserSystemLanguage(userService.getCurrentUserId());

        if (isUpdated) {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(FinancialCodeEnum.UPDATE_ADVANCE_SUCCESS);
            }
            return Response.responseMsg(FinancialCodeEnum.UPDATE_ADVANCE_SUCCESS_EN);
        } else {
            if ("zh_CN".equals(systemLanguage)) {
                return Response.responseMsg(FinancialCodeEnum.UPDATE_ADVANCE_ERROR);
            }
            return Response.responseMsg(FinancialCodeEnum.UPDATE_ADVANCE_ERROR_EN);
        }
    }

    @Override
    public void exportAdvanceCharge(QueryAdvanceChargeDTO advanceChargeDTO, HttpServletResponse response) {
        var exportMap = new ConcurrentHashMap<String, List<List<Object>>>();
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if ("zh_CN".equals(systemLanguage)) {
            var mainData = getAdvanceChargeList(advanceChargeDTO);
            if (!mainData.isEmpty()) {
                exportMap.put("收预付款", ExcelUtils.getSheetData(mainData));

                if (advanceChargeDTO.getIsExportDetail() != null && advanceChargeDTO.getIsExportDetail()) {
                    var subData = mainData.stream()
                            .flatMap(advanceChargeBO -> {
                                if (advanceChargeBO.getId() != null) {
                                    var detailResponse = getAdvanceChargeDetailById(advanceChargeBO.getId());
                                    if (detailResponse.getData() != null && detailResponse.getData().getTableData() != null) {
                                        return detailResponse.getData().getTableData().stream()
                                                .map(item -> {
                                                    var exportBO = new AdvanceChargeDataExportBO();
                                                    exportBO.setMemberName(advanceChargeBO.getMemberName());
                                                    exportBO.setReceiptNumber(advanceChargeBO.getReceiptNumber());
                                                    exportBO.setAccountName(item.getAccountName());
                                                    exportBO.setAmount(item.getAmount());
                                                    exportBO.setRemark(item.getRemark());
                                                    return exportBO;
                                                });
                                    }
                                }
                                return java.util.stream.Stream.<AdvanceChargeDataExportBO>empty();
                            })
                            .collect(Collectors.toList());
                    exportMap.put("收预付款单据明细", ExcelUtils.getSheetData(subData));
                }
                ExcelUtils.exportManySheet(response, "收预付款", exportMap);
            }
        } else {
            var mainEnData = getAdvanceChargeEnList(advanceChargeDTO);
            if (!mainEnData.isEmpty()) {
                exportMap.put("Advance Payment Receipt", ExcelUtils.getSheetData(mainEnData));

                if (advanceChargeDTO.getIsExportDetail() != null && advanceChargeDTO.getIsExportDetail()) {
                    var subEnData = mainEnData.stream()
                            .flatMap(advanceChargeEnBO -> {
                                if (advanceChargeEnBO.getId() != null) {
                                    var detailResponse = getAdvanceChargeDetailById(advanceChargeEnBO.getId());
                                    if (detailResponse.getData() != null && detailResponse.getData().getTableData() != null) {
                                        return detailResponse.getData().getTableData().stream()
                                                .map(item -> {
                                                    var exportEnBO = new AdvanceChargeDataExportEnBO();
                                                    exportEnBO.setMemberName(advanceChargeEnBO.getMemberName());
                                                    exportEnBO.setReceiptNumber(advanceChargeEnBO.getReceiptNumber());
                                                    exportEnBO.setAccountName(item.getAccountName());
                                                    exportEnBO.setAmount(item.getAmount());
                                                    exportEnBO.setRemark(item.getRemark());
                                                    return exportEnBO;
                                                });
                                    }
                                }
                                return java.util.stream.Stream.<AdvanceChargeDataExportEnBO>empty();
                            })
                            .collect(Collectors.toList());
                    exportMap.put("Advance Payment Receipt Details", ExcelUtils.getSheetData(subEnData));
                }
                ExcelUtils.exportManySheet(response, "Advance Payment Receipt", exportMap);
            }
        }
    }

    @Override
    public void exportAdvanceChargeDetail(String receiptNumber, HttpServletResponse response) {
        var financialMain = lambdaQuery()
                .eq(FinancialMain::getReceiptNumber, receiptNumber)
                .eq(FinancialMain::getDeleteFlag, CommonConstants.NOT_DELETED)
                .eq(FinancialMain::getType, "收预付款")
                .one();

        if (financialMain == null) {
            return;
        }

        var id = financialMain.getId();
        var systemLanguage = baseService.getCurrentUserSystemLanguage();

        if ("zh_CN".equals(systemLanguage)) {
            var detail = getAdvanceChargeDetailById(id);
            if (detail.getData() != null) {
                var exportData = new ArrayList<AdvanceChargeDataExportBO>();
                for (var item : detail.getData().getTableData()) {
                    var data = new AdvanceChargeDataExportBO();
                    data.setMemberName(detail.getData().getMemberName());
                    data.setReceiptNumber(detail.getData().getReceiptNumber());
                    data.setAccountName(item.getAccountName());
                    data.setAmount(item.getAmount());
                    data.setRemark(item.getRemark());
                    exportData.add(data);
                }
                ExcelUtils.export(response, "收预付款单据明细", ExcelUtils.getSheetData(exportData));
            }
        } else {
            var detail = getAdvanceChargeDetailById(id);
            if (detail.getData() != null) {
                var exportEnData = new ArrayList<AdvanceChargeDataExportEnBO>();
                for (var item : detail.getData().getTableData()) {
                    var data = new AdvanceChargeDataExportEnBO();
                    data.setMemberName(detail.getData().getMemberName());
                    data.setReceiptNumber(detail.getData().getReceiptNumber());
                    data.setAccountName(item.getAccountName());
                    data.setAmount(item.getAmount());
                    data.setRemark(item.getRemark());
                    exportEnData.add(data);
                }
                ExcelUtils.export(response, "Advance Payment Receipt Details", ExcelUtils.getSheetData(exportEnData));
            }
        }
    }
}