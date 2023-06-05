package com.benyi.energy.task;

import com.alibaba.fastjson2.JSON;
import com.benyi.energy.domain.*;
import com.benyi.energy.service.*;
import com.sun.jna.platform.win32.WinNT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component("energyTask")
public class CidEnergyTask {

    @Autowired
    private ICidDataService cidDataService;

    @Autowired
    private ICidRelationService relationService;

    @Autowired
    private ICidRelationRoadService relationRoadService;

    @Autowired
    private ICidPowerstationinfoService powerstationinfoService;

    @Autowired
    private ICidDayDataService dayDataService;

    @Autowired
    private ICidDayHourService hourService;

    @Autowired
    private ICidLoginHeartService heartService;


    /**
     * 更新电站今日发电量与发电功率
     */
    public void updatePowerTodayPowerAndEnergy() {

        CidPowerstationinfo queryPlant = new CidPowerstationinfo();
        queryPlant.setDelFlag("0");
        List<CidPowerstationinfo> powerstationinfoList = powerstationinfoService.selectCidPowerstationinfoList(queryPlant);

        for (CidPowerstationinfo plant : powerstationinfoList) {
            List<Long> idsList = new ArrayList<Long>();
            List<Long> idsMinList = new ArrayList<Long>();
            float currentPower = 0f;
            float sumMaxEnergy = 0f;
            float sumMinEnergy = 0f;
            for (Object obj : cidDataService.selectPlantCurrentPowerId(plant.getId())) {
                String s = JSON.toJSONString(obj);
                CidData cidData = JSON.parseObject(s, CidData.class);
                System.out.println(cidData.getId());
                idsList.add(cidData.getId());
            }

            for (Object obj : cidDataService.selectPlantMinId(plant.getId())) {
                String s = JSON.toJSONString(obj);
                CidData cidData = JSON.parseObject(s, CidData.class);
                System.out.println(cidData.getId());
                idsMinList.add(cidData.getId());
            }

            Long[] ids = new Long[idsList.size()];
            for (int i = 0; i < idsList.size(); i++) {
                ids[i] = idsList.get(i);
            }
            Long[] idsMin = new Long[idsMinList.size()];
            for (int i = 0; i < idsMinList.size(); i++) {
                idsMin[i] = idsMinList.get(i);
            }
            for (Object obj : cidDataService.selectPlantCurrentPowerByIds(ids)) {
                String s = JSON.toJSONString(obj);
                CidData cidData = JSON.parseObject(s, CidData.class);
                currentPower += Float.parseFloat(cidData.getPower());
                sumMaxEnergy += Float.parseFloat(cidData.getEnergy());
            }
            for (Object obj : cidDataService.selectPlantCurrentPowerByIds(idsMin)) {
                String s = JSON.toJSONString(obj);
                CidData cidData = JSON.parseObject(s, CidData.class);
                sumMinEnergy += Float.parseFloat(cidData.getEnergy());
            }
//            plant.setEnergyCurrentPower(String.valueOf(currentPower));
            plant.setEnergyTotalDay(String.valueOf(sumMaxEnergy - sumMinEnergy));
            System.out.println("===CidEnergyTask updatePowerTodayPowerAndEnergy currentPower:"+currentPower);

            powerstationinfoService.updateCidPowerstationinfo(plant);
        }
        //获取今日发电量
        System.out.println("===CidEnergyTask updatePowerTodayPowerAndEnergy end");
    }


