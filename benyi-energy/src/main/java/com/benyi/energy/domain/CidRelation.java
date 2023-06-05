package com.benyi.energy.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 网关、微逆关系对象 cid_relation
 *
 * @author wuqiguang
 * @date 2022-07-31
 */
public class CidRelation extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 逆变器
     */
    @Excel(name = "逆变器")
    private String vid;

    /**
     * 网关
     */
    @Excel(name = "网关")
    private String cid;

    /**
     * 电站Id
     */
    @Excel(name = "电站Id")
    private Long powerStationId;

    /**
     * 绑定关系是否启用(0启用，1停用)
     */
    @Excel(name = "绑定关系是否启用(0启用，1停用)")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 前端使用是否显示
     * 设置默认值：false
     */
    private boolean showIndex = false;

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
     * 标准功率
     */
    private String standardPower;

    /**
     * 当前功率
     */
    private String currentPower;

    /**
     * 当前发电量
     */
    private String currentEnergy;

    /**
     * 总发电量
     */
    private String countEnergy;

    /**
     * 周发电量
     */
    private String weekEnergy;

    /**
     * 月发电量
     */
    private String monthEnergy;

    /**
     * 年发电量
     */
    private String yearEnergy;

    /**
     * 网关：软件版本号：
     */
    @Excel(name="softVersion")
    private String softVersion;

    private String softDeputyVersion;

    /**
     * 绑定状态
     */
    private String bindType;

    private String powerStationName;

    private List<String> vidList;

    private String isConfirm;

    private String OnlineStatus;

    /**
     * 远程命令--更新关系表
     */
    private String cammandUpdateMap;

    /**
     * 远程命令--开机
     */
    private String cammandStartingUp;
    /**
     * 远程命令--升级
     */
    private String cammandUpdate;

    /**
     * 远程命令--调整功率
     */
    private String cammandPowerCoefficient;

    /**
     * 远程命令--设置并网参数
     */
    private String cammandOnGrid;

    /**
     * 远程命令--不获取并网参数
     */
    private String cammandUnGetOnGrid;



    private List<CidRelation> childList;


    private String lastDate;

    private boolean hasError;

    private String lastUpdate;

    private String lastErrorCode;

    private String lastError;

    private String lastSErrorCode;

    private String lastSError;

    private Long deptId;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setVid(String vid) {
        this.vid = vid;
    }

    public String getVid() {
        return vid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCid() {
        return cid;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public Long getPowerStationId() {
        return powerStationId;
    }

    public void setPowerStationId(Long powerStationId) {
        this.powerStationId = powerStationId;
    }


    public boolean isShowIndex() {
        return showIndex;
    }

    public void setShowIndex(boolean showIndex) {
        this.showIndex = showIndex;
    }

    public String getRoadType() {
        return roadType;
    }

    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    public String getStandardPower() {
        return standardPower;
    }

    public void setStandardPower(String standardPower) {
        this.standardPower = standardPower;
    }



    public String getCurrentEnergy() {
        return currentEnergy;
    }

    public void setCurrentEnergy(String currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public String getCountEnergy() {
        return countEnergy;
    }

    public void setCountEnergy(String countEnergy) {
        this.countEnergy = countEnergy;
    }

    public String getWeekEnergy() {
        return weekEnergy;
    }

    public void setWeekEnergy(String weekEnergy) {
        this.weekEnergy = weekEnergy;
    }

    public String getMonthEnergy() {
        return monthEnergy;
    }

    public void setMonthEnergy(String monthEnergy) {
        this.monthEnergy = monthEnergy;
    }

    public String getYearEnergy() {
        return yearEnergy;
    }

    public void setYearEnergy(String yearEnergy) {
        this.yearEnergy = yearEnergy;
    }

    public String getSoftVersion() {
        return softVersion;
    }

    public void setSoftVersion(String softVersion) {
        this.softVersion = softVersion;
    }

    public String getSoftDeputyVersion() {
        return softDeputyVersion;
    }

    public void setSoftDeputyVersion(String softDeputyVersion) {
        this.softDeputyVersion = softDeputyVersion;
    }

    public String getBindType() {
        return bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

    public String getPowerStationName() {
        return powerStationName;
    }

    public void setPowerStationName(String powerStationName) {
        this.powerStationName = powerStationName;
    }

    public String getHardVersion() {
        return hardVersion;
    }

    public void setHardVersion(String hardVersion) {
        this.hardVersion = hardVersion;
    }

    public String getCidType() {
        return cidType;
    }

    public void setCidType(String cidType) {
        this.cidType = cidType;
    }

    public List<String> getVidList() {
        return vidList;
    }

    public void setVidList(List<String> vidList) {
        this.vidList = vidList;
    }


    public String getIsConfirm() {
        return isConfirm;
    }

    public void setIsConfirm(String isConfirm) {
        this.isConfirm = isConfirm;
    }

    public String getCammandUpdateMap() {
        return cammandUpdateMap;
    }

    public void setCammandUpdateMap(String cammandUpdateMap) {
        this.cammandUpdateMap = cammandUpdateMap;
    }

    public String getCammandStartingUp() {
        return cammandStartingUp;
    }

    public void setCammandStartingUp(String cammandStartingUp) {
        this.cammandStartingUp = cammandStartingUp;
    }

    public String getCammandUpdate() {
        return cammandUpdate;
    }

    public void setCammandUpdate(String cammandUpdate) {
        this.cammandUpdate = cammandUpdate;
    }

    public String getCammandPowerCoefficient() {
        return cammandPowerCoefficient;
    }

    public void setCammandPowerCoefficient(String cammandPowerCoefficient) {
        this.cammandPowerCoefficient = cammandPowerCoefficient;
    }

    public String getCammandOnGrid() {
        return cammandOnGrid;
    }

    public void setCammandOnGrid(String cammandOnGrid) {
        this.cammandOnGrid = cammandOnGrid;
    }

    public String getCammandUnGetOnGrid() {
        return cammandUnGetOnGrid;
    }

    public void setCammandUnGetOnGrid(String cammandUnGetOnGrid) {
        this.cammandUnGetOnGrid = cammandUnGetOnGrid;
    }

    public List<CidRelation> getChildList() {
        return childList;
    }

    public void setChildList(List<CidRelation> childList) {
        this.childList = childList;
    }

    public String getCurrentPower() {
        return currentPower;
    }

    public void setCurrentPower(String currentPower) {
        this.currentPower = currentPower;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getOnlineStatus() {
        return OnlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        OnlineStatus = onlineStatus;
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

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getLastSErrorCode() {
        return lastSErrorCode;
    }

    public void setLastSErrorCode(String lastSErrorCode) {
        this.lastSErrorCode = lastSErrorCode;
    }

    public String getLastSError() {
        return lastSError;
    }

    public void setLastSError(String lastSError) {
        this.lastSError = lastSError;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("vid", getVid())
                .append("cid", getCid())
                .append("powerStationId", getPowerStationId())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("showIndex", isShowIndex())
                .append("roadType", getRoadType())
                .append("currentEnergy", getCurrentEnergy())
                .append("countEnergy", getCountEnergy())
                .append("weekEnergy", getWeekEnergy())
                .append("monthEnergy", getMonthEnergy())
                .append("yearEnergy", getYearEnergy())
                .append("softVersion", getSoftVersion())
                .append("hardVersion", getHardVersion())
                .append("cidType", getCidType())
                .append("softDeputyVersion", getSoftDeputyVersion())
                .append("bindType",getBindType())
                .append("powerStationName",getPowerStationName())
                .append("vidList",getVidList())
                .append("isConfirm",getIsConfirm())
                .append("cammandUpdateMap",getCammandUpdateMap())
                .append("cammandStartingUp",getCammandStartingUp())
                .append("cammandPowerCoefficient",getCammandPowerCoefficient())
                .append("cammandUpdate",getCammandUpdate())
                .append("cammandOnGrid",getCammandOnGrid())
                .append("cammandUnGetOnGrid",getCammandUnGetOnGrid())
                .append("childList",getChildList())
                .append("currentPower",getCurrentPower())
                .append("lastDate",getLastDate())
                .append("hasError",isHasError())
                .append("OnlineStatus",getOnlineStatus())
                .append("lastUpdate",getLastUpdate())
                .append("lastErrorCode",getLastErrorCode())
                .append("lastError",getLastError())
                .append("lastSErrorCode",getLastSErrorCode())
                .append("lastSError",getLastSError())
                .append("deptId",getDeptId())
                .toString();
    }
}
