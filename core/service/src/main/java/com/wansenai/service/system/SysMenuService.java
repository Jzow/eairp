package com.wansenai.service.system;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wansenai.dto.basic.AddOrUpdateMenuDTO;
import com.wansenai.entities.system.SysMenu;
import com.wansenai.utils.response.Response;

public interface SysMenuService extends IService<SysMenu> {

    Response<String> addOrSaveMenu(AddOrUpdateMenuDTO addOrUpdateMenuDTO);

    Response<String> deleteMenu(Integer id);

    Response<JSONObject> menuList();
}