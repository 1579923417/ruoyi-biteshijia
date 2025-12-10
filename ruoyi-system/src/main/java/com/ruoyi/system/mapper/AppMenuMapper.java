package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.AppMenu;

public interface AppMenuMapper {
    AppMenu selectById(Long id);
    List<AppMenu> selectList(AppMenu query);
    int insert(AppMenu entity);
    int update(AppMenu entity);
    int deleteByIds(Long[] ids);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}

