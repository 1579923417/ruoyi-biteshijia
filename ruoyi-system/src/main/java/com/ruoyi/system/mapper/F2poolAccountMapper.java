package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.F2poolAccount;

public interface F2poolAccountMapper {
    F2poolAccount selectById(Integer id);
    List<F2poolAccount> selectList(F2poolAccount query);
    int insert(F2poolAccount entity);
    int update(F2poolAccount entity);
    int deleteByIds(Integer[] ids);
    F2poolAccount selectByUsername(String username);
}
