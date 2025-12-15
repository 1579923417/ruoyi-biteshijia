package com.ruoyi.app.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.app.service.AppAuthService;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.service.IAppUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * APP--用户中心 前端控制器
 * @author Jamie
 * @date 2025/12/10 12:13
 */
@RestController
@RequestMapping("/app/user")
@Api(tags = "APP--用户中心")
public class AppUserController {
    @Autowired
    private IAppUserService appUserService;
    
    @Autowired
    private AppAuthService appAuthService;

    /**
     * APP 用户登录接口
     * 支持手机号+密码登录
     * 登录成功返回 token 和有效期
     *
     * @return 返回 token 信息或错误信息
     */
    @Anonymous
    @PostMapping("/login")
    @ApiOperation("app用户登录")
    public AjaxResult login(@RequestBody Map<String, String> body) {
        String phone = body == null ? null : body.get("phone");
        String password = body == null ? null : body.get("password");
        AppLoginResultVo vo = appAuthService.login(phone, password);
        return AjaxResult.success(vo);
    }

    @Anonymous
    @PostMapping("/sendCode")
    @ApiOperation("app发送登录验证码")
    public AjaxResult sendCode(@RequestParam("phone") String phone) {
        appAuthService.sendLoginCode(phone);
        return AjaxResult.success();
    }

    @Anonymous
    @PostMapping("/loginByCode")
    @ApiOperation("app验证码登录")
    public AjaxResult loginByCode(@RequestParam("phone") String phone,
                                  @RequestParam("code") String code) {
        AppLoginResultVo vo = appAuthService.loginByCode(phone, code);
        return AjaxResult.success(vo);
    }

    /**
     * APP 用户注册
     * 传入手机号与密码，完成账号创建
     */
    @Anonymous
    @PostMapping("/register")
    @ApiOperation("app用户注册")
    public AjaxResult register(@RequestParam("phone") String phone,
                               @RequestParam("password") String password){
        int rows = appUserService.register(phone, password);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("注册失败");
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request 请求对象，从请求头 Authorization 获取 token
     * @return AjaxResult 当前登录用户信息
     */
    @GetMapping("/profile")
    @ApiOperation("app当前用户信息")
    public AjaxResult profile(HttpServletRequest request) {
        AppUser user = appAuthService.getCurrentAppUser(request);
        if (user == null) {
            return AjaxResult.error("未登录或已过期");
        }
        AppUserProfileVo vo = appUserService.selectProfileByUserId(user.getId());
        return AjaxResult.success(vo);
    }

    /**
     * 修改个人中心资料
     * 可修改：用户名称、手机号码、开户行、账户号码
     * 
     * @param request 请求对象，从请求头 Authorization 获取 token
     */
    @PutMapping("/profile")
    @ApiOperation("app修改个人资料")
    public AjaxResult updateProfile(HttpServletRequest request,
                                    @RequestBody AppUser body) {
        AppUser user = appAuthService.getCurrentAppUser(request);
        if (user == null) {
            return AjaxResult.error("未登录或已过期");
        }
        int rows = appUserService.updateProfile(user.getId(), body.getName(), body.getPhone(), body.getBankName(), body.getBankAccount(), body.getAvatar());
        return rows > 0 ? AjaxResult.success() : AjaxResult.error("修改失败");
    }

    /**
     * 获取用户收益明细
     * 包括累计收益、昨日收益、今日预计收益、今日已挖等摘要信息
     * 同时返回收益列表（昨日结算项：结算时间、数量、金额）
     *
     * @param request 请求对象，从请求头 Authorization 获取 token
     * @return AjaxResult 收益摘要与收益列表
     */
    @GetMapping("/earnings")
    @ApiOperation("app收益信息")
    public AjaxResult earnings(HttpServletRequest request) {
        AppUser user = appAuthService.getCurrentAppUser(request);
        if (user == null) {
            return AjaxResult.error("未登录或已过期");
        }
        AppUserEarningsVo vo = appUserService.selectEarningsByUserId(user.getId());
        return AjaxResult.success(vo);
    }

    /**
     *
     * @param request
     * @param id
     * @return
     */
    @GetMapping("/earningDetail/{id}")
    @ApiOperation("app收益详情")
    public AjaxResult earningDetail(HttpServletRequest request,
                                    @PathVariable("id") Long id) {
        AppUser user = appAuthService.getCurrentAppUser(request);
        if (user == null) {
            return AjaxResult.error("未登录或已过期");
        }
        AppUserEarningItemDetailVo vo = appUserService.selectEarningDetail(user.getId(), id);
        return AjaxResult.success(vo);
    }

    /**
     * 获取当前用户的矿机列表
     * 1. 从请求中获取当前登录用户信息。
     * 2. 如果用户未登录或登录信息已过期，则返回错误信息。
     * 3. 如果用户已登录，则查询该用户的矿机列表并返回。
     *
     * @param request 请求对象，从请求头 Authorization 获取 token
     * @return AjaxResult 当前用户的矿机列表
     */
    @GetMapping("/miners")
    @ApiOperation("app我的矿机列表")
    public AjaxResult miners(HttpServletRequest request){
        AppUser user = appAuthService.getCurrentAppUser(request);
        if (user == null) {
            return AjaxResult.error("未登录或已过期");
        }
        AppUserMinerListVo vo = appUserService.selectMyMiners(user.getId());
        return AjaxResult.success(vo);
    }

    /**
     * APP 退出登录
     */
    @PostMapping("/logout")
    @ApiOperation("app退出登录")
    public AjaxResult logout(HttpServletRequest request) {
        AppUser user = appAuthService.getCurrentAppUser(request);
        if (user == null) {
            return AjaxResult.error("未登录或已过期");
        }
        appAuthService.logout(request);
        return AjaxResult.success("退出成功");
    }
}
