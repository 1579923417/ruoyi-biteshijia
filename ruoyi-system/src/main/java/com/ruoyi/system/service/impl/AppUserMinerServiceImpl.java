package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.net.Proxy;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.f2pool.F2PoolClient;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.AppUserMiner;
import com.ruoyi.system.domain.MinerBrand;
import com.ruoyi.system.mapper.AppUserMinerMapper;
import com.ruoyi.system.service.IAppUserMinerService;
import com.ruoyi.system.service.IAppUserService;
import com.ruoyi.system.service.IMinerBrandService;
import com.ruoyi.system.factory.ProxyFactory;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.system.service.F2PoolService;

@Service
public class AppUserMinerServiceImpl implements IAppUserMinerService {
    @Autowired
    private AppUserMinerMapper mapper;

    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private IMinerBrandService minerBrandService;

    @Autowired
    private ProxyFactory proxyFactory;

    @Autowired
    private F2PoolService f2PoolService;

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

    /**
     * 创建矿机并同步到 F2Pool，然后写入 app_user_miner
     */
    public AppUserMiner createWithF2pool(Long userId, Long brandId, String miningUserName, String apiCode, BigDecimal managementFeeRate){
        AppUser user = appUserService.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (StringUtils.isBlank(user.getF2poolToken())) {
            throw new RuntimeException("F2Pool Token不能为空");
        }
        MinerBrand brand = minerBrandService.selectById(brandId);
        if (brand == null) {
            throw new RuntimeException("矿机品牌不存在");
        }
        AppUserMiner dupQuery = new AppUserMiner();
        dupQuery.setMiningUserName(miningUserName);
        List<AppUserMiner> existed = mapper.selectList(dupQuery);
        if (existed != null) {
            for (AppUserMiner m : existed) {
                if (miningUserName.equalsIgnoreCase(m.getMiningUserName())) {
                    throw new RuntimeException("矿机名称已存在");
                }
            }
        }
        Proxy proxy = proxyFactory.buildHttpProxy();
        F2PoolClient client = new F2PoolClient(user.getF2poolUrl(), user.getF2poolToken(), proxy);
        JSONObject resp = client.addMiningUser(miningUserName);
        String createdName = resp == null ? null : resp.getString("mining_user_name");
        if (!miningUserName.equalsIgnoreCase(StringUtils.defaultString(createdName))) {
            throw new RuntimeException(resp == null ? "创建子帐户失败" : resp.toJSONString());
        }
        AppUserMiner entity = new AppUserMiner();
        entity.setUserId(userId);
        entity.setBrandId(brandId);
        entity.setMiningUserName(miningUserName);
        entity.setApiCode(apiCode);
        entity.setManagementFeeRate(managementFeeRate);
        entity.setEstimatedTodayIncome(BigDecimal.ZERO);
        entity.setTotalIncome(BigDecimal.ZERO);
        entity.setYesterdayIncome(BigDecimal.ZERO);
        entity.setStatus(1);
        mapper.insert(entity);
        try {
            f2PoolService.syncMinerData();
        } catch (Exception ignore) {
        }
        return entity;
    }
}
