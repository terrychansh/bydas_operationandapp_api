package com.benyi.energy.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.benyi.energy.mapper.StatesMapper;
import com.benyi.energy.domain.States;
import com.benyi.energy.service.IStatesService;

/**
 * 洲、省Service业务层处理
 *
 * @author wuqiguang
 * @date 2022-08-20
 */
@Service
public class StatesServiceImpl implements IStatesService
{
    @Autowired
    private StatesMapper statesMapper;

    /**
     * 查询洲、省
     *
     * @param id 洲、省主键
     * @return 洲、省
     */
    @Override
    public States selectStatesById(Integer id)
    {
        return statesMapper.selectStatesById(id);
    }

    /**
     * 查询洲、省列表
     *
     * @param states 洲、省
     * @return 洲、省
     */
    @Override
    public List<States> selectStatesList(States states)
    {
        return statesMapper.selectStatesList(states);
    }

    /**
     * 新增洲、省
     *
     * @param states 洲、省
     * @return 结果
     */
    @Override
    public int insertStates(States states)
    {
        return statesMapper.insertStates(states);
    }

    /**
     * 修改洲、省
     *
     * @param states 洲、省
     * @return 结果
     */
    @Override
    public int updateStates(States states)
    {
        return statesMapper.updateStates(states);
    }

    /**
     * 批量删除洲、省
     *
     * @param ids 需要删除的洲、省主键
     * @return 结果
     */
    @Override
    public int deleteStatesByIds(Integer[] ids)
    {
        return statesMapper.deleteStatesByIds(ids);
    }

    /**
     * 删除洲、省信息
     *
     * @param id 洲、省主键
     * @return 结果
     */
    @Override
    public int deleteStatesById(Integer id)
    {
        return statesMapper.deleteStatesById(id);
    }
}
