package com.benyi.energy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/4/22 00:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CidDataMonth {

    private long id;

    private  long plantID;

    private  String cid;

    private  String vid;

    private String energy;

    private String power;

    private String createDate;



}
