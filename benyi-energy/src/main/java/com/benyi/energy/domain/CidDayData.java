package com.benyi.energy.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

/**
 * 发电信息对象 cid_day_data
 *
 * @author wuqiguang
 * @date 2022-09-15
 */
public class CidDayData extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 网关 */
    @Excel(name = "网关")
    private String cid;

    /** 逆变器 */
    @Excel(name = "逆变器")
    private String vid;

    private String[] vidArr;

    /** 路数 */
    @Excel(name = "路数")
    private String roadType;

    /** MI 原边软件版本号 */
    @Excel(name = "MI 原边软件版本号")
    private String softVersion;

    /** MI 副边软件版本号 */
    @Excel(name = "MI 副边软件版本号")
    private String softDeputyVersion;

    /** 电池板电压：0~64V  */
    @Excel(name = "电池板电压：0~64V ")
    private String volt;

    /** 发电功率：0~1024W */
    @Excel(name = "发电功率：0~1024W")
    private String power;

    /** MI 发电量：0~(4*32768)KWh */
    @Excel(name = "MI 发电量：0~(4*32768)KWh")
    private String energy;

    /** 实时温度：-40~120℃ */
    @Excel(name = "实时温度：-40~120℃")
    private String temp;

    /** 并网电压：0~512V */
    @Excel(name = "并网电压：0~512V")
    private String gridVolt;

    /** 并网频率：0~128Hz */
    @Excel(name = "并网频率：0~128Hz")
    private String gridFreq;

    /** MI 原边故障 */
    @Excel(name = "MI 原边故障")
    private String m1MError;

    /** MI 副边边故障 */
    @Excel(name = "MI 副边边故障")
    private String m1SError;

    /**
     * MI 原边故障Code
     */
    @Excel(name = "MI M Error Code")
    private String m1MErrorCode;

    /**
     * MI 副边边故障Code
     */
    @Excel(name = "MI S Error Code")
    private String m1SErrorCode;

    /** 发送时间 */
    @Excel(name = "发送时间")
    private String sendTime;

    /** 0正常发电，1补发电 */
    @Excel(name = "0正常发电，1补发电")
    private String cidOrEmu;

    /** 日期 */
    @Excel(name = "日期")
    private String date;

    /** 更新日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "更新日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date updateDate;

    /** 创建日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "创建日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date createDate;

    private String isRemove;

    private String delFlag;

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


    public void setVidArr(String[] vidArr)
    {
        this.vidArr = vidArr;
    }
    public String[] getVidArr()
    {
        return vidArr;
    }


    public void setVid(String vid)
    {
        this.vid = vid;
    }
    public String getVid()
    {
        return vid;
    }
    public void setRoadType(String roadType)
    {
        this.roadType = roadType;
    }

    public String getRoadType()
    {
        return roadType;
    }
    public void setSoftVersion(String softVersion)
    {
        this.softVersion = softVersion;
    }

    public String getSoftVersion()
    {
        return softVersion;
    }
    public void setSoftDeputyVersion(String softDeputyVersion)
    {
        this.softDeputyVersion = softDeputyVersion;
    }

    public String getSoftDeputyVersion()
    {
        return softDeputyVersion;
    }
    public void setVolt(String volt)
    {
        this.volt = volt;
    }

    public String getVolt()
    {
        return volt;
    }
    public void setPower(String power)
    {
        this.power = power;
    }

    public String getPower()
    {
        return power;
    }
    public void setEnergy(String energy)
    {
        this.energy = energy;
    }

    public String getEnergy()
    {
        return energy;
    }
    public void setTemp(String temp)
    {
        this.temp = temp;
    }

    public String getTemp()
    {
        return temp;
    }
    public void setGridVolt(String gridVolt)
    {
        this.gridVolt = gridVolt;
    }

    public String getGridVolt()
    {
        return gridVolt;
    }
    public void setGridFreq(String gridFreq)
    {
        this.gridFreq = gridFreq;
    }

    public String getGridFreq()
    {
        return gridFreq;
    }
    public void setM1MError(String m1MError)
    {
        this.m1MError = m1MError;
    }

    public String getM1MError()
    {
        return m1MError;
    }
    public void setM1SError(String m1SError)
    {
        this.m1SError = m1SError;
    }

    public String getM1SError()
    {
        return m1SError;
    }
    public void setSendTime(String sendTime)
    {
        this.sendTime = sendTime;
    }

    public String getSendTime()
    {
        return sendTime;
    }
    public void setCidOrEmu(String cidOrEmu)
    {
        this.cidOrEmu = cidOrEmu;
    }

    public String getCidOrEmu()
    {
        return cidOrEmu;
    }
    public void setDate(String date)
    {
        this.date = date;
    }

    public String getDate()
    {
        return date;
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

    public String getM1MErrorCode() {
        return m1MErrorCode;
    }

    public void setM1MErrorCode(String m1MErrorCode) {
        this.m1MErrorCode = m1MErrorCode;
    }

    public String getM1SErrorCode() {
        return m1SErrorCode;
    }

    public void setM1SErrorCode(String m1SErrorCode) {
        this.m1SErrorCode = m1SErrorCode;
    }

    public String getIsRemove() {
        return isRemove;
    }

    public void setIsRemove(String isRemove) {
        this.isRemove = isRemove;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("cid", getCid())
                .append("vid", getVid())
                .append("roadType", getRoadType())
                .append("softVersion", getSoftVersion())
                .append("softDeputyVersion", getSoftDeputyVersion())
                .append("volt", getVolt())
                .append("power", getPower())
                .append("energy", getEnergy())
                .append("temp", getTemp())
                .append("gridVolt", getGridVolt())
                .append("gridFreq", getGridFreq())
                .append("m1MError", getM1MError())
                .append("m1MErrorCode", getM1MErrorCode())
                .append("m1SError", getM1SError())
                .append("m1SErrorCode", getM1SErrorCode())
                .append("sendTime", getSendTime())
                .append("cidOrEmu", getCidOrEmu())
                .append("date", getDate())
                .append("delFlag", getDelFlag())
                .append("isRemove", getIsRemove())
                .append("updateDate", getUpdateDate())
                .append("createDate", getCreateDate())
                .toString();
    }


}
