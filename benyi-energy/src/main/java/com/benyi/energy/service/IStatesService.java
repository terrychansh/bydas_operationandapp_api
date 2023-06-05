package com.benyi.energy.service;

import java.util.List;
import com.benyi.energy.domain.States;

/**
 * 洲、省Service接口
 *
 * @author wuqiguang
 * @date 2022-08-20
 */
public interface IStatesService
{
    /**
     * 查询洲、省
     *
     * @param id 洲、省主键
     * @return 洲、省
     */
    public States selectStatesById(Integer id);

    /**
     * 查询洲、省列表
     *
     * @param states 洲、省
     * @return 洲、省集合
     */
    public List<States> selectStatesList(States states);

    /**
     * 新增洲、省
     *
     * @param states 洲、省
     * @return 结果
     */
    public int insertStates(States states);

    /**
     * 修改洲、省
     *
     * @param states 洲、省
     * @return 结果
     */
    public int updateStates(States states);

    /**
     * 批量删除洲、省
     *
     * @param ids 需要删除的洲、省主键集合
     * @return 结果
     */
    public int deleteStatesByIds(Integer[] ids);

    /**
     * 删除洲、省信息
     *
     * @param id 洲、省主键
     * @return 结果
     */
    public int deleteStatesById(Integer id);
}
