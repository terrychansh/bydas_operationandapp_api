package com.benyi.energy.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.benyi.energy.mapper.CidLoginHeartMapper;
import com.benyi.energy.domain.CidLoginHeart;
import com.benyi.energy.service.ICidLoginHeartService;

/**
 * 登陆、心跳入库Service业务层处理
 *
 * @author wuqiguang
 * @date 2022-09-09
 */
@Service
public class CidLoginHeartServiceImpl implements ICidLoginHeartService
{
    @Autowired
    private CidLoginHeartMapper cidLoginHeartMapper;

    /**
     * 查询登陆、心跳入库
     *
     * @param id 登陆、心跳入库主键
     * @return 登陆、心跳入库
     */
    @Override
    public CidLoginHeart selectCidLoginHeartById(Long id)
    {
        return cidLoginHeartMapper.selectCidLoginHeartById(id);
    }

    /**
     * 查询登陆、心跳入库列表
     *
     * @param cidLoginHeart 登陆、心跳入库
     * @return 登陆、心跳入库
     */
    @Override
    public List<CidLoginHeart> selectCidLoginHeartList(CidLoginHeart cidLoginHeart)
    {
        return cidLoginHeartMapper.selectCidLoginHeartList(cidLoginHeart);
    }


    /**
     * 查询登陆、心跳入库列表
     *
     * @param cid
     * @param heartLoginType
     * @param deptId
     * @param powerStationId
     * @return 登陆、心跳入库集合
     */
    @Override
    public List<CidLoginHeart> selectCidLoginHeartListByQuery(String cid, String heartLoginType, String searchDate , Long deptId, Long powerStationId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("heartLoginType", heartLoginType);
        paramMap.put("deptId", deptId);
        paramMap.put("powerStationId", powerStationId);
        paramMap.put("searchDate",searchDate);
        return cidLoginHeartMapper.selectCidLoginHeartListByQuery(paramMap);
    }

    /**
     * 新增登陆、心跳入库
     *
     * @param cidLoginHeart 登陆、心跳入库
     * @return 结果
     */
    @Override
    public int insertCidLoginHeart(CidLoginHeart cidLoginHeart)
    {
        return cidLoginHeartMapper.insertCidLoginHeart(cidLoginHeart);
    }

    /**
     * 修改登陆、心跳入库
     *
     * @param cidLoginHeart 登陆、心跳入库
     * @return 结果
     */
    @Override
    public int updateCidLoginHeart(CidLoginHeart cidLoginHeart)
    {
        return cidLoginHeartMapper.updateCidLoginHeart(cidLoginHeart);
    }

    /**
     * 批量删除登陆、心跳入库
     *
     * @param ids 需要删除的登陆、心跳入库主键
     * @return 结果
     */
    @Override
    public int deleteCidLoginHeartByIds(Long[] ids)
    {
        return cidLoginHeartMapper.deleteCidLoginHeartByIds(ids);
    }

    /**
     * 删除登陆、心跳入库信息
     *
     * @param id 登陆、心跳入库主键
     * @return 结果
     */
    @Override
    public int deleteCidLoginHeartById(Long id)
    {
        return cidLoginHeartMapper.deleteCidLoginHeartById(id);
    }

    /**
     * 拿最后一条登陆和心跳记录
     *
     * @param cid
     * @return
     */
    @Override
    public List<CidLoginHeart> selectLastUpdateTime(String cid) {
        return cidLoginHeartMapper.selectLastUpdateTime(cid);
    }

    /**
     * 根据cid 获取最后一调记录
     *
     * @param cid
     * @return
     */
    @Override
    public Long selectMaxLoginHeartByCid(String cid) {
        return cidLoginHeartMapper.selectMaxLoginHeartByCid(cid);
    }
}
