package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.MinerFarm;

/**
 * 全球矿场信息 Mapper
 */
public interface MinerFarmMapper {
    MinerFarm selectById(Long id);
    List<MinerFarm> selectList(MinerFarm query);
    int insert(MinerFarm entity);
    int update(MinerFarm entity);
    int deleteByIds(Long[] ids);
}

