package com.benyi.energy.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.benyi.common.core.domain.entity.SysUser;
import com.benyi.common.core.domain.model.LoginUser;
import com.benyi.common.core.redis.RedisCache;
import com.benyi.common.utils.DateUtils;
import com.benyi.energy.domain.*;
import com.benyi.energy.mapper.CidEntityMapper;
import com.benyi.energy.service.*;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.benyi.common.annotation.Log;
import com.benyi.common.core.controller.BaseController;
import com.benyi.common.core.domain.AjaxResult;
import com.benyi.common.enums.BusinessType;
import com.benyi.common.utils.poi.ExcelUtil;
import com.benyi.common.core.page.TableDataInfo;

/**
 * 网关、微逆关系Controller
 *
 * @author wuqiguang
 * @date 2022-07-31
 */
@RestController
@RequestMapping("/energy/relation")
public class CidRelationController extends BaseController {

    @Autowired
    private ICidRelationService cidRelationService;

    @Autowired
    private ICidDataService cidDataService;

    @Autowired
    private ICidPowerstationinfoService powerstationinfoService;

    @Autowired
    private ICidLoginHeartService heartService;

    @Autowired
    private ICidDayDataService dayDataService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ICidVidRoadRelationService cidVidRoadRelationService;

    @Autowired
    private ICidRelationRoadService cidRelationRoadService;

    @Autowired
    private CidEntityMapper cidEntityMapper;

