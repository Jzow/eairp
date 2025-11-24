package com.wansenai.service.system;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.dto.department.AddOrUpdateDeptDTO;
import com.wansenai.entities.SysDepartment;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.DeptListVO;
import java.util.List;

public interface SysDepartmentService extends IService<SysDepartment> {

    Response<List<DeptListVO>> getDeptList(String deptName);

    Response<List<DeptListVO>> userDept();

    Response<String> addOrSaveDept(AddOrUpdateDeptDTO addOrUpdateDeptDTO);

    Response<String> deleteDept(String id);
}