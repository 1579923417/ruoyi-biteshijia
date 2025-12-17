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

/**
 * 页面设置--APP菜单管理 前端控制器
 *
 * @author Jamie
 */
@RestController
@RequestMapping("/admin/app/menu")
@Api(tags = "页面设置--APP菜单管理")
public class AppMenuController extends BaseController {
    @Autowired
    private IAppMenuService appMenuService;

    /**
     * 查询 APP 菜单分页列表
     *
     * @param query 查询条件（支持菜单名称、状态、父级ID等）
     * @return 分页后的菜单列表数据
     */
    @PreAuthorize("@ss.hasPermi('admin:appMenu:list')")
    @GetMapping("/list")
    public TableDataInfo list(AppMenu query){
        startPage();
        List<AppMenu> list = appMenuService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询 APP 根菜单列表
     *
     * <p>
     * 默认只返回 parentId = 0 的菜单，用于构建菜单树或父级菜单选择
     * </p>
     *
     * @param query 查询条件
     * @return 根菜单分页列表
     */
    @PreAuthorize("@ss.hasPermi('admin:appMenu:list')")
    @GetMapping("/roots")
    public TableDataInfo roots(AppMenu query){
        query.setParentId(0L);
        startPage();
        List<AppMenu> list = appMenuService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 根据菜单 ID 查询菜单详情
     *
     * @param id 菜单主键ID
     * @return 菜单详细信息
     */
    @PreAuthorize("@ss.hasPermi('admin:appMenu:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(appMenuService.selectById(id));
    }

    /**
     * 新增 APP 菜单
     *
     * @param entity 菜单实体对象
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appMenu:add')")
    @Log(title = "APP菜单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppMenu entity){
        return toAjax(appMenuService.insert(entity));
    }

    /**
     * 编辑 APP 菜单
     *
     * @param entity 菜单实体对象（需包含ID）
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appMenu:edit')")
    @Log(title = "APP菜单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AppMenu entity){
        return toAjax(appMenuService.update(entity));
    }

    /**
     * 删除 APP 菜单（支持批量）
     *
     * @param ids 菜单ID数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appMenu:remove')")
    @Log(title = "APP菜单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(appMenuService.deleteByIds(ids));
    }

    /**
     * 修改 APP 菜单状态
     *
     * <p>
     * 用于启用或禁用菜单，不影响菜单结构，仅控制前端是否展示
     * </p>
     *
     * @param id 菜单ID
     * @param status 状态值（0：禁用，1：启用）
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appMenu:edit')")
    @Log(title = "APP菜单状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status){
        return toAjax(appMenuService.updateStatus(id, status));
    }

    /**
     * 获取 APP 菜单类型枚举列表
     *
     * <p>
     * 返回菜单类型的 code / desc，用于前端下拉选择
     * </p>
     *
     * @return 菜单类型枚举集合
     */
    @PreAuthorize("@ss.hasPermi('admin:appMenu:list')")
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
