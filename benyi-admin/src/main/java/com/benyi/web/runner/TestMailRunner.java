package com.benyi.web.runner;


import cn.hutool.extra.mail.MailUtil;
import com.benyi.energy.mail.MailAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/5/23 08:03
 */
//@Component
@Slf4j
@Order(value = 1)
public class TestMailRunner  implements ApplicationRunner {

    @Resource
    MailAccount mailAccount;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        log.info("TestMailRunner.....start");
//        MailUtil.send(MailAccount.getInstance(), "26177862@qq.com","[NoReply] Register Information", "Don't replay.", false);
//        log.info("send mail.....ended");
    }
}
