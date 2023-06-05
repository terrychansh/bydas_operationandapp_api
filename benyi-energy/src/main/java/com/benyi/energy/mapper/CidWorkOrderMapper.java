package com.benyi.energy.mapper;

import java.util.List;
import com.benyi.energy.domain.CidWorkOrder;

/**
 * 工单Mapper接口
 * 
 * @author wuqiguang
 * @date 2022-08-11
 */
public interface CidWorkOrderMapper 
{
    /**
     * 查询工单
     * 
     * @param workOrderId 工单主键
     * @return 工单
     */
    public CidWorkOrder selectCidWorkOrderByWorkOrderId(Long workOrderId);

    /**
     * 查询工单列表
     * 
     * @param cidWorkOrder 工单
     * @return 工单集合
     */
    public List<CidWorkOrder> selectCidWorkOrderList(CidWorkOrder cidWorkOrder);

    /**
     * 新增工单
     * 
     * @param cidWorkOrder 工单
     * @return 结果
     */
    public int insertCidWorkOrder(CidWorkOrder cidWorkOrder);

    /**
     * 修改工单
     * 
     * @param cidWorkOrder 工单
     * @return 结果
     */
    public int updateCidWorkOrder(CidWorkOrder cidWorkOrder);

    /**
     * 删除工单
     * 
     * @param workOrderId 工单主键
     * @return 结果
     */
    public int deleteCidWorkOrderByWorkOrderId(Long workOrderId);

    /**
     * 批量删除工单
     * 
     * @param workOrderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCidWorkOrderByWorkOrderIds(Long[] workOrderIds);


    /**
     * 获取未完成工单列表
     * @param cidWorkOrder
     * @return
     */
    public List<CidWorkOrder> selectCidWorkOrderUnFinishList(CidWorkOrder cidWorkOrder);

    /**
     * 获取已完成工单列表
     * @param cidWorkOrder
     * @return
     */
    public List<CidWorkOrder> selectCidWorkOrderFinishList(CidWorkOrder cidWorkOrder);
}
