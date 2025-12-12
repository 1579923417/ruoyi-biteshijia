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
import com.ruoyi.system.domain.MinerFarm;
import com.ruoyi.system.service.IMinerFarmService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/admin/global/miner")
@Api(tags = "全球矿场信息")
public class MinerFarmController extends BaseController {
    @Autowired
    private IMinerFarmService minerFarmService;

    @PreAuthorize("@ss.hasPermi('admin:minerFarm:list')")
    @GetMapping("/list")
    public TableDataInfo list(MinerFarm query){
        startPage();
        List<MinerFarm> list = minerFarmService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('admin:minerFarm:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(minerFarmService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('admin:minerFarm:add')")
    @Log(title = "全球矿场信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MinerFarm entity){
        return toAjax(minerFarmService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('admin:minerFarm:edit')")
    @Log(title = "全球矿场信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MinerFarm entity){
        return toAjax(minerFarmService.update(entity));
    }

    @PreAuthorize("@ss.hasPermi('admin:minerFarm:remove')")
    @Log(title = "全球矿场信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(minerFarmService.deleteByIds(ids));
    }
}

