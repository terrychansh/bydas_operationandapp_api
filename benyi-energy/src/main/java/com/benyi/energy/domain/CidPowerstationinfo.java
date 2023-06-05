package com.benyi.energy.domain;

import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

/**
 * 电站列表对象 cid_powerstationinfo
 * 
 * @author wuqiguang
 * @date 2022-08-01
 */
public class CidPowerstationinfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键id */
    private Long id;

    /** 电厂名称 */
    @Excel(name = "Plant")
    private String energyName;

    /** 电站类型 */
//    @Excel(name = "电站类型")
    private String energyType;

    /** 装机容量 */
    @Excel(name = "Capacity")
    private Long energyCapacity;

    /** 业主名称 */
    @Excel(name = "Owner")
    private String energyDeptName;

    /** 业主id */
//    @Excel(name = "业主id")
    private Long energyDeptId;

    /** 国家地区 */
    @Excel(name = "Country")
    private String energyLocation;

    /** 地区 */
    @Excel(name = "State")
    private String energyState;

    /** 电价 */
    @Excel(name = "Price")
    private Float energyPrice;

    /** 电价单位 */
    @Excel(name = "priceUnit")
    private String priceUnit;

    /** 详细地址 */
    @Excel(name = "LocationDetail")
    private String energyLocationDetail;

    /** 详细地址--Lat坐标 */
    @Excel(name = "map-Lat")
    private String energyLocationLat;

    /** 详细地址--Lng坐标 */
    @Excel(name = "map-Lng")
    private String energyLocationLng;

    /** 时区 */
    @Excel(name = "TimeZone")
    private String energyTimeZone;

    /** 安装商 */
    @Excel(name = "PlantInstall")
    private String energyInstall;

    /** 日发电量 */
    @Excel(name = "energyTotalDay")
    private String energyTotalDay;

    /** 月发电量 */
    @Excel(name = "energyTotalMonth")
    private String energyTotalMonth;

    /** 年发电量 */
    @Excel(name = "energyTotalYear")
    private String energyTotalYear;

    /** 总发电量 */
    @Excel(name = "energyTotal")
    private String energyTotal;

    /** 电站布局对应内容 */
    //@Excel(name = "电站布局对应内容")
    private String energyLayout;

    /** 防逆流开关(0开启，1关闭) */
    @Excel(name = "SettingsAntiReflux(0:Open，1:Close)")
    private String energySettingsAntiReflux;

    /** 三相平衡开关(0开启，1关闭) */
    @Excel(name = "SettingsThreePhaseEquilibrium(0:Open，1:Close)")
    private String energySettingsThreePhaseEquilibrium;

    /** 电站电价 */
