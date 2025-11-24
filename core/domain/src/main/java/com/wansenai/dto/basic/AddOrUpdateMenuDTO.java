package com.wansenai.dto.basic;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddOrUpdateMenuDTO {

    private Integer id;

    private Integer menuType;

    private String name;

    private String title;

    private Integer parentId;

    private Integer sort;

    private String icon;

    private String path;

    private String component;

    private Integer status;

    private Integer blank;

    private Integer ignoreKeepAlive;

    private Integer hideMenu;
}