package com.benyi.energy.mail;
import cn.hutool.core.util.StrUtil;

/**
 * @Version 1.0
 * @Description邮件短信模版
 * @Author Terry Chan
 * create 2023/5/23 07:54
 */
public class EmailCodeTemplate {

    private String title = "[NoReply] %s";

    private String content = "Verification code for user %s: %s";

    private String type;

    private String code;

    private String faultTitle = "[faultNotify] %s";

    private String faultContent = "%s message : %s";

    private String faultType;

    private String message;

//    public EmailCodeTemplate(UserEnum.SessionEmailCodePrefix sessionEmailCodePrefix, String code){
//        this.type = sessionEmailCodePrefix.getType();
//        this.code = code;
//    }
    public String getTitle(){
        return String.format(this.title, this.type);
    }

    public String getContent(){
        return String.format(this.content, StrUtil.lowerFirst(this.type), this.code);
    }

//    public EmailCodeTemplate(UserEnum.SessionEmailCodePrefix sessionEmailCodePrefix,String message,String type){
//        this.faultType = sessionEmailCodePrefix.getType();
//        this.message = message;
//    }

    public String getFaultTitle(){
        return String.format(this.faultTitle, this.faultType);
    }

    public String getFaultContent(){
        return String.format(this.faultContent, this.faultType, this.message);
    }
}