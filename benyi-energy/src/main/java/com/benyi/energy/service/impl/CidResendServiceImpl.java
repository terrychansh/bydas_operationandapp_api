package com.benyi.energy.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.benyi.energy.mapper.CidResendMapper;
import com.benyi.energy.domain.CidResend;
import com.benyi.energy.service.ICidResendService;

/**
 * 协议补发Service业务层处理
 * 
 * @author wuqiguang
 * @date 2022-09-02
 */
@Service
public class CidResendServiceImpl implements ICidResendService 
{
    @Autowired
    private CidResendMapper cidResendMapper;


    /**
     * 查询协议补发
     * 
     * @param resendId 协议补发主键
     * @return 协议补发
     */
    @Override
    public CidResend selectCidResendByResendId(Long resendId)
    {
        return cidResendMapper.selectCidResendByResendId(resendId);
    }

    /**
     * 查询协议补发列表
     * 
     * @param cidResend 协议补发
     * @return 协议补发
     */
    @Override
    public List<CidResend> selectCidResendList(CidResend cidResend)
    {
        return cidResendMapper.selectCidResendList(cidResend);
    }


    /**
     * 根据单位查询补发信息
     *
     * @param deptId           机构Id
     * @param cid              网关(like)
     * @param powerStationName 电站名称（like）
     * @param powerStationId   电站Id
     * @return
     */
    @Override
    public List<CidResend> selectResendByDeptId(Long deptId, String cid, String powerStationName, Long powerStationId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId", deptId);
        paramMap.put("cid", cid);
        paramMap.put("powerStationName", powerStationName);
        paramMap.put("powerStationId", powerStationId);
        return cidResendMapper.selectResendByDeptId(paramMap);
    }

    /**
     * 新增协议补发
     * 
     * @param cidResend 协议补发
     * @return 结果
     */
    @Override
    public int insertCidResend(CidResend cidResend)
    {
        return cidResendMapper.insertCidResend(cidResend);
    }

    /**
     * 修改协议补发
     * 
     * @param cidResend 协议补发
     * @return 结果
     */
    @Override
    public int updateCidResend(CidResend cidResend)
    {
        return cidResendMapper.updateCidResend(cidResend);
    }

    /**
     * 首页确认后，直接修改状态
     *
     * @param deptId
     * @return
     */
    @Override
    public int updateCidResendConfirm(Long deptId) {
        return cidResendMapper.updateCidResendConfirm(deptId);
    }

    /**
     * 批量删除协议补发
     * 
     * @param resendIds 需要删除的协议补发主键
     * @return 结果
     */
    @Override
    public int deleteCidResendByResendIds(Long[] resendIds)
    {
        return cidResendMapper.deleteCidResendByResendIds(resendIds);
    }

    /**
     * 删除协议补发信息
     * 
     * @param resendId 协议补发主键
     * @return 结果
     */
    @Override
    public int deleteCidResendByResendId(Long resendId)
    {
        return cidResendMapper.deleteCidResendByResendId(resendId);
    }

    /**
     * 根据电站查询是否有补发
     *
     * @param deptId
     * @return
     */
    @Override
    public List<CidResend> selectResendValByDeptId(Long deptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        return cidResendMapper.selectResendValByDeptId(paramMap);
    }
}
