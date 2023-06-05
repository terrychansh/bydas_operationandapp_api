package com.benyi.energy.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.benyi.energy.mapper.CidDayHourMapper;
import com.benyi.energy.domain.CidDayHour;
import com.benyi.energy.service.ICidDayHourService;

/**
 * 发电信息(日-小时)Service业务层处理
 *
 * @author wuqiguang
 * @date 2022-10-10
 */
@Service
public class CidDayHourServiceImpl implements ICidDayHourService
{
    @Autowired
    private CidDayHourMapper cidDayHourMapper;

    /**
     * 查询发电信息(日-小时)
     *
     * @param id 发电信息(日-小时)主键
     * @return 发电信息(日-小时)
     */
    @Override
    public CidDayHour selectCidDayHourById(Long id)
    {
        return cidDayHourMapper.selectCidDayHourById(id);
    }

    /**
     * 查询发电信息(日-小时)列表
     *
     * @param cidDayHour 发电信息(日-小时)
     * @return 发电信息(日-小时)
     */
    @Override
    public List<CidDayHour> selectCidDayHourList(CidDayHour cidDayHour)
    {
        return cidDayHourMapper.selectCidDayHourList(cidDayHour);
    }

    /**
     * 新增发电信息(日-小时)
     *
     * @param cidDayHour 发电信息(日-小时)
     * @return 结果
     */
    @Override
    public int insertCidDayHour(CidDayHour cidDayHour)
    {
        return cidDayHourMapper.insertCidDayHour(cidDayHour);
    }

    /**
     * 修改发电信息(日-小时)
     *
     * @param cidDayHour 发电信息(日-小时)
     * @return 结果
     */
    @Override
    public int updateCidDayHour(CidDayHour cidDayHour)
    {
        return cidDayHourMapper.updateCidDayHour(cidDayHour);
    }

    /**
     * 批量删除发电信息(日-小时)
     *
     * @param ids 需要删除的发电信息(日-小时)主键
     * @return 结果
     */
    @Override
    public int deleteCidDayHourByIds(Long[] ids)
    {
        return cidDayHourMapper.deleteCidDayHourByIds(ids);
    }

    /**
     * 删除发电信息(日-小时)信息
     *
     * @param id 发电信息(日-小时)主键
     * @return 结果
     */
    @Override
    public int deleteCidDayHourById(Long id)
    {
        return cidDayHourMapper.deleteCidDayHourById(id);
    }

    /**
     * 插入前查询时间是否已存在
     *
     * @param cid
     * @param vid
     * @param roadType
     * @param searchDate
     * @return
     */
    @Override
    public List<Long> selectValDateHour(String cid, String vid, String roadType, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid", cid);
        paramMap.put("vid", vid);
        paramMap.put("roadType",roadType);
        paramMap.put("searchDate",searchDate);
        return cidDayHourMapper.selectValDateHour(paramMap);
    }

    /**
     * 修改发电信息(日-小时) cid ,vid , roadtype ,date
     *
     * @param cidDayHour 发电信息(日-小时)
     * @return 结果
     */
    @Override
    public int updateCidDayHourByCidVidRoadDate(CidDayHour cidDayHour) {
        return cidDayHourMapper.updateCidDayHourByCidVidRoadDate(cidDayHour);
    }
}
