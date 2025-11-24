package com.wansenai.api.system;

import com.wansenai.dto.basic.AddOrUpdateMenuDTO;
import com.wansenai.service.system.SysMenuService;
import com.wansenai.utils.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menu")
public class SysMenuController {

    private final SysMenuService menuService;

    @Autowired
    public SysMenuController(SysMenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/addOrUpdate")
    public Response<String> addOrUpdate(@RequestBody AddOrUpdateMenuDTO addOrUpdateMenuDTO) {
        return menuService.addOrSaveMenu(addOrUpdateMenuDTO);
    }

    @PostMapping("/delete")
    public Response<String> deleteMenu(@RequestParam(value = "id", required = true) Integer id) {
        return menuService.deleteMenu(id);
    }
}