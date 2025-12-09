package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.AppUserMiner;

public interface AppUserMinerMapper {
    AppUserMiner selectById(Long id);
    List<AppUserMiner> selectList(AppUserMiner query);
    int insert(AppUserMiner entity);
    int update(AppUserMiner entity);
    int deleteByIds(Long[] ids);
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    int bindUser(@Param("id") Long id, @Param("userId") Long userId);
    int unbindUser(@Param("id") Long id);
}
