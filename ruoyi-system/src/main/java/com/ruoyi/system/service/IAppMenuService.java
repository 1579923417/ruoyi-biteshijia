package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AppMenu;

public interface IAppMenuService {
    AppMenu selectById(Long id);
    List<AppMenu> selectList(AppMenu query);
    int insert(AppMenu entity);
    int update(AppMenu entity);
    int deleteByIds(Long[] ids);
    int updateStatus(Long id, Integer status);
}

