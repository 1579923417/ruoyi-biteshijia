package com.ruoyi.app.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.service.ISmsService;
import java.util.concurrent.TimeUnit;
import com.ruoyi.system.domain.AppUser;
import com.ruoyi.system.domain.vo.AppLoginResultVo;
import com.ruoyi.system.service.IAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

/**
 * APP 认证服务类
 * 提供用户登录和获取当前登录用户信息功能
 *
 * @author Jamie
 * @date 2025/12/10
 */
@Service
public class AppAuthService {

    @Autowired
    private IAppUserService appUserService;

    @Autowired
    private TokenService tokenService;

    @Value("${token.expireTime}")
    private int expireTime;

    @Autowired
    private ISmsService smsService;

    @Autowired
    private RedisCache redisCache;

    private static final String APP_LOGIN_CODE_KEY = "app:login:code:";

    /**
     * 生成指定位数的纯数字验证码
     * @param length 验证码位数
     * @return 纯数字验证码字符串
     */
    private String generateNumericCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int n = (int) (Math.random() * 10);
            sb.append(n);
        }
        return sb.toString();
    }

    /**
     * APP 用户登录
     * 验证手机号和密码，登录成功后生成 JWT token 并返回
     *
     * @param phone    用户手机号，不能为空
     * @param password 用户密码，不能为空
     * @return AppLoginResultVo 登录结果，包括 token 和有效期
     * @throws IllegalArgumentException 参数为空时抛出
     * @throws RuntimeException       用户不存在或密码错误时抛出
     */
    public AppLoginResultVo login(String phone, String password) {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("参数错误");
        }
        AppUser user = appUserService.selectByPhone(phone);
        if (user == null) {
            throw new RuntimeException("账号或密码错误");
        }
        String dbPwd = user.getPassword();
        if (StringUtils.isEmpty(dbPwd) || !SecurityUtils.matchesPassword(password, dbPwd)) {
            throw new RuntimeException("账号或密码错误");
        }
        SysUser su = new SysUser();
        su.setUserId(user.getId());
        su.setUserName(StringUtils.isNotEmpty(user.getPhone()) ? user.getPhone() : user.getName());
        su.setPassword(dbPwd);
        LoginUser loginUser = new LoginUser(user.getId(), null, su, null);
        String jwt = tokenService.createToken(loginUser);
        AppLoginResultVo vo = new AppLoginResultVo();
        vo.setToken(jwt);
        vo.setExpire(expireTime);
        return vo;
    }

    /**
     * 获取当前登录 APP 用户
     * 从请求头获取 token，解析出用户 ID，然后查询用户信息
     *
     * @param request HttpServletRequest 请求对象
     * @return AppUser 当前登录用户对象，如果未登录或 token 无效返回 null
     */
    public AppUser getCurrentAppUser(HttpServletRequest request) {
        LoginUser lu = tokenService.getLoginUser(request);
        if (lu == null) {
            return null;
        }
        return appUserService.selectById(lu.getUserId());
    }

    /**
     * APP 用户退出登录
     * 从请求中解析当前登录用户，如果存在则移除其缓存的登录态并记录退出日志
     *
     * @param request HttpServletRequest 请求对象
     */
    public void logout(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser != null) {
            String userName = loginUser.getUsername();
            tokenService.delLoginUser(loginUser.getToken());
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, MessageUtils.message("user.logout.success")));
        }
    }

    /**
     * 发送登录验证码（5位数字）并缓存到 Redis
     * @param phone 手机号
     */
    public void sendLoginCode(String phone) {
        if (StringUtils.isEmpty(phone)) {
            throw new IllegalArgumentException("参数错误");
        }
        String code = generateNumericCode(5);
        redisCache.setCacheObject(APP_LOGIN_CODE_KEY + phone, code, 60, TimeUnit.SECONDS);
        boolean ok = smsService.sendLoginCode(phone, code);
        if (!ok) {
            throw new RuntimeException("短信发送失败");
        }
    }

    /**
     * 验证验证码并登录
     * @param phone 手机号
     * @param code 前端输入的验证码
     * @return 登录结果（token 与有效期）
     */
    public AppLoginResultVo loginByCode(String phone, String code) {
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new IllegalArgumentException("参数错误");
        }
        String cached = redisCache.getCacheObject(APP_LOGIN_CODE_KEY + phone);
        if (StringUtils.isEmpty(cached) || !cached.equals(code)) {
            throw new RuntimeException("验证码错误或已过期");
        }
        AppUser user = appUserService.selectByPhone(phone);
        if (user == null) {
            AppUser entity = new AppUser();
            entity.setPhone(phone);
            entity.setName(phone);
            appUserService.insert(entity);
            user = appUserService.selectByPhone(phone);
        }
        SysUser su = new SysUser();
        su.setUserId(user.getId());
        su.setUserName(StringUtils.isNotEmpty(user.getPhone()) ? user.getPhone() : user.getName());
        su.setPassword(user.getPassword());
        LoginUser loginUser = new LoginUser(user.getId(), null, su, null);
        String jwt = tokenService.createToken(loginUser);
        redisCache.deleteObject(APP_LOGIN_CODE_KEY + phone);
        AppLoginResultVo vo = new AppLoginResultVo();
        vo.setToken(jwt);
        vo.setExpire(expireTime);
        return vo;
    }
}
