package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.MobileBanner;
import com.ruoyi.system.mapper.MobileBannerMapper;
import com.ruoyi.system.service.IMobileBannerService;

@Service
public class MobileBannerServiceImpl implements IMobileBannerService {
    @Autowired
    private MobileBannerMapper mapper;

    public MobileBanner selectById(Long id){
        return mapper.selectById(id);
    }

    public List<MobileBanner> selectList(MobileBanner query){
        return mapper.selectList(query);
    }

    public int insert(MobileBanner entity){
        return mapper.insert(entity);
    }

    public int update(MobileBanner entity){
        return mapper.update(entity);
    }

    public int deleteByIds(Long[] ids){
        return mapper.deleteByIds(ids);
    }

    public int updateStatus(Long id, Integer status){
        return mapper.updateStatus(id, status);
    }
}
