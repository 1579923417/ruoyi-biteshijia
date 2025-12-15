package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.AppUserMiningDailySummary;

/**
 * 用户矿机每日收益汇总 Mapper
 */
public interface AppUserMiningDailySummaryMapper {
    AppUserMiningDailySummary selectById(Long id);
    List<AppUserMiningDailySummary> selectList(AppUserMiningDailySummary query);
    int insert(AppUserMiningDailySummary entity);
    int update(AppUserMiningDailySummary entity);
    int deleteByIds(Long[] ids);
}
