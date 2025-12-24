package com.ruoyi.web.controller.app;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.ruoyi.system.domain.AppUserMiningDailySummary;
import com.ruoyi.system.service.IAppUserMiningDailySummaryService;
import io.swagger.annotations.Api;

/**
 * APP 用户每日挖矿收益汇总管理 Controller
 *
 * 主要功能：
 * 1. 查询用户每日收益汇总列表（分页）
 * 2. 查看单条收益汇总详情
 * 3. 新增每日收益汇总记录
 * 4. 修改每日收益汇总记录
 * 5. 删除每日收益汇总记录
 *
 */
@RestController
@RequestMapping("/admin/app/user/mining/daily/summary")
@Api(tags = "用户管理--APP用户每日收益汇总")
public class AppUserMinerIncomeController extends BaseController {
    @Autowired
    private IAppUserMiningDailySummaryService dailySummaryService;

    /**
     * 查询 APP 用户每日收益汇总列表（分页）
     *
     * @param query 查询条件（支持按用户、日期等字段筛选）
     * @return 分页数据列表
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiningDailySummary:list')")
    @GetMapping("/list")
    public TableDataInfo list(AppUserMiningDailySummary query){
        startPage();
        List<AppUserMiningDailySummary> list = dailySummaryService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 根据 ID 查询单条每日收益汇总详情
     *
     * @param id 主键 ID
     * @return 收益汇总详情数据
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiningDailySummary:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id){
        AppUserMiningDailySummary data = dailySummaryService.selectById(id);
        return AjaxResult.success(data);
    }

    /**
     * 新增 APP 用户每日收益汇总记录
     *
     * @param entity 每日收益汇总实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiningDailySummary:add')")
    @Log(title = "APP用户每日收益汇总", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppUserMiningDailySummary entity){
        return toAjax(dailySummaryService.insert(entity));
    }

    /**
     * 修改 APP 用户每日收益汇总记录
     *
     * @param entity 每日收益汇总实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiningDailySummary:edit')")
    @Log(title = "APP用户每日收益汇总", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AppUserMiningDailySummary entity){
        return toAjax(dailySummaryService.update(entity));
    }

    /**
     * 批量删除 APP 用户每日收益汇总记录
     *
     * @param ids 需要删除的主键 ID 数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMinerIncome:remove')")
    @Log(title = "APP用户每日收益", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(dailySummaryService.deleteByIds(ids));
    }
}
