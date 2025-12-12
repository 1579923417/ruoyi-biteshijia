package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.MinerFarm;
import com.ruoyi.system.domain.vo.MinerFarmVo;

/**
 * 全球矿场信息 Service
 */
public interface IMinerFarmService {
    MinerFarm selectById(Long id);
    List<MinerFarm> selectList(MinerFarm query);
    int insert(MinerFarm entity);
    int update(MinerFarm entity);
    int deleteByIds(Long[] ids);
    List<MinerFarmVo> selectVoList(MinerFarm query);
}
