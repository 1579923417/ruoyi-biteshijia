package com.ruoyi.system.domain;

import java.io.Serializable;
import com.ruoyi.common.core.domain.BaseEntity;

public class AppMenu extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer menuType;
    private String menuTypeDesc;
    private String title;
    private String icon;
    private Integer type;
    private String path;
    private Integer sort;
    private Integer status;
    private Long parentId;
    private String remark;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getMenuType() { return menuType; }
    public void setMenuType(Integer menuType) { this.menuType = menuType; }
    public String getMenuTypeDesc() { return menuTypeDesc; }
    public void setMenuTypeDesc(String menuTypeDesc) { this.menuTypeDesc = menuTypeDesc; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
