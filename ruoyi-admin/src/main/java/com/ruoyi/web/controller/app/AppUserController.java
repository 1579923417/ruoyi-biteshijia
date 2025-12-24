package com.ruoyi.web.controller.app;

import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.service.IAppUserService;
/**
 * 用户管理--APP用户 前端控制器
 *
 * @author Jamie
 */
@RestController
@RequestMapping("/admin/app/user")
@Api(tags = "用户管理--APP用户")
public class AppUserController extends BaseController {
    @Autowired
    private IAppUserService appUserService;

    /**
     * 用户列表查询
     *
     * 支持：
     *   手机号精确查询
     *   关键字模糊查询（用户名 / 手机号等）
     *
     * @param keyword 关键字（可选）
     * @param phone   手机号（可选）
     * @return 分页后的用户列表
     */
    @PreAuthorize("@ss.hasPermi('admin:appUser:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "phone", required = false) String phone){
        AppUser query = new AppUser();
        query.setPhone(phone);
        // 关键字搜索（支持多字段）
        if (StringUtils.isNotEmpty(keyword)) {
            query.getParams().put("keyword", keyword);
            query.setName(keyword);
        }
        startPage();
        List<AppUser> list = appUserService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询用户详情
     *
     * @param id 用户ID
     * @return 用户详情信息
     */
    @PreAuthorize("@ss.hasPermi('admin:appUser:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(appUserService.selectById(id));
    }

    /**
     * 新增 APP 用户
     *
     * 校验规则：
     *   用户名、手机号不能为空
     *   手机号必须唯一
     * 
     * 如果未传入密码，则默认设置为：123456
     * （并进行加密存储）
     *
     * @param entity 用户实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUser:add')")
    @Log(title = "APP用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppUser entity){
        if (StringUtils.isEmpty(entity.getName()) || StringUtils.isEmpty(entity.getPhone())) {
            return AjaxResult.error("用户名称与手机号不能为空");
        }
        // 校验手机号唯一性
        if (appUserService.selectByPhone(entity.getPhone()) != null) {
            return AjaxResult.error("手机号已存在");
        }
        if (StringUtils.isEmpty(entity.getPassword())) {
        // 设置固定默认密码为 "1234567"
        entity.setPassword(SecurityUtils.encryptPassword("123456"));
        }
        return toAjax(appUserService.insert(entity));
    }

    /**
     * 编辑 APP 用户信息
     *
     * 校验：
     *   ID 必须存在
     *   手机号不可被其他用户占用
     *
     * @param entity 用户实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUser:edit')")
    @Log(title = "APP用户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AppUser entity){
        if (entity.getId() == null) {
            return AjaxResult.error("缺少ID");
        }
        AppUser existed = appUserService.selectByPhone(entity.getPhone());
        if (existed != null && !existed.getId().equals(entity.getId())) {
            return AjaxResult.error("手机号已存在");
        }
        return toAjax(appUserService.update(entity));
    }

    /**
     * 删除 APP 用户（支持批量）
     *
     * @param ids 用户ID数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUser:remove')")
    @Log(title = "APP用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(appUserService.deleteByIds(ids));
    }

    /**
     * 重置 APP 用户登录密码
     *
     * 默认重置为：123456（加密存储）
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('admin:appUser:resetPwd')")
    @Log(title = "APP用户密码重置", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd/{id}")
    public AjaxResult resetPwd(@PathVariable Long id){
        AppUser user = appUserService.selectById(id);
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }
        user.setPassword(SecurityUtils.encryptPassword("123456"));
        return toAjax(appUserService.update(user));
    }
}
