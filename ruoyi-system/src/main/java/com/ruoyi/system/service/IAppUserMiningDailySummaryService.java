package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AppUserMiningDailySummary;

public interface IAppUserMiningDailySummaryService {
    AppUserMiningDailySummary selectById(Long id);
    List<AppUserMiningDailySummary> selectList(AppUserMiningDailySummary query);
    int insert(AppUserMiningDailySummary entity);
    int update(AppUserMiningDailySummary entity);
    int deleteByIds(Long[] ids);
}

