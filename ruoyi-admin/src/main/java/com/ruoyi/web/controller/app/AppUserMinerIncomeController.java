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

@RestController
@RequestMapping("/admin/app/user/mining/daily/summary")
@Api(tags = "用户管理--APP用户每日收益汇总")
public class AppUserMinerIncomeController extends BaseController {
    @Autowired
    private IAppUserMiningDailySummaryService dailySummaryService;

    @PreAuthorize("@ss.hasPermi('admin:appUserMiningDailySummary:list')")
    @GetMapping("/list")
    public TableDataInfo list(AppUserMiningDailySummary query){
        startPage();
        List<AppUserMiningDailySummary> list = dailySummaryService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiningDailySummary:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id){
        AppUserMiningDailySummary data = dailySummaryService.selectById(id);
        return AjaxResult.success(data);
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiningDailySummary:add')")
    @Log(title = "APP用户每日收益汇总", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppUserMiningDailySummary entity){
        return toAjax(dailySummaryService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiningDailySummary:edit')")
    @Log(title = "APP用户每日收益汇总", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AppUserMiningDailySummary entity){
        return toAjax(dailySummaryService.update(entity));
    }

    /** 删除记录 */
    @PreAuthorize("@ss.hasPermi('admin:appUserMinerIncome:remove')")
    @Log(title = "APP用户每日收益", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(dailySummaryService.deleteByIds(ids));
    }
}
