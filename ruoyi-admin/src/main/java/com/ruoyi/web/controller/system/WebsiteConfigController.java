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
 *
 * @author Jamie
 */
@RestController
@RequestMapping("/admin/system/websiteConfig")
@Api(tags = "系统设置--动态配置")
public class WebsiteConfigController extends BaseController {

    @Autowired
    private IWebsiteConfigService websiteConfigService;

    /**
     * 查询动态配置分页列表
     *
     * <p>
     * 支持根据配置分组、配置键、状态等条件进行查询，
     * 并通过 {@link BaseController#startPage()} 实现分页返回。
     * </p>
     *
     * @param query 查询条件封装对象
     * @return 分页后的配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(WebsiteConfig query) {
        startPage();
        List<WebsiteConfig> list = websiteConfigService.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询单个配置详情
     *
     * <p>
     * 用于后台查看或编辑配置前的数据回显。
     * </p>
     *
     * @param id 配置ID
     * @return 配置详情
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:query')")
    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable("id") Long id) {
        return AjaxResult.success(websiteConfigService.selectById(id));
    }

    /**
     * 新增动态配置
     *
     * <p>
     * 新增配置时：
     * <ul>
     *   <li>必须指定配置分组（configGroup）</li>
     *   <li>配置键统一采用 <code>group.key</code> 命名规范</li>
     *   <li>若前端仅传 key 后缀，将自动拼接分组前缀</li>
     * </ul>
     * </p>
     *
     * @param entity 动态配置实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:add')")
    @Log(title = "网站配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody WebsiteConfig entity) {
        if (StringUtils.isEmpty(entity.getConfigGroup()) || StringUtils.isEmpty(entity.getConfigKey())) {
            return AjaxResult.error("分组与key不能为空");
        }
        // 自动拼接配置键前缀：group.key
        String key = entity.getConfigKey();
        if (!key.contains(".")) {
            entity.setConfigKey(entity.getConfigGroup() + "." + key);
        }
        // 校验配置键唯一性
        if (!websiteConfigService.checkKeyUnique(entity.getConfigKey())) {
            return AjaxResult.error("配置键已存在");
        }
        return toAjax(websiteConfigService.insert(entity));
    }

    /**
     * 修改动态配置
     *
     * <p>
     * 支持修改配置值、状态、描述等信息，
     * 若传入的 key 未包含分组前缀，则自动补全。
     * </p>
     *
     * @param entity 动态配置实体
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:edit')")
    @Log(title = "网站配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody WebsiteConfig entity) {
        if (entity.getId() == null) {
            return AjaxResult.error("ID不能为空");
        }
        // 自动补全配置键前缀
        if (StringUtils.isNotEmpty(entity.getConfigGroup())
                && StringUtils.isNotEmpty(entity.getConfigKey())
                && !entity.getConfigKey().contains(".")) {
            entity.setConfigKey(entity.getConfigGroup() + "." + entity.getConfigKey());
        }
        return toAjax(websiteConfigService.update(entity));
    }

    /**
     * 删除动态配置（支持批量）
     *
     * <p>
     * 删除配置后将立即失效，
     * 请谨慎操作，避免影响系统运行。
     * </p>
     *
     * @param ids 配置ID数组
     * @return 操作结果
     */
    @PreAuthorize("@ss.hasPermi('system:websiteConfig:remove')")
    @Log(title = "网站配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(websiteConfigService.deleteByIds(ids));
    }

    /**
     * 获取配置分组枚举列表
     *
     * <p>
     * 提供给前端下拉框使用，
     * 用于新增或编辑配置时选择所属分组。
     * </p>
     *
     * @return 分组枚举列表（value / label）
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

