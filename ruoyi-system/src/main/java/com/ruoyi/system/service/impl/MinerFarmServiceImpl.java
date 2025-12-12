package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.MinerFarm;
import com.ruoyi.system.mapper.MinerFarmMapper;
import com.ruoyi.system.service.IMinerFarmService;
import com.ruoyi.system.domain.vo.MinerFarmVo;

/**
 * 全球矿场信息 Service 实现
 */
@Service
public class MinerFarmServiceImpl implements IMinerFarmService {
    @Autowired
    private MinerFarmMapper mapper;

    public MinerFarm selectById(Long id){
        return mapper.selectById(id);
    }

    public List<MinerFarm> selectList(MinerFarm query){
        return mapper.selectList(query);
    }

    public int insert(MinerFarm entity){
        return mapper.insert(entity);
    }

    public int update(MinerFarm entity){
        return mapper.update(entity);
    }

    public int deleteByIds(Long[] ids){
        return mapper.deleteByIds(ids);
    }

    public List<MinerFarmVo> selectVoList(MinerFarm query){
        List<MinerFarm> list = mapper.selectList(query);
        List<MinerFarmVo> vos = new ArrayList<>();
        for (MinerFarm item : list) {
            MinerFarmVo vo = new MinerFarmVo();
            vo.setId(item.getId());
            vo.setName(item.getName());
            vo.setLocation(item.getLocation());
            vo.setEnergyType(item.getEnergyType());
            vo.setPriceWetSeason(item.getPriceWetSeason());
            vo.setPriceDrySeason(item.getPriceDrySeason());
            vo.setPriceAllYear(item.getPriceAllYear());
            vo.setStatus(item.getStatus());
            vos.add(vo);
        }
        return vos;
    }
}
