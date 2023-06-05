package com.benyi.energy.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.benyi.energy.domain.CidData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.benyi.energy.mapper.CidDayDataMapper;
import com.benyi.energy.domain.CidDayData;
import com.benyi.energy.service.ICidDayDataService;

/**
 * 发电信息Service业务层处理
 *
 * @author wuqiguang
 * @date 2022-09-15
 */
@Service
public class CidDayDataServiceImpl implements ICidDayDataService
{
    @Autowired
    private CidDayDataMapper cidDayDataMapper;




    /**
     * 查询发电信息
     *
     * @param id 发电信息主键
     * @return 发电信息
     */
    @Override
    public CidDayData selectCidDayDataById(Long id)
    {
        return cidDayDataMapper.selectCidDayDataById(id);
    }

    /**
     * 查询发电信息列表
     *
     * @param cidDayData 发电信息
     * @return 发电信息
     */
    @Override
    public List<CidDayData> selectCidDayDataList(CidDayData cidDayData)
    {
        return cidDayDataMapper.selectCidDayDataList(cidDayData);
    }

    /**
     * 新增发电信息
     *
     * @param cidDayData 发电信息
     * @return 结果
     */
    @Override
    public int insertCidDayData(CidDayData cidDayData)
    {
        return cidDayDataMapper.insertCidDayData(cidDayData);
    }

    /**
     * 修改发电信息
     *
     * @param cidDayData 发电信息
     * @return 结果
     */
    @Override
    public int updateCidDayData(CidDayData cidDayData)
    {
        return cidDayDataMapper.updateCidDayData(cidDayData);
    }

    /**
     * 电站迁移与微逆修改绑定关系，设置历史数据delFlag和isRemove =1
     *
     * @param cidDayData
     * @return
     */
    @Override
    public int updateForRemoveAndDel(CidDayData cidDayData) {
        return cidDayDataMapper.updateForRemoveAndDel(cidDayData);
    }

    /**
     * 电站迁移与微逆修改绑定关系，设置历史数据delFlag和isRemove =1
     *
     * @param cidDayData
     * @return
     */
    @Override
    public int updateBatchForRemoveAndDel(CidDayData cidDayData) {
        return cidDayDataMapper.updateBatchForRemoveAndDel(cidDayData);
    }

    /**
     * 批量删除发电信息
     *
     * @param ids 需要删除的发电信息主键
     * @return 结果
     */
    @Override
    public int deleteCidDayDataByIds(Long[] ids)
    {
        return cidDayDataMapper.deleteCidDayDataByIds(ids);
    }

    /**
     * 删除发电信息信息
     *
     * @param id 发电信息主键
     * @return 结果
     */
    @Override
    public int deleteCidDayDataById(Long id)
    {
        return cidDayDataMapper.deleteCidDayDataById(id);
    }


    /**
     * 获取今年的总发电量（除去今日）
     *
     * @return
     */
    @Override
    public List<CidDayData> selectCurrentYearEnergy() {
        return cidDayDataMapper.selectCurrentYearEnergy();
    }

    /**
     * 获取本月的总发电量（除去今日）
     *
     * @return
     */
    @Override
    public List<CidDayData> selectCurrentMonthEnergy() {
        return cidDayDataMapper.selectCurrentMonthEnergy();
    }

    /**
     * 更新昨日发电量
     *
     * @param cidDayData
     * @return
     */
    @Override
    public int updateCidDayDataEnergy(CidDayData cidDayData) {
        return cidDayDataMapper.updateCidDayDataEnergy(cidDayData);
    }

    /**
     * 获取今日发电量信息
     * @return
     */
    @Override
    public List<CidDayData> selectTodayData(){
        return cidDayDataMapper.selectTodayData();
    }

    /**
     * 根据cid，vid，roadtype查询今日发电表信息
     *
     * @param cidDayData
     * @return
     */
    @Override
    public CidDayData selectTodayDataByCidVidRoad(CidDayData cidDayData) {
        return cidDayDataMapper.selectTodayDataByCidVidRoad(cidDayData);
    }

    /**
     * 根据cid，vid，loop，date 更新发电日数据
     * @param cidDayData
     * @return
     */
    @Override
    public int updateByCidVidLoopDate(CidDayData cidDayData){
        return cidDayDataMapper.updateByCidVidLoopDate(cidDayData);
    }

    /**
     * 根据cid，vid，loop，date 获取月发电量
     *
     * @param updateDate YYYY-mm
     * @return
     */
    @Override
    public List<CidDayData> selectCidVidLoopSumEnergyWithMonth(String updateDate) {

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("date", updateDate);
        return cidDayDataMapper.selectCidVidLoopSumEnergyWithMonth(paramMap);
    }

    /**
     * 根据cid，vid，loop，date 获取月发电量
     *
     * @param updateDate YYYY
     * @return
     */
    @Override
    public List<CidDayData> selectCidVidLoopSumEnergyWithYear(String updateDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("date", updateDate);
        return cidDayDataMapper.selectCidVidLoopSumEnergyWithYear(paramMap);
    }

    /**
     * 根据cid，vid，loop，date 获取月发电量
     *
     * @return
     */
    @Override
    public List<CidDayData> selectCidVidLoopSumEnergyWithCount() {
        return cidDayDataMapper.selectCidVidLoopSumEnergyWithCount();
    }

    /**
     * 根据cid vid loop ,date 查询是否已存在数据
     *
     * @param cid
     * @param vid
     * @param roadType
     * @param searchDate
     * @return
     */
    @Override
    public Long selectForValInsert(String cid, String vid, String roadType, String searchDate) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("roadType",roadType);
        paramMap.put("searchDate",searchDate);
        return cidDayDataMapper.selectForValInsert(paramMap);
    }

    /**
     * cid vid loop ,date 月发电量
     *
     * @param cid
     * @param vid
     * @param roadType
     * @param searchDate
     * @return
     */
    @Override
    public List<CidDayData> selectCidVidEnergyByDateAndType(String cid, String vid, String roadType, String searchDate,String searchType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("cid",cid);
        paramMap.put("vid",vid);
        paramMap.put("searchDate",searchDate);
        if(searchType.equals("m")){
            return cidDayDataMapper.getCidVidMonthByPidAndDate(paramMap);
        }else if(searchType.equals("y")){
            return cidDayDataMapper.getCidVidYearByPidAndDate(paramMap);
        }else if(searchType.equals("a")){
            return  cidDayDataMapper.getCidVidAllByPidAndDate(paramMap);
        }

        return null;
    }

    /**
     * dept lof app energy数据 10天内
     *
     * @param deptId
     * @return
     */
    @Override
    public List<CidDayData> selectLofEnergy(Long deptId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("deptId",deptId);
        return cidDayDataMapper.selectLofEnergy(paramMap);
    }
}
