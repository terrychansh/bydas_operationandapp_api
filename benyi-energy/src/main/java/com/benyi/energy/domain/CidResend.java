package com.benyi.energy.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

/**
 * 协议补发对象 cid_resend
 * 
 * @author wuqiguang
 * @date 2022-09-02
 */
public class CidResend extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 网关 */
    @Excel(name = "网关")
    private String cid;

    /** 补发条数 */
    @Excel(name = "补发条数")
    private Long count;

    /** 补发起始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "补发起始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    /** 补发结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "补发结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;

    /** 服务器是否回应 */
    @Excel(name = "服务器是否回应")
    private Long response;

    /** 删除标识(0:no,1:yes) */
    private Long delFlag;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "更新时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

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
    public void setResendCid(String cid)
    {
        this.cid = cid;
    }

    public String getCid()
    {
        return cid;
    }
    public void setCount(Long resendCount)
    {
        this.count = count;
    }

    public Long getCount()
    {
        return count;
    }
    public void setStartDate(Date resendStartDate)
    {
        this.startDate = startDate;
    }

    public Date getStartDate()
    {
        return startDate;
    }
    public void setEndDate(Date resendEndDate)
    {
        this.endDate = resendEndDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }
    public void setResponse(Long response)
    {
        this.response = response;
    }

    public Long getResponse()
    {
        return response;
    }
    public void setDelFlag(Long delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() 
    {
        return delFlag;
    }
    public void setUpdateDate(Date updateDate) 
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() 
    {
        return updateDate;
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
            .append("count", getCount())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("response", getResponse())
            .append("delFlag", getDelFlag())
            .append("updateDate", getUpdateDate())
            .append("createDate", getCreateDate())
            .toString();
    }
}