//    @Excel(name = "RolePrice")
    private Long energySettingsRolePrice;

    /** 电站允许布局(0开启，1关闭) */
    @Excel(name = "SettingsRoleAllowLayout(0:Open，1:Close)")
    private String energySettingsRoleAllowLayout;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 电站允许布局(0开启，1关闭) */
    @Excel(name = "status(0online，1offline，2build)")
    private String energyStatus;

    /** 图片地址 */
    private String energyImageUrl;

    /**
     * 当前功率
     */
    private String energyCurrentPower;


    private TreeMap<String,String> energyHourList;

    private String lastUpdate;

    private String lastErrorCode;

    private List<CidRelation> misList;


    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setEnergyName(String energyName) 
    {
        this.energyName = energyName;
    }

    public String getEnergyName() 
    {
        return energyName;
    }
    public void setEnergyType(String energyType) 
    {
        this.energyType = energyType;
    }

    public String getEnergyType() 
    {
        return energyType;
    }
    public void setEnergyCapacity(Long energyCapacity) 
    {
        this.energyCapacity = energyCapacity;
    }

    public Long getEnergyCapacity() 
    {
        return energyCapacity;
    }
    public void setEnergyDeptName(String energyDeptName) 
    {
        this.energyDeptName = energyDeptName;
    }

    public String getEnergyDeptName() 
    {
        return energyDeptName;
    }
    public void setEnergyDeptId(Long energyDeptId) 
    {
        this.energyDeptId = energyDeptId;
    }

    public Long getEnergyDeptId() 
    {
        return energyDeptId;
    }
    public void setEnergyLocation(String energyLocation) 
    {
        this.energyLocation = energyLocation;
    }

    public String getEnergyLocation() 
    {
        return energyLocation;
    }
    public void setEnergyLocationDetail(String energyLocationDetail) 
    {
        this.energyLocationDetail = energyLocationDetail;
    }

    public String getEnergyLocationDetail() 
    {
        return energyLocationDetail;
    }
    public void setEnergyTimeZone(String energyTimeZone) 
    {
        this.energyTimeZone = energyTimeZone;
    }

    public String getEnergyTimeZone() 
    {
        return energyTimeZone;
    }
    public void setEnergyInstall(String energyInstall) 
    {
        this.energyInstall = energyInstall;
    }

    public String getEnergyInstall() 
    {
        return energyInstall;
    }
    public void setEnergyTotalDay(String energyTotalDay) 
    {
        this.energyTotalDay = energyTotalDay;
    }

    public String getEnergyTotalDay() 
    {
        return energyTotalDay;
    }
    public void setEnergyTotalMonth(String energyTotalMonth) 
    {
        this.energyTotalMonth = energyTotalMonth;
    }

    public String getEnergyTotalMonth() 
    {
        return energyTotalMonth;
    }
    public void setEnergyTotalYear(String energyTotalYear) 
    {
        this.energyTotalYear = energyTotalYear;
    }

    public String getEnergyTotalYear() 
    {
        return energyTotalYear;
    }
    public void setEnergyTotal(String energyTotal) 
    {
        this.energyTotal = energyTotal;
    }

    public String getEnergyTotal() 
    {
        return energyTotal;
    }
    public void setEnergyLayout(String energyLayout)
    {
        this.energyLayout = energyLayout;
    }

    public String getEnergyLayout()
    {
        return energyLayout;
    }
    public void setEnergySettingsAntiReflux(String energySettingsAntiReflux) 
    {
        this.energySettingsAntiReflux = energySettingsAntiReflux;
    }

    public String getEnergySettingsAntiReflux() 
    {
        return energySettingsAntiReflux;
    }
    public void setEnergySettingsThreePhaseEquilibrium(String energySettingsThreePhaseEquilibrium) 
    {
        this.energySettingsThreePhaseEquilibrium = energySettingsThreePhaseEquilibrium;
    }

    public String getEnergySettingsThreePhaseEquilibrium() 
    {
        return energySettingsThreePhaseEquilibrium;
    }
    public void setEnergySettingsRolePrice(Long energySettingsRolePrice) 
    {
        this.energySettingsRolePrice = energySettingsRolePrice;
    }

    public Long getEnergySettingsRolePrice() 
    {
        return energySettingsRolePrice;
    }
    public void setEnergySettingsRoleAllowLayout(String energySettingsRoleAllowLayout)
    {
        this.energySettingsRoleAllowLayout = energySettingsRoleAllowLayout;
    }

    public String getEnergySettingsRoleAllowLayout()
    {
        return energySettingsRoleAllowLayout;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public String getEnergyStatus() {
        return energyStatus;
    }

    public void setEnergyStatus(String energyStatus) {
        this.energyStatus = energyStatus;
    }


    public String getEnergyState() {
        return energyState;
    }

    public void setEnergyState(String energyState) {
        this.energyState = energyState;
    }

    public Float getEnergyPrice() {
        return energyPrice;
    }

    public void setEnergyPrice(Float energyPrice) {
        this.energyPrice = energyPrice;
    }

    public String getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }


    public String getEnergyImageUrl() {
        return energyImageUrl;
    }

    public void setEnergyImageUrl(String energyImageUrl) {
        this.energyImageUrl = energyImageUrl;
    }


    public String getEnergyLocationLat() {
        return energyLocationLat;
    }

    public void setEnergyLocationLat(String energyLocationLat) {
        this.energyLocationLat = energyLocationLat;
    }

    public String getEnergyLocationLng() {
        return energyLocationLng;
    }

    public void setEnergyLocationLng(String energyLocationLng) {
        this.energyLocationLng = energyLocationLng;
    }


    public String getEnergyCurrentPower() {
        return energyCurrentPower;
    }

    public void setEnergyCurrentPower(String energyCurrentPower) {
        this.energyCurrentPower = energyCurrentPower;
    }

    public TreeMap<String,String> getEnergyHourList() {
        return energyHourList;
    }

    public void setEnergyHourList(TreeMap<String,String> energyHourList) {
        this.energyHourList = energyHourList;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastErrorCode() {
        return lastErrorCode;
    }

    public void setLastErrorCode(String lastErrorCode) {
        this.lastErrorCode = lastErrorCode;
    }

    public List<CidRelation> getMisList() {
        return misList;
    }

    public void setMisList(List<CidRelation> misList) {
        this.misList = misList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("energyName", getEnergyName())
            .append("energyType", getEnergyType())
            .append("energyCapacity", getEnergyCapacity())
            .append("energyDeptName", getEnergyDeptName())
            .append("energyDeptId", getEnergyDeptId())
            .append("energyLocation", getEnergyLocation())
            .append("energyState", getEnergyState())
            .append("energyPrice", getEnergyPrice())
            .append("priceUnit", getPriceUnit())
            .append("energyLocationDetail", getEnergyLocationDetail())
            .append("energyLocationLat", getEnergyLocationLat())
            .append("energyLocationLng", getEnergyLocationLng())
            .append("energyTimeZone", getEnergyTimeZone())
            .append("energyInstall", getEnergyInstall())
            .append("energyImageUrl", getEnergyImageUrl())
            .append("energyTotalDay", getEnergyTotalDay())
            .append("energyTotalMonth", getEnergyTotalMonth())
            .append("energyTotalYear", getEnergyTotalYear())
            .append("energyTotal", getEnergyTotal())
            .append("energyLayout", getEnergyLayout())
            .append("energyStatus", getEnergyStatus())
            .append("energySettingsAntiReflux", getEnergySettingsAntiReflux())
            .append("energySettingsThreePhaseEquilibrium", getEnergySettingsThreePhaseEquilibrium())
            .append("energySettingsRolePrice", getEnergySettingsRolePrice())
            .append("energySettingsRoleAllowLayout", getEnergySettingsRoleAllowLayout())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("energyCurrentPower", getEnergyCurrentPower())
            .append("energyHourList", getEnergyHourList())
            .append("lastErrorCode", getLastErrorCode())
            .append("lastUpdate", getLastUpdate())
                .append("misList", getMisList())
            .toString();
    }
}
