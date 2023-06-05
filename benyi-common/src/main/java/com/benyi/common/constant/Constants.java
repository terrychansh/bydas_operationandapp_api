package com.benyi.common.constant;

import io.jsonwebtoken.Claims;

/**
 * 通用常量信息
 * 
 * @author wuqiguang
 */
public class Constants
{
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    public static final String SUCCESS = "0";

    /**
     * 通用失败标识
     */
    public static final String FAIL = "1";

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";
 
    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌
     */
    public static final String TOKEN = "token";
    /**
     * 电站名字
     */
    public static final String PLANT_NAME = "plant_name";

    /**
     * 电站ID
     */
    public static final String PLANT_ID = "plant_id";

    /**
     * 全部电站
     */
    public static final String TOTAL_PLANT = "total_plant";

    /**
     * 全部电量
     */
    public static final String TOTAL_ENERGY = "total_energy";
    /**
     * 设备列表
     */
    public static final String DEVICE_List = "device_list";
    /**
     * 设备总数
     */
    public static final String DEVICE_TOTAL_COUNT = "device_total";
    /**
     * mis设备列表
     */
    public static final String DEVICE_MIS_List = "device_mis_list";
    /**
     * mis设备总数
     */
    public static final String DEVICE_MIS_TOTAL_COUNT = "device_mis_total";

    /**
     * 当前电压
     */
    public static final String CURRENT_POWER = "current_power";
    /**
     * 今日电量
     */
    public static final String DAILY_ENERGY = "daily_energy";

    /**
     * 设备normal总数
     */
    public static final String DEVICE_NORMAL_COUNT = "device_normal_total";

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 用户ID
     */
    public static final String JWT_USERID = "userid";

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME = Claims.SUBJECT;

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR = "avatar";

    /**
     * 创建时间
     */
    public static final String JWT_CREATED = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES = "authorities";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * RMI 远程方法调用
     */
    public static final String LOOKUP_RMI = "rmi:";

    /**
     * LDAP 远程方法调用
     */
    public static final String LOOKUP_LDAP = "ldap:";

    /**
     * LDAPS 远程方法调用
     */
    public static final String LOOKUP_LDAPS = "ldaps:";

    /**
     * 定时任务白名单配置（仅允许访问的包名，如其他需要可以自行添加）
     */
    public static final String[] JOB_WHITELIST_STR = { "com.benyi" };

    /**
     * 定时任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = { "java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.benyi.common.utils.file" };
}