    public void analyseCurrentDayEnergy() {

//        System.out.println("===CidEnergyTask analyseCurrentDayEnergy 000000000");
//
//        List<CidPowerstationinfo> cidPowerstationinfoList =powerstationinfoService.selectAllCidPowerstationinfoTimezoneList();
//        Date now=new Date();
//        Integer untZoneMinute=0;
//        for (int i=0;i<cidPowerstationinfoList.size();i++){
//
//            System.out.println("===CidEnergyTask cidPowerstationinfo:"+cidPowerstationinfoList.get(i).getId());
//
//            String ss = JSON.toJSONString(cidPowerstationinfoList.get(i));
//            CidPowerstationinfo itemPowerstation = JSON.parseObject(ss, CidPowerstationinfo.class);
//
//            String utcZoneStr = String.valueOf(itemPowerstation.getEnergyTimeZone());
//            untZoneMinute = getUTCZoneMinute(utcZoneStr);
//
//            if (untZoneMinute==null)
//                untZoneMinute=0;
//            Date nowUTC=new Date(now.getTime()+untZoneMinute*60*1000);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//            String startDate = sdf.format(nowUTC) + " 00:00:00";
//            String endDate = sdf.format(nowUTC) + " 23:59:59";
//
//            List<CidRelation> cidRelationList = relationService.selectCidRelationListByPowerStationIds(itemPowerstation.getId(),null);
//
//            float currentDayEnergy=0;
//            Map<String,Float> cidDayEnergyMap=new HashMap();
//            for (int a=0;a<cidRelationList.size();a++){
//
//                String s = JSON.toJSONString(cidRelationList.get(a));
//                CidRelation cidRelation = JSON.parseObject(s, CidRelation.class);
//
//                if (cidRelation==null)
//                    continue;
//                long maxCidId =0l;
//
//                String vidHeadStr = cidRelation.getVid()!=null?cidRelation.getVid().substring(0,1):"1";
//                int vidHeadI=Integer.parseInt(vidHeadStr);
//
//                for (int b=0;b<vidHeadI;b++) {
//
//                    try {
//                        System.out.println("===CidEnergyTask cid:" + cidRelation.getCid() + " vid:" + cidRelation.getVid());
//                        maxCidId = cidDataService.selectTaskMaxIds(cidRelation.getCid(), cidRelation.getVid(), null, startDate, endDate);
//                    } catch (Exception exception) {
//                       // exception.printStackTrace();
//                        continue;
//                    }
//
//                    long minCidId = cidDataService.selectTaskMinIds(cidRelation.getCid(), cidRelation.getVid(), null, startDate, endDate);
//                    CidData maxCid = cidDataService.selectCidDataById(maxCidId);
//                    CidData minCid = cidDataService.selectCidDataById(minCidId);
//                    float dayEnergy = Float.parseFloat(maxCid.getEnergy()) - Float.parseFloat(minCid.getEnergy());
//                    if (dayEnergy < 0) {
//                        System.out.println("===CidEnergyTask analyseCurrentDayEnergy end ,itemPowerstation.getId():" + itemPowerstation.getId() + "  vid:" + cidRelation.getVid() + "  cid:" + cidRelation.getCid() + "  dayEnergy:" + dayEnergy);
//                        dayEnergy = 0;
//                    }
//
//                    currentDayEnergy = currentDayEnergy + dayEnergy;
//                    System.out.println("===CidEnergyTask  analyseCurrentDayEnergy  end ,itemPowerstation.getId():" + itemPowerstation.getId() + "  vid:" + cidRelation.getVid() + "  cid:" + cidRelation.getCid() + "  currentDayEnergy:" + currentDayEnergy);
//
//                    String key = cidRelation.getCid() + "_" + cidRelation.getVid()+"_"+(b+1);
//                    if (cidDayEnergyMap.get(key) == null) {
//                        cidDayEnergyMap.put(key, dayEnergy);
//                    } else {
//                        cidDayEnergyMap.put(key, cidDayEnergyMap.get(key) + dayEnergy);
//                    }
//                }
//            }
//
//            CidPowerstationinfo updatePowerstation =new CidPowerstationinfo();
//            updatePowerstation.setId(itemPowerstation.getId());
//            updatePowerstation.setUpdateTime(nowUTC);
//            updatePowerstation.setEnergyTotalDay(String.valueOf(currentDayEnergy));
//            powerstationinfoService.updateCidPowerstationinfo(updatePowerstation);
//
//            Map<String,Float> cidMultiRelationMap=new HashMap();
//
//            for (String key : cidDayEnergyMap.keySet()) {
//
//                String cid=key.substring(0,key.indexOf("_"));
//                String tmp1 = key.substring(key.indexOf("_")+1,key.length());
//                String vid= tmp1.substring(0,tmp1.indexOf("_"));
//                String road = tmp1.substring(tmp1.indexOf("_")+1,tmp1.length());
//
//                CidRelation relation = new CidRelation();
//                relation.setCid(cid);
//                relation.setVid(vid);
//                relation.setPowerStationId(itemPowerstation.getId());
//                relation.setCurrentEnergy(String.valueOf(cidDayEnergyMap.get(key)));
//                System.out.println("===CidEnergyTask updatePowerTodayPowerAndEnergy end ,itemPowerstation.getId():"+itemPowerstation.getId()+"  vid:"+vid+"  cid:"+cid+"  setCurrentEnergy:"+relation.getCurrentEnergy());
//
//                if (vid.substring(0,1).equals("1")){;
//                    relationService.updateRelationByCidVidLoop(relation);
//                }else{
//                    relation.setRoadType(road);
//                    System.out.println("===CidEnergyTask updatePowerTodayPowerAndEnergy end multiRelation:  vid:"+vid+"  cid:"+cid+"  setCurrentEnergy:"+relation.getCurrentEnergy()+"  road:"+road);
//
//                    relationRoadService.updateRelationByCidVidLoop(relation);
//                    String keyTmp = cid+"_"+vid;
//                    if (cidMultiRelationMap.get(keyTmp)==null){
//                        cidMultiRelationMap.put(keyTmp,cidDayEnergyMap.get(key));
//                    }else {
//                        cidMultiRelationMap.put(keyTmp,cidDayEnergyMap.get(key)+cidMultiRelationMap.get(keyTmp));
//                    }
//                }
//            }
//
//            System.out.println("===CidEnergyTask updatePowerTodayPowerAndEnergy end cidMultiRelationMap:"+JSON.toJSONString(cidMultiRelationMap));
//            for (String key1 : cidMultiRelationMap.keySet()) {
//
//                String cidTMP= key1.substring(0,key1.indexOf("_"));
//                String vidTMP = key1.substring(key1.indexOf("_")+1,key1.length());
//
//                CidRelation relation = new CidRelation();
//                relation.setCid(cidTMP);
//                relation.setVid(vidTMP);
//                relation.setPowerStationId(itemPowerstation.getId());
//                relation.setCurrentEnergy(String.valueOf(cidMultiRelationMap.get(key1)));
//                System.out.println("===CidEnergyTask updatePowerTodayPowerAndEnergy end cidMultiRelationMaprelation:"+JSON.toJSONString(relation));
//
//                relationService.updateRelationByCidVidLoop(relation);
//            }
//
//        }

    }


