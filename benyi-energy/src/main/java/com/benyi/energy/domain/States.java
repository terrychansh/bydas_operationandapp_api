package com.benyi.energy.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

/**
 * 洲、省对象 states
 *
 * @author wuqiguang
 * @date 2022-08-21
 */
public class States extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Integer id;

    /** 英文名 */
    @Excel(name = "英文名")
    private String name;

    /** 国家id */
    @Excel(name = "国家id")
    private Integer countryId;

    /** 国家code */
    @Excel(name = "国家code")
    private String countryCode;

    /** fipscode */
    @Excel(name = "fipscode")
    private String fipsCode;

    /** iso */
    @Excel(name = "iso")
    private String iso2;

    /** 坐标 */
    @Excel(name = "坐标")
    private BigDecimal latitude;

    /** 坐标 */
    @Excel(name = "坐标")
    private BigDecimal longitude;

    /** Rapid API GeoDB Cities */
    @Excel(name = "Rapid API GeoDB Cities")
    private String wikidataid;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
    {
        return id;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
    public void setCountryId(Integer countryId)
    {
        this.countryId = countryId;
    }

    public Integer getCountryId()
    {
        return countryId;
    }
    public void setCountryCode(String countryCode)
    {
        this.countryCode = countryCode;
    }

    public String getCountryCode()
    {
        return countryCode;
    }
    public void setFipsCode(String fipsCode)
    {
        this.fipsCode = fipsCode;
    }

    public String getFipsCode()
    {
        return fipsCode;
    }
    public void setIso2(String iso2)
    {
        this.iso2 = iso2;
    }

    public String getIso2()
    {
        return iso2;
    }
    public void setLatitude(BigDecimal latitude)
    {
        this.latitude = latitude;
    }

    public BigDecimal getLatitude()
    {
        return latitude;
    }
    public void setLongitude(BigDecimal longitude)
    {
        this.longitude = longitude;
    }

    public BigDecimal getLongitude()
    {
        return longitude;
    }
    public void setWikidataid(String wikidataid)
    {
        this.wikidataid = wikidataid;
    }

    public String getWikidataid()
    {
        return wikidataid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("name", getName())
                .append("countryId", getCountryId())
                .append("countryCode", getCountryCode())
                .append("fipsCode", getFipsCode())
                .append("iso2", getIso2())
                .append("latitude", getLatitude())
                .append("longitude", getLongitude())
                .append("wikidataid", getWikidataid())
                .toString();
    }
}
