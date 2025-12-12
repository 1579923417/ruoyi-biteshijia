package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.GlobalPowerInfo;
import com.ruoyi.system.mapper.GlobalPowerInfoMapper;
import com.ruoyi.system.service.IGlobalPowerInfoService;
import com.ruoyi.system.domain.vo.GlobalPowerInfoVo;

@Service
public class GlobalPowerInfoServiceImpl implements IGlobalPowerInfoService {
    @Autowired
    private GlobalPowerInfoMapper mapper;

    public GlobalPowerInfo selectById(Long id){
        return mapper.selectById(id);
    }

    public List<GlobalPowerInfo> selectList(GlobalPowerInfo query){
        return mapper.selectList(query);
    }

    public int insert(GlobalPowerInfo entity){
        return mapper.insert(entity);
    }

    public int update(GlobalPowerInfo entity){
        return mapper.update(entity);
    }

    public int deleteByIds(Long[] ids){
        return mapper.deleteByIds(ids);
    }

    public List<GlobalPowerInfoVo> selectVoList(GlobalPowerInfo query){
        List<GlobalPowerInfo> list = mapper.selectList(query);
        List<GlobalPowerInfoVo> vos = new ArrayList<>();
        for (GlobalPowerInfo item : list) {
            GlobalPowerInfoVo vo = new GlobalPowerInfoVo();
            vo.setId(item.getId());
            vo.setName(item.getName());
            vo.setCountry(item.getCountry());
            vo.setPowerPriceCny(item.getPowerPriceCny());
            vo.setSupplyPeriod(item.getSupplyPeriod());
            vo.setStatus(item.getStatus());
            vos.add(vo);
        }
        return vos;
    }
}
