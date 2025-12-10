package com.ruoyi.app.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.service.IAppUserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

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
    private TokenService tokenService;

    @Autowired
    private RedisCache redisCache;

    private static final String APP_SMS_CODE_KEY = "app:sms_code:";

    public static class SmsSendBody {
        private String phone;
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class SmsLoginBody {
        private String phone;
        private String code;
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }

    public static class PwdLoginBody {
        private String phone;
        private String password;
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @Anonymous
    @PostMapping("/sms/send")
    public AjaxResult sendLoginSms(@RequestBody SmsSendBody body) {
        if (body == null || StringUtils.isEmpty(body.getPhone())) {
            return AjaxResult.error("手机号不能为空");
        }
        AppUser user = appUserService.selectByPhone(body.getPhone());
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        redisCache.setCacheObject(APP_SMS_CODE_KEY + body.getPhone(), code, 5, TimeUnit.MINUTES);
        return AjaxResult.success("验证码已发送");
    }

    @Anonymous
    @PostMapping("/login/sms")
    public AjaxResult loginBySms(@RequestBody SmsLoginBody body) {
        if (body == null || StringUtils.isEmpty(body.getPhone()) || StringUtils.isEmpty(body.getCode())) {
            return AjaxResult.error("手机号与验证码不能为空");
        }
        AppUser user = appUserService.selectByPhone(body.getPhone());
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }
        String cacheCode = redisCache.getCacheObject(APP_SMS_CODE_KEY + body.getPhone());
        if (StringUtils.isEmpty(cacheCode)) {
            return AjaxResult.error("验证码已过期或未发送");
        }
        if (!cacheCode.equals(body.getCode())) {
            return AjaxResult.error("验证码错误");
        }
        redisCache.deleteObject(APP_SMS_CODE_KEY + body.getPhone());
        return buildTokenResult(user);
    }

    @Anonymous
    @PostMapping("/login/password")
    public AjaxResult loginByPassword(@RequestBody PwdLoginBody body) {
        if (body == null || StringUtils.isEmpty(body.getPhone()) || StringUtils.isEmpty(body.getPassword())) {
            return AjaxResult.error("手机号与密码不能为空");
        }
        AppUser user = appUserService.selectByPhone(body.getPhone());
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }
        if (StringUtils.isEmpty(user.getPassword()) || !SecurityUtils.matchesPassword(body.getPassword(), user.getPassword())) {
            return AjaxResult.error("密码错误");
        }
        return buildTokenResult(user);
    }

    @GetMapping("/profile")
    public AjaxResult profile() {
        String phone = SecurityUtils.getUsername();
        AppUser user = appUserService.selectByPhone(phone);
        return AjaxResult.success(user);
    }

    private AjaxResult buildTokenResult(AppUser appUser) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(appUser.getId());
        sysUser.setUserName(appUser.getPhone());
        sysUser.setPassword(appUser.getPassword());
        LoginUser loginUser = new LoginUser(sysUser, null);
        loginUser.setUserId(appUser.getId());
        String token = tokenService.createToken(loginUser);
        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }
}
