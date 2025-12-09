package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AppUserMiner;

public interface IAppUserMinerService {
    AppUserMiner selectById(Long id);
    List<AppUserMiner> selectList(AppUserMiner query);
    int insert(AppUserMiner entity);
    int update(AppUserMiner entity);
    int deleteByIds(Long[] ids);
    int updateStatus(Long id, Integer status);
    int bindUser(Long id, Long userId);
    int unbindUser(Long id);
}
