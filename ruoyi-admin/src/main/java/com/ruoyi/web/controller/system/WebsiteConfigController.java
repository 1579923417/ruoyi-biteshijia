package com.ruoyi.web.controller.system;

import java.util.*;
import java.util.stream.Collectors;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.WebsiteConfig;
import com.ruoyi.system.enums.ConfigGroupEnum;
import com.ruoyi.system.service.IWebsiteConfigService;

/**
 * 系统设置--动态配置 前端控制器
 */
@RestController
@RequestMapping("/admin/system/websiteConfig")
@Api(tags = "系统设置--动态配置")
public class WebsiteConfigController extends BaseController {

    @Autowired
    private IWebsiteConfigService websiteConfigService;

    /**
     * 分页列表
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(WebsiteConfig query) {
        startPage();
        List<WebsiteConfig> list = websiteConfigService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询详情
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id) {
        return AjaxResult.success(websiteConfigService.selectById(id));
    }

    /**
     * 新增配置（自动拼接前缀 group.keySuffix）
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:add')")
    @Log(title = "网站配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WebsiteConfig entity) {
        if (StringUtils.isEmpty(entity.getConfigGroup()) || StringUtils.isEmpty(entity.getConfigKey())) {
            return AjaxResult.error("分组与key不能为空");
        }
        String key = entity.getConfigKey();
        if (!key.contains(".")) {
            entity.setConfigKey(entity.getConfigGroup() + "." + key);
        }
        if (!websiteConfigService.checkKeyUnique(entity.getConfigKey())) {
            return AjaxResult.error("配置键已存在");
        }
        return toAjax(websiteConfigService.insert(entity));
    }

    /**
     * 修改配置（如传入未带前缀的key则自动拼接）
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:edit')")
    @Log(title = "网站配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WebsiteConfig entity) {
        if (entity.getId() == null) {
            return AjaxResult.error("ID不能为空");
        }
        if (StringUtils.isNotEmpty(entity.getConfigGroup()) && StringUtils.isNotEmpty(entity.getConfigKey()) && !entity.getConfigKey().contains(".")) {
            entity.setConfigKey(entity.getConfigGroup() + "." + entity.getConfigKey());
        }
        return toAjax(websiteConfigService.update(entity));
    }

    /**
     * 批量删除
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:remove')")
    @Log(title = "网站配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(websiteConfigService.deleteByIds(ids));
    }

    /**
     * 返回分组枚举给前端下拉框
     */
    @GetMapping("/groups")
    public AjaxResult listGroups() {
        List<Map<String, String>> groups = Arrays.stream(ConfigGroupEnum.values())
                .map(e -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("value", e.value());
                    m.put("label", e.label());
                    return m;
                })
                .collect(Collectors.toList());
        return AjaxResult.success(groups);
    }
}

