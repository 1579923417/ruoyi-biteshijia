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

/**
 * 全球电力信息管理 前端控制器
 *
 * @author Jamie
 */
@RestController
@RequestMapping("/admin/global/power")
@Api(tags = "全球电力信息")
public class GlobalPowerInfoController extends BaseController {

    @Autowired
    private IGlobalPowerInfoService globalPowerInfoService;

    /**
     * 查询全球电力信息列表
     *
     * 支持根据实体字段进行条件查询（如国家、地区等），
     * 并通过 {@link BaseController#startPage()} 实现分页查询。
     *
     * @param query 查询条件封装对象
     * @return 分页后的全球电力信息列表
     */
    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(GlobalPowerInfo query){
        startPage();
        List<GlobalPowerInfo> list = globalPowerInfoService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询单条全球电力信息详情
     *
     * @param id 电力信息ID
     * @return 电力信息详情
     */
    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(globalPowerInfoService.selectById(id));
    }

    /**
     * 新增全球电力信息
     *
     * @param entity 全球电力信息实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:add')")
    @Log(title = "全球电力信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody GlobalPowerInfo entity){
        return toAjax(globalPowerInfoService.insert(entity));
    }

    /**
     * 修改全球电力信息
     *
     * @param entity 全球电力信息实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:edit')")
    @Log(title = "全球电力信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody GlobalPowerInfo entity){
        return toAjax(globalPowerInfoService.update(entity));
    }

    /**
     * 删除全球电力信息（支持批量）
     *
     * @param ids 电力信息ID数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:globalPowerInfo:remove')")
    @Log(title = "全球电力信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(globalPowerInfoService.deleteByIds(ids));
    }
}