        /**
         * 按日期更新发电日数据 启用
         *
         * @param updateDate * * 0/1 * * ?
         */
//    @Scheduled(cron = "* * 0/1 * * ?")
    public void updateCidVidLoopEnergyToday(String updateDate) {



        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String startDate = formatter.format(date) + " 00:00:00";
        String endDate = formatter.format(date) + " 23:59:59";
        System.out.println(updateDate != null);
        System.out.println(!updateDate.equals(""));
        if (updateDate != null && !updateDate.equals("")) {
            startDate = updateDate + " 00:00:00";
            endDate = updateDate + " 23:59:59";
        }

        List<CidData> energyCidList = new ArrayList<>();
        HashMap<String, Float> cidEnergyMap = new HashMap<String, Float>();

        //更新cid day data 表数据 1天的数据
        for (Object obj : cidDataService.selectTaskCidVidLoop(startDate, endDate)) {

            String s = JSON.toJSONString(obj);
            CidData cidData = JSON.parseObject(s, CidData.class);
            String cid = cidData.getCid();
            String vid = cidData.getVid();
            String roadType = cidData.getRoadType();

            System.out.println("cid:" + cid + ",vid:" + vid + ",loop" + roadType);

            long maxCidId = cidDataService.selectTaskMaxIds(cid, vid, roadType, startDate, endDate);
            long minCidId = cidDataService.selectTaskMinIds(cid, vid, roadType, startDate, endDate);

            CidData maxCid = cidDataService.selectCidDataById(maxCidId);
            CidData minCid = cidDataService.selectCidDataById(minCidId);

            float dayEnergy = Float.parseFloat(maxCid.getEnergy()) - Float.parseFloat(minCid.getEnergy());

            if (cidEnergyMap.get(cid) != null) {
                cidEnergyMap.put(cid, cidEnergyMap.get(cid) + dayEnergy);
            } else {
                cidEnergyMap.put(cid, dayEnergy);
            }

            CidDayData dayData = new CidDayData();
            dayData.setEnergy(String.valueOf(dayEnergy));
            dayData.setPower(maxCid.getPower());
            dayData.setGridFreq(maxCid.getGridFreq());
            dayData.setM1MError(maxCid.getM1MError());
            dayData.setM1SError(maxCid.getM1SError());
            dayData.setVolt(maxCid.getVolt());
            dayData.setGridVolt(maxCid.getGridVolt());
            dayData.setTemp(maxCid.getTemp());
            dayData.setCid(maxCid.getCid());
            dayData.setVid(maxCid.getVid());
            if (maxCid.getRoadType() != null) {
                dayData.setRoadType(maxCid.getRoadType());
            }
            if (updateDate != null && !updateDate.equals("")) {
                dayData.setDate(updateDate);
            } else {
                dayData.setDate(formatter.format(date));
            }
            System.out.println("updateDate========>:" + updateDate);
            Long insertId = dayDataService.selectForValInsert(dayData.getCid(), dayData.getVid(), dayData.getRoadType(), dayData.getDate());
            if (insertId != null) {
                dayDataService.updateByCidVidLoopDate(dayData);
            } else {
                Date insertDate = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String createDate = "";
                if (updateDate != null && !updateDate.equals("")) {
                    createDate = updateDate + " 00:01:11";
                } else {
                    createDate = formatter.format(date) + " 00:01:11";
                }//formatter.format(date)

                try {
                    System.out.println("createDate:" + createDate);
                    System.out.println("fmt.parse ===>createDate:" + sdf.parse(createDate));
                    //System.out.println("createDate:"+createDate);
                    insertDate = sdf.parse(createDate);
                    dayData.setCreateDate(insertDate);
                    dayDataService.insertCidDayData(dayData);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            CidRelation relation = new CidRelation();
            relation.setCid(maxCid.getCid());
            relation.setVid(maxCid.getVid());
            relation.setRoadType(maxCid.getRoadType());

        }
        analyseCurrentDayEnergy();

    }

    /**
     * 每一小时更新网关发电统计信息  不器用
     */
//    @Scheduled(cron = "* * 0/1 * * ?")
    public void updateRelationEnergy_old() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat formatterYear = new SimpleDateFormat("yyyy");
        Date date = new Date(System.currentTimeMillis());
        String updateDateMonth = formatter.format(date);
        String updateDateYear = formatterYear.format(date);
        for (Object obj : dayDataService.selectCidVidLoopSumEnergyWithMonth(updateDateMonth)) {
            String s = JSON.toJSONString(obj);
            CidDayData cidData = JSON.parseObject(s, CidDayData.class);
            CidRelation relation = new CidRelation();
            relation.setCid(cidData.getCid());
            relation.setVid(cidData.getVid());
            relation.setRoadType(cidData.getRoadType());
            relation.setMonthEnergy(cidData.getEnergy());
            relation.setDelFlag("0");
//            relation.setIsConfirm("0");
            relation.setBindType("0");
            List<CidRelation> relationList = relationService.selectCidRelationList(relation);
            if (relationList.size() == 1) {
                relation.setId(relationList.get(0).getId());
                relationService.updateCidRelation(relation);
            }
        }
        for (Object obj : dayDataService.selectCidVidLoopSumEnergyWithYear(updateDateYear)) {
            String s = JSON.toJSONString(obj);
            CidDayData cidData = JSON.parseObject(s, CidDayData.class);
            CidRelation relation = new CidRelation();
            relation.setCid(cidData.getCid());
            relation.setVid(cidData.getVid());
            relation.setRoadType(cidData.getRoadType());
            relation.setYearEnergy(cidData.getEnergy());
            relation.setDelFlag("0");
            relation.setBindType("0");

            List<CidRelation> relationList = relationService.selectCidRelationList(relation);
            if (relationList.size() == 1) {
                relation.setId(relationList.get(0).getId());
                relationService.updateCidRelation(relation);
            }
            //relationService.updateRelationByCidVidLoop(relation);
        }
        for (Object obj : dayDataService.selectCidVidLoopSumEnergyWithCount()) {
            String s = JSON.toJSONString(obj);
            CidDayData cidData = JSON.parseObject(s, CidDayData.class);
            CidRelation relation = new CidRelation();
            relation.setCid(cidData.getCid());
            relation.setVid(cidData.getVid());
            relation.setRoadType(cidData.getRoadType());
//            relation.setCountEnergy(cidData.getEnergy());
            relation.setDelFlag("0");
//            relation.setIsConfirm("0");
            relation.setBindType("0");
            List<CidRelation> relationList = relationService.selectCidRelationList(relation);
            if (relationList.size() == 1) {
                relation.setId(relationList.get(0).getId());
                relationService.updateCidRelation(relation);
            }
        }


    }

    /**
     * 更新电站发电信息  启用
     */
//    @Scheduled(cron = "* * 0/1 * * ?")
//    @Scheduled(cron = "* 0/2 * * * ?")
    public void updatePlantEnergy() {
//        for (Object obj : relationService.selectPlantMonthEnergy()) {
//            String s = JSON.toJSONString(obj);
//            CidRelation relation = JSON.parseObject(s, CidRelation.class);
//            CidPowerstationinfo plant = new CidPowerstationinfo();
//            plant.setEnergyTotalMonth(relation.getMonthEnergy());
//            plant.setId(relation.getPowerStationId());
//            powerstationinfoService.updateCidPowerstationinfo(plant);
//        }
//        for (Object obj : relationService.selectPlantYearEnergy()) {
//            String s = JSON.toJSONString(obj);
//            CidRelation relation = JSON.parseObject(s, CidRelation.class);
//            CidPowerstationinfo plant = new CidPowerstationinfo();
//            plant.setEnergyTotalYear(relation.getYearEnergy());
//            plant.setId(relation.getPowerStationId());
//            System.out.println(plant.getEnergyTotalYear());
//            powerstationinfoService.updateCidPowerstationinfo(plant);
//        }
//        List<CidRelation> plantCountEnergyList = relationService.selectPlantCountEnergy();
//
//        for (Object obj : plantCountEnergyList) {
//            String s = JSON.toJSONString(obj);
//            CidRelation relation = JSON.parseObject(s, CidRelation.class);
//
//            if (relation.getPowerStationId()==null)
//                continue;
//
//            CidPowerstationinfo plant = new CidPowerstationinfo();
//            plant.setEnergyTotal(relation.getCountEnergy());
//            plant.setId(relation.getPowerStationId());
//
//
//            String currentPower = relationService.sumCurrentPowerByPowerStationID(plant.getId());
//            if (currentPower == null)
//                plant.setEnergyCurrentPower("0");
//            else
//                plant.setEnergyCurrentPower(currentPower);
//
//            System.out.println("===CidEnergyTask ,updatePlantEnergy-currentPower:" + currentPower+"  plant id:"+plant.getId());
//
//            powerstationinfoService.updateCidPowerstationinfo(plant);
//        }
    }

    /**
     * remove!!
     */
    public void updateEnergyToRelationOld() {
        //获取今日发电量
        System.out.println("update mis emu energy day");
    }

    /**
     * 更新日发电量信息
     */
    public void updateEnergyToDayData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String startDate = formatter.format(date) + " 00:00:00";
        String endDate = formatter.format(date) + " 23:59:59";
//        String startDate="2022-09-12"+" 00:00:00";
//        String endDate="2022-09-12"+" 23:59:59";

        List<CidData> todayFirstList = new ArrayList<CidData>();
        List<CidData> lastDayLastList = new ArrayList<CidData>();
        List<CidData> updateLastList = new ArrayList<CidData>();
        List<Long> idList = new ArrayList<Long>();
        //获取今天第一条发电记录
        for (Object obj : cidDataService.selectTodayFirstData(startDate, endDate)) {
            String s = JSON.toJSONString(obj);
            CidData cidData = JSON.parseObject(s, CidData.class);
            todayFirstList.add(cidData);
        }

        //获取昨天最后一条发电记录
        for (Object objIds : cidDataService.selectLastDayLastDataGetMaxId()) {
            String s = JSON.toJSONString(objIds);
            CidData cidData = JSON.parseObject(s, CidData.class);
            idList.add(cidData.getId());
        }
        Long[] ids = new Long[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            ids[i] = idList.get(i);
        }

        for (Object lastObj : cidDataService.selectLastDayLastData(ids)) {
            String s = JSON.toJSONString(lastObj);
            CidData cidData = JSON.parseObject(s, CidData.class);
            lastDayLastList.add(cidData);
        }

        //多重遍历，计算当前发电量
        for (CidData cidFirst : todayFirstList) {
            //遍历获取昨日发电量情况
            for (CidData lastCidLast : lastDayLastList) {
                if (lastCidLast.getCid().equals(cidFirst.getCid()) && lastCidLast.getVid().equals(cidFirst.getVid())) {
                    CidData cidData = new CidData();
                    float currentEnergy = 0f;
                    if (lastCidLast.getRoadType() != null && cidFirst.getRoadType() != null) {
                        if (lastCidLast.getRoadType().equals(cidFirst.getRoadType())) {
                            currentEnergy = Float.parseFloat(cidFirst.getEnergy()) - Float.parseFloat(lastCidLast.getEnergy());
                            cidData.setCid(cidFirst.getCid());
                            cidData.setVid(cidFirst.getVid());
                            cidData.setRoadType(cidFirst.getRoadType());
                            cidData.setEnergy(String.valueOf(currentEnergy));
                            updateLastList.add(cidData);
                        }
                    } else {
                        currentEnergy = Float.parseFloat(cidFirst.getEnergy()) - Float.parseFloat(lastCidLast.getEnergy());
                        cidData.setCid(cidFirst.getCid());
                        cidData.setVid(cidFirst.getVid());
                        cidData.setEnergy(String.valueOf(currentEnergy));
                        updateLastList.add(cidData);
                    }
                }
            }
        }

        //update lastDay energy
        for (CidData updateData : updateLastList) {
            CidDayData cidDayData = new CidDayData();
            cidDayData.setCid(updateData.getCid());
            cidDayData.setVid(updateData.getVid());
            if (updateData.getRoadType() != null) {
                cidDayData.setRoadType(updateData.getRoadType());
            }
            cidDayData.setEnergy(updateData.getEnergy());

            dayDataService.updateCidDayDataEnergy(cidDayData);
        }
        System.out.println("update energy to daydata");
    }

