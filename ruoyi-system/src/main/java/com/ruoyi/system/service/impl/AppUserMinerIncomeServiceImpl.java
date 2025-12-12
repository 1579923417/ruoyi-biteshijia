package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.AppUserMinerIncome;
import com.ruoyi.system.mapper.AppUserMinerIncomeMapper;
import com.ruoyi.system.service.IAppUserMinerIncomeService;

/**
 * 用户矿机每日收益记录 Service 实现
 */
@Service
public class AppUserMinerIncomeServiceImpl implements IAppUserMinerIncomeService {
    @Autowired
    private AppUserMinerIncomeMapper mapper;

    public AppUserMinerIncome selectById(Long id){
        return mapper.selectById(id);
    }

    public List<AppUserMinerIncome> selectList(AppUserMinerIncome query){
        return mapper.selectList(query);
    }

    public int insert(AppUserMinerIncome entity){
        return mapper.insert(entity);
    }

    public int update(AppUserMinerIncome entity){
        return mapper.update(entity);
    }

    public int deleteByIds(Long[] ids){
        return mapper.deleteByIds(ids);
    }
}

