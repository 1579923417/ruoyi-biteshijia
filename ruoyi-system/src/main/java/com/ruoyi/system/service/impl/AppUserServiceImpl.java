package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.mapper.AppUserMapper;
import com.ruoyi.system.service.IAppUserService;

@Service
public class AppUserServiceImpl implements IAppUserService {
    @Autowired
    private AppUserMapper mapper;

    public AppUser selectById(Long id){
        return mapper.selectById(id);
    }

    public AppUser selectByPhone(String phone){
        return mapper.selectByPhone(phone);
    }

    public List<AppUser> selectList(AppUser query){
        return mapper.selectList(query);
    }

    public int insert(AppUser entity){
        return mapper.insert(entity);
    }

    public int update(AppUser entity){
        return mapper.update(entity);
    }

    public int deleteByIds(Long[] ids){
        return mapper.deleteByIds(ids);
    }
}
