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

    /**
     * 创建矿机并同步到 F2Pool，然后写入 app_user_miner
     */
    AppUserMiner createWithF2pool(Long userId, Long brandId, String miningUserName, String apiCode, java.math.BigDecimal managementFeeRate);
}
