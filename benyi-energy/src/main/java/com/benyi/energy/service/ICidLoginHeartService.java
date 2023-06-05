package com.benyi.energy.service;

import java.util.List;
import com.benyi.energy.domain.CidLoginHeart;

/**
 * 登陆、心跳入库Service接口
 *
 * @author wuqiguang
 * @date 2022-09-09
 */
public interface ICidLoginHeartService
{
    /**
     * 查询登陆、心跳入库
     *
     * @param id 登陆、心跳入库主键
     * @return 登陆、心跳入库
     */
    public CidLoginHeart selectCidLoginHeartById(Long id);

    /**
     * 查询登陆、心跳入库列表
     *
     * @param cidLoginHeart 登陆、心跳入库
     * @return 登陆、心跳入库集合
     */
    public List<CidLoginHeart> selectCidLoginHeartList(CidLoginHeart cidLoginHeart);

    /**
     * 查询登陆、心跳入库列表
     *
     * @return 登陆、心跳入库集合
     */
    public List<CidLoginHeart> selectCidLoginHeartListByQuery(String cid,String heartLoginType,String searchDate,Long deptId,Long powerStationId);

    /**
     * 新增登陆、心跳入库
     *
     * @param cidLoginHeart 登陆、心跳入库
     * @return 结果
     */
    public int insertCidLoginHeart(CidLoginHeart cidLoginHeart);

    /**
     * 修改登陆、心跳入库
     *
     * @param cidLoginHeart 登陆、心跳入库
     * @return 结果
     */
    public int updateCidLoginHeart(CidLoginHeart cidLoginHeart);

    /**
     * 批量删除登陆、心跳入库
     *
     * @param ids 需要删除的登陆、心跳入库主键集合
     * @return 结果
     */
    public int deleteCidLoginHeartByIds(Long[] ids);

    /**
     * 删除登陆、心跳入库信息
     *
     * @param id 登陆、心跳入库主键
     * @return 结果
     */
    public int deleteCidLoginHeartById(Long id);

    /**
     * 拿最后一条登陆和心跳记录
     * @param cid
     * @return
     */
    public List<CidLoginHeart> selectLastUpdateTime(String cid);


    /**
     * 根据cid 获取最后一调记录
     * @param cid
     * @return
     */
    public Long selectMaxLoginHeartByCid(String cid);
}
