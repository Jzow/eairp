package com.wansenai.service.system.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wansenai.dto.basic.AddOrUpdateMenuDTO;
import com.wansenai.entities.role.SysRoleMenuRel;
import com.wansenai.entities.system.SysMenu;
import com.wansenai.entities.user.SysUserRoleRel;
import com.wansenai.mappers.role.SysRoleMenuRelMapper;
import com.wansenai.mappers.system.SysMenuMapper;
import com.wansenai.service.role.SysRoleMenuRelService;
import com.wansenai.service.system.SysMenuService;
import com.wansenai.service.user.ISysUserRoleRelService;
import com.wansenai.service.user.ISysUserService;
import com.wansenai.utils.constants.CommonConstants;
import com.wansenai.utils.enums.BaseCodeEnum;
import com.wansenai.utils.enums.MenuCodeEnum;
import com.wansenai.utils.response.Response;
import com.wansenai.vo.MenuVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysRoleMenuRelService roleMenuRelService;
    private final ISysUserRoleRelService userRoleRelService;
    private final ISysUserService userService;
    private final SysRoleMenuRelMapper roleMenuRelMapper;

    public SysMenuServiceImpl(
            SysRoleMenuRelService roleMenuRelService,
            ISysUserRoleRelService userRoleRelService,
            ISysUserService userService,
            SysRoleMenuRelMapper roleMenuRelMapper) {
        this.roleMenuRelService = roleMenuRelService;
        this.userRoleRelService = userRoleRelService;
        this.userService = userService;
        this.roleMenuRelMapper = roleMenuRelMapper;
    }

    @Transactional
    @Override
    public Response<String> addOrSaveMenu(AddOrUpdateMenuDTO addOrUpdateMenuDTO) {
        if (addOrUpdateMenuDTO == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        if (addOrUpdateMenuDTO.getId() == null) {
            var menu = new SysMenu();
            menu.setName(addOrUpdateMenuDTO.getName());
            menu.setTitle(addOrUpdateMenuDTO.getTitle());
            menu.setIcon(addOrUpdateMenuDTO.getIcon());
            menu.setParentId(addOrUpdateMenuDTO.getParentId());
            menu.setMenuType(addOrUpdateMenuDTO.getMenuType());
            menu.setPath(addOrUpdateMenuDTO.getPath());
            menu.setComponent(addOrUpdateMenuDTO.getComponent());
            menu.setStatus(addOrUpdateMenuDTO.getStatus());
            menu.setSort(addOrUpdateMenuDTO.getSort());
            menu.setHideMenu(addOrUpdateMenuDTO.getHideMenu());
            menu.setIgnoreKeepAlive(addOrUpdateMenuDTO.getIgnoreKeepAlive());
            menu.setBlank(addOrUpdateMenuDTO.getBlank());
            menu.setCreateTime(LocalDateTime.now());

            var saveResult = save(menu);
            if (!saveResult) {
                return Response.responseMsg(MenuCodeEnum.ADD_MENU_ERROR);
            } else {
                // Add this menu to the administrator by default
                var adminRoleMenuRel = roleMenuRelService.getById(0L);
                var menuIds = new StringBuilder();
                if (adminRoleMenuRel != null) {
                    menuIds.append(adminRoleMenuRel.getMenuId());
                }
                menuIds.append("[").append(menu.getId()).append("]");

                var updateResult = roleMenuRelService.lambdaUpdate()
                        .eq(SysRoleMenuRel::getRoleId, 0L)
                        .set(SysRoleMenuRel::getMenuId, menuIds.toString())
                        .update();

                if (updateResult) {
                    return Response.responseMsg(MenuCodeEnum.ADD_MENU_SUCCESS);
                } else {
                    return Response.responseMsg(MenuCodeEnum.ADD_MENU_ERROR);
                }
            }
        } else {
            // update
            var updateWrapper = lambdaUpdate()
                    .eq(SysMenu::getId, addOrUpdateMenuDTO.getId())
                    .set(SysMenu::getName, addOrUpdateMenuDTO.getName())
                    .set(SysMenu::getTitle, addOrUpdateMenuDTO.getTitle())
                    .set(SysMenu::getIcon, addOrUpdateMenuDTO.getIcon())
                    .set(SysMenu::getParentId, addOrUpdateMenuDTO.getParentId())
                    .set(SysMenu::getMenuType, addOrUpdateMenuDTO.getMenuType())
                    .set(SysMenu::getPath, addOrUpdateMenuDTO.getPath())
                    .set(SysMenu::getComponent, addOrUpdateMenuDTO.getComponent())
                    .set(SysMenu::getStatus, addOrUpdateMenuDTO.getStatus())
                    .set(SysMenu::getSort, addOrUpdateMenuDTO.getSort())
                    .set(SysMenu::getHideMenu, addOrUpdateMenuDTO.getHideMenu())
                    .set(SysMenu::getIgnoreKeepAlive, addOrUpdateMenuDTO.getIgnoreKeepAlive())
                    .set(SysMenu::getBlank, addOrUpdateMenuDTO.getBlank())
                    .set(SysMenu::getUpdateTime, LocalDateTime.now());

            var updateResult = updateWrapper.update();

            if (!updateResult) {
                return Response.responseMsg(MenuCodeEnum.UPDATE_MENU_ERROR);
            } else {
                return Response.responseMsg(MenuCodeEnum.UPDATE_MENU_SUCCESS);
            }
        }
    }

    @Override
    public Response<String> deleteMenu(Integer id) {
        if (id == null) {
            return Response.responseMsg(BaseCodeEnum.PARAMETER_NULL);
        }

        var deleteResult = lambdaUpdate()
                .eq(SysMenu::getId, id)
                .set(SysMenu::getDeleteFlag, CommonConstants.DELETED)
                .update();

        if (deleteResult) {
            return Response.responseMsg(MenuCodeEnum.DELETE_MENU_SUCCESS);
        } else {
            return Response.responseMsg(MenuCodeEnum.DELETE_MENU_ERROR);
        }
    }

    @Override
    public Response<JSONObject> menuList() {
        var menuData = new JSONObject();
        var menuVos = new ArrayList<MenuVO>();

        var userId = userService.getCurrentUserId();
        if (userId == null) {
            return Response.fail();
        }

        var userRoles = userRoleRelService.queryByUserId(userId);
        var roleIds = userRoles.stream()
                .map(SysUserRoleRel::getRoleId)
                .collect(Collectors.toList());

        if (!roleIds.isEmpty()) {
            var menusReals = roleMenuRelMapper.listByRoleId(roleIds);
            if (!menusReals.isEmpty()) {
                var numberList = menusReals.stream()
                        .map(SysRoleMenuRel::getMenuId)
                        .flatMap(item -> {
                            var pattern = Pattern.compile("\\d+");
                            var matcher = pattern.matcher(item);
                            var numbers = new ArrayList<String>();
                            while (matcher.find()) {
                                numbers.add(matcher.group());
                            }
                            return numbers.stream();
                        })
                        .distinct()
                        .collect(Collectors.toList());

                var menus = lambdaQuery()
                        .in(SysMenu::getId, numberList)
                        .eq(SysMenu::getDeleteFlag, CommonConstants.NOT_DELETED)
                        .list();

                if (!menus.isEmpty()) {
                    for (var menu : menus) {
                        var meta = getMetaJsonObject(menu);
                        var menuVoBuilder = MenuVO.builder()
                                .id(menu.getId())
                                .name(menu.getName())
                                .title(menu.getTitle())
                                .titleEnglish(menu.getTitleEnglish())
                                .menuType(menu.getMenuType())
                                .path(menu.getPath())
                                .component(menu.getComponent())
                                .icon(menu.getIcon())
                                .sort(menu.getSort())
                                .redirect(menu.getRedirect())
                                .createTime(menu.getCreateTime())
                                .status(menu.getStatus())
                                .hideMenu(menu.getHideMenu())
                                .blank(menu.getBlank())
                                .ignoreKeepAlive(menu.getIgnoreKeepAlive())
                                .meta(meta);

                        if (menu.getParentId() != null) {
                            menuVoBuilder.parentId(menu.getParentId());
                        }

                        var menuVo = menuVoBuilder.build();
                        menuVos.add(menuVo);
                    }

                    menuVos.sort(Comparator.comparingInt(MenuVO::getSort));
                }

                menuData.put("total", menuVos.size());
                menuData.put("data", menuVos);
            }
        }

        return Response.responseData(menuData);
    }

    private JSONObject getMetaJsonObject(SysMenu menu) {
        var meta = new JSONObject();
        meta.put("title", menu.getTitle());
        meta.put("icon", menu.getIcon());
        meta.put("hideBreadcrumb", menu.getHideBreadcrumb());
        meta.put("hideTab", menu.getHideTab());
        meta.put("carryParam", menu.getCarryParam());
        meta.put("hideChildrenInMenu", menu.getHideChildrenInMenu());
        meta.put("affix", menu.getAffix());
        meta.put("frameSrc", menu.getFrameSrc());
        meta.put("realPath", menu.getRealPath());
        meta.put("dynamicLevel", 20);
        return meta;
    }
}