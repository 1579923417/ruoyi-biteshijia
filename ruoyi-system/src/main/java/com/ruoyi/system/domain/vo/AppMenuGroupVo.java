package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppMenuGroupVo {
    private Integer menuType;
    private String menuTypeDesc;
    private List<AppMenuVo> items;

    public Integer getMenuType() { return menuType; }
    public void setMenuType(Integer menuType) { this.menuType = menuType; }
    public String getMenuTypeDesc() { return menuTypeDesc; }
    public void setMenuTypeDesc(String menuTypeDesc) { this.menuTypeDesc = menuTypeDesc; }
    public List<AppMenuVo> getItems() { return items; }
    public void setItems(List<AppMenuVo> items) { this.items = items; }
}