    /**
     * 查询网关、微逆关系列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:list')")
    @GetMapping("/list")
    public TableDataInfo list(CidRelation cidRelation) {

        startPage();
        List<CidRelation> list = cidRelationService.selectCidRelationList(cidRelation);
        return getDataTable(list);
    }

    @GetMapping("/cidList")
    public TableDataInfo getCidList(Long powerStationId, String cid, String searchDate, Integer pageNum, Integer pageSize) {
        LoginUser loginUser = getLoginUser();
        PageHelper.startPage(pageNum, pageSize);
        List<CidRelation> list = cidRelationService.getCidList(loginUser.getDeptId(), powerStationId, cid, searchDate);
        return getDataTable(list);
    }

    /**
     * 查询网关、微逆关系列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:listGroup')")
    @GetMapping("/listGroup")
    public AjaxResult listGroup(CidRelation queryRelation) {
        queryRelation.setDelFlag("0");
        queryRelation.setIsConfirm("0");
        queryRelation.setBindType("0");
        return AjaxResult.success(cidRelationService.selectCidRelationListGroupBy(queryRelation));
    }

    /**
     * 查询网关、微逆关系列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:listVidGroup')")
    @GetMapping("/listVidGroup")
    public AjaxResult listVidGroup(CidRelation queryRelation) {
        return AjaxResult.success(cidRelationService.selectCidRelationListGroupByWithCid(queryRelation));
    }


    /**
     * 查询网关、微逆关系列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:getRelationList')")
    @GetMapping("/getRelationList")
    public AjaxResult getRelationList(CidRelation cidRelation) throws ParseException {

        Integer untZoneMinute = 0;

        logger.info("cidRelation:" + JSON.toJSONString(cidRelation));
        List<Map> allPowerStationUTCZoneList = powerstationinfoService.selectUTCZoneOfPowerstationByID(cidRelation.getPowerStationId());
        if (allPowerStationUTCZoneList != null & allPowerStationUTCZoneList.size() > 0) {
            String utcZoneStr = String.valueOf(allPowerStationUTCZoneList.get(0).get("utcZone"));
            untZoneMinute = DateUtils.getUTCZoneMinute(utcZoneStr);
        }

        List<CidRelation> relationList = cidRelationService.selectCidRelationByStationIdWithGroup(cidRelation.getPowerStationId(), cidRelation.getCid(), cidRelation.getVid());
        List<CidRelation> returnList = new ArrayList<>();
        for (Object obj : relationList) {
            String s = JSON.toJSONString(obj);
            CidRelation relation = null;
            try {
                relation = JSON.parseObject(s, CidRelation.class);
            } catch (Exception ex) {
                logger.info("s:" + s);
                ex.printStackTrace();
            }
            if (relation == null || relation.getVid() == null)
                continue;

            relation.setPowerStationId(cidRelation.getPowerStationId());
            String vidHead = relation.getVid().substring(0, 1);
            if ( !relation.getStatus().equals("3")) {

                List<CidRelation> cidVidRoadRelationList = cidVidRoadRelationService.selectVidListByCidAndPowerStationId(relation);
                relation.setChildList(cidVidRoadRelationList);


            }
            returnList.add(relation);
        }
        return AjaxResult.success(returnList);
    }


    /**
     * 查询网关、微逆关系列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:getRelationListWithPower')")
    @GetMapping("/getRelationListWithPower")
    public AjaxResult getRelationListWithPower(CidRelation cidRelation) {
        List<CidRelation> relationList = cidRelationService.selectCidRelationByStationIdWithGroup(cidRelation.getPowerStationId(), null, null);
        return AjaxResult.success(relationList);
    }

    @GetMapping("/getCidVidDetail")
    public AjaxResult getCidVidDetail(String cid, String vid, String roadType) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String beginTime = format.format(new Date());
        CidRelation relation = cidRelationService.getCidOrVidDetail(cid, vid, null);
        logger.info("===cid:" + cid + " vid:" + vid + " getCidVidDetail:" + JSON.toJSONString(relation));
        Long maxId = cidDataService.getMaxDataByCidVidLoop(relation.getCid(), relation.getVid(), relation.getRoadType());

        Integer untZoneMinute = 0;
        List<Map> allPowerStationUTCZoneList = powerstationinfoService.selectUTCZoneOfPowerstationByID(relation.getPowerStationId());
        if (allPowerStationUTCZoneList != null & allPowerStationUTCZoneList.size() > 0) {
            String utcZoneStr = String.valueOf(allPowerStationUTCZoneList.get(0).get("utcZone"));
            untZoneMinute = DateUtils.getUTCZoneMinute(utcZoneStr);

            if (untZoneMinute == null) {
                System.out.println("===CidRelationController getCidVidDetail untZoneMinute is null ,plantId:" + relation.getPowerStationId());
                untZoneMinute = 0;
            }
        }

        if (maxId != null && maxId > 0) {
            String status = "";
            CidData data = cidDataService.selectCidDataById(maxId);
//            relation.setCurrentPower(data.getPower());
            relation.setLastDate(format.format(data.getCreateDate()));
            relation.setSoftVersion(data.getSoftVersion());
            relation.setHardVersion(data.getSoftDeputyVersion());
            relation.setLastError(data.getM1MError());
            relation.setLastErrorCode(data.getM1MErrorCode());
            relation.setLastSError(data.getM1SError());
            relation.setLastSErrorCode(data.getM1SErrorCode());
            String endTime = format.format(data.getCreateDate());

            Date date1 = format.parse(beginTime);
            Date date2 = format.parse(endTime);

            long beginMillisecond = date1.getTime();
            long endMillisecond = date2.getTime();
            long jian = beginMillisecond - endMillisecond;

            Date onlineDate = new Date(data.getCreateDate().getTime() + 60 * 60 * 1000);
            Date nowUTC = new Date(new Date().getTime() + untZoneMinute * 60 * 1000);
            System.out.println("===CidRelationController getCidVidDetail cid:" + data.getCid() + " vid:" + data.getVid() + " onlineDate:" + onlineDate + "   nowUTC：" + nowUTC);

            if (relation.getStatus() == null) {
                relation.setStatus("OffLine");
            } else {
                if (relation.getStatus().equals("0")) {
                    status = "OnLine";
                } else if (relation.getStatus().equals("1")) {
                    status = "Error";
                } else {
                    status = "OffLine";
                    relation.setLastErrorCode("----");
                    relation.setLastError("----");
                    relation.setLastSErrorCode("----");
                    relation.setLastSError("----");
                    relation.setCurrentPower("0");
                    relation.setCurrentEnergy("0");
                }
            }
            relation.setStatus(status);
        }

//        CidPowerstationinfo powerstationinfo = powerstationinfoService.selectCidPowerstationinfoById(relation.getPowerStationId());
//        relation.setPowerStationName(powerstationinfo.getEnergyName());
        return AjaxResult.success(relation);
    }

    /**
     * 查询根据网关查询微逆
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:getRelationList')")
    @GetMapping("/getEmuListByCid")
    public AjaxResult getEmuListByCid(String cid) {
        CidRelation query = new CidRelation();
        query.setCid(cid);
        query.setDelFlag("0");
        List<CidRelation> relationList = cidRelationService.selectCidRelationList(query);
        return AjaxResult.success(relationList);
    }


    @GetMapping("/getEmuList")
    public AjaxResult getEmuList(Long plantId, String cidType) {

        long startTime = System.currentTimeMillis();
        LoginUser loginUser = getLoginUser();
        List<CidPowerstationinfo> list = null;

        if (cidType == null) {
            cidType = "02";
        } else if (cidType.equals("all")) {
            cidType = null;
        }

        if (plantId == null) {
            CidPowerstationinfo cidPowerstationinfo = new CidPowerstationinfo();
            cidPowerstationinfo.setDelFlag("0");
            if (loginUser.getDeptId() != 100) {
                cidPowerstationinfo.setEnergyDeptId(loginUser.getDeptId());
            }
            //  startPage();
            list = powerstationinfoService.selectCidPowerstationinfoListForPlantList(cidPowerstationinfo);

        } else {
            list = new ArrayList<>();
            CidPowerstationinfo cidPowerstationinfo = new CidPowerstationinfo();
            cidPowerstationinfo.setDelFlag("0");
            cidPowerstationinfo.setId(plantId);
            list = powerstationinfoService.selectCidPowerstationinfoListForPlantList(cidPowerstationinfo);
        }

        List<Map> emuTotalList = new ArrayList<>();
        logger.info("list:"+list.size());
        for (int a = 0; a < list.size(); a++) {

            Map paramMap =new HashMap();
            paramMap.put("plantID",list.get(a).getId());
            paramMap.put("cidType",cidType);
            logger.info("paramMap :"+paramMap);

            List<Map> emuList = cidEntityMapper.selectCidList(paramMap);
            logger.info("emuList size:"+emuList.size());

            if (emuList != null) {
                for (int i = 0; i < emuList.size(); i++) {

                    logger.info(JSON.toJSONString(emuList.get(i)));
                    if (emuList.get(i).get("status").equals("2")) {
                        emuList.get(i).put("status", "3");
                    }
                    if (emuList.get(i).get("status").equals("1")) {
                        emuList.get(i).put("status", "0");
                    }
                    emuList.get(i).put("energyName", list.get(a).getEnergyName());
                    emuList.get(i).put("energyId", list.get(a).getId());
                    emuTotalList.add(emuList.get(i));
                }
            }
        }

//        for (int a = 0; a < list.size(); a++) {
//            List<Map> emuList = cidRelationService.selectEmuListOnly(loginUser.getDeptId(), list.get(a).getId(), null, cidType);
//            if (emuList != null) {
//                for (int i = 0; i < emuList.size(); i++) {
//                    if (emuList.get(i).get("status").equals("2")) {
//                        emuList.get(i).put("status", "3");
//                    }
//                    if (emuList.get(i).get("status").equals("1")) {
//                        emuList.get(i).put("status", "0");
//                    }
//                    emuList.get(i).put("energyName", list.get(a).getEnergyName());
//                    emuList.get(i).put("energyId", list.get(a).getId());
//                    emuTotalList.add(emuList.get(i));
//                }
//            }
//
//        }

        AjaxResult ajaxResult = AjaxResult.success(emuTotalList);
        return ajaxResult;
    }

    @GetMapping("/getVidList")
    public AjaxResult getVidList(String cid, Long plantId) {
        LoginUser loginUser = getLoginUser();
        CidRelation queryRelation = new CidRelation();
        queryRelation.setDelFlag("0");
        queryRelation.setCid(cid);
        queryRelation.setPowerStationId(plantId);
        List<CidRelation> vidList = cidRelationService.selectCidRelationList(queryRelation);

//        logger.info("===CidRelatonController getVidList:"+vidList);

        List<CidRelation> vidListNew = new ArrayList<>(vidList.size());
        for (CidRelation cidRelation : vidList) {

            CidRelation cidVidRoadRelation = JSON.parseObject(JSON.toJSONString(cidRelation), CidRelation.class);
            if (cidVidRoadRelation.getVid() == null) {
                logger.info("cidVidRoadRelation.getVid()==null  cidVidRoadRelation:" + cidVidRoadRelation);
                continue;
            }
            String vidHeadStr = cidVidRoadRelation.getVid().substring(0, 1);

            int vidHead = 0;
            Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
            vidListNew.add(cidVidRoadRelation);
        }

        logger.info("===CidRelationController  getVidList vidListNew:" + JSON.toJSONString(vidListNew));
        AjaxResult ajaxResult = AjaxResult.success(vidListNew);
        return ajaxResult;
    }

    @GetMapping("/analyseDeviceStatus")
    public AjaxResult analyseDeviceStatus(Long plantId) throws ParseException {

        LoginUser loginUser = getLoginUser();
        if (plantId == null)
            plantId = -1l;

        Map analyseResultMap = new HashMap();
        logger.info("===CidRelationController analyseDeviceStatus plantId:" + plantId + "  loginUser.getDeptId()：" + loginUser.getDeptId());

        List<Map> analyseResultList = cidRelationService.analyseDeviceStatus(plantId, loginUser.getDeptId());
        logger.info("===CidRelationController analyseDeviceStatus analyseResultList:" + analyseResultList);

        if (analyseResultList != null) {
            for (int i = 0; i < analyseResultList.size(); i++) {
                analyseResultMap.put(analyseResultList.get(i).get("status"), analyseResultList.get(i).get("count"));
            }
        }

        logger.info("===CidRelationController analyseDeviceStatus analyseResultMap:" + analyseResultMap);
        AjaxResult ajaxResult = AjaxResult.success(analyseResultMap);
        return ajaxResult;
    }


    @GetMapping("/getEmuTreeTable")
    public AjaxResult getEmuTreeTable(String cid, Long plantId) throws ParseException {

        long beginCost = System.currentTimeMillis();
        LoginUser loginUser = getLoginUser();
        System.out.println("===CidRelationController getEmuTreeTable cid:" + cid + "  plantId:" + plantId);

        List<CidRelation> returnList = new ArrayList<>();
        List<Map> emuList = null;
        if (plantId != null & cid != null) {
            Map map = new HashMap();
            map.put("cid", cid);
            map.put("powerStationId", plantId);

            emuList = new ArrayList<>();
            emuList.add(map);

        } else {
            emuList = cidRelationService.selectEmuList(loginUser.getDeptId(), plantId, cid);
            System.out.println("===CidRelationController emuList size:" + emuList.size());

        }


        int i = 0;
        for (Object obj : emuList) {

            String s = JSON.toJSONString(obj);
            CidRelation relation = JSON.parseObject(s, CidRelation.class);


            CidRelation queryRelation = new CidRelation();
            queryRelation.setDelFlag("0");
            queryRelation.setCid(relation.getCid());
            queryRelation.setPowerStationId(plantId);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            i = i + 1;
            Integer untZoneMinute = 0;

            List<Map> allPowerStationUTCZoneList = powerstationinfoService.selectUTCZoneOfPowerstationByID(relation.getPowerStationId());

            if (allPowerStationUTCZoneList != null & allPowerStationUTCZoneList.size() > 0) {
                String utcZoneStr = String.valueOf(allPowerStationUTCZoneList.get(0).get("utcZone"));
                untZoneMinute = DateUtils.getUTCZoneMinute(utcZoneStr);

                if (untZoneMinute == null) {
                    untZoneMinute = 0;
                }
            }


            List<CidRelation> vidList = cidRelationService.selectCidRelationList(queryRelation);
            List<CidRelation> childList = new ArrayList<>();
            Float currentPower = 0f;
            Float currentEnergy = 0f;

            boolean hasError = false;

            String onlineStatus = "OnLine";
            List<Map> heartList = cidDataService.selectLastUpdateTime(relation.getCid());
            String cidLastHeart = "2022-10-01 00:00:00";

            if (heartList.size() > 0) {
                try {
                    Date date1 = format.parse(String.valueOf(heartList.get(0).get("createDate")));
                    cidLastHeart = format.format(date1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            int offlineCount = 0;
            int onlineCount = 0;
            int errorCount = 0;
            String addTime = "";
            boolean isHeart = false;
            for (CidRelation r : vidList) {
                Long id = cidDataService.selectEmuTreeMisId(r.getCid(), r.getVid(), r.getRoadType());

                if (id != null) {
                    CidData child = cidDataService.selectCidDataById(id);
                    relation.setSoftVersion(child.getSoftVersion());
                    CidRelation addRelation = new CidRelation();
                    addRelation.setCid(child.getVid());
                    addRelation.setCidType("MIs");

                    if (child.getRoadType() != null) {
                        addRelation.setRoadType(child.getRoadType());
                    }

                    String dateNow = sdf.format(new Date());
                    String lastIndexDate = sdf.format(child.getCreateDate());

                    String beginTime = format.format(new Date());
                    String endTime = format.format(child.getCreateDate());
                    Date date1 = format.parse(beginTime);
                    Date date2 = format.parse(endTime);

                    long beginMillisecond = date1.getTime();
                    long endMillisecond = date2.getTime();
                    long jian = beginMillisecond - endMillisecond;
                    String status = "";

                    if (addTime != "") {
                        Date date3 = format.parse(addTime);
                        if (date3.before(date2)) {
                            addTime = format.format(child.getCreateDate());
                        }
                    } else {
                        addTime = format.format(child.getCreateDate());
                    }

                    if (r.getStatus() != null & r.getStatus().equals("0")) {
                        status = "OnLine";
                        onlineStatus = "OnLine";
                        onlineCount++;
                        relation.setOnlineStatus("OnLine");
                        relation.setStatus("OnLine");
                    } else {
                        status = "OffLine";
                        onlineStatus = "OffLine";
                        offlineCount++;
                    }
                    if (r.getStatus() != null & r.getStatus().equals("1")) {
                        onlineStatus = "Error";
                        errorCount++;
                    }

//                    if (child.getM1MErrorCode() != null) {
//                        if (!child.getM1MErrorCode().equals("0000") && !child.getM1MErrorCode().equals("0200") && !child.getM1MErrorCode().equals("0008")) {
//                            hasError = true;
//                            if (!onlineStatus.equals("OffLine")) {
//                                onlineStatus = "Error";
//                                errorCount++;
//                            }
//                            if (!status.equals("OffLine")) {
//                                status = "Error";
//                            }
//                        }
//                    }

                    if (dateNow.equals(lastIndexDate)) {
                        CidRelation childQueryRelation = new CidRelation();
                        childQueryRelation.setCid(child.getCid());
                        childQueryRelation.setVid(child.getVid());
                        if (child.getRoadType() != null) {
                            childQueryRelation.setRoadType(child.getRoadType());
                        } else {
                            childQueryRelation.setRoadType(null);
                        }
                        childQueryRelation.setDelFlag("0");
                        childQueryRelation.setIsConfirm("0");
                        List<CidRelation> childEnergy = cidRelationService.selectCidRelationList(childQueryRelation);
                        if (childEnergy.size() > 0) {
                            CidRelation childRelation = childEnergy.get(0);
                            currentEnergy = (currentEnergy + Float.parseFloat(childRelation.getCurrentEnergy()));
                            addRelation.setCurrentEnergy(childRelation.getCurrentEnergy());
                            addRelation.setCurrentPower(child.getPower());
                        }

                    } else {
                        currentPower = (currentPower + 0f);
                        currentEnergy = (currentEnergy + 0f);
                        addRelation.setCurrentEnergy("0");
                        addRelation.setCurrentPower("0");
                    }
                    addRelation.setHasError(hasError);
                    addRelation.setStatus(status);
                    addRelation.setVid(r.getCid());


                    if (status.equals("OffLine")) {
                        //currentPower=(currentPower-Float.parseFloat(child.getPower()));
                        addRelation.setCurrentPower("0");
                        addRelation.setLastError("----");
                        addRelation.setLastErrorCode("----");
                    } else {
                        currentPower = (currentPower + Float.parseFloat(child.getPower()));
                    }
                    addRelation.setLastDate(endTime);
                    //addRelation.setPowerStationName(plantName);
                    addRelation.setSoftVersion(child.getSoftVersion());
                    addRelation.setSoftDeputyVersion(child.getSoftDeputyVersion());
                    childList.add(addRelation);

                    //心跳和发电时间比较 并且判断 是否为error
                    //System.out.println("cidLastHeart:"+cidLastHeart.equals(""));
//                    if(cidLastHeart.equals("")){
                    relation.setLastDate(endTime);

                    if (offlineCount == childList.size() && !cidLastHeart.equals("")) {
                        isHeart = true;
                        Date heartDate = format.parse(cidLastHeart);
                        long cidBeginMillisecond = heartDate.getTime();
                        long cidEndMillisecond = date1.getTime();
                        long cidJian = cidEndMillisecond - cidBeginMillisecond;
                        //beginTime.substring(0,10).equals(endTime.substring(0,10))
                        if (beginTime.substring(0, 10).equals(cidLastHeart.substring(0, 10))) {
                            if (!(cidJian > 1000 * 60 * 60) && cidJian > 0) {
                                onlineStatus = "OnLine";
                                onlineCount++;
                            } else {
                                onlineStatus = "OffLine";
                            }
                        } else {
                            onlineStatus = "OffLine";
                        }
//                        System.out.println("onlineStatus:"+onlineStatus);
                        relation.setCurrentPower("0");
                        relation.setStatus(onlineStatus);
                        if (relation.getUpdateTime() != null) {
                            Date cidLastHeartDate = format.parse(cidLastHeart);
                            Date relationLastUpdateDate = relation.getUpdateTime();
                            if (cidLastHeartDate.after(relationLastUpdateDate))
                                relation.setLastDate(cidLastHeart);
                        }

                    }
                } else {
                    onlineStatus = "OffLine";
                    CidRelation addRelation = new CidRelation();
                    addRelation.setCid(r.getVid());
                    addRelation.setVid(r.getCid());
                    addRelation.setHasError(false);
                    addRelation.setCidType("MIs");
                    addRelation.setStatus("OffLine");
                    addRelation.setSoftVersion(r.getSoftVersion());
                    addRelation.setSoftDeputyVersion(r.getSoftDeputyVersion());
                    addRelation.setCurrentPower("0");
                    addRelation.setCurrentEnergy("0");
                    addRelation.setLastErrorCode("----");
                    addRelation.setLastError("----");
                    addRelation.setRoadType(r.getRoadType());
                    //addRelation.setPowerStationName(plantName);
                    childList.add(addRelation);
                    offlineCount++;
                }

            }

            if (!isHeart) {
                if (onlineCount > 0 && errorCount == 0) {
                    relation.setStatus("OnLine");
                } else if (onlineCount > 0 && errorCount > 0) {
                    System.out.println("Addtime:" + addTime);
                    relation.setStatus("Error");
                } else if (offlineCount == childList.size()) {
                    relation.setStatus("OffLine");
                }
                if (addTime != null && !addTime.equals("") & relation.getUpdateTime() != null) {
                    Date addTimeDate = format.parse(addTime);
                    Date relationLastUpdateDate = relation.getUpdateTime();
                    if (addTimeDate.after(relationLastUpdateDate))
                        relation.setLastDate(addTime);
                }
            }

            if (onlineStatus.equals("OffLine")) {
                relation.setLastErrorCode("----");
                relation.setLastError("----");
            }

            relation.setHasError(hasError);
            relation.setCidType("EMU");
            relation.setChildList(childList);
            relation.setCurrentEnergy(String.valueOf(currentEnergy));
            relation.setCurrentPower(String.valueOf(currentPower));

            String plantName = "";
            plantName = powerstationinfoService.selectCidPowerstationinfoById(relation.getPowerStationId()).getEnergyName();
            relation.setPowerStationName(plantName);
            returnList.add(relation);

        }
        System.out.println("===CidRelationController returnList:" + returnList.size());

        AjaxResult ajaxResult = AjaxResult.success(returnList);
        return ajaxResult;
    }


//    @GetMapping("/getEmuTreeTable")
//    public AjaxResult getEmuTreeTable(String cid,Long plantId) throws ParseException {
//
//        long beginCost= System.currentTimeMillis();
//        LoginUser loginUser = getLoginUser();
//        System.out.println("===CidRelationController getEmuTreeTable cid:"+cid+"  plantId:"+plantId);
//
//        List<CidRelation> returnList= new ArrayList<>();
//        List<Map> emuList = cidRelationService.selectEmuList(loginUser.getDeptId(),plantId,cid);
//        System.out.println("===CidRelationController getEmuTreeTable cost0:"+(beginCost-System.currentTimeMillis()));
//
//        int i=0;
//        for(Object obj:emuList){
//
//            String s= JSON.toJSONString(obj);
//            CidRelation relation = JSON.parseObject(s,CidRelation.class);
//            CidRelation queryRelation =new CidRelation();
//            queryRelation.setDelFlag("0");
//            queryRelation.setCid(relation.getCid());
//            queryRelation.setPowerStationId(plantId);
//
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//            SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
//
//            System.out.println("===CidRelationController getEmuTreeTable cost1 i:"+i+" ========================== cid:"+queryRelation.getCid());
//            i=i+1;
//            Integer untZoneMinute=0;
//
//            List<Map> allPowerStationUTCZoneList = powerstationinfoService.selectUTCZoneOfPowerstationByID(relation.getPowerStationId());
//            System.out.println("===CidRelationController getEmuTreeTable cost10:"+(beginCost-System.currentTimeMillis()));
//
//            if (allPowerStationUTCZoneList != null & allPowerStationUTCZoneList.size() > 0) {
//                String utcZoneStr = String.valueOf(allPowerStationUTCZoneList.get(0).get("utcZone"));
//                untZoneMinute = getUTCZoneMinute(utcZoneStr);
//
//                if (untZoneMinute==null){
//                    System.out.println("===CidRelationController getEmuTreeTable untZoneMinute is null ,plantId:"+plantId );
//                    untZoneMinute=0;
//                }
//            }
//
//            System.out.println("===CidRelationController getEmuTreeTable selectCidRelationList queryRelation:"+JSON.toJSONString(queryRelation));
//
//            List<CidRelation> vidList=cidRelationService.selectCidRelationList(queryRelation);
//            System.out.println("===CidRelationController getEmuTreeTable cost11:"+(beginCost-System.currentTimeMillis()));
//
//            List<CidRelation> childList=new ArrayList<>();
//            Float currentPower=0f;
//            Float currentEnergy=0f;
//
//            boolean hasError=false;
//
//            String onlineStatus="OnLine";
//            List<Map> heartList=cidDataService.selectLastUpdateTime(relation.getCid());
//            String cidLastHeart="2022-10-01 00:00:00";
//            System.out.println("===CidRelationController getEmuTreeTable cost12:"+(beginCost-System.currentTimeMillis()));
//
//            if(heartList.size()>0){
//                try {
//                    Date date1 = format.parse(String.valueOf(heartList.get(0).get("createDate")));
//                    cidLastHeart = format.format(date1);
//                }catch (Exception exception){
//                    exception.printStackTrace();
//                }
//            }
//
//
//            System.out.println("===CidRelationController getEmuTreeTable cost-19:"+(beginCost-System.currentTimeMillis())+" vid size:"+vidList.size());
//
//            int offlineCount=0;
//            int onlineCount=0;
//            int errorCount=0;
//            String addTime="";
//            boolean isHeart=false;
//            for (CidRelation r:vidList) {
//                Long id=cidDataService.selectEmuTreeMisId(r.getCid(),r.getVid(),r.getRoadType());
//
//                if(id!=null){
//                    CidData child=cidDataService.selectCidDataById(id);
//                    relation.setSoftVersion(child.getSoftVersion());
//                    CidRelation addRelation=new CidRelation();
//                    addRelation.setCid(child.getVid());
//                    addRelation.setCidType("MIs");
//
//                    if(child.getRoadType()!=null){
//                        System.out.println("add???---"+child.getRoadType());
//                        addRelation.setRoadType(child.getRoadType().substring(1,2));
//                    }
//
//                    String dateNow=sdf.format(new Date());
//                    String lastIndexDate=sdf.format(child.getCreateDate());
//
//                    String beginTime = format.format(new Date());
//                    String endTime = format.format(child.getCreateDate());
//                    Date date1 = format.parse(beginTime);
//                    Date date2 = format.parse(endTime);
//
//                    long beginMillisecond = date1.getTime();
//                    long endMillisecond = date2.getTime();
//                    long jian=beginMillisecond-endMillisecond;
//                    String status="";
//
//                    if(addTime!=""){
//                        Date date3 = format.parse(addTime);
//                        if(date3.before(date2)){
//                            addTime=format.format(child.getCreateDate());
//                        }
//                    }else{
//                        addTime=format.format(child.getCreateDate());
//                    }
//
//
//                    Date onlineDate=new Date(child.getCreateDate().getTime()+15*60*1000);
//                    Date nowUTC=new Date(new Date().getTime()+untZoneMinute*60*1000);
//                    if(onlineDate.getTime() >nowUTC.getTime()){
//                        System.out.println("childAddtime:"+addTime);
//                        status="OnLine";
//                        onlineStatus="OnLine";
//                        onlineCount++;
//                        relation.setOnlineStatus("OnLine");
//                        relation.setStatus("OnLine");
//                    }else{
//                        status="OffLine";
//                        onlineStatus="OffLine";
//                        offlineCount++;
//                    }
////                    }else{
////                        status="OffLine";
////                        onlineStatus="OffLine";
////                        offlineCount++;
////                    }
//
//
//                    if(child.getM1MErrorCode()!=null){
//                        if(!child.getM1MErrorCode().equals("0000")&&!child.getM1MErrorCode().equals("0200")){
//                            hasError=true;
//                            if(!onlineStatus.equals("OffLine")){
//                                onlineStatus="Error";
//                                errorCount++;
//                            }
//                            if(!status.equals("OffLine")){
//                                status="Error";
//                            }
//                        }
//                    }
//
//                    if(dateNow.equals(lastIndexDate)){
//                        CidRelation childQueryRelation = new CidRelation();
//                        childQueryRelation.setCid(child.getCid());
//                        childQueryRelation.setVid(child.getVid());
//                        if(child.getRoadType()!=null){
//                            childQueryRelation.setRoadType(child.getRoadType());
//                        }else{
//                            childQueryRelation.setRoadType(null);
//                        }
//                        childQueryRelation.setDelFlag("0");
//                        childQueryRelation.setIsConfirm("0");
//                        List<CidRelation> childEnergy = cidRelationService.selectCidRelationList(childQueryRelation);
//
//                        CidRelation childRelation = childEnergy.get(0);
//                        currentEnergy=(currentEnergy+Float.parseFloat(childRelation.getCurrentEnergy()));
//                        addRelation.setCurrentEnergy(childRelation.getCurrentEnergy());
//                        addRelation.setCurrentPower(child.getPower());
//                    }else{
//                        currentPower=(currentPower+0f);
//                        currentEnergy=(currentEnergy+0f);
//                        addRelation.setCurrentEnergy("0");
//                        addRelation.setCurrentPower("0");
//                    }
//                    addRelation.setHasError(hasError);
//                    addRelation.setStatus(status);
//                    addRelation.setVid(r.getCid());
//
//
//                    if(status.equals("OffLine")){
//                        //currentPower=(currentPower-Float.parseFloat(child.getPower()));
//                        addRelation.setCurrentPower("0");
//                        addRelation.setLastError("----");
//                        addRelation.setLastErrorCode("----");
//                    }else{
//                        currentPower=(currentPower+Float.parseFloat(child.getPower()));
//                    }
//                    addRelation.setLastDate(endTime);
//                    //addRelation.setPowerStationName(plantName);
//                    addRelation.setSoftVersion(child.getSoftVersion());
//                    addRelation.setSoftDeputyVersion(child.getSoftDeputyVersion());
//                    childList.add(addRelation);
//
//                    //心跳和发电时间比较 并且判断 是否为error
//                    //System.out.println("cidLastHeart:"+cidLastHeart.equals(""));
////                    if(cidLastHeart.equals("")){
//                    relation.setLastDate(endTime);
//
//                    if(offlineCount==childList.size()&&!cidLastHeart.equals("")){
//                        isHeart=true;
//                        Date heartDate = format.parse(cidLastHeart);
//                        long cidBeginMillisecond = heartDate.getTime();
//                        long cidEndMillisecond = date1.getTime();
//                        long cidJian=cidEndMillisecond-cidBeginMillisecond;
//                        //beginTime.substring(0,10).equals(endTime.substring(0,10))
//                        if(beginTime.substring(0,10).equals(cidLastHeart.substring(0,10))){
//                            if(!(cidJian>1000*60*30)&&cidJian>0){
//                                onlineStatus="OnLine";
//                                onlineCount++;
//                            }else{
//                                onlineStatus="OffLine";
//                            }
//                        }else{
//                            onlineStatus="OffLine";
//                        }
////                        System.out.println("onlineStatus:"+onlineStatus);
//                        relation.setCurrentPower("0");
//                        relation.setStatus(onlineStatus);
//                        if (relation.getUpdateTime()!=null){
//                            Date cidLastHeartDate = format.parse(cidLastHeart);
//                            Date relationLastUpdateDate = relation.getUpdateTime();
//                            if (cidLastHeartDate.after(relationLastUpdateDate))
//                                relation.setLastDate(cidLastHeart);
//                        }
//
//                    }
//                }else{
//                    onlineStatus="OffLine";
//                    CidRelation addRelation=new CidRelation();
//                    addRelation.setCid(r.getVid());
//                    addRelation.setVid(r.getCid());
//                    addRelation.setHasError(false);
//                    addRelation.setCidType("MIs");
//                    addRelation.setStatus("OffLine");
//                    addRelation.setSoftVersion(r.getSoftVersion());
//                    addRelation.setSoftDeputyVersion(r.getSoftDeputyVersion());
//                    addRelation.setCurrentPower("0");
//                    addRelation.setCurrentEnergy("0");
//                    addRelation.setLastErrorCode("----");
//                    addRelation.setLastError("----");
//                    addRelation.setRoadType(r.getRoadType());
//                    //addRelation.setPowerStationName(plantName);
//                    childList.add(addRelation);
//                    offlineCount++;
//                }
//
//            }
//            System.out.println("===CidRelationController getEmuTreeTable cost-2:"+(beginCost-System.currentTimeMillis()));
//
//            if(!isHeart){
//                if(onlineCount>0&&errorCount==0) {
//                    relation.setStatus("OnLine");
//                }else if(onlineCount>0&&errorCount>0){
//                    System.out.println("Addtime:"+addTime);
//                    relation.setStatus("Error");
//                }else if(offlineCount==childList.size()){
//                    relation.setStatus("OffLine");
//                }
//                if (addTime!=null && !addTime.equals("") & relation.getUpdateTime()!=null){
//                    Date addTimeDate = format.parse(addTime);
//                    Date relationLastUpdateDate = relation.getUpdateTime();
//                    if (addTimeDate.after(relationLastUpdateDate))
//                        relation.setLastDate(addTime);
//                }
//            }
//
//
//            if(onlineStatus.equals("OffLine")){
//                relation.setLastErrorCode("----");
//                relation.setLastError("----");
//            }
//            relation.setHasError(hasError);
//            relation.setCidType("EMU");
//            relation.setChildList(childList);
//            relation.setCurrentEnergy(String.valueOf(currentEnergy));
//            relation.setCurrentPower(String.valueOf(currentPower));
//
//            String plantName="";
//            plantName=powerstationinfoService.selectCidPowerstationinfoById(relation.getPowerStationId()).getEnergyName();
//            relation.setPowerStationName(plantName);
//            returnList.add(relation);
//
//        }
//        System.out.println("===CidRelationController getEmuTreeTable cost3:"+(beginCost-System.currentTimeMillis()));
//
//
//        System.out.println("===CidRelationController returnList:"+returnList.size());
//
//        AjaxResult ajaxResult =AjaxResult.success(returnList);
//        return ajaxResult;
//    }

    /**
     * 导出网关、微逆关系列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:export')")
    @Log(title = "网关、微逆关系", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CidRelation cidRelation) {
        List<CidRelation> list = cidRelationService.selectCidRelationList(cidRelation);
        ExcelUtil<CidRelation> util = new ExcelUtil<CidRelation>(CidRelation.class);
        util.exportExcel(response, list, "网关、微逆关系数据");
    }

    /**
     * 获取网关、微逆关系详细信息
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(cidRelationService.selectCidRelationById(id));
    }


    /**
     * 新增网关、微逆关系
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:add')")
    @Log(title = "网关、微逆关系", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult addDevice(@RequestBody CidRelation cidRelationReq) {

        System.out.println("===CidRelationController addDevice cidRelationReq:" + JSON.toJSONString(cidRelationReq));
        AjaxResult ajaxResult = AjaxResult.success();
        long plantId = cidRelationReq.getPowerStationId();

        if (cidRelationReq.getCidType() == null)
            cidRelationReq.setCidType("02");

        if (plantId <= 0 || cidRelationReq.getCid() == null || cidRelationReq.getVid() == null) {
            logger.error("===CidRelationController addDevice cidRelationReq parameter is error :" + JSON.toJSONString(cidRelationReq));
            ajaxResult.put("msg", "parameter is illegal");
            ajaxResult.put("code", 100);
            return ajaxResult;
        }


        String[] vidArr = null;
        List vidList = new ArrayList();

        if (cidRelationReq != null && cidRelationReq.getVid() != null) {
            vidArr = cidRelationReq.getVid().split(",");
        }
        Map vidIsScanMap = new HashMap();
        if (vidArr != null) {
            for (int i = 0; i < vidArr.length; i++) {
                if (vidArr[i] != null && !vidArr[i].equals("")) {
                    if (vidArr[i].indexOf("_") > 0) {
                        vidList.add(vidArr[i].substring(0, vidArr[i].indexOf("_")));
                        vidIsScanMap.put(vidArr[i].substring(0, vidArr[i].indexOf("_")), vidArr[i].substring(vidArr[i].indexOf("_") + 1, vidArr[i].length()));
                    } else {
                        vidList.add(vidArr[i]);
                    }
                }
            }
        }


        Integer untZoneMinute = 0;
        List<Map> allPowerStationUTCZoneList = powerstationinfoService.selectUTCZoneOfPowerstationByID(plantId);
        if (allPowerStationUTCZoneList != null & allPowerStationUTCZoneList.size() > 0) {
            String utcZoneStr = String.valueOf(allPowerStationUTCZoneList.get(0).get("utcZone"));
            untZoneMinute = DateUtils.getUTCZoneMinute(utcZoneStr);
            if (untZoneMinute == null) {
                System.out.println("===CidRelationController addDevice untZoneMinute is null ,plantId:" + plantId);
                untZoneMinute = 0;
            }
        }

        int returnResult = 0;
        Date nowUTC = new Date(new Date().getTime() + untZoneMinute * 60 * 1000);

        Map roadCidRelationMap = new HashMap();
        roadCidRelationMap.put("cid",cidRelationReq.getCid());
        roadCidRelationMap.put("plantID",plantId);
        List<CidEntity>  cidEntityList = cidEntityMapper.selectCidEntityByCidAndPlantID(roadCidRelationMap);
        System.out.println("===cidEntityList:" + JSON.toJSONString(cidEntityList));

        if (cidEntityList.size()==0){
            CidEntity cidEntity = new CidEntity();
            cidEntity.setCidType(cidRelationReq.getCidType());
            cidEntity.setCid(cidRelationReq.getCid());
            cidEntity.setPlantID(plantId);
            cidEntity.setCreateTime(nowUTC);
            cidEntity.setStatus("3");
            cidEntityMapper.insertCidEntity(cidEntity);
        }

        String exitstVidMsg="";
        for (int a = 0; a < vidList.size(); a++) {

            String vid = vidList.get(a).toString();
            String vidHeadStr = vid.substring(0, 1);
            if (vidHeadStr.toLowerCase().equals("c")) {
                vidHeadStr = "4";
            } else if (vidHeadStr.toLowerCase().equals("a")) {
                vidHeadStr = "1";
            } else if (vidHeadStr.toLowerCase().equals("b")) {
                vidHeadStr = "2";
            }

            vid=vidHeadStr+vid.substring(1,vid.length());
            Map paramMap = new HashMap();
            paramMap.put("cid", cidRelationReq.getCid());
            paramMap.put("vid", vid);
            System.out.println("===existedaRelationList paramMap:" + JSON.toJSONString(paramMap));

            String isScanFlag = null;
            List<Map> existedaRelationList = cidRelationService.selectPowerstationIDByCidAndVid(paramMap);
            System.out.println("===existedaRelationList:" + JSON.toJSONString(existedaRelationList));

            if (existedaRelationList.size() > 0) {
                //如果不是扫描的，重复的vid将不会被添加
                isScanFlag = vidIsScanMap.get(vid) != null ? vidIsScanMap.get(vid).toString() : null;
                if (isScanFlag == null || (isScanFlag != null & isScanFlag.equals("1"))) {
                    logger.error("===CidRelationController addDevice cidRelationReq parameter is error :" + JSON.toJSONString(cidRelationReq));
                    exitstVidMsg=exitstVidMsg+" "+vid;
                    continue;
                }
            }

            List<Map> cidRelationList = cidRelationService.selectVidByCidAndPlantID(vid, plantId);
            int roadCount = 0;
            for (int i = 0; i < cidRelationList.size(); i++) {

                Map cidRelation = JSON.parseObject(JSON.toJSONString(cidRelationList.get(i)), Map.class);
                if (cidRelation.get("vid") == null)
                    continue;
                int vidHead = Integer.parseInt(vidHeadStr);
                roadCount = roadCount + vidHead;
            }

            if (roadCount >= 24) {
                ajaxResult.put("msg", "The total number of microinverter roads exceeded 32.");
                ajaxResult.put("code", 100);
                return ajaxResult;
            }


            CidRelation cidRelation = new CidRelation();
            cidRelation.setCidType(cidRelationReq.getCidType());
            String vidHead = vid.substring(0, 1);
            if (vidHead.toLowerCase().equals("c")) {
                vidHead = "4";
            } else if (vidHead.toLowerCase().equals("a")) {
                vidHead = "1";
            } else if (vidHead.toLowerCase().equals("b")) {
                vidHead = "2";
            }
            cidRelation.setRoadType(vidHead);

            if (cidRelationReq.getCidType() != null) {
                cidRelation.setCid(cidRelationReq.getCid());
                cidRelation.setVid(vidHead + vid.substring(1, vid.length()));
                cidRelation.setCidType(cidRelationReq.getCidType());

            } else {
                cidRelation.setVid(vid);
                cidRelation.setCid(cidRelationReq.getCid());
                cidRelation.setCidType("02");
            }

            cidRelation.setPowerStationId(plantId);
            cidRelation.setUpdateTime(nowUTC);
            cidRelation.setStatus("3");
            cidRelation.setRoadType(vidHead);


            //先不区分多路mi
            int   updateResult = cidRelationService.updateCidRelationForMigrate(cidRelation);
            if (!vidHeadStr.substring(0,1).equals("1") ){
                cidRelationRoadService.updateCidRelationForMigrate(cidRelation);

                CidEntity cidEntity = new CidEntity();
                cidEntity.setCidType(cidRelationReq.getCidType());
                cidEntity.setCid(cidRelationReq.getCid());
                cidEntity.setPlantID(plantId);
                cidEntity.setUpdateTime(nowUTC);
                cidEntityMapper.updateCidEntity(cidEntity);
            }

//            if (updateResult <= 0) {
                cidRelation.setUpdateTime(null);
                cidRelation.setCreateTime(nowUTC);
                int insertResult = cidRelationService.insertCidRelation(cidRelation);
                logger.info("insertResult:"+insertResult);
                List<CidRelation> cidVidRoadList = new ArrayList<>();

                int vidHeadInt = Integer.parseInt(cidRelation.getVid().substring(0, 1));
                if (insertResult > 0) {
                    if (vidHeadInt > 1) {
                        for (int b = 0; b < vidHeadInt; b++) {
                            CidRelation cidVidRoadRelation = JSON.parseObject(JSON.toJSONString(cidRelation), CidRelation.class);
                            cidVidRoadRelation.setRoadType(String.valueOf(b + 1));
                            cidVidRoadList.add(cidVidRoadRelation);
                        }
                    }
                }
                if (cidVidRoadList.size() > 0) {
                    int insertCidVidRoadRelationResult = cidVidRoadRelationService.insertBatchCidVidRoadRelation(cidVidRoadList);
                    logger.error("===CidRelationController addDevice cidRelationReq error cidVidRoadList:" + cidVidRoadList.size() + "  insertCidVidRoadRelationResult:" + insertCidVidRoadRelationResult);
                }
                returnResult = returnResult + insertResult;

//            } else {
//                returnResult = returnResult + updateResult;
//            }

            if (returnResult > 0) {
                logger.error("===CidRelationController addDevice cidRelationReq error plantId:" + plantId);
                Map updatedDeviceMap = redisCache.getCacheMap("updatedDeviceMap");
                if (updatedDeviceMap == null) {
                    updatedDeviceMap = new HashMap();
                }

                if (cidRelationReq.getCidType() != null & cidRelationReq.getCidType().equals("03")) {
                    for (int i = 0; i < vidList.size(); i++) {
                        String vidTmp = vidList.get(i).toString();
                        updatedDeviceMap.put(vidTmp, 1);
                    }
                } else {
                    updatedDeviceMap.put(cidRelationReq.getCid(), 1);
                }
                redisCache.setCacheMap("updatedDeviceMap", updatedDeviceMap);
                if (returnResult > 0) {
                    ajaxResult.put("msg", "success");
                    ajaxResult.put("code", 200);
                    ajaxResult.put("data", returnResult);
                } else {
                    ajaxResult.put("msg", "error");
                    ajaxResult.put("code", 100);
                    ajaxResult.put("data", 0);
                }

            } else {
                ajaxResult.put("msg", "error");
                ajaxResult.put("code", 100);
            }
        }
        if (!exitstVidMsg.equals("")){
            ajaxResult.put("code", 100);
            ajaxResult.put("msg", exitstVidMsg+"  is existed. please check the device list, or contact the supplier");
        }
        return ajaxResult;
    }

//    /**
//     * 新增网关、微逆关系
//     */
////    @PreAuthorize("@ss.hasPermi('energy:relation:add')")
//    @Log(title = "网关、微逆关系", businessType = BusinessType.INSERT)
//    @PostMapping()
//    public AjaxResult add(@RequestBody CidRelation cidRelationReq)
//    {
//        System.out.println("===CidRelationController add cidRelationReq:"+JSON.toJSONString(cidRelationReq));
//        AjaxResult ajaxResult =AjaxResult.success();
//        long plantId = cidRelationReq.getPowerStationId();
//        if (plantId<=0){
//            logger.error("===CidRelationController add cidRelationReq error plantId:"+plantId);
//            ajaxResult.put("msg","error");
//            ajaxResult.put("code",100);
//            return ajaxResult;
//        }
//
//        String[] vidArr=null;
//        List vidList=new ArrayList();
//        if (cidRelationReq!=null && cidRelationReq.getVid()!=null){
//            vidArr=cidRelationReq.getVid().split(",");
//        }
//
//        if (vidArr!=null){
//            for (int i=0;i<vidArr.length;i++){
//                if (vidArr[i]!=null&&!vidArr[i].equals("")){
//                    vidList.add(vidArr[i]);
//                }
//            }
//        }
//
//
//        boolean isInsert=false;
//
//        CidRelation queryRelation =new CidRelation();
//        queryRelation.setCid(cidRelationReq.getCid());
//        queryRelation.setBindType("0");
//        queryRelation.setDelFlag("0");
//        List<CidRelation> relationList=cidRelationService.selectCidRelationList(queryRelation);
//        boolean hasBind=false;
//        Long bindId=plantId;
//
//        System.out.println("===CidRelationController add relationList:"+JSON.toJSONString(relationList));
//
//        for(CidRelation re:relationList){
//            System.out.println("===CidRelationController add re:"+JSON.toJSONString(re)+"  plantId:"+plantId);
//
//            try{
//                if(re!=null & re.getPowerStationId()!=null & re.getPowerStationId()!=plantId){
//                    hasBind=true;
//                    bindId=re.getPowerStationId();
//                }
//            }catch (Exception ex){
//                ex.printStackTrace();
//            }
//        }
//
//        String loopType=null;
//        boolean vidEmpty=true;
//        System.out.println("===CidRelationController add  vidList :"+vidList.size());
//
//        for(int i=0;i<vidList.size();i++){
//
//            CidRelation cidRelation =new CidRelation();
//            cidRelation.setCid(cidRelationReq.getCid());
//            cidRelation.setVid(vidList.get(i).toString());
//            cidRelation.setPowerStationId(plantId);
//
//            if(cidRelation.getVid()!=null&&!cidRelation.getVid().equals("")){
//                loopType=cidRelation.getVid().substring(0,1);
//                vidEmpty=false;
////                //删除空的vid
////                CidRelation removeEmpty=cidRelationService.getEmptyVidInfoByCid(cidRelation.getCid());
////                if(removeEmpty!=null){
////                    Long delId=removeEmpty.getId();
////                    //物理删除empty vid
////                    cidRelationService.deleteEmptyVidById(delId);
////                }
//            }
//
//            //判断是否已绑定 >0 已绑定
//            System.out.println("===CidRelationController add  hasBind :"+hasBind);
//
//            if(hasBind){
//                String vid = cidRelation.getVid();
//                //先更新老字段 removeBind =1;
//                cidRelation.setBindType("1");
//                cidRelation.setVid(null);
//                System.out.println("===CidRelationController add updateCidRelationForAdd cidRelation:"+JSON.toJSONString(cidRelation));
//
//                int column=cidRelationService.updateCidRelationForAdd(cidRelation);
//                //然后执行插入
//                if(column>0){
//                    cidRelation.setVid(vid);
//                    cidRelation.setPowerStationId(plantId);
//                    cidRelation.setBindType("0");
//                    cidRelation.setDelFlag("0");
//                    if(!vidEmpty){
//                        if(loopType.equals("1")){
//                            cidRelationService.insertCidRelation(cidRelation);
//                            isInsert=true;
//                        }else{
//                            for (int a=0;a<Integer.parseInt(loopType);a++){
//                                String loop="";
//                                if(loopType.equals("2")){
//                                    loop = "1"+(a+1);
//                                }else if(loopType.equals("4")){
//                                    loop = "2"+(a+1);
//                                }
//                                cidRelation.setRoadType(loop);
//                                isInsert=true;
//                                cidRelationService.insertCidRelation(cidRelation);
//                            }
//                        }
//                    }else{
//                        isInsert=true;
//                        cidRelationService.insertCidRelation(cidRelation);
//                    }
//
//                }
//            }else{
//                //未绑定 新绑定
//                //判断单路多路
//                System.out.println("===CidRelationController add   vidEmpty cidRelation:"+JSON.toJSONString(cidRelation));
//
//                if(!vidEmpty){
//                    cidRelation.setDelFlag("0");
//                    if(loopType.equals("1")){
//                        isInsert=true;
//                        cidRelationService.insertCidRelation(cidRelation);
//                    }else{
//                        for (int a=0;a<Integer.parseInt(loopType);a++){
//                            String loop="";
//                            if(loopType.equals("2")){
//                                loop = "1"+(a+1);
//                            }else if(loopType.equals("4")){
//                                loop = "2"+(a+1);
//                            }
//                            cidRelation.setRoadType(loop);
//                            cidRelationService.insertCidRelation(cidRelation);
//                        }
//                    }
//                }else{
//                    isInsert=true;
//                    cidRelationService.insertCidRelation(cidRelation);
//                }
//                int result = cidRelationService.updateCidRelationByCammand(cidRelation.getCid(),"1");
//                System.out.println("===CidRelationController add   vidEmpty updateCidRelationByCammand:"+result);
//
//            }
//        }
//
//
//
//        if(vidList!=null &&  vidList.size()>0){
//            ajaxResult.put("msg","success");
//            ajaxResult.put("code",200);
//            ajaxResult.put("id",JSON.toJSONString(vidList));
//        }else{
//            ajaxResult.put("msg","error");
//            ajaxResult.put("code",100);
//        }
//
//        if (isInsert){
//            Map updatedDeviceMap = redisCache.getCacheMap("updatedDeviceMap");
//            if (updatedDeviceMap==null){
//                updatedDeviceMap=new HashMap();
//            }
//            for (int i=0;i<vidList.size();i++){
//                updatedDeviceMap.put(cidRelationReq.getCid(),1);
//            }
//            redisCache.setCacheMap("updatedDeviceMap",updatedDeviceMap);
//
//            logger.info("===updatedDeviceMap add :"+JSON.toJSONString(redisCache.getCacheMap("updatedDeviceMap")));
//
//
//        }
//        return ajaxResult;
//    }

