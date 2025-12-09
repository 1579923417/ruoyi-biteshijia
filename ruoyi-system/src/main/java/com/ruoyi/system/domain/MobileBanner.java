package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class MobileBanner extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String imageUrl;
    private Integer sort;
    private Integer status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
