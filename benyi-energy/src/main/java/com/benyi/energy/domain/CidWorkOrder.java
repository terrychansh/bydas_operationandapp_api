package com.benyi.energy.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.benyi.common.annotation.Excel;
import com.benyi.common.core.domain.BaseEntity;

/**
 * 工单对象 cid_work_order
 * 
 * @author wuqiguang
 * @date 2022-08-11
 */
public class CidWorkOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long workOrderId;

    /** 工单描述 */
    @Excel(name = "工单描述")
    private String workOrderInfo;

    /** 工单接收单位 */
    @Excel(name = "工单接收单位")
    private Long workOrderSentTo;

    /** 工单回复内容 */
    @Excel(name = "工单回复内容")
    private String workOrderReplayInfo;

    /** 工单状态(0：待办，1：在途，2：结束) */
    @Excel(name = "工单状态(0：待办，1：在途，2：结束)")
    private String workOrderStatus;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 工单归属单位） */
    @Excel(name = "工单状态(0：待办，1：在途，2：结束)")
    private Long createDeptId;

    /** 工单接收机构名称 **/
    @Excel(name = "工单接收机构名称")
    private String sentDeptName;

    /** 工单提交机构名称 **/
    @Excel(name = "工单提交机构")
    private String createDeptName;
    /** 派发类型 **/
    @Excel(name = "派发类型")
    private String workOrderType;

    public void setWorkOrderId(Long workOrderId) 
    {
        this.workOrderId = workOrderId;
    }

    public Long getWorkOrderId() 
    {
        return workOrderId;
    }
    public void setWorkOrderInfo(String workOrderInfo) 
    {
        this.workOrderInfo = workOrderInfo;
    }

    public String getWorkOrderInfo() 
    {
        return workOrderInfo;
    }
    public void setWorkOrderSentTo(Long workOrderSentTo) 
    {
        this.workOrderSentTo = workOrderSentTo;
    }

    public Long getWorkOrderSentTo() 
    {
        return workOrderSentTo;
    }
    public void setWorkOrderReplayInfo(String workOrderReplayInfo) 
    {
        this.workOrderReplayInfo = workOrderReplayInfo;
    }

    public String getWorkOrderReplayInfo() 
    {
        return workOrderReplayInfo;
    }
    public void setWorkOrderStatus(String workOrderStatus) 
    {
        this.workOrderStatus = workOrderStatus;
    }

    public String getWorkOrderStatus() 
    {
        return workOrderStatus;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public Long getCreateDeptId() {
        return createDeptId;
    }

    public void setCreateDeptId(Long createDeptId) {
        this.createDeptId = createDeptId;
    }

    public String getSentDeptName() {
        return sentDeptName;
    }

    public void setSentDeptName(String sentDeptName) {
        this.sentDeptName = sentDeptName;
    }

    public String getCreateDeptName() {
        return createDeptName;
    }

    public void setCreateDeptName(String createDeptName) {
        this.createDeptName = createDeptName;
    }

    public String getWorkOrderType() {
        return workOrderType;
    }

    public void setWorkOrderType(String workOrderType) {
        this.workOrderType = workOrderType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("workOrderId", getWorkOrderId())
            .append("workOrderInfo", getWorkOrderInfo())
            .append("workOrderSentTo", getWorkOrderSentTo())
            .append("workOrderReplayInfo", getWorkOrderReplayInfo())
            .append("workOrderStatus", getWorkOrderStatus())
            .append("createDeptId",getCreateDeptId())
            .append("createDeptName",getCreateDeptName())
            .append("sentDeptName",getSentDeptName())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("workOrderType", getWorkOrderType())
            .toString();
    }
}
