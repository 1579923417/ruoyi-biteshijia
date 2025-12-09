package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.MinerBrand;

public interface MinerBrandMapper {
    MinerBrand selectById(Long id);
    List<MinerBrand> selectList(MinerBrand query);
    int insert(MinerBrand entity);
    int update(MinerBrand entity);
    int deleteByIds(Long[] ids);
}
