package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.AppUserMiner;
import com.ruoyi.system.mapper.AppUserMinerMapper;
import com.ruoyi.system.service.IAppUserMinerService;

@Service
public class AppUserMinerServiceImpl implements IAppUserMinerService {
    @Autowired
    private AppUserMinerMapper mapper;

    public AppUserMiner selectById(Long id){
        return mapper.selectById(id);
    }

    public List<AppUserMiner> selectList(AppUserMiner query){
        return mapper.selectList(query);
    }

    public int insert(AppUserMiner entity){
        return mapper.insert(entity);
    }

    public int update(AppUserMiner entity){
        return mapper.update(entity);
    }

    public int deleteByIds(Long[] ids){
        return mapper.deleteByIds(ids);
    }

    public int updateStatus(Long id, Integer status){
        return mapper.updateStatus(id, status);
    }

    public int bindUser(Long id, Long userId){
        return mapper.bindUser(id, userId);
    }

    public int unbindUser(Long id){
        return mapper.unbindUser(id);
    }
}
