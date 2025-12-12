package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.GlobalPowerInfo;

public interface GlobalPowerInfoMapper {
    GlobalPowerInfo selectById(Long id);
    List<GlobalPowerInfo> selectList(GlobalPowerInfo query);
    int insert(GlobalPowerInfo entity);
    int update(GlobalPowerInfo entity);
    int deleteByIds(Long[] ids);
}

