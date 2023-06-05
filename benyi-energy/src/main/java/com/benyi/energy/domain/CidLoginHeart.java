package com.benyi.energy.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

/**
 * 登陆、心跳入库对象 cid_login_heart
 *
 * @author wuqiguang
 * @date 2022-09-09
 */
public class CidLoginHeart extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** key */
    private Long id;

    /** 网关 */
    @Excel(name = "网关")
    private String cid;

    /** 0：login；1：heart */
    @Excel(name = "0：login；1：heart")
    private String heartLoginType;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setCid(String cid)
    {
        this.cid = cid;
    }

    public String getCid()
    {
        return cid;
    }
    public void setHeartLoginType(String heartLoginType)
    {
        this.heartLoginType = heartLoginType;
    }

    public String getHeartLoginType()
    {
        return heartLoginType;
    }
    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("cid", getCid())
                .append("heartLoginType", getHeartLoginType())
                .append("createDate", getCreateDate())
                .toString();
    }
}