    /**
     * 更新电站表发电量数据
     */
    public void updatePowerStationEnergy() {
//        CidPowerstationinfo queryPowerstation = new CidPowerstationinfo();
//        queryPowerstation.setDelFlag("0");
//        List<CidPowerstationinfo> powerstationinfoList = powerstationinfoService.selectCidPowerstationinfoList(queryPowerstation);
//        List<CidRelation> relationList = new ArrayList<CidRelation>();
//        for (Object obj : relationService.selectPowerEnergy()) {
//            String s = JSON.toJSONString(obj);
//            CidRelation relation = JSON.parseObject(s, CidRelation.class);
//            relationList.add(relation);
//        }
//        for (CidPowerstationinfo plant : powerstationinfoList) {
//            for (CidRelation relation : relationList) {
//                if (plant.getId().equals(relation.getPowerStationId())) {
//                    plant.setEnergyTotal(relation.getCountEnergy());
//                    plant.setEnergyTotalYear(relation.getYearEnergy());
//                    plant.setEnergyTotalMonth(relation.getMonthEnergy());
//                    powerstationinfoService.updateCidPowerstationinfo(plant);
//                }
//            }
//        }
//
//        System.out.println("update powerStation energy");
    }

    public void updatePowerStationCurrentPower() {
//
//        CidPowerstationinfo queryPower = new CidPowerstationinfo();
//        queryPower.setDelFlag("0");
//        List<CidPowerstationinfo> powerstationinfoList = powerstationinfoService.selectCidPowerstationinfoList(queryPower);
//        List<CidData> dataList = new ArrayList<CidData>();
//
//        List<Long> idList = new ArrayList<>();
//
//        for (Object obj : cidDataService.selectCurrentPowerGetMaxId()) {
//            String s = JSON.toJSONString(obj);
//            CidData cidData = JSON.parseObject(s, CidData.class);
//            idList.add(cidData.getId());
//        }
//        Long[] ids = new Long[idList.size()];
//        for (int i = 0; i < idList.size(); i++) {
//            ids[i] = idList.get(i);
//        }
//
//        for (Object obj : cidDataService.selectCurrentPower(ids)) {
//            String s = JSON.toJSONString(obj);
//            CidData cidData = JSON.parseObject(s, CidData.class);
//            dataList.add(cidData);
//        }



//        System.out.println("===CidEnergyTask  updatePowerStationCurrentPower end ");
    }

    /**
     * 更新今日发电量表数据
     */
    public void updateDayDateByToday() {
        List<CidDayData> todayDataList = new ArrayList<CidDayData>();
        List<CidData> todayList = new ArrayList<CidData>();

        for (Object obj : cidDataService.selectLastEnergyDataByToday()) {
            String s = JSON.toJSONString(obj);
            CidData cidData = JSON.parseObject(s, CidData.class);
            CidDayData queryValData = JSON.parseObject(s, CidDayData.class);
            todayList.add(cidData);

            CidDayData dayData = dayDataService.selectTodayDataByCidVidRoad(queryValData);


            queryValData.setId(null);
            if (dayData == null) {
                queryValData.setCreateDate(new Date());
                dayDataService.insertCidDayData(queryValData);
            } else {
                dayData.setVolt(cidData.getVolt());
                dayData.setEnergy(cidData.getEnergy());
                dayData.setPower(cidData.getPower());
                dayData.setTemp(cidData.getTemp());
                dayData.setM1MError(cidData.getM1MError());
                dayData.setM1SError(cidData.getM1SError());
                dayData.setGridVolt(cidData.getGridVolt());
                dayData.setGridFreq(cidData.getGridFreq());
                dayData.setSoftVersion(cidData.getSoftVersion());
                dayData.setSoftDeputyVersion(cidData.getSoftDeputyVersion());
                dayDataService.updateCidDayData(dayData);
            }

        }
        System.out.println("update day data");
    }


