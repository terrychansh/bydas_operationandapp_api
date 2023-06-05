package com.benyi.energy.domain;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

/**
 * 发电信息对象 cid_data
 *
 * @author wuqiguang
 * @date 2022-07-31
 */

@Data
public class CidData extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    private Long plantID;

    /**
     * 网关
     */
    @Excel(name = "MIs")
    private String cid;

    /**
     * 微逆
     */
    @Excel(name = "Emu")
    private String vid;

    /**
     * MI 原边软件版本号
     */
    @Excel(name = "MI softVersion")
    private String softVersion;

    /**
     * MI 副边软件版本号
     */
    @Excel(name = "MI softDeputyVersion")
    private String softDeputyVersion;

    /**
     * 电池板电压：0~64V
     */
    @Excel(name = "volt：0~64V ")
    private String volt;

    /**
     * 发电功率：0~1024W
     */
    @Excel(name = "power：0~1024W")
    private String power;

    //标准功率
    @Excel(name = "standardPower")
    private  float standardPower;

    /**
     * MI 发电量：0~(4*32768)KWh
     */
    @Excel(name = "MI energy：0~(4*32768)KWh")
    private String energy;

    /**
     * 实时温度：-40~120℃
     */
    @Excel(name = "temp：-40~120℃")
    private String temp;

    /**
     * 并网电压：0~512V
     */
    @Excel(name = "gridVolt：0~512V")
    private String gridVolt;

    /**
     * 并网频率：0~128Hz
     */
    @Excel(name = "gridFreq：0~128Hz")
    private String gridFreq;

    /**
     * MI 原边故障
     */
    @Excel(name = "MI M Error")
    private String m1MError;

    /**
     * MI 副边边故障
     */
    @Excel(name = "MI S Error")
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

    /**
     * 发送时间
     */
    @Excel(name = "sendTime")
    private String sendTime;

    /**
     * cid==0 emu==1
     */
    @Excel(name = "Is replay send(0?1)")
    private String cidOrEmu;

    /**
     * 日期
     */
    @Excel(name = "date")
    private String date;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "createDate", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;


    /**
     * 更新日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "updateDate", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date updateDate;

    /**
     * 状态
     */
    @Excel(name = "status")
    private String status;

    /**
     * 关系列表
     */
    private List<CidRelation> relationsList;

    /**
     * 是否选中
     */
    private String isChecked;

    /**
     * 所属电站
     */
//    @Excel(name = "所属电站")
    private String powerStationName;

    @Excel(name = "loop")
    private String roadType;

    /**
     * 电站状态
     */
    private String energyStatus;

    /**
     * 查询类型
     */
    private String searchType;


    /**
     * 查询电站ID
     */
    private String queryStationId;

    private String m1MErrorCn;

    private String m1SErrorCn;

    /**
     * 查询时间
     */
    private String searchDate;


    /**
     * 设备类型
     */
    private String cidType;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCid() {
        return cid;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getVid() {
        return vid;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftDeputyVersion(String softDeputyVersion) {
        this.softDeputyVersion = softDeputyVersion;
    }

    public String getSoftDeputyVersion() {
        return softDeputyVersion;
    }

    public void setVolt(String volt) {
        this.volt = volt;
    }

    public String getVolt() {
        return volt;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getPower() {
        return power;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getEnergy() {
        return energy;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTemp() {
        return temp;
    }

    public void setGridVolt(String gridVolt) {
        this.gridVolt = gridVolt;
    }

    public String getGridVolt() {
        return gridVolt;
    }

    public void setGridFreq(String gridFreq) {
        this.gridFreq = gridFreq;
    }

    public String getGridFreq() {
        return gridFreq;
    }

    public void setM1MError(String m1MError) {
        this.m1MError = m1MError;
    }

    public String getM1MError() {
        return m1MError;
    }

    public void setM1SError(String m1SError) {
        this.m1SError = m1SError;
    }

    public String getM1SError() {
        return m1SError;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setCidOrEmu(String cidOrEmu) {
        this.cidOrEmu = cidOrEmu;
    }

    public String getCidOrEmu() {
        return cidOrEmu;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPowerStationName() {
        return powerStationName;
    }

    public void setPowerStationName(String powerStationName) {
        this.powerStationName = powerStationName;
    }


    public List<CidRelation> getRelationsList() {
        return relationsList;
    }

    public void setRelationsList(List<CidRelation> relationsList) {
        this.relationsList = relationsList;
    }


    public String getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = isChecked;
    }


    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getEnergyStatus() {
        return energyStatus;
    }

    public void setEnergyStatus(String energyStatus) {
        this.energyStatus = energyStatus;
    }

    public String getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }

    public String getQueryStationId() {
        return queryStationId;
    }

    public void setQueryStationId(String queryStationId) {
        this.queryStationId = queryStationId;
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

    public String getM1MErrorCn() {
        return m1MErrorCn;
    }

    public void setM1MErrorCn(String m1MErrorCn) {
        this.m1MErrorCn = m1MErrorCn;
    }

    public String getM1SErrorCn() {
        return m1SErrorCn;
    }

    public void setM1SErrorCn(String m1SErrorCn) {
        this.m1SErrorCn = m1SErrorCn;
    }

    public String getCidType() {
        return cidType;
    }

    public void setCidType(String cidType) {
        this.cidType = cidType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("cid", getCid())
                .append("vid", getVid())
                .append("softVersion", getSoftVersion())
                .append("softDeputyVersion", getSoftDeputyVersion())
                .append("volt", getVolt())
                .append("power", getPower())
                .append("energy", getEnergy())
                .append("temp", getTemp())
                .append("gridVolt", getGridVolt())
                .append("gridFreq", getGridFreq())
                .append("m1MError", getM1MError())
                .append("m1MErrorCn", getM1MErrorCn())
                .append("m1MErrorCode", getM1MErrorCode())
                .append("m1SError", getM1SError())
                .append("m1SErrorCn", getM1SErrorCn())
                .append("m1SErrorCode", getM1SErrorCode())
                .append("sendTime", getSendTime())
                .append("cidOrEmu", getCidOrEmu())
                .append("date", getDate())
                .append("updateDate", getUpdateDate())
                .append("createDate", getCreateDate())
                .append("status", getStatus())
                .append("powerStationName", getPowerStationName())
                .append("relationList",getRelationsList())
                .append("isChecked",getIsChecked())
                .append("roadType",getRoadType())
                .append("searchType",getSearchType())
                .append("energyStatus",getEnergyStatus())
                .append("searchDate",getSearchDate())
                .append("queryStationId",getQueryStationId())
                .toString();
    }
}
