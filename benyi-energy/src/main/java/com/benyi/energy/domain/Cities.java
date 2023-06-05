package com.benyi.energy.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

/**
 * 城市对象 cities
 *
 * @author wuqiguang
 * @date 2022-08-21
 */
public class Cities extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Integer id;

    /** 所属州省代码 */
    @Excel(name = "所属州省代码")
    private Integer stateId;

    /** 市代码 */
    @Excel(name = "市代码")
    private String code;

    /** 英文名 */
    @Excel(name = "英文名")
    private String name;

    /** 中文名 */
    @Excel(name = "中文名")
    private String cname;

    /** 简称 */
    @Excel(name = "简称")
    private String lowerName;

    /** 地区代码 */
    @Excel(name = "地区代码")
    private String codeFull;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setStateId(Integer stateId)
    {
        this.stateId = stateId;
    }

    public Integer getStateId()
    {
        return stateId;
    }
    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setCname(String cname)
    {
        this.cname = cname;
    }

    public String getCname()
    {
        return cname;
    }
    public void setLowerName(String lowerName)
    {
        this.lowerName = lowerName;
    }

    public String getLowerName()
    {
        return lowerName;
    }
    public void setCodeFull(String codeFull)
    {
        this.codeFull = codeFull;
    }

    public String getCodeFull()
    {
        return codeFull;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("stateId", getStateId())
                .append("code", getCode())
                .append("name", getName())
                .append("cname", getCname())
                .append("lowerName", getLowerName())
                .append("codeFull", getCodeFull())
                .toString();
    }
}
