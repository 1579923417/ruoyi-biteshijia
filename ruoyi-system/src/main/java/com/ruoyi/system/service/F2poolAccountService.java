package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.F2poolAccount;

public interface F2poolAccountService {
    F2poolAccount selectById(Integer id);
    List<F2poolAccount> selectList(F2poolAccount query);
    F2poolAccount getByUsername(String username);
    int insert(F2poolAccount entity);
    int update(F2poolAccount entity);
    int deleteByIds(Integer[] ids);
}
