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
 */
@RestController
@RequestMapping("/admin/app/user")
@Api(tags = "用户管理--APP用户")
public class AppUserController extends BaseController {
    @Autowired
    private IAppUserService appUserService;

    @PreAuthorize("@ss.hasPermi('app:appUser:list')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "keyword", required = false) String keyword,
                              @RequestParam(value = "phone", required = false) String phone){
        AppUser query = new AppUser();
        query.setPhone(phone);
        if (StringUtils.isNotEmpty(keyword)) {
            query.getParams().put("keyword", keyword);
            query.setName(keyword);
        }
        startPage();
        List<AppUser> list = appUserService.selectList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('app:appUser:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id){
        return AjaxResult.success(appUserService.selectById(id));
    }

    @PreAuthorize("@ss.hasPermi('app:appUser:add')")
    @Log(title = "APP用户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AppUser entity){
        if (StringUtils.isEmpty(entity.getName()) || StringUtils.isEmpty(entity.getPhone())) {
            return AjaxResult.error("用户名称与手机号不能为空");
        }
        if (appUserService.selectByPhone(entity.getPhone()) != null) {
            return AjaxResult.error("手机号已存在");
        }
        if (StringUtils.isEmpty(entity.getPassword())) {
            String defaultPwd = IdUtils.fastSimpleUUID().substring(0, 8);
            entity.setPassword(SecurityUtils.encryptPassword(defaultPwd));
        }
        return toAjax(appUserService.insert(entity));
    }

    @PreAuthorize("@ss.hasPermi('app:appUser:edit')")
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

    @PreAuthorize("@ss.hasPermi('app:appUser:remove')")
    @Log(title = "APP用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids){
        return toAjax(appUserService.deleteByIds(ids));
    }

    @PreAuthorize("@ss.hasPermi('app:appUser:resetPwd')")
    @Log(title = "APP用户密码重置", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd/{id}")
    public AjaxResult resetPwd(@PathVariable Long id){
        AppUser user = appUserService.selectById(id);
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }
        String defaultPwd = IdUtils.fastSimpleUUID().substring(0, 8);
        user.setPassword(SecurityUtils.encryptPassword(defaultPwd));
        return toAjax(appUserService.update(user));
    }
}
