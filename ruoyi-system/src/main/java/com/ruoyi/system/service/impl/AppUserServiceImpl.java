package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ruoyi.system.factory.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.AppUserMiner;
import com.ruoyi.system.domain.vo.AppUserProfileVo;
import com.ruoyi.system.domain.vo.AppUserEarningsVo;
import com.ruoyi.system.domain.vo.AppUserEarningsDetailVo;
import com.ruoyi.system.domain.vo.AppUserMinerItemVo;
import com.ruoyi.system.domain.vo.AppUserMinerListVo;
import com.ruoyi.system.mapper.AppUserMapper;
import com.ruoyi.system.service.IAppUserMinerService;
import com.ruoyi.system.service.IAppUserMiningDailySummaryService;
import com.ruoyi.system.service.IAppUserService;
import com.ruoyi.system.service.IMinerBrandService;
import com.ruoyi.system.domain.MinerBrand;
import com.ruoyi.system.domain.AppUserMiningDailySummary;

import com.ruoyi.system.domain.vo.F2poolOverviewVo;
import com.ruoyi.system.service.IAppF2poolService;
import org.springframework.context.annotation.Lazy;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.utils.http.HttpUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

import static java.time.ZoneId.systemDefault;
/**
 * App 用户核心业务实现类
 *
 * 主要职责：
 * 1. 用户基础信息的 CRUD
 * 2. 用户个人中心数据聚合（矿机数量、收益统计）
 * 3. 收益换算（BTC → USDT → CNY）
 * 4. 用户收益摘要与明细构建
 * 5. 用户矿机列表与算力汇总
 *
 * 说明：
 * - 实时数据主要来源于 F2Pool
 * - 金额相关统一使用 BigDecimal，避免精度问题
 *
 * @author Jamie
 */
@Service
public class AppUserServiceImpl implements IAppUserService {

    /** 用户表 Mapper */
    @Autowired
    private AppUserMapper mapper;

    /** 用户矿机 Service */
    @Autowired
    private IAppUserMinerService appUserMinerService;

    /** 矿机品牌 Service（用于算力信息） */
    @Autowired
    private IMinerBrandService minerBrandService;

    /** 用户每日挖矿收益汇总 Service */
    @Autowired
    private IAppUserMiningDailySummaryService appUserMiningDailySummaryService;

    /**
     * F2Pool 对接 Service
     * 使用 @Lazy 避免循环依赖
     */
    @Autowired
    @Lazy
    private IAppF2poolService appF2poolService;

    @Autowired
    private ProxyFactory proxyFactory;

    private RestTemplate proxyRestTemplate;

    /* =========================
       基础查询与 CRUD
       ========================= */
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

    /* =========================
       用户个人中心数据
       ========================= */

    /**
     * 查询用户个人中心信息
     *
     * 数据来源说明：
     * - 基础信息：本地 AppUser 表
     * - 矿机数量 & 收益：实时从 F2Pool 拉取
     * - 收益单位：BTC → 换算为 CNY 后落库
     *
     * @param id 用户ID
     * @return 用户个人中心 VO
     */
    public AppUserProfileVo selectProfileByUserId(Long id){
        AppUser u = mapper.selectById(id);
        if (u == null) return null;

        // ===== 从 F2Pool 同步最新概览数据 =====
        F2poolOverviewVo overview = appF2poolService.getOverview(id);
        if (overview != null) {
            // 原始 BTC 收益
            BigDecimal btcYesterday = overview.getTotalYesterdayIncome();
            BigDecimal btcTotal = overview.getTotalIncome();
            BigDecimal btcToday = overview.getTotalTodayIncome();
            // BTC → CNY 换算
            BigDecimal cnyYesterday = convertBtcToCny(btcYesterday);
            BigDecimal cnyTotal = convertBtcToCny(btcTotal);
            BigDecimal cnyToday = convertBtcToCny(btcToday);

            // ===== 回写数据库（用于列表 / 缓存展示）=====
            AppUser userUpdate = new AppUser();
            userUpdate.setId(u.getId());
            userUpdate.setMinerCount(overview.getMinerCount());
            userUpdate.setTotalIncome(cnyTotal);
            userUpdate.setYesterdayIncome(cnyYesterday);
            userUpdate.setTodayIncome(cnyToday);
            mapper.update(userUpdate);

            // ===== 同步到内存对象，用于 VO 返回 =====
            if (overview.getMinerCount() != null) {
                u.setMinerCount(overview.getMinerCount());
            }
            u.setTotalIncome(cnyTotal);
            u.setYesterdayIncome(cnyYesterday);
            u.setTodayIncome(cnyToday);
        }

        // 构建个人中心返回对象
        AppUserProfileVo vo = new AppUserProfileVo();
        vo.setId(u.getId());
        vo.setName(u.getName());
        vo.setPhone(u.getPhone());
        vo.setBankName(u.getBankName());
        vo.setBankAccount(u.getBankAccount());
        vo.setAvatar(u.getAvatar());
        vo.setMinerCount(u.getMinerCount());
        vo.setTotalIncome(u.getTotalIncome());
        vo.setYesterdayIncome(u.getYesterdayIncome());
        vo.setTodayIncome(u.getTodayIncome());
        return vo;
    }