    /**
     * 修改网关、微逆关系
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:edit')")
    @Log(title = "网关、微逆关系", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CidRelation cidRelation) {
        return toAjax(cidRelationService.updateCidRelation(cidRelation));
    }

    /**
     * 删除网关、微逆关系
     */
//    @PreAuthorize("@ss.hasPermi('energy:relation:remove')")
    @Log(title = "网关、微逆关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(cidRelationService.deleteCidRelationByIds(ids));
    }

    //    @PreAuthorize("@ss.hasPermi('energy:relation:removeByCid')")
    @Log(title = "网关、微逆关系", businessType = BusinessType.DELETE)
    @GetMapping("/removeByCid")
    public AjaxResult removeByCid(CidRelation cidRelation) {

        logger.info("cidRelation:"+JSON.toJSONString(cidRelation));
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();

        CidEntity cidEntity = new CidEntity();
        cidEntity.setCid(cidRelation.getCid());
        cidEntity.setPlantID(cidRelation.getPowerStationId());
        cidEntity.setDelFlag("1");
        int updateEntityResult=cidEntityMapper.updateCidEntity(cidEntity);
        cidRelationService.deleteCidRelationByCid(cidRelation.getCid(),cidRelation.getPowerStationId(),user.getUserId().toString());


        CidDayData updateDayDate = new CidDayData();
        updateDayDate.setCid(cidRelation.getCid());
        dayDataService.updateCidDayData(updateDayDate);
        AjaxResult ajaxResult = AjaxResult.success(cidRelationService.deleteCidRelationByCid(cidRelation.getCid(), cidRelation.getPowerStationId(), user.getUserId().toString()));


        Map updatedDeviceMap = redisCache.getCacheMap("updatedDeviceMap");
        if (updatedDeviceMap == null) {
            updatedDeviceMap = new HashMap();
        }
        updatedDeviceMap.put(cidRelation.getCid(), 1);
        redisCache.setCacheMap("updatedDeviceMap", updatedDeviceMap);

        Map deletedDeviceMap = redisCache.getCacheMap("deletedDeviceMap");
        if (deletedDeviceMap == null) {
            deletedDeviceMap = new HashMap();
        }
        updatedDeviceMap.put(cidRelation.getCid(), 1);
        redisCache.setCacheMap("deletedDeviceMap", deletedDeviceMap);


        logger.info("===updatedDeviceMap removeByCid :" + JSON.toJSONString(redisCache.getCacheMap("updatedDeviceMap")));

        return ajaxResult;
    }


