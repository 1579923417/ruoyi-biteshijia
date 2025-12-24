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

/**
 * 全球矿场信息管理 前端控制器
 *
 * @author Jamie
 */
@RestController
@RequestMapping("/admin/global/miner")
@Api(tags = "全球矿场信息")
public class MinerFarmController extends BaseController {

    @Autowired
    private IMinerFarmService minerFarmService;

    /**
     * 查询全球矿场信息列表
     *
     * 支持根据矿场名称、国家、地区等条件进行查询，
     * 并通过 {@link BaseController#startPage()} 实现分页返回。
     *
     * @param query 查询条件封装对象
     * @return 分页后的矿场信息列表
     */
    @PreAuthorize("@ss.hasPermi('admin:minerFarm:list')")
    @GetMapping("/list")
    public TableDataInfo list(MinerFarm query){
        startPage();
        List<MinerFarm> list = minerFarmService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询单个矿场信息详情
     *
     * 根据矿场ID获取对应的详细信息，
     * 用于后台详情查看或编辑前的数据回显。
     *
     * @param id 矿场ID
     * @return 矿场信息详情
     */
    @PreAuthorize("@ss.hasPermi('admin:minerFarm:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(minerFarmService.selectById(id));
    }

    /**
     * 新增全球矿场信息
     *
     * @param entity 矿场信息实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:minerFarm:add')")
    @Log(title = "全球矿场信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MinerFarm entity){
        return toAjax(minerFarmService.insert(entity));
    }

    /**
     * 修改全球矿场信息
     *
     * @param entity 矿场信息实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:minerFarm:edit')")
    @Log(title = "全球矿场信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MinerFarm entity){
        return toAjax(minerFarmService.update(entity));
    }

    /**
     * 删除全球矿场信息（支持批量删除）
     *
     * @param ids 矿场ID数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:minerFarm:remove')")
    @Log(title = "全球矿场信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(minerFarmService.deleteByIds(ids));
    }
}

