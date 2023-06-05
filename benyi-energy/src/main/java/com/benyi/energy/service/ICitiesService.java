package com.benyi.energy.service;

import java.util.List;
import com.benyi.energy.domain.Cities;

/**
 * 城市Service接口
 *
 * @author wuqiguang
 * @date 2022-08-20
 */
public interface ICitiesService
{
    /**
     * 查询城市
     *
     * @param id 城市主键
     * @return 城市
     */
    public Cities selectCitiesById(Integer id);

    /**
     * 查询城市列表
     *
     * @param cities 城市
     * @return 城市集合
     */
    public List<Cities> selectCitiesList(Cities cities);

    /**
     * 新增城市
     *
     * @param cities 城市
     * @return 结果
     */
    public int insertCities(Cities cities);

    /**
     * 修改城市
     *
     * @param cities 城市
     * @return 结果
     */
    public int updateCities(Cities cities);

    /**
     * 批量删除城市
     *
     * @param ids 需要删除的城市主键集合
     * @return 结果
     */
    public int deleteCitiesByIds(Integer[] ids);

    /**
     * 删除城市信息
     *
     * @param id 城市主键
     * @return 结果
     */
    public int deleteCitiesById(Integer id);
}