    @Log(title = "网关、微逆关系", businessType = BusinessType.DELETE)
    @GetMapping("/removeBatchVid")
    public AjaxResult removeBatchVid(long powerStationId, String[] deviceToDelete, HttpServletRequest request) {

        LoginUser loginUser = getLoginUser();

        JSONArray deviceArr = JSONArray.parseArray(request.getParameter("deviceToDelete"));
        logger.info("deviceArr:"+JSON.toJSONString(deviceArr));
        int result = 0;
        for (int i = 0; i < deviceArr.size(); i++) {
            JSONObject itemObj = deviceArr.getJSONObject(i);
            String cid = String.valueOf(itemObj.get("cid"));
            JSONArray vidArr = JSONArray.parseArray(JSONObject.toJSONString(itemObj.get("vid")));
            String[] vidStrArr = new String[vidArr.size()];
            vidStrArr = vidArr.toArray(vidStrArr);

            CidDayData updateDayDate = new CidDayData();
            updateDayDate.setCid(cid);

            updateDayDate.setVidArr(vidStrArr);

            CidEntity cidEntity = new CidEntity();
            cidEntity.setCid(cid);
            cidEntity.setPlantID(powerStationId);
            cidEntity.setDelFlag("1");
            int updateEntityResult=cidEntityMapper.updateCidEntity(cidEntity);

            cidRelationService.updateCidRelationByCammand(cid, "1");
            dayDataService.updateBatchForRemoveAndDel(updateDayDate);

            result = result + cidRelationService.deleteBatchCidRelationByInfo(cid, vidStrArr, powerStationId, loginUser.getUserId().toString());


            Map updatedDeviceMap = redisCache.getCacheMap("updatedDeviceMap");
            if (updatedDeviceMap == null) {
                updatedDeviceMap = new HashMap();
            }
            updatedDeviceMap.put(cid, 1);
            redisCache.setCacheMap("updatedDeviceMap", updatedDeviceMap);
            logger.info("===updatedDeviceMap removeByInfo :" + JSON.toJSONString(redisCache.getCacheMap("updatedDeviceMap")));

        }
        AjaxResult ajaxResult = AjaxResult.success(result);
        return ajaxResult;
    }


