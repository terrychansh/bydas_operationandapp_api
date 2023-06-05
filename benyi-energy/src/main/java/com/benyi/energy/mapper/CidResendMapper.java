package com.benyi.energy.mapper;

import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidRelation;
import com.benyi.energy.domain.CidResend;

/**
 * 协议补发Mapper接口
 * 
 * @author wuqiguang
 * @date 2022-09-02
 */
public interface CidResendMapper 
{


    /**
     * 查询协议补发
     * 
     * @param resendId 协议补发主键
     * @return 协议补发
     */
    public CidResend selectCidResendByResendId(Long resendId);

    /**
     * 查询协议补发列表
     * 
     * @param cidResend 协议补发
     * @return 协议补发集合
     */
    public List<CidResend> selectCidResendList(CidResend cidResend);

    /**
     * 根据单位查询补发信息
     * @param map
     * @return
     */
    public List<CidResend> selectResendByDeptId(Map<String, Object> map);

    /**
     * 新增协议补发
     * 
     * @param cidResend 协议补发
     * @return 结果
     */
    public int insertCidResend(CidResend cidResend);

    /**
     * 修改协议补发
     * 
     * @param cidResend 协议补发
     * @return 结果
     */
    public int updateCidResend(CidResend cidResend);

    /**
     * 首页确认后，直接修改状态
     * @param deptId
     * @return
     */
    public int updateCidResendConfirm(Long deptId);

    /**
     * 删除协议补发
     * 
     * @param resendId 协议补发主键
     * @return 结果
     */
    public int deleteCidResendByResendId(Long resendId);

    /**
     * 批量删除协议补发
     * 
     * @param resendIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCidResendByResendIds(Long[] resendIds);


    /**
     * 根据电站查询是否有补发
     * @param param
     * @return
     */
    public List<CidResend> selectResendValByDeptId(Map<String,Object> param);
}
