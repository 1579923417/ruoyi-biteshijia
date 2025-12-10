package com.ruoyi.app.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.system.service.IAppMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * APP--菜单 前端控制器
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@RestController
@RequestMapping("/app/menu")
@Api(tags = "APP--菜单")
@Anonymous
public class AppMenuAppController extends BaseController {

    @Autowired
    private IAppMenuService appMenuService;

    @GetMapping("/list")
    @ApiOperation("app菜单分组列表")
    public AjaxResult list(){
        List<com.ruoyi.system.domain.vo.AppMenuGroupVo> groups = appMenuService.selectVisibleGrouped();
        return AjaxResult.success(groups);
    }
}
