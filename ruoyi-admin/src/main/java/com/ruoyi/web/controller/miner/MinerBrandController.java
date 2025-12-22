package com.ruoyi.web.controller.miner;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 矿机品牌 业务层接口
     */
    @Autowired
    private IMinerBrandService minerBrandService;
    /**
     * 查询矿机品牌列表（分页）
     *
     * 权限：
     * - miner:minerBrand:list
     *
     * 说明：
     * - 支持条件查询（通过 MinerBrand 对象接收参数）
     * - 使用 BaseController 的分页机制
     *
     * @param query 查询条件
     * @return 分页后的矿机品牌数据
     */
    @PreAuthorize("@ss.hasPermi('miner:minerBrand:list')")
    @GetMapping("/list")
    public TableDataInfo list(MinerBrand query){
        startPage();
        List<MinerBrand> list = minerBrandService.selectList(query);
        return getDataTable(list);
    }
    /**
     * 根据 ID 查询矿机品牌详情
     *
     * 权限：
     * - miner:minerBrand:query
     *
     * @param id 矿机品牌主键 ID
     * @return 矿机品牌详细信息
     */
    @PreAuthorize("@ss.hasPermi('miner:minerBrand:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(minerBrandService.selectById(id));
    }
    /**
     * 新增矿机品牌
     *
     * 权限：
     * - miner:minerBrand:add
     *
     * 日志：
     * - 记录新增操作日志
     *
     * @param entity 矿机品牌实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('miner:minerBrand:add')")
    @Log(title = "矿机品牌", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MinerBrand entity){
        return toAjax(minerBrandService.insert(entity));
    }
    /**
     * 修改矿机品牌
     *
     * 权限：
     * - miner:minerBrand:edit
     *
     * 日志：
     * - 记录修改操作日志
     *
     * @param entity 矿机品牌实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('miner:minerBrand:edit')")
    @Log(title = "矿机品牌", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MinerBrand entity){
        return toAjax(minerBrandService.update(entity));
    }
    /**
     * 删除矿机品牌（支持批量）
     *
     * 权限：
     * - miner:minerBrand:remove
     *
     * 日志：
     * - 记录删除操作日志
     *
     * 说明：
     * - 支持通过 ID 数组批量删除
     *
     * @param ids 矿机品牌主键 ID 集合
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('miner:minerBrand:remove')")
    @Log(title = "矿机品牌", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(minerBrandService.deleteByIds(ids));
    }
}
