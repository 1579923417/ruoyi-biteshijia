package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.F2poolAccount;
import com.ruoyi.system.mapper.F2poolAccountMapper;
import com.ruoyi.system.service.F2poolAccountService;

@Service
public class F2poolAccountServiceImpl implements F2poolAccountService {

    @Autowired
    private F2poolAccountMapper mapper;

    public F2poolAccount selectById(Integer id){
        return mapper.selectById(id);
    }

    public List<F2poolAccount> selectList(F2poolAccount query){
        return mapper.selectList(query);
    }

    public int insert(F2poolAccount entity){
        return mapper.insert(entity);
    }

    public int update(F2poolAccount entity){
        return mapper.update(entity);
    }

    public int deleteByIds(Integer[] ids){
        return mapper.deleteByIds(ids);
    }

    public F2poolAccount getByUsername(String username){
        return mapper.selectByUsername(username);
    }
}
