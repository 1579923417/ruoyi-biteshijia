package com.ruoyi.web.controller.system;

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
import com.ruoyi.system.domain.MobileBanner;
import com.ruoyi.system.service.IMobileBannerService;

import io.swagger.annotations.Api;

/**
 * 页面设置--手机轮播图 前端控制器
 */
@RestController
@RequestMapping("/admin/system/banner")
@Api(tags = "页面设置--手机轮播图")
public class SysBannerController extends BaseController {
    @Autowired
    private IMobileBannerService mobileBannerService;

    @PreAuthorize("@ss.hasPermi('system:banner:list')")
    @GetMapping("/list")
    public TableDataInfo list(MobileBanner query){
        startPage();
        List<MobileBanner> list = mobileBannerService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('system:banner:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(mobileBannerService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('system:banner:add')")
    @Log(title = "轮播图", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MobileBanner entity){
        return toAjax(mobileBannerService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('system:banner:edit')")
    @Log(title = "轮播图", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MobileBanner entity){
        return toAjax(mobileBannerService.update(entity));
    }

    @PreAuthorize("@ss.hasPermi('system:banner:remove')")
    @Log(title = "轮播图", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(mobileBannerService.deleteByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('system:banner:edit')")
    @Log(title = "轮播图状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status){
        return toAjax(mobileBannerService.updateStatus(id, status));
    }
}
