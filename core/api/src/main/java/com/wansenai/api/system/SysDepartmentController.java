/*
 * Copyright 2023-2025 EAIRP Team, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://opensource.wansenai.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package com.wansenai.api.system;

import com.wansenai.dto.department.AddOrUpdateDeptDTO;
import com.wansenai.service.system.SysDepartmentService;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.DeptListVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
@RequestMapping("/dept")
public class SysDepartmentController {

    private final SysDepartmentService departmentService;

    @Autowired
    public SysDepartmentController(SysDepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/list")
    public Response<List<DeptListVO>> getDeptList(@RequestParam(value = "deptName", required = false) String deptName) {
        return departmentService.getDeptList(deptName);
    }

    @GetMapping("/userBindDept")
    public Response<List<DeptListVO>> userDept() {
        return departmentService.userDept();
    }

    @PostMapping("/addOrUpdate")
    public Response<String> addOrUpdate(@RequestBody AddOrUpdateDeptDTO addOrUpdateDeptDTO) {
        return departmentService.addOrSaveDept(addOrUpdateDeptDTO);
    }

    @PostMapping("/delete")
    public Response<String> deleteDept(@RequestParam(value = "id", required = true) String id) {
        return departmentService.deleteDept(id);
    }
}