package com.ruoyi.web.controller.f2pool;

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
import com.ruoyi.system.domain.F2poolAccount;
import com.ruoyi.system.service.F2poolAccountService;
import io.swagger.annotations.Api;

/**
 * 用户管理--f2pool用户管理 前端控制器
 */
@RestController
@RequestMapping("/admin/f2pool/account")
@Api(tags = "f2pool--用户管理")
public class F2poolAccountController extends BaseController {
    @Autowired
    private F2poolAccountService f2poolAccountService;

    @PreAuthorize("@ss.hasPermi('f2pool:f2poolAccount:list')")
    @GetMapping("/list")
    public TableDataInfo list(F2poolAccount query){
        startPage();
        List<F2poolAccount> list = f2poolAccountService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('f2pool:f2poolAccount:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Integer id){
        return AjaxResult.success(f2poolAccountService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('f2pool:f2poolAccount:add')")
    @Log(title = "F2Pool账号", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody F2poolAccount entity){
        return toAjax(f2poolAccountService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('f2pool:f2poolAccount:edit')")
    @Log(title = "F2Pool账号", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody F2poolAccount entity){
        return toAjax(f2poolAccountService.update(entity));
    }

    @PreAuthorize("@ss.hasPermi('f2pool:f2poolAccount:remove')")
    @Log(title = "F2Pool账号", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids){
        return toAjax(f2poolAccountService.deleteByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('f2pool:f2poolAccount:edit')")
    @Log(title = "F2Pool账号状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestParam("id") Integer id, @RequestParam("status") Integer status){
        F2poolAccount entity = new F2poolAccount();
        entity.setId(id);
        entity.setStatus(status);
        return toAjax(f2poolAccountService.update(entity));
    }
}
