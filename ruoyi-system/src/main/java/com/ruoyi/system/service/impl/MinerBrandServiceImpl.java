package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.MinerBrand;
import com.ruoyi.system.mapper.MinerBrandMapper;
import com.ruoyi.system.service.IMinerBrandService;

@Service
public class MinerBrandServiceImpl implements IMinerBrandService {
    @Autowired
    private MinerBrandMapper mapper;

    public MinerBrand selectById(Long id){
        return mapper.selectById(id);
    }

    public List<MinerBrand> selectList(MinerBrand query){
        return mapper.selectList(query);
    }

    public int insert(MinerBrand entity){
        return mapper.insert(entity);
    }

    public int update(MinerBrand entity){
        return mapper.update(entity);
    }

    public int deleteByIds(Long[] ids){
        return mapper.deleteByIds(ids);
    }
}
