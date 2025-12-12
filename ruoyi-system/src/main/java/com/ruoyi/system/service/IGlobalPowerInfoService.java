package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.GlobalPowerInfo;
import com.ruoyi.system.domain.vo.GlobalPowerInfoVo;

public interface IGlobalPowerInfoService {
    GlobalPowerInfo selectById(Long id);
    List<GlobalPowerInfo> selectList(GlobalPowerInfo query);
    int insert(GlobalPowerInfo entity);
    int update(GlobalPowerInfo entity);
    int deleteByIds(Long[] ids);
    List<GlobalPowerInfoVo> selectVoList(GlobalPowerInfo query);
}
