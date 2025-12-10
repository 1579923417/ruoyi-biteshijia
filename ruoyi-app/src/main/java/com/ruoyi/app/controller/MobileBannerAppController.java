package com.ruoyi.app.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.vo.MobileBannerVo;
import com.ruoyi.system.service.IMobileBannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * APP--轮播图 前端控制器
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@RestController
@RequestMapping("/app/banner")
@Api(tags = "APP--轮播图")
@Anonymous
public class MobileBannerAppController {
    @Autowired
    private IMobileBannerService mobileBannerService;

    @GetMapping("/list")
    @ApiOperation(value = "app首页轮播列表")
    public AjaxResult list() {
        List<MobileBannerVo> result = mobileBannerService.selectVisibleList();
        return AjaxResult.success(result);
    }
}