    /* =========================
       收益换算逻辑
       ========================= */
    /**
     * BTC → CNY 换算
     *
     * 计算链路：
     * BTC × (BTC → USDT 汇率) × (USDT → CNY 汇率)
     *
     * 说明：
     * - 不在入口直接 return
     * - 统一在末尾做安全兜底
     */
    private BigDecimal convertBtcToCny(BigDecimal btcAmount) {

        // 1. 参数规范化（不直接中断流程）
        BigDecimal safeBtcAmount = btcAmount == null
                ? BigDecimal.ZERO
                : btcAmount;

        // 2. 获取 BTC → USDT 汇率
        BigDecimal btcToUsdt = fetchRate(
                "https://www.exchange-rates.org/zh/api/v2/rates/lookup" +
                        "?isoTo=USDT&isoFrom=BTC&amount=1&pageCode=ConverterForPair"
        );

        // 3. 获取 USDT → CNY 汇率
        BigDecimal usdtToCny = fetchRate(
                "https://www.exchange-rates.org/zh/api/v2/rates/lookup" +
                        "?isoTo=CNY&isoFrom=USDT&amount=1&pageCode=ConverterForPair"
        );

        // 4. 汇率异常兜底（这里才 return）
        if (btcToUsdt == null || usdtToCny == null) {
            return BigDecimal.ZERO;
        }

        // 5. 金额换算
        BigDecimal rawAmount = safeBtcAmount
                .multiply(btcToUsdt)
                .multiply(usdtToCny);

        // 6. 金额格式化（统一出口）
        return toMoney(rawAmount);
    }