    /**
     * 自动绑定
     */
    public void autoRelation() {
        List<CidRelation> relationList = new ArrayList<CidRelation>();
        List<CidData> hasDateList = new ArrayList<CidData>();
        List<CidData> notInList = new ArrayList<CidData>();
        List<String> reStringList = new ArrayList<String>();
        List<String> daStringList = new ArrayList<String>();

        for (Object obj : relationService.selectCidVidLoopPlantForRealtion()) {
            String s = JSON.toJSONString(obj);
            CidRelation relation = JSON.parseObject(s, CidRelation.class);
            reStringList.add(relation.getCid() + "_" + relation.getVid() + "_" + relation.getRoadType());
            relationList.add(relation);
        }

        for (Object obj : cidDataService.selectCidVidLoopForRelation()) {
            String s = JSON.toJSONString(obj);
            CidData cidData = JSON.parseObject(s, CidData.class);
            daStringList.add(cidData.getCid() + "_" + cidData.getVid() + "_" + cidData.getRoadType());
            hasDateList.add(cidData);
        }
        //获取未绑定的 cid_vid_road
        daStringList.removeAll(relationList);

        for (String s : daStringList) {
            String cid = s.split("_")[0];
            //System.out.println("cid:"+cid);
            for (Object obj : relationService.getPowerStationIdByCid(cid)) {
                String sObj = JSON.toJSONString(obj);
                CidRelation re = JSON.parseObject(sObj, CidRelation.class);
                CidRelation relation = new CidRelation();
                relation.setCid(cid);
                relation.setVid(s.split("_")[1]);
                if (s.split("_")[2] != null && !s.split("_")[2].equals("")) {
                    relation.setRoadType(s.split("_")[2]);
                }
                relation.setPowerStationId(re.getPowerStationId());
                relation.setIsConfirm("1");
                relation.setDelFlag("0");
                relationService.insertCidRelation(relation);
            }
        }

        System.out.println("auto relation ");
    }

    /**
     * 更新在线状态
     *
     * @param online
     * @param offline
     */
    public void updateRelationAndPlantStatus(Integer online, Integer offline) {

//        List<String> relationPlantList = new ArrayList<>();
//        List<Long> plantList = new ArrayList<>();
//        System.out.println("===cidEnergyTask updateRelationAndPlantStatus start.....");
//
//        List<String> relationHasComparedList = new ArrayList<String>();
//        List<CidData>  cidDataList = cidDataService.selectCidVidLoopDateForStatus();
//        //更新网关不在线状态
//        for (Object obj : cidDataList) {
//
//            String s = JSON.toJSONString(obj);
//            CidData cidData = JSON.parseObject(s, CidData.class);
//            String onlineStatus = "0";
//
//            Integer untZoneMinute = 0;
//            Map paramMap = new HashMap();
//            paramMap.put("cid", cidData.getCid());
//            paramMap.put("vid", cidData.getVid());
//            List<Map> cidRelationList = relationService.selectPowerstationIDByCidAndVid(paramMap);
//
//            if (cidRelationList != null & cidRelationList.size() > 0) {
//
//                long powerstationID = Long.parseLong(cidRelationList.get(0).get("powerstationID").toString());
//                List<Map> allPowerStationUTCZoneList = powerstationinfoService.selectUTCZoneOfPowerstationByID(powerstationID);
//
//                if (allPowerStationUTCZoneList != null & allPowerStationUTCZoneList.size() > 0) {
//                    String utcZoneStr = String.valueOf(allPowerStationUTCZoneList.get(0).get("utcZone"));
//                    untZoneMinute = getUTCZoneMinute(utcZoneStr);
//
//                    if (untZoneMinute == null) {
//                        untZoneMinute = 0;
//                    }
//                }
//            }
//
//            Date cDate = cidData.getCreateDate();
//            Date onlineDate = new Date(cDate==null?new Date().getTime():(cDate.getTime() + 60 * 60 * 1000));
//            Date nowUTC = new Date(new Date().getTime() + untZoneMinute * 60 * 1000);
//            System.out.println("===CidEnergyTask updateRelationAndPlantStatus  cidData.getCreateDate:"+ cidData.getCreateDate()+" onlineDate:"+onlineDate+" nowUTC:"+nowUTC+"  cid:"+cidData.getCid()+" vid:"+cidData.getVid());
//
//            //只判断离线，在线在网关交互判断,
//            List<CidRelation> cidRelationTmpList = relationService.getRelationByCidVidLoop(cidData.getCid(), cidData.getVid(), null);
//            SimpleDateFormat fmtDay=new SimpleDateFormat("yyyyMMdd");
//            for (Object objRe : cidRelationTmpList) {
//                String re = JSON.toJSONString(objRe);
//                CidRelation relation = JSON.parseObject(re, CidRelation.class);
//                String oldStatus=relation.getStatus()!=null?relation.getStatus():"";
//
//
//                if(!fmtDay.format(onlineDate).equals( fmtDay.format(nowUTC) )) {
//                    System.out.println("===CidEnergyTask updateRelationAndPlantStatus  not-today offline:" + cidData.getCreateDate() + " onlineDate:" + onlineDate + " nowUTC:" + nowUTC + "  cid:" + cidData.getCid() + " vid:" + cidData.getVid());
//
//                    float currentPowerTMP = relation.getCurrentPower() != null ? Float.parseFloat(relation.getCurrentPower()) : 0f;
//                    float currentEnergyTMP = relation.getCurrentEnergy() != null ? Float.parseFloat(relation.getCurrentEnergy()) : 0f;
//                    if (currentEnergyTMP != 0f || currentPowerTMP != 0f || relation.getStatus()==null || !relation.getStatus().equals("3")) {
//                        System.out.println("===CidEnergyTask updateRelationAndPlantStatus  not-today-2 offline:" + cidData.getCreateDate() + " onlineDate:" + onlineDate + " nowUTC:" + nowUTC + "  cid:" + cidData.getCid() + " vid:" + cidData.getVid());
//                        relationService.updateRelationOffline(relation.getId());
//                    }
//                }else{
//                    if (onlineDate.getTime() < nowUTC.getTime() & !oldStatus.equals("3")) {
//
//                            System.out.println("===CidEnergyTask updateRelationAndPlantStatus  onlineDate.getTime()<nowUTC.getTime() offline:" + cidData.getCreateDate() + " onlineDate:" + onlineDate + " nowUTC:" + nowUTC + "  cid:" + cidData.getCid() + " vid:" + cidData.getVid());
//                            relation.setStatus("3");
//                            relation.setCurrentPower("0");
//                            relationService.updateCidRelationWhenOffline(relation);
//                    }
//                }
//
//
//                if (relation.getPowerStationId()==null)
//                    continue;
//                String plantStatus = relation.getPowerStationId() + "_" + onlineStatus;
//                relationPlantList.add(plantStatus);
//                plantList.add(relation.getPowerStationId());
//                relationHasComparedList.add(cidData.getCid()+""+ cidData.getVid()+relation.getPowerStationId());
//
//            }
//        }
//
//
//        //
//        List<CidRelation> notOfflineCidRelationList = relationService.selectNotOfflineCidRelation();
//        Integer untZoneMinute = 0;
//        for (int i = 0; i < notOfflineCidRelationList.size(); i++) {
//
////            if (notOfflineCidRelationList.get(i)!=null)
////            System.out.println("===CidEnergyTask updateRelationAndPlantStatus  ,cidRelationList:" + notOfflineCidRelationList.size()+" cid:"+ notOfflineCidRelationList.get(i).getCid()+" vid:"+ notOfflineCidRelationList.get(i).getVid());
//
//            CidRelation relation = notOfflineCidRelationList.get(i);
//            long powerstationID = Long.parseLong(relation.getPowerStationId()!=null?relation.getPowerStationId().toString():"-1");
//            List<Map> allPowerStationUTCZoneList = powerstationinfoService.selectUTCZoneOfPowerstationByID(powerstationID);
//
//            if (allPowerStationUTCZoneList != null & allPowerStationUTCZoneList.size() > 0) {
//                String utcZoneStr = String.valueOf(allPowerStationUTCZoneList.get(0).get("utcZone"));
//                untZoneMinute = getUTCZoneMinute(utcZoneStr);
//            }
//
//
//
//        }
//
//        Date nowUTC = new Date(new Date().getTime() + untZoneMinute * 60 * 1000);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String today = sdf.format(nowUTC);
//        try {
//            Date  weeHours = sdf.parse(today+" 00:00:00");
//            System.out.println("===cidEnergyTask updateRelationAndPlantStatus weeHours:"+weeHours);
//            int result=  relationService.resetCurrentPowerEnergyForOfflineCidrelation(weeHours);
//            System.out.println("===cidEnergyTask updateRelationAndPlantStatus weeHours:"+weeHours +" result:"+result);
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//
//        //更新电站状态
//        for (Long plantId : plantList) {
//            int onCount = 0;
//            int inCount = 0;
//            int offCount = 0;
//            for (String s : relationPlantList.stream().distinct().collect(Collectors.toList())) {
//                long id =0;
//                try{
//                    id = Long.parseLong(s.split("_")[0]);
//                }catch (Exception ex){
//                    System.out.println("===cidEnergyTask updateRelationAndPlantStatus relationPlantList s:"+s);
//                    continue;
//                }
//                String status = s.split("_")[1];
//                System.out.println("===cidEnergyTask updateRelationAndPlantStatus relationPlantList id:"+id+" plantId:"+plantId);
//
//                if (id == plantId) {
//                    if (status==null)
//                        offCount++;
//                    else{
//                        if (status.equals("0")) {
//                            onCount++;
//                        } else if (status.equals("1")) {
//                            inCount++;
//                        } else {
//                            offCount++;
//                        }
//                    }
//                }
//                CidPowerstationinfo plant = powerstationinfoService.selectCidPowerstationinfoById(plantId);
//                if (plant != null) {
//                    if (onCount > 0) {
//                        plant.setEnergyStatus("0");
//                        powerstationinfoService.updateCidPowerstationinfo(plant);
//                    } else if (onCount == 0 && inCount > 0) {
//                        plant.setEnergyStatus("1");
//                        powerstationinfoService.updateCidPowerstationinfo(plant);
//                    } else if (onCount == 0 && inCount == 0 && offCount > 0) {
//                        plant.setEnergyStatus("3");
//                        powerstationinfoService.updateCidPowerstationinfo(plant);
//                    }
//                }
//            }
//        }
//
//        System.out.println("update relation and plant status");
    }

