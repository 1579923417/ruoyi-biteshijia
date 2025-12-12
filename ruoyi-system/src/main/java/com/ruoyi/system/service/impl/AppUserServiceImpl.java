package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.AppUserMiner;
import com.ruoyi.system.domain.vo.AppUserProfileVo;
import com.ruoyi.system.domain.vo.AppUserEarningsVo;
import com.ruoyi.system.domain.vo.AppUserEarningsDetailVo;
import com.ruoyi.system.domain.vo.AppUserMinerItemVo;
import com.ruoyi.system.domain.vo.AppUserMinerListVo;
import com.ruoyi.system.mapper.AppUserMapper;
import com.ruoyi.system.service.IAppUserMinerService;
import com.ruoyi.system.service.IAppUserMinerIncomeService;
import com.ruoyi.system.service.IAppUserService;
import com.ruoyi.system.service.IMinerBrandService;
import com.ruoyi.system.domain.MinerBrand;
import com.ruoyi.system.domain.AppUserMinerIncome;
import com.ruoyi.system.domain.vo.AppUserEarningItemDetailVo;

import static java.time.ZoneId.systemDefault;

@Service
public class AppUserServiceImpl implements IAppUserService {
    @Autowired
    private AppUserMapper mapper;
    @Autowired
    private IAppUserMinerService appUserMinerService;
    @Autowired
    private IMinerBrandService minerBrandService;
    @Autowired
    private IAppUserMinerIncomeService appUserMinerIncomeService;

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

    /**
     *
     * @param id
     * @return
     */
    public AppUserProfileVo selectProfileByUserId(Long id){
        AppUser u = mapper.selectById(id);
        if (u == null) return null;
        AppUserProfileVo vo = new AppUserProfileVo();
        vo.setId(u.getId());
        vo.setName(u.getName());
        vo.setPhone(u.getPhone());
        vo.setBankName(u.getBankName());
        vo.setBankAccount(u.getBankAccount());
        vo.setMinerCount(u.getMinerCount());
        vo.setTotalIncome(u.getTotalIncome());
        vo.setYesterdayIncome(u.getYesterdayIncome());
        vo.setTodayIncome(u.getTodayIncome());
        return vo;
    }

    /**
     * 构建用户收益摘要与收益列表
     * 汇总累计挖矿、昨日挖矿、今日预计挖矿、今日已挖，以及昨日收益金额
     * 同时返回昨日结算的收益列表项（结算时间为每日 08:00）
     */
    public AppUserEarningsVo selectEarningsByUserId(Long id){
        AppUserMiner query = new AppUserMiner();
        query.setUserId(id);
        List<AppUserMiner> miners = appUserMinerService.selectList(query);

        BigDecimal zero = BigDecimal.ZERO;

        BigDecimal totalMined = miners.stream()
                .map(m -> m.getTotalMined() == null ? zero : m.getTotalMined())
                .reduce(zero, BigDecimal::add);

        BigDecimal yesterdayMined = miners.stream()
                .map(m -> m.getYesterdayMined() == null ? zero : m.getYesterdayMined())
                .reduce(zero, BigDecimal::add);

        BigDecimal todayMined = miners.stream()
                .map(m -> m.getTodayMined() == null ? zero : m.getTodayMined())
                .reduce(zero, BigDecimal::add);


        BigDecimal yesterdayIncomeAmount = miners.stream()
                .map(m -> m.getYesterdayIncome() == null ? zero : m.getYesterdayIncome())
                .reduce(zero, BigDecimal::add);

        // 从数据库查询收益列表：结算时间、数量、金额
        AppUserMinerIncome incomeQuery = new AppUserMinerIncome();
        incomeQuery.setUserId(id);
        List<AppUserMinerIncome> incomes = appUserMinerIncomeService.selectList(incomeQuery);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        java.util.List<AppUserEarningsDetailVo> details = incomes.stream().map(income -> {
            AppUserEarningsDetailVo d = new AppUserEarningsDetailVo();
            d.setId(income.getId());
            java.time.LocalDate settleDate = income.getDate().toInstant().atZone(systemDefault()).toLocalDate();
            d.setSettleTime(settleDate.atTime(8, 0).format(dtf));
            d.setQuantity(income.getMinedAmount());
            d.setAmount(income.getIncomeRmb());
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

    /**
     *
     * @param userId
     * @param id
     * @return
     */
    public AppUserEarningItemDetailVo selectEarningDetail(Long userId, Long id){
        AppUserMinerIncome income = appUserMinerIncomeService.selectById(id);
        if (income == null || (income.getUserId() != null && !income.getUserId().equals(userId))) {
            return null;
        }
        AppUserEarningItemDetailVo vo = new AppUserEarningItemDetailVo();
        vo.setId(income.getId());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = income.getDate() == null ? null : income.getDate().toInstant().atZone(systemDefault()).toLocalDate().format(df);
        vo.setDate(dateStr);
        vo.setQuantity(income.getMinedAmount());
        vo.setAmount(income.getIncomeRmb());
        vo.setPowerCost(income.getPowerCost());
        vo.setManagementFee(income.getManagementFee());
        return vo;
    }

    /**
     *
     * @param userId
     * @return
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
            item.setApiCode(m.getApiCode());
            item.setTotalMined(m.getTotalMined());
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

    /**
     * APP 用户注册：校验手机号唯一，密码加密存储
     */
    public int register(String phone, String rawPassword){
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(rawPassword)){
            throw new IllegalArgumentException("参数错误");
        }
        AppUser existed = mapper.selectByPhone(phone);
        if (existed != null){
            throw new RuntimeException("手机号已存在");
        }
        AppUser entity = new AppUser();
        entity.setPhone(phone);
        entity.setName(phone);
        entity.setPassword(SecurityUtils.encryptPassword(rawPassword));
        return mapper.insert(entity);
    }

    /**
     * 更新个人中心资料：名称、手机号、开户行、账户号码
     */
    public int updateProfile(Long userId, String name, String phone, String bankName, String bankAccount){
        if (userId == null){
            throw new IllegalArgumentException("缺少ID");
        }
        if (StringUtils.isEmpty(name) && StringUtils.isEmpty(phone) && StringUtils.isEmpty(bankName) && StringUtils.isEmpty(bankAccount)) {
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
        return mapper.update(entity);
    }
}
