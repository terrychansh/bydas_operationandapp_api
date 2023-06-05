package com.benyi.energy.domain;

import lombok.Data;

import java.util.Date;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/3/16 01:47
 */
@Data
public class Feedback {

    private long id;
    private String suggest;
    private String contact;
    private String file;
    private Date createDate;

}
