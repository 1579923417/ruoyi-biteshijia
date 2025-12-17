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
     * <p>
     * 支持根据轮播图标题、状态等条件进行查询，
     * 并通过 {@link BaseController#startPage()} 实现分页返回。
     * </p>
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
     * <p>
     * 用于后台查看或编辑轮播图前的数据回显。
     * </p>
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
     * <p>
     * 用于后台新增一条轮播图配置，
     * 通常包含图片地址、跳转链接、展示顺序等信息。
     * </p>
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
     * <p>
     * 对已有轮播图进行维护更新，
     * 包括图片替换、跳转地址修改、排序调整等。
     * </p>
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
     * <p>
     * 通常用于清理无效、过期或录入错误的轮播图数据。
     * </p>
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
     * <p>
     * 用于快速启用或禁用轮播图，
     * 不删除数据，仅控制是否在 APP 首页展示。
     * </p>
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
