package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.AppUserMiningDailySummary;
import com.ruoyi.system.mapper.AppUserMiningDailySummaryMapper;
import com.ruoyi.system.service.IAppUserMiningDailySummaryService;

/**
 * 用户矿机每日收益汇总 Service 实现
 */
@Service
public class AppUserMiningDailySummaryServiceImpl implements IAppUserMiningDailySummaryService {
    @Autowired
    private AppUserMiningDailySummaryMapper mapper;

    public AppUserMiningDailySummary selectById(Long id){
        return mapper.selectById(id);
    }

    public List<AppUserMiningDailySummary> selectList(AppUserMiningDailySummary query){
        return mapper.selectList(query);
    }

    public int insert(AppUserMiningDailySummary entity){
        return mapper.insert(entity);
    }

    public int update(AppUserMiningDailySummary entity){
        return mapper.update(entity);
    }

    public int deleteByIds(Long[] ids){
        return mapper.deleteByIds(ids);
    }
}
