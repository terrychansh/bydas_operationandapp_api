package com.benyi.energy.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidData;
import com.benyi.energy.domain.CidDayData;

/**
 * 发电信息Service接口
 *
 * @author wuqiguang
 * @date 2022-09-15
 */
public interface ICidDayDataService
{



    /**
     * 查询发电信息
     *
     * @param id 发电信息主键
     * @return 发电信息
     */
    public CidDayData selectCidDayDataById(Long id);

    /**
     * 查询发电信息列表
     *
     * @param cidData 发电信息
     * @return 发电信息集合
     */
    public List<CidDayData> selectCidDayDataList(CidDayData cidData);

    /**
     * 新增发电信息
     *
     * @param cidData 发电信息
     * @return 结果
     */
    public int insertCidDayData(CidDayData cidData);

    /**
     * 修改发电信息
     *
     * @param cidDayData 发电信息
     * @return 结果
     */
    public int updateCidDayData(CidDayData cidDayData);


    /**
     * 电站迁移与微逆修改绑定关系，设置历史数据delFlag和isRemove =1
     * @param cidDayData
     * @return
     */
    public int updateForRemoveAndDel(CidDayData cidDayData);


    /**
     * 电站迁移与微逆修改绑定关系，设置历史数据delFlag和isRemove =1
     * @param cidDayData
     * @return
     */
    public int updateBatchForRemoveAndDel (CidDayData cidDayData);

    /**
     * 批量删除发电信息
     *
     * @param ids 需要删除的发电信息主键集合
     * @return 结果
     */
    public int deleteCidDayDataByIds(Long[] ids);

    /**
     * 删除发电信息信息
     *
     * @param id 发电信息主键
     * @return 结果
     */
    public int deleteCidDayDataById(Long id);


    /**
     * 获取今年的总发电量（除去今日）
     * @return
     */
    public List<CidDayData> selectCurrentYearEnergy();

    /**
     * 获取本月的总发电量（除去今日）
     * @return
     */
    public List<CidDayData> selectCurrentMonthEnergy();

    /**
     * 更新昨日发电量
     * @param cidDayData
     * @return
     */
    public int updateCidDayDataEnergy(CidDayData cidDayData);

    /**
     * 获取今日发电量信息
     * @return
     */
    public List<CidDayData> selectTodayData();

    /**
     * 根据cid，vid，roadtype查询今日发电表信息
     * @param cidDayData
     * @return
     */
    public CidDayData selectTodayDataByCidVidRoad(CidDayData cidDayData);

    /**
     * 根据cid，vid，loop，date 更新发电日数据
     * @param cidDayData
     * @return
     */
    public int updateByCidVidLoopDate(CidDayData cidDayData);



    /**
     * 根据cid，vid，loop，date 获取月发电量
     * @param updateDate YYYY-mm
     * @return
     */
    public List<CidDayData> selectCidVidLoopSumEnergyWithMonth(String updateDate);

    /**
     * 根据cid，vid，loop，date 获取月发电量
     * @param updateDate  YYYY
     * @return
     */
    public List<CidDayData> selectCidVidLoopSumEnergyWithYear(String updateDate);

    /**
     * 根据cid，vid，loop，date 获取月发电量
     * @return
     */
    public List<CidDayData> selectCidVidLoopSumEnergyWithCount();


    /**
     * 根据cid vid loop ,date 查询是否已存在数据
     * @param cid
     * @param vid
     * @param roadType
     * @param searchDate
     * @return
     */
    public Long selectForValInsert(String cid,String vid ,String roadType,String searchDate);


    /**
     * cid vid loop ,date 月发电量
     * @param cid
     * @param vid
     * @param roadType
     * @param searchDate
     * @return
     */
    public List<CidDayData> selectCidVidEnergyByDateAndType(String cid,String vid ,String roadType,String searchDate,String searchType);

    /**
     * dept lof app energy数据 10天内
     * @param deptId
     * @return
     */
    public List<CidDayData> selectLofEnergy(Long deptId);
}
