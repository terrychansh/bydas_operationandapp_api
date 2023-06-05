package com.benyi.web.controller.system;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.hutool.extra.mail.MailUtil;
import com.benyi.common.constant.UserConstants;
import com.benyi.common.core.redis.RedisCache;
import com.benyi.energy.mail.MailAccount;
import com.benyi.system.service.ISysUserService;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.benyi.common.constant.Constants;
import com.benyi.common.core.domain.AjaxResult;
import com.benyi.common.core.domain.entity.SysMenu;
import com.benyi.common.core.domain.entity.SysUser;
import com.benyi.common.core.domain.model.LoginBody;
import com.benyi.common.utils.SecurityUtils;
import com.benyi.framework.web.service.SysLoginService;
import com.benyi.framework.web.service.SysPermissionService;
import com.benyi.system.service.ISysMenuService;

import javax.annotation.Resource;

/**
 * 登录验证
 *
 * @author wuqiguang
 */
@Slf4j
@RestController
public class SysLoginController {

    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Autowired
    private ISysUserService userService;

    @Resource
    private RedisCache redisCache;

    /**
     * 发送验证码到
     *
     * @param
     * @return 结果
     */
    @GetMapping("/sendCheckCode")
    public AjaxResult sendCheckCode(String email) {

        AjaxResult ajax = AjaxResult.success();

        SysUser user= new SysUser();
        user.setEmail(email);
        if (userService.checkEmailUnique(user).equals(UserConstants.NOT_UNIQUE)){
            ajax = AjaxResult.error("The Email existed.");
            return ajax;
        }

        String code = captchaProducer.createText();
        String title="[NoReply] The CheckCode For Registration";
        try {
            log.info("code:"+code);
            MailUtil.send(MailAccount.getInstance(), email,title, "The checkCode is: "+code, false);
            redisCache.setCacheObject("Reg_"+email, code, 5, TimeUnit.MINUTES);
        }catch (Exception exception){
            exception.printStackTrace();
            ajax = AjaxResult.error("The Email send failed.");
        }

        ajax.put(Constants.TOKEN, "1");
        return ajax;
    }


    /**
     * 注 册
     *
     * @param
     * @return 结果
     */
    @GetMapping("/register")
    public AjaxResult register(String email,String checkCode,String passwd ) {

        AjaxResult ajax = AjaxResult.success();
        if (!redisCache.getCacheObject("Reg_"+email).equals(checkCode)){
            ajax = AjaxResult.error("The checkCode is error.");
            return ajax;
        }

        SysUser user= new SysUser();
        user.setUserName(email);
        user.setEmail(email);
        user.setNickName(email);
        user.setPassword(SecurityUtils.encryptPassword(passwd));
        boolean result = userService.registerUser(user);

        if (!result){
            ajax = AjaxResult.error("The registeration is error.");
            return ajax;
        }
        ajax.put(Constants.TOKEN, "1");
        return ajax;
    }

    /**
     * 登录方法
     *
     * @param
     * @return 结果
     */
    @GetMapping("/demoLogin")
    public AjaxResult demoLogin() {

        AjaxResult ajax = AjaxResult.success();

        // 生成令牌
        String token = loginService.login("visitor", "12345678");
        ajax.put(Constants.TOKEN, token);

        return ajax;

    }

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody) {
        AjaxResult ajax = AjaxResult.success();
        String visitorLoginID = loginBody.getVisitorLogin();
        System.out.println("visitorLoginID:" + visitorLoginID);
        if (visitorLoginID == null) {
            String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                    loginBody.getUuid());
            ajax.put(Constants.TOKEN, token);
        } else if (visitorLoginID.equals("demo_account")) {

            System.out.println("=== start to visitor Login:" + visitorLoginID);
            // 生成令牌
            String token = loginService.login("visitor", "12345678");
            ajax.put(Constants.TOKEN, token);
        }

        return ajax;
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo() {
        SysUser user = SecurityUtils.getLoginUser().getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters() {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
