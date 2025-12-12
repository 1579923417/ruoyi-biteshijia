package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.AppUserMinerIncome;

/**
 * 用户矿机每日收益记录 Mapper
 */
public interface AppUserMinerIncomeMapper {
    AppUserMinerIncome selectById(Long id);
    List<AppUserMinerIncome> selectList(AppUserMinerIncome query);
    int insert(AppUserMinerIncome entity);
    int update(AppUserMinerIncome entity);
    int deleteByIds(Long[] ids);
}

