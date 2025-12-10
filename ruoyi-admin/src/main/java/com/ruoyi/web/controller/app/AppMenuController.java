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
import com.ruoyi.system.domain.AppMenu;
import com.ruoyi.system.service.IAppMenuService;

import io.swagger.annotations.Api;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.ruoyi.system.enums.MenuType;

@RestController
@RequestMapping("/admin/app/menu")
@Api(tags = "页面设置--APP菜单管理")
public class AppMenuController extends BaseController {
    @Autowired
    private IAppMenuService appMenuService;

    @PreAuthorize("@ss.hasPermi('app:appMenu:list')")
    @GetMapping("/list")
    public TableDataInfo list(AppMenu query){
        startPage();
        List<AppMenu> list = appMenuService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('app:appMenu:list')")
    @GetMapping("/roots")
    public TableDataInfo roots(AppMenu query){
        query.setParentId(0L);
        startPage();
        List<AppMenu> list = appMenuService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('app:appMenu:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(appMenuService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('app:appMenu:add')")
    @Log(title = "APP菜单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppMenu entity){
        return toAjax(appMenuService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('app:appMenu:edit')")
    @Log(title = "APP菜单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AppMenu entity){
        return toAjax(appMenuService.update(entity));
    }

    @PreAuthorize("@ss.hasPermi('app:appMenu:remove')")
    @Log(title = "APP菜单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(appMenuService.deleteByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('app:appMenu:edit')")
    @Log(title = "APP菜单状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status){
        return toAjax(appMenuService.updateStatus(id, status));
    }

    @PreAuthorize("@ss.hasPermi('app:appMenu:list')")
    @GetMapping("/types")
    public AjaxResult types(){
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (MenuType t : MenuType.values()) {
            Map<String, Object> m = new HashMap<>();
            m.put("code", t.getCode());
            m.put("desc", t.getDesc());
            list.add(m);
        }
        return AjaxResult.success(list);
    }
}
