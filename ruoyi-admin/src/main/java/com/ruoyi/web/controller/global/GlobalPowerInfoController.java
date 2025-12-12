package com.ruoyi.web.controller.global;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.GlobalPowerInfo;
import com.ruoyi.system.service.IGlobalPowerInfoService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/admin/global/power")
@Api(tags = "全球电力信息")
public class GlobalPowerInfoController extends BaseController {
    @Autowired
    private IGlobalPowerInfoService globalPowerInfoService;

    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(GlobalPowerInfo query){
        startPage();
        List<GlobalPowerInfo> list = globalPowerInfoService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(globalPowerInfoService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:add')")
    @Log(title = "全球电力信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GlobalPowerInfo entity){
        return toAjax(globalPowerInfoService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:edit')")
    @Log(title = "全球电力信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GlobalPowerInfo entity){
        return toAjax(globalPowerInfoService.update(entity));
    }

    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:remove')")
    @Log(title = "全球电力信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(globalPowerInfoService.deleteByIds(ids));
    }
}

