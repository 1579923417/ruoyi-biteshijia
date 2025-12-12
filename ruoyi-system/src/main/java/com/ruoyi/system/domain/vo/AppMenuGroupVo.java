package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppMenuGroupVo {
    private Integer menuType;
    private String menuTypeDesc;
    private Long groupId;
    private String groupTitle;
    private List<AppMenuVo> items;

    public Integer getMenuType() { return menuType; }
    public void setMenuType(Integer menuType) { this.menuType = menuType; }
    public String getMenuTypeDesc() { return menuTypeDesc; }
    public void setMenuTypeDesc(String menuTypeDesc) { this.menuTypeDesc = menuTypeDesc; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public String getGroupTitle() { return groupTitle; }
    public void setGroupTitle(String groupTitle) { this.groupTitle = groupTitle; }
    public List<AppMenuVo> getItems() { return items; }
    public void setItems(List<AppMenuVo> items) { this.items = items; }
}
