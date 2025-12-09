package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.MinerBrand;

public interface IMinerBrandService {
    MinerBrand selectById(Long id);
    List<MinerBrand> selectList(MinerBrand query);
    int insert(MinerBrand entity);
    int update(MinerBrand entity);
    int deleteByIds(Long[] ids);
}
