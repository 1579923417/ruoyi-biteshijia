package com.ruoyi.web.controller.app;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.AppUserMiner;
import com.ruoyi.system.service.IAppUserMinerService;

import io.swagger.annotations.Api;
/**
 * 用户管理--APP用户矿机 前端控制器
 */
@RestController
@RequestMapping("/admin/app/user/miner")
@Api(tags = "用户管理--APP用户矿机")
public class AppUserMinerController extends BaseController {
    @Autowired
    private IAppUserMinerService appUserMinerService;

    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:list')")
    @GetMapping("/list")
    public TableDataInfo list(AppUserMiner query){
        startPage();
        List<AppUserMiner> list = appUserMinerService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(appUserMinerService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:add')")
    @Log(title = "APP用户矿机", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppUserMiner entity){
        return toAjax(appUserMinerService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:edit')")
    @Log(title = "APP用户矿机", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AppUserMiner entity){
        return toAjax(appUserMinerService.update(entity));
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:remove')")
    @Log(title = "APP用户矿机", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(appUserMinerService.deleteByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:edit')")
    @Log(title = "APP用户矿机状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status){
        return toAjax(appUserMinerService.updateStatus(id, status));
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:edit')")
    @Log(title = "矿机绑定用户", businessType = BusinessType.UPDATE)
    @PutMapping("/bind")
    public AjaxResult bind(@RequestParam("id") Long id, @RequestParam("userId") Long userId){
        return toAjax(appUserMinerService.bindUser(id, userId));
    }

    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:edit')")
    @Log(title = "矿机解绑用户", businessType = BusinessType.UPDATE)
    @PutMapping("/unbind")
    public AjaxResult unbind(@RequestParam("id") Long id){
        return toAjax(appUserMinerService.unbindUser(id));
    }
}
