package com.ruoyi.app.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.vo.SysNoticeAppVo;
import com.ruoyi.system.domain.vo.SysNoticeSimpleVo;
import com.ruoyi.system.service.ISysNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/notice")
@Api(tags = "APP--通知")
@Anonymous
public class AppNoticeAppController {
    @Autowired
    private ISysNoticeService noticeService;

    /**
     * app通知列表
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("app通知列表")
    public AjaxResult list() {
        List<SysNoticeSimpleVo> list = noticeService.selectAppSimpleList();
        return AjaxResult.success(list);
    }

    /**
     * app通知详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("app通知详情")
    public AjaxResult detail(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        SysNoticeAppVo vo = noticeService.selectAppNoticeDetail(id);
        return AjaxResult.success(vo);
    }
}
