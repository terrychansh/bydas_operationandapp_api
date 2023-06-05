package com.benyi.energy.domain;

import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Version 1.0
 * @Description
 * @Author Terry Chan
 * create 2023/5/26 12:04
 */
@Data
public class CidEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 网关
     */
    @Excel(name = "网关")
    private String cid;

    /**
     * 电站Id
     */
    @Excel(name = "电站Id")
    private Long plantID;

    /**
     * 绑定关系是否启用(0启用，1停用)
     */
    @Excel(name = "绑定关系是否启用(0启用，1停用)")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    private Integer standardPower;

    @Excel(name = "路数")
    private String roadType;

    /**
     * 网关：硬件版本号
     */
    @Excel(name = "hardVersion")
    private String hardVersion;
    /**
     * 网关类型
     */
    @Excel(name = "DtuType")
    private String cidType;

    /**
     * 当前功率
     */
    private String currentPower;

    /**
     * 今日发电量
     */
    private String dailyEnergy;

    /**
     * 网关：软件版本号：
     */
    @Excel(name = "softVersion")
    private String softVersion;

    private String softDeputyVersion;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
