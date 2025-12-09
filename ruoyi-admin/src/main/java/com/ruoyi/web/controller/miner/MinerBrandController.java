package com.ruoyi.web.controller.miner;

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
import com.ruoyi.system.domain.MinerBrand;
import com.ruoyi.system.service.IMinerBrandService;

import io.swagger.annotations.Api;
/**
 * 矿机管理--矿机品牌 前端控制器
 */
@RestController
@RequestMapping("/admin/miner/brand")
@Api(tags = "矿机管理--矿机品牌")
public class MinerBrandController extends BaseController {
    @Autowired
    private IMinerBrandService minerBrandService;

    @PreAuthorize("@ss.hasPermi('miner:minerBrand:list')")
    @GetMapping("/list")
    public TableDataInfo list(MinerBrand query){
        startPage();
        List<MinerBrand> list = minerBrandService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('miner:minerBrand:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(minerBrandService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('miner:minerBrand:add')")
    @Log(title = "矿机品牌", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MinerBrand entity){
        return toAjax(minerBrandService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('miner:minerBrand:edit')")
    @Log(title = "矿机品牌", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MinerBrand entity){
        return toAjax(minerBrandService.update(entity));
    }

    @PreAuthorize("@ss.hasPermi('miner:minerBrand:remove')")
    @Log(title = "矿机品牌", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(minerBrandService.deleteByIds(ids));
    }
}
