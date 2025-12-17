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
     * <p>
     * 支持根据矿场名称、国家、地区等条件进行查询，
     * 并通过 {@link BaseController#startPage()} 实现分页返回。
     * </p>
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
     * <p>
     * 根据矿场ID获取对应的详细信息，
     * 用于后台详情查看或编辑前的数据回显。
     * </p>
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
     * <p>
     * 用于后台录入新的矿场数据，
     * 通常包含矿场所在地区、基础规模、备注说明等信息。
     * </p>
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
     * <p>
     * 对已存在的矿场数据进行更新维护，
     * 如矿场规模调整、状态变更或备注信息修改等。
     * </p>
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
     * <p>
     * 建议仅在矿场数据录入错误或确定不再使用时执行删除，
     * 避免影响历史算力统计或收益分析结果。
     * </p>
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

