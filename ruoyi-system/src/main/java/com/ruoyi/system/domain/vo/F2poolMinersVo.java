package com.ruoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.util.List;

/**
 * F2Pool 我的矿机返回对象
 *
 * 聚合第三方 F2Pool 接口的数据，用于“我的矿机”页面展示：
 * - totalYesterdayIncome：所有子帐户昨日收益之和
 * - totalHashRate：所有子帐户实时算力之和（来自 /v2/hash_rate/info_list）
 * - items：每个子帐户的列表项（mining_user_name、total_income）
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class F2poolMinersVo {
    private BigDecimal totalYesterdayIncome;
    private BigDecimal totalHashRate;
    private List<F2poolMinerItemVo> items;

    /** 获取昨日收益总和 */
    public BigDecimal getTotalYesterdayIncome() { return totalYesterdayIncome; }
    public void setTotalYesterdayIncome(BigDecimal totalYesterdayIncome) { this.totalYesterdayIncome = totalYesterdayIncome; }
    /** 获取实时算力总和 */
    public BigDecimal getTotalHashRate() { return totalHashRate; }
    public void setTotalHashRate(BigDecimal totalHashRate) { this.totalHashRate = totalHashRate; }
    /** 获取子帐户列表项 */
    public List<F2poolMinerItemVo> getItems() { return items; }
    public void setItems(List<F2poolMinerItemVo> items) { this.items = items; }
}