    /**
     * 更新在线状态
     */
    public void updateRelationEnergy() {

//
//        CidRelation queryRelation = new CidRelation();
//        queryRelation.setDelFlag("0");
//
//        List<CidRelation> relationList = relationService.selectCidRelationList(queryRelation);
//        for (CidRelation relation : relationList) {
//            CidData queryData = new CidData();
//            queryData.setCid(relation.getCid());
//            queryData.setVid(relation.getVid());
//            queryData.setRoadType(relation.getRoadType());
//            //
//            Integer untZoneMinute = 0;
//            System.out.println("===updateRelationEnergy  untZoneMinute  ,powerstationinfoService" + powerstationinfoService + "  relation:" + JSON.toJSONString(relation));
//            try {
//                if (relation!=null){
//                    List<Map> allPowerStationUTCZoneList = powerstationinfoService.selectUTCZoneOfPowerstationByID(relation.getPowerStationId());
//                    if (allPowerStationUTCZoneList != null & allPowerStationUTCZoneList.size() > 0) {
//                        String utcZoneStr = String.valueOf(allPowerStationUTCZoneList.get(0).get("utcZone"));
//                        untZoneMinute = getUTCZoneMinute(utcZoneStr);
//
//                        if (untZoneMinute == null) {
//                            System.out.println("===updateRelationEnergy error untZoneMinute is null ,cid:" + relation.getCid() + "  vid:" + relation.getVid());
//                        }
//                    }
//                }
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//
//
//            List<CidData> lastList = cidDataService.selectLastCidList(queryData);
//
//            if (lastList != null && lastList.size() > 0) {
//
//                CidData lastData = lastList.get(0);
//
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String lastUpdate = sdf.format(lastData.getCreateDate());
//                relation.setLastUpdate(lastUpdate);
//                relation.setLastErrorCode(lastData.getM1MErrorCode());
//
//                if (relation.getLastErrorCode()!=null & relation.getStatus().equals("1")) {
//                    if (relation.getLastErrorCode().equals("0000") || relation.getLastErrorCode().equals("0200") || relation.getLastErrorCode().equals("0008")) {
//                        relation.getStatus().equals("0");
//                    }
//                }
//
//
//                String searchDate = dateMinut(new Date(), untZoneMinute);
//
////               // 今日 getStationEnergyDayByCidVidAndDate
//
//
//                //本月
//                List<CidDayData> resultListMonth = dayDataService.selectCidVidEnergyByDateAndType(lastData.getCid(), lastData.getVid(),
//                        null, searchDate, "m");
//                float monthEnergy = 0f;
//                System.out.println("===CidEnergyTask updateRelationEnergy relation resultListMonth:"+JSON.toJSONString(resultListMonth));
//                System.out.println("===CidEnergyTask updateRelationEnergy relation resultListMonth vid:"+ lastData.getVid() +" resultListMonth:"+resultListMonth.size()+"  searchDate:"+searchDate);
//
//                if (resultListMonth.size() > 0) {
//                    for (Object obj : resultListMonth) {
//                        String s = JSON.toJSONString(obj);
//                        CidDayData d = JSON.parseObject(s, CidDayData.class);
//
//                        monthEnergy = monthEnergy + Float.parseFloat(d.getEnergy());
//                        System.out.println("===CidEnergyTask updateRelationEnergy relation resultListMonth vid:"+ lastData.getVid() +" monthEnergy:"+monthEnergy);
//
//                    }
//                }
//
//                //今年
//                float energyYear = 0f;
//                List<CidDayData> resultListYear = dayDataService.selectCidVidEnergyByDateAndType(lastData.getCid(), lastData.getVid(),
//                        null, searchDate, "y");
//
//                if (resultListYear.size() > 0) {
//                    for (Object obj : resultListYear) {
//                        String s = JSON.toJSONString(obj);
//                        CidDayData d = JSON.parseObject(s, CidDayData.class);
//                        energyYear = energyYear + Float.parseFloat(d.getEnergy());
//                    }
//                }
//                //总
//                List<CidDayData> resultListAll = dayDataService.selectCidVidEnergyByDateAndType(lastData.getCid(), lastData.getVid(),
//                        lastData.getRoadType(), searchDate, "a");
//                float energyAll = 0f;
//                if (resultListAll.size() > 0) {
//                    for (Object obj : resultListAll) {
//                        String s = JSON.toJSONString(obj);
//                        CidDayData d = JSON.parseObject(s, CidDayData.class);
//                        energyAll = energyAll + Float.parseFloat(d.getEnergy());
//                    }
//                }
//
//                relation.setMonthEnergy(String.valueOf(monthEnergy));
//                relation.setYearEnergy(String.valueOf(energyYear));
//                relation.setCountEnergy(String.valueOf(energyAll));
//
//               System.out.println("===CidEnergyTask updateRelationEnergy relation cid:"+lastData.getVid()+" setMonthEnergy:" +relation.getMonthEnergy()+"  setYearEnergy:"+relation.getYearEnergy() +"  searchDate:"+searchDate);
//
//                relationService.updateCidRelationEnergyInfo(relation);
//            }
//
//        }
    }

