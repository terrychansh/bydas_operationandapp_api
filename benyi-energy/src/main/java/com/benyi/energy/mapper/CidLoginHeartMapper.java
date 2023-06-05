package com.benyi.energy.mapper;

import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidLoginHeart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登陆、心跳入库Mapper接口
 *
 * @author wuqiguang
 * @date 2022-09-09
 */
@Mapper
public interface CidLoginHeartMapper
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

    public List<CidLoginHeart> selectCidLoginHeartListByQuery(Map<String, Object> map);

    public List<CidLoginHeart> selectLastUpdateTime(String cid);

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
     * 删除登陆、心跳入库
     *
     * @param id 登陆、心跳入库主键
     * @return 结果
     */
    public int deleteCidLoginHeartById(Long id);

    /**
     * 批量删除登陆、心跳入库
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCidLoginHeartByIds(Long[] ids);

    /**
     * 根据cid 获取最后一调记录
     * @param cid
     * @return
     */
    public Long selectMaxLoginHeartByCid(String cid);
}
