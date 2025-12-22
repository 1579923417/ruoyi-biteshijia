package com.ruoyi.web.controller.app;

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
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.AppUserMiner;
import com.ruoyi.system.domain.vo.AppUserMinerAddReq;
import com.ruoyi.system.service.F2PoolService;
import com.ruoyi.system.service.IAppUserMinerService;
import com.ruoyi.system.service.IAppUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.alibaba.fastjson2.JSONObject;

/**
 * 用户管理--APP用户矿机 前端控制器
 */
@RestController
@RequestMapping("/admin/app/user/miner")
@Api(tags = "用户管理--APP用户矿机")
@Anonymous
public class AppUserMinerController extends BaseController {
    @Autowired
    private IAppUserMinerService appUserMinerService;

    @Autowired
    private IAppUserService appUserService;
    /**
     * 查询 APP 用户矿机列表（分页）
     *
     * @param query 查询条件（可包含用户ID、矿机品牌、状态等）
     * @return 分页后的矿机列表数据
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:list')")
    @GetMapping("/list")
    public TableDataInfo list(AppUserMiner query){
        startPage();
        List<AppUserMiner> list = appUserMinerService.selectList(query);
        return getDataTable(list);
    }
    /**
     * 查询 APP 用户矿机详情
     *
     * @param id 矿机ID
     * @return 矿机详细信息
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(appUserMinerService.selectById(id));
    }
    /**
     * 新增 APP 用户矿机
     *
     * 业务逻辑：
     * 1. 校验请求参数完整性
     * 2. 校验 APP 用户是否存在
     * 3. 校验用户是否已配置 F2Pool Token
     * 4. 调用 F2Pool 接口创建矿机并保存本地数据
     *
     * @param req 新增矿机请求参数
     * @return 创建成功后的矿机信息
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:add')")
    @Log(title = "APP用户矿机", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppUserMinerAddReq req){
        // 基础参数校验
        if (req == null || req.getUserId() == null) {
            return AjaxResult.error("缺少用户ID");
        }
        if (req.getBrandId() == null) {
            return AjaxResult.error("缺少矿机品牌");
        }
        if (StringUtils.isEmpty(req.getMiningUserName())) {
            return AjaxResult.error("矿机名称不能为空");
        }
        // 校验 APP 用户是否存在
        AppUser user = appUserService.selectById(req.getUserId());
        if (user == null) {
            return AjaxResult.error("APP用户不存在");
        }
        // 校验 F2Pool Token
        if (StringUtils.isEmpty(user.getF2poolToken())) {
            return AjaxResult.error("F2Pool Token为空");
        }
        // 创建矿机并同步 F2Pool 数据
        AppUserMiner entity = appUserMinerService.createWithF2pool(
                req.getUserId(),
                req.getBrandId(),
                req.getMiningUserName(),
                req.getApiCode(),
                req.getManagementFeeRate()
        );
        return AjaxResult.success(entity);
    }

    /**
     * 修改 APP 用户矿机信息
     *
     * @param entity 矿机实体（需包含ID）
     * @return 修改结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:edit')")
    @Log(title = "APP用户矿机", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AppUserMiner entity){
        return toAjax(appUserMinerService.update(entity));
    }
    /**
     * 删除 APP 用户矿机（支持批量）
     *
     * @param ids 矿机ID数组
     * @return 删除结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:remove')")
    @Log(title = "APP用户矿机", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(appUserMinerService.deleteByIds(ids));
    }
    /**
     * 修改 APP 用户矿机状态
     *
     * 状态说明：
     * - 1：启用
     * - 0：停用
     *
     * @param id     矿机ID
     * @param status 状态值
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUserMiner:edit')")
    @Log(title = "APP用户矿机状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status){
        return toAjax(appUserMinerService.updateStatus(id, status));
    }

}