    public void updateHeartLogin() {
        CidRelation queryRelation = new CidRelation();
        queryRelation.setDelFlag("0");
        queryRelation.setBindType("0");
        queryRelation.setIsConfirm("0");

        List<CidRelation> relationList = relationService.selectCidRelationList(queryRelation);
        for (CidRelation relation : relationList) {
            CidLoginHeart queryHeart = new CidLoginHeart();
//            queryHeart.setCid(relation.getCid());
            List<CidLoginHeart> list = heartService.selectLastUpdateTime(relation.getCid());
            if (list.size() > 0) {
                queryHeart = list.get(0);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String lastUpdateTime = sdf.format(queryHeart.getCreateDate());
                relation.setLastUpdate(lastUpdateTime);
                relationService.updateCidRelation(relation);
            }
        }
    }

    /**
     * 更新今日发电量与发电功率
     */
    public void updateTodayEnergyAndPower() {
//        CidPowerstationinfo cidPowerstationinfo = new CidPowerstationinfo();
//        cidPowerstationinfo.setDelFlag("0");
//
//        //更新当前功率
//
//        List<CidPowerstationinfo> list = powerstationinfoService.selectCidPowerstationinfoList(cidPowerstationinfo);
//        for (CidPowerstationinfo plant : list) {
//            CidPowerstationinfo updatePlant = new CidPowerstationinfo();
//            updatePlant.setId(plant.getId());
//
//            List<Long> idsList = new ArrayList<>();
//            for (Object obj : cidDataService.selectPlantCurrentPowerIdNew(plant.getId())) {
//                String s = JSON.toJSONString(obj);
//                CidData d = JSON.parseObject(s, CidData.class);
//                idsList.add(d.getId());
//            }
//
//
//            Object returnObjId = cidDataService.selectPlantLastUpdateId(plant.getId());
//
//            if (returnObjId != null) {
//                Long lastId = Long.parseLong(returnObjId.toString());
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                CidData lastData = cidDataService.selectCidDataById(lastId);
//                String lastUpdate = sdf.format(lastData.getCreateDate());
//
//                updatePlant.setLastErrorCode(lastData.getM1MErrorCode());
//                updatePlant.setLastUpdate(lastUpdate);
//                if (new Date().before(lastData.getCreateDate())) {
//                    updatePlant.setUpdateTime(lastData.getCreateDate());
//                } else {
//                    updatePlant.setUpdateTime(new Date());
//                }
//            }
//            SimpleDateFormat sdfnow = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = new Date();
//            String searchDate = sdfnow.format(date);
//            //今日
////            List<CidData> resultList=cidDataService.getStationEnergyByPidAndDateForDay(plant.getId(),null,searchDate,"d");
//            List<CidDayData> resultListDay = cidDataService.getStationPowerByPidAndDate(plant.getId(), null, searchDate, "d");
//            float todayEnergy = 0f;
//            for (Object obj : resultListDay) {
//                String s = JSON.toJSONString(obj);
//                CidDayData d = JSON.parseObject(s, CidDayData.class);
//                todayEnergy = todayEnergy + Float.parseFloat(d.getEnergy());
//            }
//
//
//            //本月
//            float monthEnergy = 0f;
//
//            CidRelation queryRelation = new CidRelation();
//            queryRelation.setPowerStationId(plant.getId());
//            queryRelation.setBindType("0");
//            queryRelation.setDelFlag("0");
//            queryRelation.setIsConfirm("0");
//
//            //年，月，总
//            if (searchDate.length() <= 4) {
//                searchDate = searchDate + "-01-01";
//            }
//            float energyYear = 0f;
//            float energyAll = 0f;
//            List<CidRelation> relationListYear = relationService.selectCidRelationList(queryRelation);
//            if (relationListYear.size() > 0) {
//                for (Object obj : relationListYear) {
//                    String s = JSON.toJSONString(obj);
//                    CidRelation d = JSON.parseObject(s, CidRelation.class);
//                    monthEnergy = monthEnergy + Float.parseFloat(d.getMonthEnergy());
//                    energyYear = energyYear + Float.parseFloat(d.getYearEnergy());
//                    energyAll = energyAll + Float.parseFloat(d.getCountEnergy());
//                }
//            }
//
//
//
//            updatePlant.setEnergyTotalDay(String.valueOf(todayEnergy));
//            if (monthEnergy<todayEnergy)
//                monthEnergy=monthEnergy;
//            updatePlant.setEnergyTotalMonth(String.valueOf(monthEnergy));
//            updatePlant.setEnergyTotalYear(String.valueOf(energyYear));
//            if (energyAll<energyYear)
//                energyAll=energyYear;
//            updatePlant.setEnergyTotal(String.valueOf(energyAll));
//
//            System.out.println("===CidEnergyTask updateTodayEnergyAndPower updatePlant:" + JSON.toJSONString(updatePlant));
//            powerstationinfoService.updateCidPowerstationinfo(updatePlant);
//
//        }

//        System.out.println("update current power and energy and error and updateTime");
    }


