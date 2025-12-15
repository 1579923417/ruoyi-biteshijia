package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.vo.AppUserEarningItemDetailVo;
import com.ruoyi.system.domain.vo.AppUserProfileVo;
import com.ruoyi.system.domain.vo.AppUserEarningsVo;
import com.ruoyi.system.domain.vo.AppUserMinerListVo;

public interface IAppUserService {
    AppUser selectById(Long id);
    AppUser selectByPhone(String phone);
    List<AppUser> selectList(AppUser query);
    int insert(AppUser entity);
    int update(AppUser entity);
    int deleteByIds(Long[] ids);
    AppUserProfileVo selectProfileByUserId(Long id);
    AppUserEarningsVo selectEarningsByUserId(Long id);
    AppUserMinerListVo selectMyMiners(Long userId);
    int register(String phone, String rawPassword);
    int updateProfile(Long userId, String name, String phone, String bankName, String bankAccount, String avatar);
    AppUserEarningItemDetailVo selectEarningDetail(Long userId, Long id);
}
