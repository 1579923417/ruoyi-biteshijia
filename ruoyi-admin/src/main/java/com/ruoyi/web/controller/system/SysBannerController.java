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
 * 页面设置 —— 手机轮播图管理 前端控制器
 *
 * @author Jamie
 */
@RestController
@RequestMapping("/admin/system/banner")
@Api(tags = "页面设置--手机轮播图")
public class SysBannerController extends BaseController {

    @Autowired
    private IMobileBannerService mobileBannerService;

    /**
     * 查询手机轮播图列表
     *
     * 支持根据轮播图标题、状态等条件进行查询，
     * 并通过 {@link BaseController#startPage()} 实现分页返回。
     *
     * @param query 查询条件封装对象
     * @return 分页后的轮播图列表
     */
    @PreAuthorize("@ss.hasPermi('system:banner:list')")
    @GetMapping("/list")
    public TableDataInfo list(MobileBanner query){
        startPage();
        List<MobileBanner> list = mobileBannerService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询单个轮播图详情
     *
     * @param id 轮播图ID
     * @return 轮播图详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:banner:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(mobileBannerService.selectById(id));
    }

    /**
     * 新增手机轮播图
     *
     * @param entity 轮播图实体对象
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:banner:add')")
    @Log(title = "轮播图", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MobileBanner entity){
        return toAjax(mobileBannerService.insert(entity));
    }

    /**
     * 修改手机轮播图
     *
     * @param entity 轮播图实体对象
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:banner:edit')")
    @Log(title = "轮播图", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MobileBanner entity){
        return toAjax(mobileBannerService.update(entity));
    }

    /**
     * 删除手机轮播图（支持批量删除）
     *
     * @param ids 轮播图ID数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:banner:remove')")
    @Log(title = "轮播图", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(mobileBannerService.deleteByIds(ids));
    }

    /**
     * 修改轮播图展示状态
     *
     * @param id 轮播图ID
     * @param status 展示状态（如：1=启用，0=禁用）
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:banner:edit')")
    @Log(title = "轮播图状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status){
        return toAjax(mobileBannerService.updateStatus(id, status));
    }
}