    /**
     * 更新小时表的数据
     *
     * @param searchDate
     */
    public void updateDayHour(String searchDate) throws ParseException {
        CidPowerstationinfo queryPlant = new CidPowerstationinfo();
        queryPlant.setDelFlag("0");
        List<CidPowerstationinfo> plantList = powerstationinfoService.selectCidPowerstationinfoList(queryPlant);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String sdfDate = sdf.format(date);
//        System.out.println("searchDate:"+searchDate);
        if (searchDate == null || searchDate.equals("")) {
            searchDate = sdfDate;
        } else {
            sdfDate = searchDate;
        }
        System.out.println("searchDate:" + searchDate);
        for (CidPowerstationinfo plant : plantList) {
            List<CidData> dataList = new ArrayList<>();
            for (int i = 0; i < 24; i++) {
                if (i == 0) {
                    searchDate = sdfDate + " 00";
                } else if (i == 1) {
                    searchDate = sdfDate + " 01";
                } else if (i == 2) {
                    searchDate = sdfDate + " 02";
                } else if (i == 3) {
                    searchDate = sdfDate + " 03";
                } else if (i == 4) {
                    searchDate = sdfDate + " 04";
                } else if (i == 5) {
                    searchDate = sdfDate + " 05";
                } else if (i == 6) {
                    searchDate = sdfDate + " 06";
                } else if (i == 7) {
                    searchDate = sdfDate + " 07";
                } else if (i == 8) {
                    searchDate = sdfDate + " 08";
                } else if (i == 9) {
                    searchDate = sdfDate + " 09";
                } else if (i == 10) {
                    searchDate = sdfDate + " 10";
                } else if (i == 11) {
                    searchDate = sdfDate + " 11";
                } else if (i == 12) {
                    searchDate = sdfDate + " 12";
                } else if (i == 13) {
                    searchDate = sdfDate + " 13";
                } else if (i == 14) {
                    searchDate = sdfDate + " 14";
                } else if (i == 15) {
                    searchDate = sdfDate + " 15";
                } else if (i == 16) {
                    searchDate = sdfDate + " 16";
                } else if (i == 17) {
                    searchDate = sdfDate + " 17";
                } else if (i == 18) {
                    searchDate = sdfDate + " 18";
                } else if (i == 19) {
                    searchDate = sdfDate + " 19";
                } else if (i == 20) {
                    searchDate = sdfDate + " 20";
                } else if (i == 21) {
                    searchDate = sdfDate + " 21";
                } else if (i == 22) {
                    searchDate = sdfDate + " 22";
                } else if (i == 23) {
                    searchDate = sdfDate + " 23";
                }
                dataList = cidDataService.getStationEnergyHourByPidAndDate(plant.getId(), null, searchDate);
                if (dataList.size() > 0) {
                    String sFirst = JSON.toJSONString(dataList.get(0));
                    CidData firstData = JSON.parseObject(sFirst, CidData.class);
                    String sLast = "";
                    Float energy = 0f;
                    if (dataList.size() == 1) {
                        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
                        Date dNow = sdfDay.parse(searchDate);//new Date();   //当前时间
                        Date dBefore = new Date();
                        Calendar calendar = Calendar.getInstance(); //得到日历
                        calendar.setTime(dNow);//把当前时间赋给日历
                        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
                        dBefore = calendar.getTime();   //得到前一天的时间

//        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //设置时间格式
                        String defaultStartDate = sdfDay.format(dBefore);    //格式化前一天
                        //String defaultEndDate = sdf.format(dNow); //格式化当前时间
                        System.out.println("前一天的时间是：" + defaultStartDate);
                        long maxCidId = cidDataService.selectTaskMaxIdsByLastDate(firstData.getCid(), firstData.getVid(), firstData.getRoadType(), defaultStartDate);
                        CidData maxCid = cidDataService.selectCidDataById(maxCidId);
                        energy = Float.parseFloat(firstData.getEnergy()) - Float.parseFloat(maxCid.getEnergy());
                    }
                    if (dataList.size() > 1) {
                        int length = dataList.size();
                        sLast = JSON.toJSONString(dataList.get(length - 1));
                        CidData lastData = JSON.parseObject(sLast, CidData.class);
                        energy = Float.parseFloat(lastData.getEnergy()) - Float.parseFloat(firstData.getEnergy());
//                        System.out.println("Float.parseFloat(lastData.getEnergy()):"+Float.parseFloat(lastData.getEnergy()));
//                        System.out.println("Float.parseFloat(firstData.getEnergy():"+Float.parseFloat(firstData.getEnergy()));
//                        System.out.println("Float energy ="+(Float.parseFloat(lastData.getEnergy())-Float.parseFloat(firstData.getEnergy())));
                    }
                    if (energy < 0) {
                        energy = 0.01f;
                    }

                    for (Object obj : dataList) {
                        String s = JSON.toJSONString(obj);
                        CidData d = JSON.parseObject(s, CidData.class);
                        System.out.println("cid,vid,roadType:" + d.getCid() + "," + d.getVid() + "," + d.getRoadType());
                        List<Long> valList = hourService.selectValDateHour(d.getCid(), d.getVid(), d.getRoadType(), searchDate);
                        CidDayHour hour = new CidDayHour();
                        hour.setCid(d.getCid());
                        hour.setVid(d.getVid());
                        hour.setRoadType(d.getRoadType());
                        hour.setCidOrEmu(d.getCidOrEmu());
                        hour.setGridVolt(d.getGridVolt());
                        hour.setGridFreq(d.getGridFreq());
                        hour.setTemp(d.getTemp());
                        hour.setDate(searchDate);
                        hour.setM1MError(d.getM1MError());
                        hour.setM1SError(d.getM1SError());
                        hour.setM1MErrorCode(d.getM1MErrorCode());
                        hour.setM1SErrorCode(d.getM1SErrorCode());
                        hour.setVolt(d.getVolt());
                        hour.setSoftVersion(d.getSoftVersion());
                        hour.setSoftDeputyVersion(d.getSoftDeputyVersion());
                        hour.setEnergy(String.valueOf(energy));
                        hour.setPower(d.getPower());
                        //已有，更新
                        if (valList.size() > 0) {
                            hourService.updateCidDayHourByCidVidRoadDate(hour);
                        } else {
                            hourService.insertCidDayHour(hour);
                        }
                    }
                }

            }

        }
    }

    /**
     * 每天0点重置 今日发电量和当前发电功率
     */
    public void resetCurrentPowerEnergy() {
        relationService.updateForResetCurrentPowerEnergy();
    }

    public static String dateMinut(Date date, int x) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, x);// 24小时制
        date = cal.getTime();
        return format.format(date);
    }

    public static int getUTCZoneMinute(String utcZoneStr) {
        int untZoneMinute = 0;
        if (utcZoneStr != null & utcZoneStr.indexOf(":") > -1 & utcZoneStr.indexOf("+") > -1) {
            int hour = Integer.parseInt(utcZoneStr.substring(utcZoneStr.indexOf("+"), utcZoneStr.indexOf(":")));
            int minute = Integer.parseInt(utcZoneStr.substring(utcZoneStr.indexOf(":") + 1, utcZoneStr.indexOf(":") + 3));
            untZoneMinute = hour * 60 + minute;
        }
        return untZoneMinute;
    }

    public static void main(String[] args) throws ParseException {

        String key="111_222_b";

        String cid=key.substring(0,key.indexOf("_"));
        System.out.println(cid);
        String tmp1 = key.substring(key.indexOf("_")+1,key.length());
        System.out.println(tmp1);
        String vid= tmp1.substring(0,tmp1.indexOf("_"));
        System.out.println(vid);

        String road = tmp1.substring(tmp1.indexOf("_")+1,tmp1.length());

        System.out.println(road);


    }

}