    @PostConstruct
    private void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setProxy(proxyFactory.buildHttpProxy());
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(30000);
        this.proxyRestTemplate = new RestTemplate(factory);
    }

    /**
     * 获取实时汇率
     *
     * 从官方 JSON 返回中读取 Rate 字段
     */
    private BigDecimal fetchRate(String url) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "Mozilla/5.0 (Java Proxy RestTemplate)");

            HttpEntity<String> entity = new HttpEntity<>("{}", headers);
            // 发起请求并获取原始响应
            String resp = proxyRestTemplate.postForObject(url, entity, String.class);
            if (resp == null || resp.isEmpty()) {
                return null;
            }

            JSONObject obj = JSON.parseObject(resp);
            return obj.getBigDecimal("Rate");

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 金额格式化（用于前端展示）
     *
     * 处理规则：
     * 1. 统一保留 2 位小数，四舍五入
     * 2. 若真实金额 > 0，但格式化后为 0.00
     *    → 强制返回 0.01，避免用户误解为“无收益”
     */
    private BigDecimal toMoney(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO;

        BigDecimal scaled = amount.setScale(2, RoundingMode.HALF_UP);

        if (scaled.signum() == 0 && amount.signum() > 0) {
            return new BigDecimal("0.01");
        }

        return scaled;
    }

    /* =========================
       用户收益汇总与明细
       ========================= */

    /**
     * 构建用户收益摘要与收益明细
     *
     * 包含：
     * - 今日预计收益
     * - 昨日收益金额
     * - 昨日结算记录（08:00 结算）
     *
     * @param id 用户ID
     * @return 收益汇总 VO
     */
    public AppUserEarningsVo selectEarningsByUserId(Long id){
        AppUserMiner query = new AppUserMiner();
        query.setUserId(id);
        List<AppUserMiner> miners = appUserMinerService.selectList(query);

        BigDecimal zero = BigDecimal.ZERO;

        BigDecimal totalMined = BigDecimal.ZERO; 

        BigDecimal yesterdayMined = BigDecimal.ZERO; 

        // 今日预计挖矿收益
        BigDecimal todayMined = miners.stream()
                .map(m -> m.getEstimatedTodayIncome() == null ? zero : m.getEstimatedTodayIncome())
                .reduce(zero, BigDecimal::add);

        // 昨日收益金额
        BigDecimal yesterdayIncomeAmount = miners.stream()
                .map(m -> m.getYesterdayIncome() == null ? zero : m.getYesterdayIncome())
                .reduce(zero, BigDecimal::add);

        // 从每日汇总表查询昨日收益项：统计日期、昨日收益、累计收益
        AppUserMiningDailySummary dailyQuery = new AppUserMiningDailySummary();
        dailyQuery.setUserId(id);
        List<AppUserMiningDailySummary> incomes = appUserMiningDailySummaryService.selectList(dailyQuery);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        java.util.List<AppUserEarningsDetailVo> details = incomes.stream().map(income -> {
            AppUserEarningsDetailVo d = new AppUserEarningsDetailVo();
            d.setId(income.getId());
            java.time.LocalDate settleDate = income.getStatDate().toInstant().atZone(systemDefault()).toLocalDate();
            d.setSettleTime(settleDate.atTime(8, 0).format(dtf));
            d.setQuantity(null);
            d.setAmount(income.getIncome());
            return d;
        }).collect(Collectors.toList());

        AppUserEarningsVo vo = new AppUserEarningsVo();
        vo.setTotalMined(totalMined);
        vo.setYesterdayMined(yesterdayMined);
        vo.setTodayMined(todayMined);
        vo.setYesterdayIncomeAmount(yesterdayIncomeAmount);
        vo.setDetails(details);
        return vo;
    }

    /* =========================
       用户矿机列表
       ========================= */

    /**
     * 查询当前用户的矿机列表及总算力
     *
     * @param userId 用户ID
     * @return 矿机列表 VO
     */
    public AppUserMinerListVo selectMyMiners(Long userId){
        AppUserMiner query = new AppUserMiner();
        query.setUserId(userId);
        List<AppUserMiner> miners = appUserMinerService.selectList(query);
        BigDecimal zero = BigDecimal.ZERO;
        List<AppUserMinerItemVo> items = new ArrayList<>();
        BigDecimal totalHash = zero;
        for (AppUserMiner m : miners){
            AppUserMinerItemVo item = new AppUserMinerItemVo();
            item.setId(m.getId());
            item.setBrandName(m.getBrandName());
            item.setMiningUserName(m.getMiningUserName());
            item.setApiCode(m.getApiCode());
            item.setTotalIncome(m.getTotalIncome());
            BigDecimal hr = zero;
            if (m.getBrandId() != null){
                MinerBrand b = minerBrandService.selectById(m.getBrandId());
                if (b != null && b.getHashRate() != null){
                    hr = b.getHashRate();
                }
            }
            item.setHashRate(hr);
            totalHash = totalHash.add(hr);
            items.add(item);
        }
        AppUserMinerListVo vo = new AppUserMinerListVo();
        vo.setItems(items);
        vo.setTotalHashRate(totalHash);
        return vo;
    }

//    /**
//     * APP 用户注册：校验手机号唯一，密码加密存储
//     */
//    public int register(String phone, String rawPassword){
//        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(rawPassword)){
//            throw new IllegalArgumentException("参数错误");
//        }
//        AppUser existed = mapper.selectByPhone(phone);
//        if (existed != null){
//            throw new RuntimeException("手机号已存在");
//        }
//        AppUser entity = new AppUser();
//        entity.setPhone(phone);
//        entity.setName(phone);
//        entity.setPassword(SecurityUtils.encryptPassword(rawPassword));
//        return mapper.insert(entity);
//    }

    /**
     * 更新个人中心资料
     *
     * 可修改字段：
     * - 昵称
     * - 手机号（唯一校验）
     * - 银行信息
     * - 头像
     */
    public int updateProfile(Long userId, String name, String phone, String bankName, String bankAccount, String avatar){
        if (userId == null){
            throw new IllegalArgumentException("缺少ID");
        }
        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(phone) && StringUtils.isEmpty(bankName) && StringUtils.isEmpty(bankAccount) && StringUtils.isEmpty(avatar)) {
            throw new IllegalArgumentException("未提交任何修改内容");
        }
        if (StringUtils.isNotEmpty(phone)){
            AppUser existed = mapper.selectByPhone(phone);
            if (existed != null && !existed.getId().equals(userId)){
                throw new RuntimeException("手机号已存在");
            }
        }
        AppUser entity = new AppUser();
        entity.setId(userId);
        entity.setName(name);
        entity.setPhone(phone);
        entity.setBankName(bankName);
        entity.setBankAccount(bankAccount);
        entity.setAvatar(avatar);
        return mapper.update(entity);
    }
}
