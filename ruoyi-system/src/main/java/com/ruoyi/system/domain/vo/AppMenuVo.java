package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppMenuVo {
    private Long id;
    private String title;
    private String icon;
    private Integer type;
    private String path;
    private Integer sort;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
}

