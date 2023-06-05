package com.benyi.energy.mapper;

import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidDayHour;
import org.apache.ibatis.annotations.Mapper;

/**
 * 发电信息(日-小时)Mapper接口
 *
 * @author wuqiguang
 * @date 2022-10-10
 */
@Mapper
public interface CidDayHourMapper
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
     * 修改发电信息(日-小时) cid ,vid , roadtype ,date
     *
     * @param cidDayHour 发电信息(日-小时)
     * @return 结果
     */
    public int updateCidDayHourByCidVidRoadDate(CidDayHour cidDayHour);

    /**
     * 删除发电信息(日-小时)
     *
     * @param id 发电信息(日-小时)主键
     * @return 结果
     */
    public int deleteCidDayHourById(Long id);

    /**
     * 批量删除发电信息(日-小时)
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCidDayHourByIds(Long[] ids);

    /**
     * 插入前查询时间是否已存在
     * @param map
     * @return
     */
    public List<Long> selectValDateHour(Map<String, Object> map);
}
