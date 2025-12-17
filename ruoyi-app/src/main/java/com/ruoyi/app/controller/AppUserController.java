package com.ruoyi.app.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.app.service.AppAuthService;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.service.IAppUserService;
import com.ruoyi.system.service.IAppF2poolService;
import com.ruoyi.system.domain.vo.F2poolOverviewVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * APP--用户中心 前端控制器
 *
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

    @Autowired
    private IAppF2poolService appF2poolService;

    /**
     * APP 用户登录（手机号 + 密码）
     *
     * <p>
     * 登录成功后返回登录凭证信息（token、有效期等），
     * 前端需在后续请求的 Authorization 头中携带该 token。
     * </p>
     *
     * @param body 请求体，包含 phone、password
     * @return AjaxResult 登录结果
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

    /**
     * 发送登录验证码
     *
     * <p>
     * 用于验证码登录场景，验证码发送至用户手机号。
     * </p>
     *
     * @param phone 手机号
     */
    @Anonymous
    @PostMapping("/sendCode")
    @ApiOperation("app发送登录验证码")
    public AjaxResult sendCode(@RequestParam("phone") String phone) {
        appAuthService.sendLoginCode(phone);
        return AjaxResult.success();
    }

    /**
     * 验证码登录
     *
     * <p>
     * 校验手机号与验证码后完成登录，
     * 登录成功返回 token 信息。
     * </p>
     *
     * @param phone 手机号
     * @param code  验证码
     * @return AjaxResult 登录结果
     */
    @Anonymous
    @PostMapping("/loginByCode")
    @ApiOperation("app验证码登录")
    public AjaxResult loginByCode(@RequestParam("phone") String phone,
                                  @RequestParam("code") String code) {
        AppLoginResultVo vo = appAuthService.loginByCode(phone, code);
        return AjaxResult.success(vo);
    }

//    /**
//     * APP 用户注册
//     *
//     * <p>
//     * 使用手机号和密码创建新用户账号。
//     * </p>
//     *
//     * @param phone    手机号
//     * @param password 密码
//     */
//    @Anonymous
//    @PostMapping("/register")
//    @ApiOperation("app用户注册")
//    public AjaxResult register(@RequestParam("phone") String phone,
//                               @RequestParam("password") String password){
//        int rows = appUserService.register(phone, password);
//        return rows > 0 ? AjaxResult.success() : AjaxResult.error("注册失败");
//    }

    /**
     * 获取当前登录用户信息
     *
     * @param request 请求对象，从请求头 Authorization 获取 token
     * @return AjaxResult 当前登录用户信息
     */
    @GetMapping("/profile")
    @ApiOperation("app我的页面信息")
    public AjaxResult profile(HttpServletRequest request) {
        AppUser user = appAuthService.getCurrentAppUser(request);
        if (user == null) {
            return AjaxResult.error("未登录或已过期");
        }
        AppUserProfileVo vo = appUserService.selectProfileByUserId(user.getId());
        F2poolOverviewVo overview = appF2poolService.getOverview(user.getId());
        if (overview != null) {
            vo.setYesterdayIncome(overview.getTotalYesterdayIncome());
            vo.setTodayIncome(overview.getTotalTodayIncome());
            vo.setTotalIncome(overview.getTotalIncome());
            if (overview.getMinerCount() != null) {
                vo.setMinerCount(overview.getMinerCount());
            }
        }
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
        AppUserEarningsAggregateVo vo = appF2poolService.getEarnings(user.getId());
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
        F2poolMinersVo vo = appF2poolService.getMiners(user.getId());
        return AjaxResult.success(vo);
    }

    /**
     * APP 用户退出登录
     *
     * <p>
     * 清除当前用户的登录态（token 失效）。
     * </p>
     *
     * @param request 请求对象
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