    //    @PreAuthorize("@ss.hasPermi('energy:relation:removeByInfo')")
    @Log(title = "网关、微逆关系", businessType = BusinessType.DELETE)
    @GetMapping("/removeByInfo")
    public AjaxResult removeByInfo(CidRelation cidRelation) {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();

        CidDayData updateDayDate = new CidDayData();
        updateDayDate.setCid(cidRelation.getCid());
        updateDayDate.setVidArr(cidRelation.getVid().split(","));

        //cidRelationService.updateCidRelationByCammand(cidRelation.getCid(),"1");
        cidRelationService.updateCidRelationByCammand(cidRelation.getCid(), "1");
        dayDataService.updateBatchForRemoveAndDel(updateDayDate);


        AjaxResult ajaxResult = AjaxResult.success(cidRelationService.deleteCidRelationByInfo(cidRelation.getCid(),
                cidRelation.getVid(), cidRelation.getRoadType(), cidRelation.getPowerStationId(), user.getUserId().toString()));

        Map updatedDeviceMap = redisCache.getCacheMap("updatedDeviceMap");
        if (updatedDeviceMap == null) {
            updatedDeviceMap = new HashMap();
        }
        updatedDeviceMap.put(cidRelation.getCid(), 1);
        redisCache.setCacheMap("updatedDeviceMap", updatedDeviceMap);

        return ajaxResult;
    }


