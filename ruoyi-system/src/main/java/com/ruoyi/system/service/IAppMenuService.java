package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AppMenu;
import com.ruoyi.system.domain.vo.AppMenuGroupVo;

public interface IAppMenuService {
    AppMenu selectById(Long id);
    List<AppMenu> selectList(AppMenu query);
    List<AppMenuGroupVo> selectVisibleGrouped();
    int insert(AppMenu entity);
    int update(AppMenu entity);
    int deleteByIds(Long[] ids);
    int updateStatus(Long id, Integer status);
}
