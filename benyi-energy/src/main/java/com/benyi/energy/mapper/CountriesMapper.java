package com.benyi.energy.mapper;

import java.util.List;
import com.benyi.energy.domain.Countries;

/**
 * 国家Mapper接口
 *
 * @author wuqiguang
 * @date 2022-08-20
 */
public interface CountriesMapper
{
    /**
     * 查询国家
     *
     * @param id 国家主键
     * @return 国家
     */
    public Countries selectCountriesById(Integer id);

    /**
     * 查询国家
     *
     * @param name 国家英文名
     * @return 国家
     */
    public Countries selectCountriesByName(String name);

    /**
     * 查询国家列表
     *
     * @param countries 国家
     * @return 国家集合
     */
    public List<Countries> selectCountriesList(Countries countries);

    /**
     * 新增国家
     *
     * @param countries 国家
     * @return 结果
     */
    public int insertCountries(Countries countries);

    /**
     * 修改国家
     *
     * @param countries 国家
     * @return 结果
     */
    public int updateCountries(Countries countries);

    /**
     * 删除国家
     *
     * @param id 国家主键
     * @return 结果
     */
    public int deleteCountriesById(Integer id);

    /**
     * 批量删除国家
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCountriesByIds(Integer[] ids);
}
