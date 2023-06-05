package com.benyi.energy.service;

import java.util.List;
import com.benyi.energy.domain.CidDayHour;

/**
 * 发电信息(日-小时)Service接口
 *
 * @author wuqiguang
 * @date 2022-10-10
 */
public interface ICidDayHourService
{
    /**
     * 查询发电信息(日-小时)
     *
     * @param id 发电信息(日-小时)主键
     * @return 发电信息(日-小时)
     */
    public CidDayHour selectCidDayHourById(Long id);

    /**
     * 查询发电信息(日-小时)列表
     *
     * @param cidDayHour 发电信息(日-小时)
     * @return 发电信息(日-小时)集合
     */
    public List<CidDayHour> selectCidDayHourList(CidDayHour cidDayHour);

    /**
     * 新增发电信息(日-小时)
     *
     * @param cidDayHour 发电信息(日-小时)
     * @return 结果
     */
    public int insertCidDayHour(CidDayHour cidDayHour);

    /**
     * 修改发电信息(日-小时)
     *
     * @param cidDayHour 发电信息(日-小时)
     * @return 结果
     */
    public int updateCidDayHour(CidDayHour cidDayHour);

    /**
     * 批量删除发电信息(日-小时)
     *
     * @param ids 需要删除的发电信息(日-小时)主键集合
     * @return 结果
     */
    public int deleteCidDayHourByIds(Long[] ids);

    /**
     * 删除发电信息(日-小时)信息
     *
     * @param id 发电信息(日-小时)主键
     * @return 结果
     */
    public int deleteCidDayHourById(Long id);

    /**
     * 插入前查询时间是否已存在
     * @param cid
     * @param vid
     * @param roadType
     * @param searchDate
     * @return
     */
    public List<Long> selectValDateHour(String cid,String vid,String roadType,String searchDate);

    /**
     * 修改发电信息(日-小时) cid ,vid , roadtype ,date
     *
     * @param cidDayHour 发电信息(日-小时)
     * @return 结果
     */
    public int updateCidDayHourByCidVidRoadDate(CidDayHour cidDayHour);
}