    @Log(title = "网关、微逆关系", businessType = BusinessType.UPDATE)
    @GetMapping("/sendCammandUpdateMap")
    public AjaxResult sendCammandUpdateMap(String cid) {

        cidRelationService.updateCidRelationByCammand(cid, "1");
        AjaxResult ajaxResult = AjaxResult.success();
        return ajaxResult;
    }

    /**
     * 批量添加微逆
     *
     * @return
     */
    @PostMapping("/mutAddMis")
    public AjaxResult addMisMut(String cid, String mutVids, Long plantId) {


        boolean is_insert = false;
        String[] vids = mutVids.split(",");

        for (String vid : vids) {
            if (vid.substring(0, 1).equals("2")) {//双路
                for (int i = 1; i <= 2; i++) {
                    CidRelation relation = new CidRelation();
                    relation.setCid(cid);
                    relation.setVid(vid);
                    relation.setRoadType("1" + i);
                    relation.setBindType("0");
                    relation.setIsConfirm("0");
                    relation.setDelFlag("0");
                    relation.setPowerStationId(plantId);
                    cidRelationService.insertCidRelation(relation);
                    is_insert = true;
                }
            } else if (vid.substring(0, 1).equals("4")) {//4路
                for (int i = 1; i <= 4; i++) {
                    CidRelation relation = new CidRelation();
                    relation.setCid(cid);
                    relation.setVid(vid);
                    relation.setRoadType("2" + i);
                    relation.setBindType("0");
                    relation.setIsConfirm("0");
                    relation.setDelFlag("0");
                    relation.setPowerStationId(plantId);
                    cidRelationService.insertCidRelation(relation);
                    is_insert = true;
                }
            } else {
                CidRelation relation = new CidRelation();
                relation.setCid(cid);
                relation.setVid(vid);
                relation.setBindType("0");
                relation.setIsConfirm("0");
                relation.setDelFlag("0");
                relation.setPowerStationId(plantId);
                cidRelationService.insertCidRelation(relation);
                is_insert = true;
            }
        }

        if (is_insert == true) {
            Map updatedDeviceMap = redisCache.getCacheMap("updatedDeviceMap");
            if (updatedDeviceMap == null) {
                updatedDeviceMap = new HashMap();
            }
            updatedDeviceMap.put(cid, 1);
            redisCache.setCacheMap("updatedDeviceMap", updatedDeviceMap);
        }

        AjaxResult ajaxResult = AjaxResult.success();
        return ajaxResult;
    }


    public static Map<String, String> mapStringToMap(String str) {
        str = str.substring(1, str.length() - 1);
        String[] strs = str.split(",");
        Map<String, String> map = new HashMap<String, String>();
        for (String string : strs) {
            String key = string.split("=")[0];
            String value = string.split("=")[1];
            // 去掉头部空格
            String key1 = key.trim();
            String value1 = value.trim();
            map.put(key1, value1);
        }
        return map;
    }

    public static void main(String[] args) throws ParseException {

    }

}
