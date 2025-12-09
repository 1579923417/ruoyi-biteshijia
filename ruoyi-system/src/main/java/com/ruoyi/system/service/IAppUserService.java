package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AppUser;

public interface IAppUserService {
    AppUser selectById(Long id);
    AppUser selectByPhone(String phone);
    List<AppUser> selectList(AppUser query);
    int insert(AppUser entity);
    int update(AppUser entity);
    int deleteByIds(Long[] ids);
}
