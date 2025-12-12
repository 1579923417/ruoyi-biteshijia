package com.ruoyi.web.controller.app;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.AppUserMinerIncome;
import com.ruoyi.system.service.IAppUserMinerIncomeService;
import io.swagger.annotations.Api;

/**
 * 用户管理--APP用户每日收益 前端控制器
 */
@RestController
@RequestMapping("/admin/app/user/miner/income")
@Api(tags = "用户管理--APP用户每日收益")
public class AppUserMinerIncomeController extends BaseController {
    @Autowired
    private IAppUserMinerIncomeService appUserMinerIncomeService;

    /** 列表查询 */
    @PreAuthorize("@ss.hasPermi('admin:appUserMinerIncome:list')")
    @GetMapping("/list")
    public TableDataInfo list(AppUserMinerIncome query){
        startPage();
        List<AppUserMinerIncome> list = appUserMinerIncomeService.selectList(query);
        return getDataTable(list);
    }

    /** 详情查询 */
    @PreAuthorize("@ss.hasPermi('admin:appUserMinerIncome:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id){
        AppUserMinerIncome data = appUserMinerIncomeService.selectById(id);
        return AjaxResult.success(data);
    }

    /** 新增记录 */
    @PreAuthorize("@ss.hasPermi('admin:appUserMinerIncome:add')")
    @Log(title = "APP用户每日收益", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppUserMinerIncome entity){
        return toAjax(appUserMinerIncomeService.insert(entity));
    }

    /** 编辑记录 */
    @PreAuthorize("@ss.hasPermi('admin:appUserMinerIncome:edit')")
    @Log(title = "APP用户每日收益", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AppUserMinerIncome entity){
        return toAjax(appUserMinerIncomeService.update(entity));
    }

    /** 删除记录 */
    @PreAuthorize("@ss.hasPermi('admin:appUserMinerIncome:remove')")
    @Log(title = "APP用户每日收益", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(appUserMinerIncomeService.deleteByIds(ids));
    }
}
