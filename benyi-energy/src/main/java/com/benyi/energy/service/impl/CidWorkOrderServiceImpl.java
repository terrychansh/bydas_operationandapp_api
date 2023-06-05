package com.benyi.energy.service.impl;

import java.util.List;
import com.benyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.benyi.energy.mapper.CidWorkOrderMapper;
import com.benyi.energy.domain.CidWorkOrder;
import com.benyi.energy.service.ICidWorkOrderService;

/**
 * 工单Service业务层处理
 * 
 * @author wuqiguang
 * @date 2022-08-11
 */
@Service
public class CidWorkOrderServiceImpl implements ICidWorkOrderService 
{
    @Autowired
    private CidWorkOrderMapper cidWorkOrderMapper;

    /**
     * 查询工单
     * 
     * @param workOrderId 工单主键
     * @return 工单
     */
    @Override
    public CidWorkOrder selectCidWorkOrderByWorkOrderId(Long workOrderId)
    {
        return cidWorkOrderMapper.selectCidWorkOrderByWorkOrderId(workOrderId);
    }

    /**
     * 查询工单列表
     * 
     * @param cidWorkOrder 工单
     * @return 工单
     */
    @Override
    public List<CidWorkOrder> selectCidWorkOrderList(CidWorkOrder cidWorkOrder)
    {
        return cidWorkOrderMapper.selectCidWorkOrderList(cidWorkOrder);
    }

    /**
     * 新增工单
     * 
     * @param cidWorkOrder 工单
     * @return 结果
     */
    @Override
    public int insertCidWorkOrder(CidWorkOrder cidWorkOrder)
    {
        cidWorkOrder.setCreateTime(DateUtils.getNowDate());
        return cidWorkOrderMapper.insertCidWorkOrder(cidWorkOrder);
    }

    /**
     * 修改工单
     * 
     * @param cidWorkOrder 工单
     * @return 结果
     */
    @Override
    public int updateCidWorkOrder(CidWorkOrder cidWorkOrder)
    {
        cidWorkOrder.setUpdateTime(DateUtils.getNowDate());
        return cidWorkOrderMapper.updateCidWorkOrder(cidWorkOrder);
    }

    /**
     * 批量删除工单
     * 
     * @param workOrderIds 需要删除的工单主键
     * @return 结果
     */
    @Override
    public int deleteCidWorkOrderByWorkOrderIds(Long[] workOrderIds)
    {
        return cidWorkOrderMapper.deleteCidWorkOrderByWorkOrderIds(workOrderIds);
    }

    /**
     * 删除工单信息
     * 
     * @param workOrderId 工单主键
     * @return 结果
     */
    @Override
    public int deleteCidWorkOrderByWorkOrderId(Long workOrderId)
    {
        return cidWorkOrderMapper.deleteCidWorkOrderByWorkOrderId(workOrderId);
    }


    /**
     * 获取未完成工单列表
     *
     * @param cidWorkOrder
     * @return
     */
    @Override
    public List<CidWorkOrder> selectCidWorkOrderUnFinishList(CidWorkOrder cidWorkOrder) {
        return cidWorkOrderMapper.selectCidWorkOrderUnFinishList(cidWorkOrder);
    }

    /**
     * 获取已完成工单列表
     *
     * @param cidWorkOrder
     * @return
     */
    @Override
    public List<CidWorkOrder> selectCidWorkOrderFinishList(CidWorkOrder cidWorkOrder) {
        return cidWorkOrderMapper.selectCidWorkOrderFinishList(cidWorkOrder);
    }
}
