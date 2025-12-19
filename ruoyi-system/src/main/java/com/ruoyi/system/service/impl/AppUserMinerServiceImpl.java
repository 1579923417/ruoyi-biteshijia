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

/**
 * App 用户矿机管理业务实现类
 *
 * @author Jamie
 */
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

    /**
     * 根据主键查询矿机
     *
     * @param id 矿机ID
     * @return 矿机实体
     */
    public AppUserMiner selectById(Long id){
        return mapper.selectById(id);
    }

    /**
     * 根据条件查询矿机列表
     *
     * @param query 查询条件
     * @return 矿机列表
     */
    public List<AppUserMiner> selectList(AppUserMiner query){
        return mapper.selectList(query);
    }

    /**
     * 新增矿机记录（仅数据库层）
     *
     * <p>
     * 注意：该方法不会同步 F2Pool，
     * 仅用于内部或已确认来源的数据写入。
     * </p>
     *
     * @param entity 矿机实体
     * @return 影响行数
     */
    public int insert(AppUserMiner entity){
        return mapper.insert(entity);
    }

    /**
     * 更新矿机信息
     *
     * @param entity 矿机实体
     * @return 影响行数
     */
    public int update(AppUserMiner entity){
        return mapper.update(entity);
    }

    /**
     * 批量删除矿机
     *
     * @param ids 矿机ID数组
     * @return 影响行数
     */
    public int deleteByIds(Long[] ids){
        return mapper.deleteByIds(ids);
    }

    /**
     * 启用 / 禁用矿机
     *
     * @param id     矿机ID
     * @param status 状态（1 启用，0 禁用）
     * @return 影响行数
     */
    public int updateStatus(Long id, Integer status){
        return mapper.updateStatus(id, status);
    }

    /**
     * 绑定矿机到指定用户
     *
     * @param id     矿机ID
     * @param userId 用户ID
     * @return 影响行数
     */
    public int bindUser(Long id, Long userId){
        return mapper.bindUser(id, userId);
    }

    /**
     * 解绑矿机与用户的关系
     *
     * @param id 矿机ID
     * @return 影响行数
     */
    public int unbindUser(Long id){
        return mapper.unbindUser(id);
    }

/**
     * 创建矿机并同步到 F2Pool，然后写入 app_user_miner 表
     *
     * <p>
     * 业务流程：
     * <ol>
     *   <li>校验用户及 F2Pool Token 是否存在</li>
     *   <li>校验矿机品牌是否合法</li>
     *   <li>校验矿机名称是否已存在（全局唯一）</li>
     *   <li>调用 F2Pool API 创建子账户（矿机）</li>
     *   <li>本地数据库写入矿机基础信息</li>
     *   <li>触发一次矿机数据同步，补全收益信息</li>
     * </ol>
     * </p>
     *
     * <p>
     * 说明：
     * <ul>
     *   <li>该方法是<strong>唯一允许创建矿机的入口</strong></li>
     *   <li>定时任务不会执行创建逻辑，只负责数据同步</li>
     * </ul>
     * </p>
     *
     * @param userId             用户ID
     * @param brandId            矿机品牌ID
     * @param miningUserName     F2Pool 子账户名称
     * @param apiCode            API 标识码
     * @param managementFeeRate  管理费率
     * @return 创建成功的矿机实体
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
