package com.benyi.energy.mail;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/5/23 07:52
 */
@Slf4j
@Component
public class MailAccount {

    private static String staticProfile;

    @Value("${spring.profiles.active}")
    private String profile;

    @PostConstruct
    public void init() {
        staticProfile = profile;
    }
    private static cn.hutool.extra.mail.MailAccount instance;

    public static cn.hutool.extra.mail.MailAccount getInstance(){

        if (instance == null) {
//            instance = new cn.hutool.extra.mail.MailAccount(String.format("mail-%s.setting", staticProfile));
            instance = new cn.hutool.extra.mail.MailAccount(String.format("mail.setting", staticProfile));

        }

        log.info("instance:"+ JSON.toJSONString(instance));
        return instance;

    }



}
