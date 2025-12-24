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

/**
 * APP —— 通知中心 前端控制器
 *
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@RestController
@RequestMapping("/app/notice")
@Api(tags = "APP--通知")
@Anonymous
public class AppNoticeAppController {
    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 查询 APP 通知列表
     *
     * 接口说明：
     * - 返回用于 APP 首页 / 通知中心展示的通知列表
     * - 返回数据为简要信息（标题、发布时间等）
     * - 默认按发布时间倒序排列（具体排序规则由 Service 层控制）
     *
     * @return 通知简要信息列表
     */
    @GetMapping("/list")
    @ApiOperation("app通知列表")
    public AjaxResult list() {
        List<SysNoticeSimpleVo> list = noticeService.selectAppSimpleList();
        return AjaxResult.success(list);
    }

    /**
     * 查询 APP 通知详情
     *
     * 接口说明：
     * - 根据通知 ID 查询完整通知内容
     * - 用于 APP 通知详情页展示
     * - 若通知不存在，返回空数据或由 Service 层抛出业务异常
     *
     * @param id 通知主键 ID
     * @return 通知详情数据
     */
    @GetMapping("/{id}")
    @ApiOperation("app通知详情")
    public AjaxResult detail(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        SysNoticeAppVo vo = noticeService.selectAppNoticeDetail(id);
        return AjaxResult.success(vo);
    }
}
