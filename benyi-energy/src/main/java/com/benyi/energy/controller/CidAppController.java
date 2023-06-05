package com.benyi.energy.controller;


import com.alibaba.fastjson2.JSON;
import com.benyi.common.annotation.Log;
import com.benyi.common.config.RuoYiConfig;
import com.benyi.common.constant.Constants;
import com.benyi.common.constant.HttpStatus;
import com.benyi.common.core.controller.BaseController;
import com.benyi.common.core.domain.AjaxResult;
import com.benyi.common.core.domain.entity.SysDept;
import com.benyi.common.core.domain.entity.SysUser;
import com.benyi.common.core.domain.model.LoginUser;
import com.benyi.common.core.page.TableDataInfo;

import com.benyi.common.core.redis.RedisCache;
import com.benyi.common.enums.BusinessType;
import com.benyi.common.utils.file.FileUploadUtils;
import com.benyi.energy.domain.*;
import com.benyi.energy.mapper.CidDataDayMapper;
import com.benyi.energy.mapper.CidDataHourMapper;
import com.benyi.energy.mapper.CidDayDataMapper;
import com.benyi.energy.service.*;
import com.benyi.system.service.ISysDeptService;
import com.github.pagehelper.PageHelper;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/energy/app/info")
public class CidAppController extends BaseController {


    @Autowired
    private ICidPowerstationinfoService powerstationinfoService;

    @Autowired
    private ICidRelationService relationService;

    @Autowired
    private ICidRelationRoadService relationRoadService;

    @Autowired
    private ICidDataService cidDataService;

    @Autowired
    CidDataHourMapper cidDataHourMapper;

    @Autowired
    private ICidDayDataService dayDataService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ICidWorkOrderService cidWorkOrderService;

    @Autowired
    private ICidLoginHeartService heartService;

    @Autowired
    CidDataDayMapper cidDataDayMapper;
    @Autowired
    private ICidDayDataService dayService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private IFeebackService feebackService;

    @Autowired
    private ICidRelationService cidRelationService;

    /**
     * 取得vid,cid的故障代码及描述
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/getErrorCodeAndDesc")
    public AjaxResult getErrorCodeAndDescByCidAndVid(String cid, String vid) throws ParseException {
        AjaxResult webResult = AjaxResult.success();
        LoginUser loginUser = getLoginUser();
        List<Map> cidDataList = cidDataService.getErrorCodeAndDescByCidAndVid(cid, vid);
        if (cidDataList.size() > 0)
            webResult.put("data", cidDataList.get(0));
        else
            webResult.put("data", new HashMap<>());
        return webResult;
    }

    /**
     * 取的emu列表数据
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/getEmuList")
    public AjaxResult getEmuList(long powerStationId) throws ParseException {
        AjaxResult webResult = AjaxResult.success();
        List<String> emuList = relationService.getEmuList(powerStationId);
        webResult.put("emuList", emuList);
        return webResult;
    }


    @PostMapping("/feedback")
    public AjaxResult feedback(@RequestBody FeedBackObj feedBackObj) {

        String fileNames = "";
        if (feedBackObj.getImgs() != null) {
            for (int a = 0; a < feedBackObj.getImgs().length; a++) {
                String attachmentFileName = FileUploadUtils.generateBase64StringToFile(feedBackObj.getImgs()[a], RuoYiConfig.getProfile());
                if (attachmentFileName != null) {
                    if (fileNames == null) {
                        fileNames = attachmentFileName;
                    } else {
                        fileNames = fileNames + "," + attachmentFileName;
                    }
                } else {
                    logger.info("=== CidAppController feedback 上传失败：" + feedBackObj.getImgs()[a]);
                }
            }
            logger.info("=== CidAppController feedback 上传成功：" + fileNames);
        }

        Feedback feedback = new Feedback();
        feedback.setContact(feedBackObj.getContact());
        feedback.setSuggest(feedBackObj.getSuggest());
        feedback.setFile(fileNames);
        feedback.setCreateDate(new Date());

        int result = feebackService.insertFeedback(feedback);
        return AjaxResult.success(result);
    }


    /**
     * app 电站频道，我的MIs设备
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/getPlantDevices")
    public AjaxResult getPlantDevices() throws ParseException {

        AjaxResult webResult = AjaxResult.success();
        LoginUser loginUser = getLoginUser();
        //判断有没有plant
        List<CidPowerstationinfo> CidPowerstationinfoList = powerstationinfoService.selectCidPowerstationinfoListByDeptID(loginUser.getDeptId());

        if (CidPowerstationinfoList != null & CidPowerstationinfoList.size() > 0) {
            webResult.put(Constants.PLANT_NAME, CidPowerstationinfoList.get(0).getEnergyName());
            webResult.put(Constants.PLANT_ID, CidPowerstationinfoList.get(0).getId());
        }

        List<Map> devicesList = relationService.selectAllDeviceByDeptId(loginUser.getDeptId());

        webResult.put(Constants.DEVICE_TOTAL_COUNT, devicesList.size());
        webResult.put(Constants.DEVICE_List, devicesList);
        return webResult;
    }

    /**
     * app 电站频道，我的mis设备
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/getPlantMIs")
    public AjaxResult getPlantMisdata() throws ParseException {

        AjaxResult webResult = AjaxResult.success();
        LoginUser loginUser = getLoginUser();

        List<CidPowerstationinfo> CidPowerstationinfoList = powerstationinfoService.selectCidPowerstationinfoListByDeptID(loginUser.getDeptId());
        if (CidPowerstationinfoList != null & CidPowerstationinfoList.size() > 0) {
            CidPowerstationinfo cidPowerstationinfo = CidPowerstationinfoList.get(0);
            webResult.put(Constants.PLANT_NAME, cidPowerstationinfo.getEnergyName());
        }

        List<CidRelation> miList = relationService.selectVidDataByDeptId(loginUser.getDeptId());
        List<CidRelation> fourRoadMIList = new ArrayList<>();
        List<CidRelation> twoRoadMIList = new ArrayList<>();
        List<CidRelation> oneRoadMIList = new ArrayList<>();

        for (int i = 0; i < miList.size(); i++) {

            CidRelation cidRelation = JSON.parseObject(JSON.toJSONString(miList.get(i)), CidRelation.class);

            if (cidRelation.getVid() != null) {

                String vidHeadStr = cidRelation.getVid().substring(0, 1);
                if ((vidHeadStr.equals("4") || vidHeadStr.equals("2"))) {
                    List<CidRelation> cidRetionRoadList = relationRoadService.selectByCidAndVidAndPlantID(cidRelation.getCid(), cidRelation.getVid(), cidRelation.getPowerStationId());

                    for (int d = 0; d < cidRetionRoadList.size(); d++) {
                        if (cidRelation.equals("3")) {
                            cidRetionRoadList.get(d).setStatus("3");
                            cidRetionRoadList.get(d).setCurrentPower("0");
                        }
                    }

                    if (vidHeadStr.equals("4")) {
                        for (CidRelation map : cidRetionRoadList) {
                            if (cidRelation.getStatus() != null) {
                                if (cidRelation.getStatus().equals("3"))
                                    map.setCurrentPower("0");
                            }
                            map.setStatus(cidRelation.getStatus());
                            fourRoadMIList.add(map);
                        }
                    } else if (vidHeadStr.equals("2")) {
                        for (CidRelation map : cidRetionRoadList) {
                            if (cidRelation.getStatus() != null) {
                                if (cidRelation.getStatus().equals("3"))
                                    map.setCurrentPower("0");
                            }
                            map.setStatus(cidRelation.getStatus());
                            twoRoadMIList.add(map);
                        }
                    }
                } else {
                    oneRoadMIList.add(cidRelation);
                }
            }
        }

        List<Object> returnList = new ArrayList<>();
        for (int i = 0; i < fourRoadMIList.size(); i++) {
            returnList.add(fourRoadMIList.get(i));
        }

        for (int i = 0; i < twoRoadMIList.size(); i++) {
            returnList.add(twoRoadMIList.get(i));
        }
        for (int i = 0; i < oneRoadMIList.size(); i++) {
            returnList.add(oneRoadMIList.get(i));
        }

        webResult.put(Constants.DEVICE_MIS_TOTAL_COUNT, miList.size());
        webResult.put(Constants.DEVICE_MIS_List, returnList);
        return webResult;
    }

    /**
     * app home页面统计数据
     */
    @GetMapping("/getHomeData")
    public AjaxResult getHomeData() throws ParseException {

        LoginUser loginUser = getLoginUser();
        SysUser sysUser = loginUser.getUser();

        AjaxResult webResult = AjaxResult.success();
        Map data = new HashMap();

        //判断有没有plant
        List<CidPowerstationinfo> CidPowerstationinfoList = powerstationinfoService.selectCidPowerstationinfoListByDeptID(sysUser.getDeptId());
        if (CidPowerstationinfoList == null || CidPowerstationinfoList.size() == 0) {
            data.put(Constants.TOTAL_PLANT, 0);
        } else {

            CidPowerstationinfo cidPowerstationinfo = CidPowerstationinfoList.get(0);
            //全部电量、当日电量、当前电压
            data.put(Constants.TOTAL_ENERGY, cidPowerstationinfo.getEnergyTotal());
            data.put(Constants.PLANT_ID, cidPowerstationinfo.getId());
            data.put(Constants.PLANT_NAME, cidPowerstationinfo.getEnergyName());
            data.put(Constants.TOTAL_PLANT, CidPowerstationinfoList.size());
            data.put(Constants.DAILY_ENERGY, cidPowerstationinfo.getEnergyTotalDay());
            data.put(Constants.CURRENT_POWER, cidPowerstationinfo.getEnergyCurrentPower());

            //今日
            SimpleDateFormat sdfnow = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            String searchDate = sdfnow.format(now);

            CidRelation queryRelation = new CidRelation();
            queryRelation.setPowerStationId(cidPowerstationinfo.getId());
            queryRelation.setBindType("0");
            queryRelation.setDelFlag("0");
            queryRelation.setIsConfirm("0");
            System.out.println("===today total energy queryRelation:" + JSON.toJSONString(queryRelation));


            List<CidRelation> relationList = relationService.selectCidRelationList(queryRelation);
            int normalCount = 0;
            for (CidRelation re : relationList) {
                String vidStatus = re.getStatus() == null ? "3" : re.getStatus().toString();
                if (vidStatus.equals("0") || vidStatus.equals("1") || vidStatus.equals("2"))
                    normalCount = normalCount + 1;
            }

            data.put(Constants.DEVICE_TOTAL_COUNT, relationList.size());
            data.put(Constants.DEVICE_NORMAL_COUNT, normalCount);
        }
        webResult.put("data", data);
        return webResult;
    }

    /**
     * 获取电站列表
     */
    @GetMapping
    public AjaxResult info() throws ParseException {

        LoginUser loginUser = getLoginUser();
        CidPowerstationinfo queryPower = new CidPowerstationinfo();
        queryPower.setEnergyDeptId(loginUser.getDeptId());
        queryPower.setDelFlag("0");
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String beginTime = format.format(new Date());
        List<CidPowerstationinfo> returnList = powerstationinfoService.selectCidPowerstationinfoList(queryPower);
        for (CidPowerstationinfo plant : returnList) {
            String endTime = plant.getLastUpdate();
            if (!plant.getLastUpdate().equals("0")) {
                Date date1 = format.parse(beginTime);
                Date date2 = format.parse(endTime);

                long beginMillisecond = date1.getTime();
                long endMillisecond = date2.getTime();
                long jian = beginMillisecond - endMillisecond;
                if (beginTime.substring(0, 10).equals(endTime.substring(0, 10))) {
                    if (!(jian > 1000 * 60 * 30) && jian > 0) {
                        if (plant.getLastErrorCode().equals("0000") || plant.getLastErrorCode().equals("0200")) {
                            plant.setEnergyStatus("0");
                        } else {
                            plant.setEnergyStatus("1");
                        }
                    } else {
                        plant.setEnergyStatus("3");
                        plant.setEnergyCurrentPower("0");
                    }
                } else {
                    plant.setEnergyStatus("3");
                    plant.setEnergyCurrentPower("0");
                }
            } else {
                plant.setEnergyStatus("2");
                plant.setEnergyCurrentPower("0");
            }
        }
        for (CidPowerstationinfo plant : returnList) {
            CidRelation queryRelation = new CidRelation();
            queryRelation.setPowerStationId(plant.getId());
            queryRelation.setBindType("0");
            queryRelation.setDelFlag("0");
            queryRelation.setIsConfirm("0");
            List<CidRelation> misList = relationService.selectCidRelationList(queryRelation);
            plant.setMisList(misList);
        }

        AjaxResult ajax = AjaxResult.success(returnList);
        return ajax;
    }

    @GetMapping("/getPlantCount")
    public AjaxResult getPlantCount() throws ParseException {
        AjaxResult ajax = AjaxResult.success();
        LoginUser loginUser = getLoginUser();
        CidPowerstationinfo queryPlant = new CidPowerstationinfo();
        queryPlant.setDelFlag("0");
        queryPlant.setEnergyDeptId(loginUser.getDeptId());
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        int count = 0;
        int on = 0;
        int off = 0;
        int error = 0;
        int build = 0;
        int cut = 0;
        List<CidPowerstationinfo> plantList = powerstationinfoService.selectCidPowerstationinfoList(queryPlant);
        count = plantList.size();
        for (CidPowerstationinfo plant : plantList) {
            if (!plant.getLastUpdate().equals("0")) {
                String lastUpdate = plant.getLastUpdate();
                Date lastDate = format.parse(lastUpdate);
                Date dateNow = new Date();
                String formatDateNow = format.format(dateNow);

                if (formatDateNow.substring(0, 10).equals(lastUpdate.substring(0, 10))) {
                    long beginMillisecond = lastDate.getTime();
                    long endMillisecond = dateNow.getTime();
                    long jian = endMillisecond - beginMillisecond;
                    if (!(jian > 1000 * 60 * 30) && jian > 0) {
                        if (plant.getLastErrorCode().equals("0000") || plant.getLastErrorCode().equals("0200")) {
                            on++;
                        } else {
                            error++;
                        }
                    } else {
                        CidRelation queryRelation = new CidRelation();
                        queryRelation.setDelFlag("0");
                        queryRelation.setBindType("0");
                        queryRelation.setIsConfirm("0");
                        queryRelation.setPowerStationId(plant.getId());
                        //offline++;
                        List<CidRelation> relationList = relationService.selectCidRelationList(queryRelation);
                        boolean isCut = false;
                        for (CidRelation re : relationList) {
                            Long maxId = heartService.selectMaxLoginHeartByCid(re.getCid());
                            if (maxId != null) {
                                CidLoginHeart lh = heartService.selectCidLoginHeartById(maxId);
                                Date dateNowHeart = new Date();
                                String heartBeginDate = format.format(dateNowHeart);
                                String ymd = format.format(lh.getCreateDate()).substring(0, 10);
                                String beginTimeYmd = heartBeginDate.substring(0, 10);
                                if (ymd.equals(beginTimeYmd)) {
                                    long beginHeartMillisecond = dateNowHeart.getTime();
                                    long endHeartMillisecond = lh.getCreateDate().getTime();
                                    long jianHeart = beginHeartMillisecond - endHeartMillisecond;
                                    if (!(jianHeart > 1000 * 60 * 30) && jianHeart > 0) {
                                        //power.setEnergyStatus("5");
                                        isCut = true;
                                        System.out.println("有心跳了");
                                        //break;
                                    }
                                } else {
                                    System.out.println("没心跳了");
                                }
                            }
                        }
                        if (isCut) {
                            cut++;
                        } else {
                            off++;
                        }
                    }
                } else {
                    off++;
                }
            } else {
                build++;
            }
        }
        ajax.put("count", count);
        ajax.put("on", on);
        ajax.put("off", off);
        ajax.put("error", error);
        ajax.put("build", build);
        ajax.put("cut", cut);
        return ajax;
    }

    @GetMapping("/getDeviceCount")
    public AjaxResult getDeviceCount() throws ParseException {
        AjaxResult ajax = AjaxResult.success();
        CidRelation queryRelation = new CidRelation();
        queryRelation.setDelFlag("0");
        queryRelation.setIsConfirm("0");
        queryRelation.setBindType("0");
        queryRelation.setDeptId(getLoginUser().getDeptId());
        int cidCount = 0;
        int vidCount = 0;
        int cidOn = 0;
        int vidOn = 0;
        int cidOff = 0;
        int vidOff = 0;
        int cidError = 0;
        int vidError = 0;
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        List<String> cidList = new ArrayList<>();
        List<String> errorList = new ArrayList<String>();
        List<String> errorValList = new ArrayList<String>();
        List<String> onList = new ArrayList<String>();
        List<String> onValList = new ArrayList<String>();
        HashMap<String, String> offList = new HashMap<String, String>();
        List<String> offValList = new ArrayList<String>();
        List<CidRelation> relationList = relationService.selectCidRelationList(queryRelation);
        for (CidRelation relation : relationList) {
            if (!cidList.contains(relation.getCid())) {
                cidList.add(relation.getCid());
            }
            if (!relation.getLastUpdate().equals("0")) {
                String lastUpdate = relation.getLastUpdate();
                Date lastDate = format.parse(lastUpdate);
                Date dateNow = new Date();
                String formatDateNow = format.format(dateNow);
                //beginTime.substring(0,10).equals(endTime.substring(0,10))

                if (formatDateNow.substring(0, 10).equals(lastUpdate.substring(0, 10))) {
                    long beginMillisecond = lastDate.getTime();
                    long endMillisecond = dateNow.getTime();
                    long jian = endMillisecond - beginMillisecond;
                    if (!(jian > 1000 * 60 * 30) && jian > 0) {
                        if (relation.getStatus().equals("1") || relation.getStatus().equals("1")) {
                            onList.add(relation.getCid());
                            vidOn++;
                        } else {
                            vidError++;
                            errorList.add(relation.getCid());
                        }
//                        if (relation.getLastErrorCode().equals("0000") || relation.getLastErrorCode().equals("0200")) {
//                            onList.add(relation.getCid());
//                            vidOn++;
//                        } else {
//                            vidError++;
//                            errorList.add(relation.getCid());
//                        }
                    } else {
                        offList.put(relation.getCid(), relation.getLastUpdate());
                        vidOff++;
                    }
                } else {
                    offList.put(relation.getCid(), relation.getLastUpdate());
                    vidOff++;
                }
            } else {
                offList.put(relation.getCid(), relation.getLastUpdate());
                vidOff++;
            }
        }

        for (String s : errorList) {
            if (!errorValList.contains(s)) {
                errorValList.add(s);
            }
        }
        for (String s : onList) {
            if (!onValList.contains(s)) {
                onValList.add(s);
            }
        }

        ajax.put("emuCount", cidList.size());
        ajax.put("emuOn", onValList.size());
        ajax.put("emuOff", offList.size());
        ajax.put("emuError", errorValList.size());
        ajax.put("misCount", relationList.size());
        ajax.put("misOn", vidOn);
        ajax.put("misOff", vidOff);
        ajax.put("misError", vidError);
        return ajax;
    }

    /**
     * app首页头部数据
     *
     * @return
     */
    @GetMapping("/inedxTopData")
    public AjaxResult getIndexTopData() throws ParseException {
        LoginUser loginUser = getLoginUser();
        AjaxResult ajax = AjaxResult.success();

        //电站数量
        CidPowerstationinfo queryPower = new CidPowerstationinfo();
        queryPower.setEnergyDeptId(loginUser.getDeptId());
        queryPower.setDelFlag("0");

        //日发电
        float energyDay = 0;
        //日功率
        float powerDay = 0;
        //月发电
        float energyMonth = 0;
        //年发电
        float energyYear = 0;
        //总发电量
        float energyTotal = 0;
        List<CidPowerstationinfo> list = powerstationinfoService.selectCidPowerstationinfoList(queryPower);
        for (CidPowerstationinfo plant : list) {
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            if (plant.getLastUpdate() != null && !plant.getLastUpdate().equals("0")) {
                String beginTime = format.format(new Date());
                String endTime = plant.getLastUpdate();

                Date date1 = format.parse(beginTime);
                Date date2 = format.parse(endTime);
                long beginMillisecond = date1.getTime();
                long endMillisecond = date2.getTime();
                long jian = beginMillisecond - endMillisecond;
                if (beginTime.substring(0, 10).equals(endTime.substring(0, 10))) {
                    if (!(jian > 1000 * 60 * 30) && jian > 0) {
                        if (plant.getLastUpdate() != null) {
                            powerDay += Float.parseFloat(plant.getEnergyCurrentPower());
                        }
                    }
                }
            }
            energyDay += Float.parseFloat(plant.getEnergyTotalDay());
            energyMonth += Float.parseFloat(plant.getEnergyTotalMonth());
            energyYear += Float.parseFloat(plant.getEnergyTotalYear());
            energyTotal += Float.parseFloat(plant.getEnergyTotal());
        }

        //cidCount
        CidRelation queryRelation = new CidRelation();
        ajax.put("energyDay", energyDay);
        ajax.put("powerDay", powerDay);
        ajax.put("energyMonth", energyMonth);
        ajax.put("energyYear", energyYear);
        ajax.put("energyTotal", energyTotal);

        return ajax;
    }

    /**
     * app 首页发电量
     *
     * @return
     */
    @GetMapping("/getIndexChartPower")
    public AjaxResult getIndexChartPower() {

        List<CidData> returnList = new ArrayList<>();

        // 获取当前的用户名称
        LoginUser loginUser = getLoginUser();
        Long deptId = loginUser.getDeptId();
        CidPowerstationinfo queryPowerstation = new CidPowerstationinfo();
        //增加判断，如果dept=100 查询所有
        queryPowerstation.setEnergyDeptId(loginUser.getDeptId());
        List<CidPowerstationinfo> plantList = powerstationinfoService.selectCidPowerstationinfoList(queryPowerstation);
        String dateType = "";
        List<CidData> resultList = null;
        List<CidDayData> resultListDay = null;
        List<CidData> finalList = new ArrayList<>();
        AjaxResult ajaxResult = null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String startDate = sdfDay.format(date);
        String endDate = sdfDay.format(date);


        String startMonth = startDate.substring(5, 7);
        String endMonth = endDate.substring(5, 7);
        List<String> searchDayList = new ArrayList<>();
        if (startMonth.equals(endMonth)) {

            String startDay = startDate.substring(startDate.length() - 2, startDate.length());
            String endDay = endDate.substring(endDate.length() - 2, endDate.length());
            int dayZone = Integer.parseInt(endDay) - Integer.parseInt(startDay);
            String searchDateZone = startDate.substring(0, startDate.length() - 2);

            for (int i = 0; i <= dayZone; i++) {
                searchDateZone = startDate.substring(0, startDate.length() - 2);
                String addDate = "";
                if (Integer.parseInt(startDay) + i < 10) {
                    addDate = "0" + (Integer.parseInt(startDay) + i);
                } else {
                    addDate = String.valueOf((Integer.parseInt(startDay) + i));
                }
                searchDateZone = searchDateZone + addDate;
                searchDayList.add(searchDateZone);
            }
        }
        for (String dateZone : searchDayList) {

            List<CidData> addList = new ArrayList<>();
            TreeMap<String, String> returnMap = new TreeMap<>();
            TreeMap<String, String> removeZeroMap = new TreeMap<>();
            for (CidPowerstationinfo plant : plantList) {

                addList = cidDataService.getStationPowerByPidAndDateForDayForMinute(plant.getId(), null, dateZone, null);
                TreeMap<String, String> dataMap = new TreeMap<>();
                for (Object obj : addList) {
                    String s = JSON.toJSONString(obj);
                    CidData d = JSON.parseObject(s, CidData.class);
                    dataMap.put(d.getDate().substring(d.getDate().length() - 5, d.getDate().length()), d.getPower());
                }

                TreeMap<String, String> rmap = CidDataController.subTreeMap(dataMap, dateZone);
                for (String s : rmap.keySet()) {
                    if (returnMap.containsKey(s)) {
                        Float addPower = Float.parseFloat(returnMap.get(s)) + Float.parseFloat(rmap.get(s));
                        returnMap.put(s, String.valueOf(addPower));
                    } else {
                        returnMap.put(s, rmap.get(s));
                    }
                }
            }
            for (String s : returnMap.keySet()) {
                if (!returnMap.get(s).equals("0.0")) {
                    removeZeroMap.put(s, returnMap.get(s));
                }
            }
            for (String dateZoneFinal : searchDayList) {
                TreeMap<String, String> finalMap = CidDataController.subTreeMapNotAddForIndex(removeZeroMap, dateZoneFinal);
                for (String s : finalMap.keySet()) {
                    CidData data = new CidData();
                    data.setDate(s);
                    data.setPower(finalMap.get(s));
                    returnList.add(data);
                }
            }
        }
        TreeMap<String, String> returnMap = new TreeMap<>();
        for (CidData data : returnList) {
            if (returnMap.get(data.getDate()) == null) {
                returnMap.put(data.getDate(), data.getPower());
            } else {
                Float add = Float.parseFloat(returnMap.get(data.getDate())) + Float.parseFloat(data.getPower());
                returnMap.put(data.getDate(), String.valueOf(add));
            }
        }
        List<String> ajaxList = new ArrayList<>();
        for (String s : returnMap.keySet()) {
            String add = s + "_" + returnMap.get(s);
            ajaxList.add(add);
        }

        ajaxResult = AjaxResult.success();
        ajaxResult.put("powerList", ajaxList);
        return ajaxResult;

    }


    @GetMapping("/getIndexChartEnergy")
    public AjaxResult getIndexChartEnergy() {
        List<CidDayData> resultList = dayService.selectLofEnergy(getLoginUser().getDeptId());
        return AjaxResult.success(resultList);
    }

    @GetMapping("/echarts")
    public AjaxResult echarts(Long id, String searchDate, String searchDateType, String startDate, String endDate) {

        if (null == searchDateType) {
            searchDateType = "1";
        }

        AjaxResult ajax = AjaxResult.success();
        List<String> powerList = new ArrayList<>();
        if (searchDateType.equals("1")) {
            Date date = new Date();
            SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
            List<CidData> returnList = new ArrayList<>();
            if (startDate == null) {
                startDate = sdfDay.format(date);
                endDate = sdfDay.format(date);
            }
            String startMonth = startDate.substring(5, 7);
            String endMonth = endDate.substring(5, 7);
            List<String> searchDayList = new ArrayList<>();

            if (startMonth.equals(endMonth)) {
                String startDay = startDate.substring(startDate.length() - 2, startDate.length());
                String endDay = endDate.substring(endDate.length() - 2, endDate.length());
                int dayZone = Integer.parseInt(endDay) - Integer.parseInt(startDay);
                String searchDateZone = startDate.substring(0, startDate.length() - 2);
                for (int i = 0; i <= dayZone; i++) {
                    searchDateZone = startDate.substring(0, startDate.length() - 2);
                    String addDate = "";
                    if (Integer.parseInt(startDay) + i < 10) {
                        addDate = "0" + (Integer.parseInt(startDay) + i);
                    } else {
                        addDate = String.valueOf((Integer.parseInt(startDay) + i));
                    }
                    searchDateZone = searchDateZone + addDate;
                    searchDayList.add(searchDateZone);
                }
            } else {
                String startDay = startDate.substring(startDate.length() - 2, startDate.length());
                String endDay = endDate.substring(endDate.length() - 2, endDate.length());
                String year = startDate.substring(0, 4);
                int startMontDay = CidDataController.getDays(Integer.parseInt(year), Integer.parseInt(startMonth));
                String searchDateZone = startDate.substring(0, startDate.length() - 2);
                String searchDateEndZone = endDate.substring(0, endDate.length() - 2);
                int startDay_1 = Integer.parseInt(startDay);
                for (int i = 0; i <= (startMontDay - startDay_1); i++) {
                    searchDateZone = startDate.substring(0, startDate.length() - 2);
                    String addDate = "";
                    if (Integer.parseInt(startDay) + i < 10) {
                        addDate = "0" + (Integer.parseInt(startDay) + i);
                    } else {
                        addDate = String.valueOf((Integer.parseInt(startDay) + i));
                    }
                    searchDateZone = searchDateZone + addDate;
                    searchDayList.add(searchDateZone);
                }
                for (int j = 1; j <= Integer.parseInt(endDay); j++) {

                    if (j < 10) {
                        searchDateEndZone = year + "-" + endMonth + "-0" + (j);
                    } else {
                        searchDateEndZone = year + "-" + endMonth + "-" + (j);
                    }
                    searchDayList.add(searchDateEndZone);
                }
            }

            for (String dateZone : searchDayList) {
                List<CidData> resultList = cidDataService.getStationPowerByPidAndDateForDayForMinute(id, null, dateZone, searchDateType);
                TreeMap<String, String> dataMap = new TreeMap<>();
                for (Object obj : resultList) {
                    String s = JSON.toJSONString(obj);
                    CidData d = JSON.parseObject(s, CidData.class);
                    dataMap.put(d.getDate(), d.getPower());
                }
                TreeMap<String, String> rmap = CidDataController.subTreeMap(dataMap, dateZone);


                for (String s : rmap.keySet()) {
                    String add = s + "_" + rmap.get(s);
                    powerList.add(add);
                }
            }
            ajax.put("powerList", powerList);
        }
        return ajax;
    }

    @GetMapping("/echartEnergy")
    public AjaxResult echartsEnergy(Long energyId, String dateType, String searchDate) {

        AjaxResult ajax = AjaxResult.success(new ArrayList<>());
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();

        if (null == dateType) {
            dateType = "1";
        }

        List<CidDayData> resultEnergyListDay = null;
        if (energyId == null) {
            List<CidPowerstationinfo> cidPowerstationinfoList = powerstationinfoService.selectCidPowerstationinfoListByDeptID(user.getDeptId());
            if (cidPowerstationinfoList == null || cidPowerstationinfoList.size() == 0) {
                return ajax;
            }
            energyId = cidPowerstationinfoList.get(0).getId();
        }

        if (dateType.equals("1")) {//getStationEnergyDayByCidVidAndDate

            Map paramMap = new HashMap();

            paramMap.put("plantID", energyId);
            paramMap.put("createDate", searchDate);
            List<Map> cidDataHourList = cidDataHourMapper.analyseByOneDay(paramMap);

            TreeMap<String,String> treeMap=new TreeMap();
            for (int i=0;i<24;i++){
                if (i<10){
                    treeMap.put("0"+i,"0"+i+"_"+0);
                }else{
                    treeMap.put(String.valueOf(i),i+"_"+0);
                }
            }

            for (Map map : cidDataHourList) {
                treeMap.put(map.get("date").toString(),map.get("date") + "_" + map.get("energy"));
            }

            List<String> returnList = new ArrayList<String>();
            for(String s:treeMap.keySet()){
                returnList.add(treeMap.get(s));
            }
            ajax.put("energyList", returnList);
        } else if (dateType.equals("2")) {
            resultEnergyListDay = cidDataService.getStationEnergyByPidAndDate(energyId, null, searchDate, "w");
            ajax.put("energyList", resultEnergyListDay);
        } else if (dateType.equals("3")) {

            Map paramMap = new HashMap();
            List<Long> plantIDList = new ArrayList<Long>();
            plantIDList.add(energyId);
            paramMap.put("plantID", plantIDList);
            paramMap.put("startDate", searchDate.substring(0, searchDate.lastIndexOf("-")) + "-00");
            paramMap.put("endDate", searchDate.substring(0, searchDate.lastIndexOf("-")) + "-31");

            List<Map> cidDataDayList = cidDataDayMapper.selectByDay(paramMap);


            String mothStr= searchDate.substring(0, searchDate.lastIndexOf("-"));

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            TreeMap<String,Map> treeMap=new TreeMap();

            for (int i=1;i<=daysOfMonth;i++){
                Map map=new HashMap();
                map.put("energy",0);
                String date=mothStr+"-"+i;
                if (i<10){
                    date=mothStr+"-"+"0"+i;
                }
                map.put("date",date);
                treeMap.put(date,map);
            }

            for (Map item :cidDataDayList){
                treeMap.put(item.get("date").toString(),item);
            }

            List<Map> returnList=new ArrayList<>();
            for (String s:treeMap.keySet()){
                returnList.add(treeMap.get(s));
            }

            ajax.put("energyList", returnList);


        } else if (dateType.equals("4")) {
            resultEnergyListDay = cidDataService.getStationEnergyByPidAndDate(energyId, null, searchDate, "y");
            List<CidDayData> returnResultList = new ArrayList<>();
            if (resultEnergyListDay.size() > 0) {
                List<String> list = new ArrayList<>();
                String year = "";
                List<CidDayData> dataList = new ArrayList<>();
                for (Object obj : resultEnergyListDay) {
                    String s = JSON.toJSONString(obj);
                    CidDayData d = JSON.parseObject(s, CidDayData.class);
                    dataList.add(d);
                    year = d.getDate().split("-")[0];
                    list.add(d.getDate().split("-")[1]);
                }
                List<Integer> returnList = CidDataController.reAddYearList(list);
                Map<Integer, String> resultMap = new HashMap<>();
                for (int i = 0; i < returnList.size(); i++) {
                    resultMap.put(returnList.get(i), "0");
                }
                for (int i = 0; i < dataList.size(); i++) {
                    String data = "";
                    data = dataList.get(i).getEnergy();
                    resultMap.put(Integer.parseInt(dataList.get(i).getDate().split("-")[1]), data);
                }
                for (Integer key : resultMap.keySet()) {
                    CidDayData d = new CidDayData();
                    if (key < 10) {
                        d.setDate(year + "-0" + key);
                    } else {
                        d.setDate(year + "-" + key);
                    }
                    d.setEnergy(resultMap.get(key));
                    returnResultList.add(d);
                }
            } else {
                List<String> list = new ArrayList<>();
                String year = searchDate.split("-")[0];
                List<Integer> returnList = CidDataController.reAddYearList(list);
                Map<Integer, String> resultMap = new HashMap<>();
                for (int i = 0; i < returnList.size(); i++) {
                    resultMap.put(returnList.get(i), "0");
                }
                for (Integer key : resultMap.keySet()) {
                    CidDayData d = new CidDayData();
                    if (key < 10) {
                        d.setDate(year + "-0" + key);
                    } else {
                        d.setDate(year + "-" + key);
                    }
                    d.setEnergy(resultMap.get(key));
                    returnResultList.add(d);
                }
            }
            ajax.put("energyList", returnResultList);
        } else if (dateType.equals("5")) {

            resultEnergyListDay = cidDataService.getStationEnergyByPidAndDate(energyId, null, searchDate, "a");
            ajax.put("energyList", resultEnergyListDay);
        } else if (dateType.equals("11")) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式

            Calendar cal = Calendar.getInstance();
            try {
                cal.setTime(sdf.parse(searchDate));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            //
            // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
            int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
            if (1 == dayWeek) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
            cal.setFirstDayOfWeek(Calendar.SUNDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
            int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
            cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
            String weekBegin = sdf.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
            String monday = sdf.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
            String tuesday = sdf.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
            String wednesday = sdf.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
            String thursday = sdf.format(cal.getTime());
            cal.add(Calendar.DATE, 1);
            String friday = sdf.format(cal.getTime());

            cal.add(Calendar.DATE, 1);
            String weekTailer = sdf.format(cal.getTime());

            Map map=new HashMap();
            map.put("energy",0);

            TreeMap<String,Map> treeMap = new TreeMap();
            map.put("date",weekBegin);
            treeMap.put(weekBegin,map);

            map.put("date",monday);
            treeMap.put(monday,map);

            map.put("date",tuesday);
            treeMap.put(tuesday,map);

            map.put("date",wednesday);
            treeMap.put(wednesday,map);

            map.put("date",thursday);
            treeMap.put(thursday,map);

            map.put("date",friday);
            treeMap.put(friday,map);

            map.put("date",weekTailer);
            treeMap.put(weekTailer,map);


            Map paramMap = new HashMap();
            List<Long> plantIDList = new ArrayList<Long>();
            plantIDList.add(energyId);
            paramMap.put("plantID", plantIDList);
            paramMap.put("startDate", weekBegin);
            paramMap.put("endDate", weekTailer);

            List<Map> cidDataDayList = cidDataDayMapper.selectByDay(paramMap);
            for (Map itemMap:cidDataDayList){
                treeMap.put(itemMap.get("date").toString(),itemMap);
            }

            List<Map> returnList =new ArrayList<>();
            for (String s:treeMap.keySet()){
                returnList.add(treeMap.get(s));
            }
            ajax.put("energyList", returnList);

        }
        return ajax;
    }


    @GetMapping("/details")
    public AjaxResult details(Long energyId) throws ParseException {

        CidPowerstationinfo plant = powerstationinfoService.selectCidPowerstationinfoById(energyId);
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String beginTime = format.format(new Date());
        String endTime = plant.getLastUpdate();

        if (!plant.getLastUpdate().equals("0")) {
            Date date1 = format.parse(beginTime);
            Date date2 = format.parse(endTime);
            long beginMillisecond = date1.getTime();
            long endMillisecond = date2.getTime();
            long jian = beginMillisecond - endMillisecond;
            if (beginTime.substring(0, 10).equals(endTime.substring(0, 10))) {
                if (!(jian > 1000 * 60 * 30) && jian > 0) {
                    if (plant.getLastErrorCode().equals("0000") || plant.getLastErrorCode().equals("0200")) {
                        plant.setEnergyStatus("0");
                    } else {
                        plant.setEnergyStatus("1");
                    }
                } else {
                    plant.setEnergyStatus("3");
                    plant.setEnergyCurrentPower("0");
                }
            } else {
                plant.setEnergyStatus("3");
                plant.setEnergyCurrentPower("0");
            }
        } else {
            plant.setEnergyStatus("2");
        }
        AjaxResult ajaxResult = AjaxResult.success();
        if (energyId == null) {
            ajaxResult.put("code", 100);
        } else {
            ajaxResult.put("code", 200);
            ajaxResult.put("plant", plant);
        }
        return ajaxResult;
    }

    @GetMapping("/getPower")
    public AjaxResult getPower(Long energyId) {

        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();

        List<CidRelation> relationList = null;
        if (energyId == null) {
            relationList = relationService.selectCidRelationListByPowerStationIds(null, user.getDeptId());
        } else {
            relationList = relationService.selectCidRelationListByPowerStationIds(energyId, null);
        }

        String[] cids = new String[relationList.size()];
        for (int i = 0; i < relationList.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) relationList.get(i);
            cids[i] = (String) map.get("cid");
        }

        String countPower = cidDataService.getCidPowerCount(cids);
        if (countPower == null || countPower.equals("")) {
            countPower = "0";
        }

        AjaxResult ajax = AjaxResult.success();
        ajax.put("countPower", countPower);
        return ajax;
    }

    @GetMapping("/getCvCount")
    public AjaxResult getCvCount(Long energyId) {

        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();

        List<CidRelation> relationList = null;
        List<String> cidList = new ArrayList<String>();
        if (energyId == null) {
            relationList = relationService.selectCidRelationListByPowerStationIds(null, user.getDeptId());
        } else {
            relationList = relationService.selectCidRelationListByPowerStationIds(energyId, null);
        }
        for (int i = 0; i < relationList.size(); i++) {
            Map<String, Object> map = (Map<String, Object>) relationList.get(i);
            cidList.add((String) map.get("cid"));
        }
        cidList = cidList.stream()
                .distinct()
                .collect(Collectors.toList());
        int cidCount = cidList.size();

        AjaxResult ajax = AjaxResult.success();
        ajax.put("cidCount", cidCount);
        ajax.put("vidCount", relationList.size());
        return ajax;
    }

    @GetMapping("/getCidList")
    public TableDataInfo getCidList(String cid, String vid, String searchDate, Long powerStationId, Integer currentPage, Integer pageSize) {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        if (currentPage == null) {
            currentPage = 1;
        }
        if (pageSize == null) {
            pageSize = 20;
        }
        PageHelper.startPage(currentPage, pageSize);
        //startPage();
        List<CidData> resultList = cidDataService.getAppCidList(user.getDeptId(), cid, vid, searchDate, powerStationId);
        return getDataTable(resultList);
    }

    @GetMapping("/getCidVidCount")
    public AjaxResult getCidVidCount(Long plantId) {

        List<CidRelation> cidList = relationService.getAppCidVidCount(plantId, "cid");
        List<CidRelation> vidList = relationService.getAppCidVidCount(plantId, "vid");
        AjaxResult ajaxResult = AjaxResult.success();
        ajaxResult.put("cidCount", cidList.size());
        ajaxResult.put("vidCount", vidList.size());
        return ajaxResult;
    }

    @GetMapping("/getWorkList")
    public TableDataInfo getWorkList() {
        startPage();
        //当前登陆用户 deptId过滤
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        SysDept dept = deptService.selectDeptById(user.getDeptId());
        Long deptId = user.getDeptId();

        CidWorkOrder cidWorkOrder = new CidWorkOrder();

        if (deptId == 100) {
            cidWorkOrder.setWorkOrderSentTo(100l);
        } else {
            cidWorkOrder.setWorkOrderSentTo(dept.getParentId());
        }
        List<CidWorkOrder> list = cidWorkOrderService.selectCidWorkOrderUnFinishList(cidWorkOrder);
        for (CidWorkOrder order : list) {
            if (deptId == 100) {
                order.setSentDeptName("Bjzeny");
            } else {
                order.setSentDeptName(deptService.selectDeptById(dept.getParentId()).getDeptName());
            }
        }
        return getDataTable(list);
    }

    @GetMapping("/getWorkFinishList")
    public TableDataInfo getWorkFinishList() {
        startPage();
        //当前登陆用户 deptId过滤
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        SysDept dept = deptService.selectDeptById(user.getDeptId());
        Long deptId = user.getDeptId();

        CidWorkOrder cidWorkOrder = new CidWorkOrder();
        cidWorkOrder.setWorkOrderStatus("2");
        if (deptId == 100) {
            cidWorkOrder.setWorkOrderSentTo(100l);
        } else {
            cidWorkOrder.setWorkOrderSentTo(dept.getParentId());
        }
        List<CidWorkOrder> list = cidWorkOrderService.selectCidWorkOrderFinishList(cidWorkOrder);
        for (CidWorkOrder order : list) {
            if (deptId == 100) {
                order.setSentDeptName("Bjzeny");
            } else {
                order.setSentDeptName(deptService.selectDeptById(dept.getParentId()).getDeptName());
            }
        }
        return getDataTable(list);
    }

    @GetMapping("/getDeptType")
    public AjaxResult getDeptType() {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        Long deptId = user.getDeptId();
        SysDept dept = deptService.selectDeptById(deptId);
        AjaxResult ajax = AjaxResult.success(dept);
        return ajax;
    }

    @GetMapping("/getWorkReplayList")
    public TableDataInfo getWorkReplayList() {
        startPage();
        //当前登陆用户 deptId过滤
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        Long deptId = user.getDeptId();
        CidWorkOrder cidWorkOrder = new CidWorkOrder();

        if (deptId != 100) {//厂家可以看所有工单
            cidWorkOrder.setWorkOrderSentTo(deptId);
        }
        List<CidWorkOrder> list = cidWorkOrderService.selectCidWorkOrderList(cidWorkOrder);
        for (CidWorkOrder order : list) {
            if (deptId == 100) {
                order.setCreateDeptName("Bjzeny");
            } else {
                order.setCreateDeptName(deptService.selectDeptById(Long.parseLong(order.getCreateBy())).getDeptName());
            }
        }
        return getDataTable(list);
    }

    @GetMapping("/updateWork")
    public AjaxResult updateWork(Long id, String info, String status) {

        CidWorkOrder workOrder = cidWorkOrderService.selectCidWorkOrderByWorkOrderId(id);
        workOrder.setWorkOrderReplayInfo(info);
        workOrder.setWorkOrderStatus(status);
        AjaxResult ajax = AjaxResult.success(cidWorkOrderService.updateCidWorkOrder(workOrder));
        return ajax;
    }

    @GetMapping("/createWork")
    public AjaxResult createWork(String info) {

        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        Long deptId = user.getDeptId();
        SysDept dept = deptService.selectDeptById(deptId);

        CidWorkOrder cidWorkOrder = new CidWorkOrder();
        cidWorkOrder.setWorkOrderInfo(info);
        if (dept.getDeptId() == 100) {
            cidWorkOrder.setWorkOrderSentTo(100l);
        } else {
            cidWorkOrder.setWorkOrderSentTo(dept.getParentId());
        }
        cidWorkOrder.setCreateDeptId(deptId);
        cidWorkOrder.setWorkOrderStatus("0");

        AjaxResult ajax = AjaxResult.success(cidWorkOrderService.insertCidWorkOrder(cidWorkOrder));
        return ajax;
    }


    @PostMapping("/addRelation")
    public AjaxResult addRelation(Long powerStationId, String cid, String vid, String roadType) {

        AjaxResult ajax = AjaxResult.success();

        if (powerStationId <= 0 || cid == null || vid == null || roadType == null) {
            logger.error("===CidRelationController addDevice cidRelationReq parameter is error cid:" + cid + " powerStationId:" + powerStationId + "  cid:" + cid + " vid:" + vid + " roadType:" + roadType);
            ajax.put("msg", "parameter is illegal");
            ajax.put("code", 100);
            return ajax;
        }

        CidRelation relation = new CidRelation();
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        boolean isInsert = false;

        if (powerStationId != null && cid != null) {

            List<Map> cidRelationByCidAndPlantIdList = cidRelationService.selectVidByCidAndPlantID(cid, powerStationId);
            int roadCount = 0;
            for (int i = 0; i < cidRelationByCidAndPlantIdList.size(); i++) {
                Map cidRelation = JSON.parseObject(JSON.toJSONString(cidRelationByCidAndPlantIdList.get(i)), Map.class);
                if (cidRelation.get("vid") == null) {
                    continue;
                }
                if (cidRelation.get("vid").equals(vid)) {
                    logger.error("===CidRelationController addDevice cidRelationReq parameter is error :" + JSON.toJSONString(cidRelation));
                    ajax.put("msg", "MI  existed.");
                    ajax.put("code", 100);
                    return ajax;
                }

                String vidHeadStr = cidRelation.get("vid").toString().substring(0, 1);
                int vidHead = Integer.parseInt(vidHeadStr);
                roadCount = roadCount + vidHead;
            }

            if (roadCount >= 24) {
                ajax.put("msg", "The total number of microinverter roads exceeded 32.");
                ajax.put("code", 100);
                return ajax;
            }

            String vidHead = cid.substring(0, 1);
            boolean isMIW = false;
            if (vidHead.toLowerCase().equals("c")) {
                vidHead = "4";
                isMIW = true;

            } else if (vidHead.toLowerCase().equals("a")) {
                vidHead = "1";
                isMIW = true;
            } else if (vidHead.toLowerCase().equals("b")) {
                vidHead = "2";
                isMIW = true;
            }

            relation.setCid(cid);
            if (isMIW) {
                relation.setVid(vidHead + cid.substring(1, cid.length()));
            } else {
                relation.setVid(vid);
            }
            relation.setPowerStationId(powerStationId);
            relation.setDelFlag("0");
            relation.setCidType("02");
            relation.setStatus("3");
            relation.setCreateTime(new Date());
            relation.setCreateBy(user.getUserId().toString());
            relation.setRoadType(vidHead);

            if (roadType != null) {
                relation.setRoadType(roadType);
            }
            if (vid != null && !vid.equals("")) {

                List<CidRelation> cidRelationList = relationService.selectCidRelationByStationIdWithGroup(powerStationId, cid, vid);
                if (cidRelationList == null || cidRelationList.size() == 0) {

                    String loopType = vid.substring(0, 1);
                    if (loopType.equals("2") || loopType.equals("4")) {
                        for (int i = 0; i < Integer.parseInt(loopType); i++) {
                            String loop = "";
                            if (loopType.equals("2")) {
                                loop = "1" + (i + 1);
                            } else if (loopType.equals("4")) {
                                loop = "2" + (i + 1);
                            }
                            relation.setRoadType(loop);
                            relationService.insertCidRelation(relation);
                            isInsert = true;
                        }
                    } else {
                        relationService.insertCidRelation(relation);
                        isInsert = true;
                    }
                    ajax.put("code", 200);
                } else {
                    ajax.put("code", 100);
                    ajax.put("msg", "Mis " + vid + " is existed");
                }
            } else {
                List<Map> emuList = relationService.selectEmuList(user.getDeptId(), powerStationId, cid);
                if (emuList == null || emuList.size() == 0) {
                    relationService.insertCidRelation(relation);

                    Map updatedDeviceMap = redisCache.getCacheMap("updatedDeviceMap");
                    if (updatedDeviceMap == null) {
                        updatedDeviceMap = new HashMap();
                        updatedDeviceMap.put(relation.getCid(), 1);
                    }
                    ajax.put("code", 200);
                } else {
                    ajax.put("code", 100);
                    ajax.put("msg", "EMU is existed");
                }
            }


        } else {
            if (powerStationId == null) {
                ajax.put("msg", "powerStationId is null");
            }
            if (cid == null) {
                ajax.put("msg", "EMU is null");
            }
            ajax.put("code", 100);
        }
        return ajax;
    }

    @GetMapping("/removeRelationByVid")
    public AjaxResult removeRelationByVid(Long powerStationId, String cid, String vid, String roadType) {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success();
        if (cid == null) {//powerStationId==null||
            ajax.put("code", 100);
            if (cid == null) {
                ajax.put("msg", "cid is null");
            }
        } else {
            relationService.deleteCidRelationByInfo(cid, vid, roadType, powerStationId, user.getUserId().toString());
            ajax.put("code", 200);
        }
        return ajax;
    }

    @GetMapping("/removeRelationByCid")
    public AjaxResult addRelationByCid(Long powerStationId, String cid) {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success();
        if (cid == null) {//powerStationId==null||
            ajax.put("code", 100);
            if (cid == null) {
                ajax.put("msg", "cid is null");
            }
        } else {
            relationService.deleteCidRelationByCid(cid, powerStationId, user.getUserId().toString());
            ajax.put("code", 200);
        }
        return ajax;
    }

    @GetMapping("/valPowerStationCount")
    public AjaxResult getPowerStationCount() {
        LoginUser loginUser = getLoginUser();
        CidPowerstationinfo query = new CidPowerstationinfo();
        query.setEnergyDeptId(loginUser.getDeptId());
        query.setDelFlag("0");
        List<CidPowerstationinfo> list = powerstationinfoService.selectCidPowerstationinfoList(query);
        AjaxResult ajaxResult = AjaxResult.success();
        ajaxResult.put("powerStationCount", list.size());
        return ajaxResult;
    }

    /**
     * 废弃
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/getCidListForEquip")
    public AjaxResult getCidListForEquip() throws ParseException {

        LoginUser loginUser = getLoginUser();
        List<CidRelation> returnList = new ArrayList<>();

        for (Object obj : relationService.selectEmuList(loginUser.getDeptId(), null, null)) {
            String s = JSON.toJSONString(obj);
            CidRelation relation = JSON.parseObject(s, CidRelation.class);
            CidRelation queryRelation = new CidRelation();
            queryRelation.setDelFlag("0");
            queryRelation.setBindType("0");
            queryRelation.setCid(relation.getCid());
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            List<CidRelation> vidList = relationService.selectCidRelationList(queryRelation);
            List<CidRelation> childList = new ArrayList<>();
            Float currentPower = 0f;
            Float currentEnergy = 0f;

            boolean hasError = false;

            String onlineStatus = "OnLine";
            List<CidLoginHeart> heartList = heartService.selectLastUpdateTime(relation.getCid());

            String cidLastHeart = "";
            if (heartList.size() > 0) {
                cidLastHeart = format.format(heartList.get(0).getCreateDate());
            }
            int offlineCount = 0;
            int onlineCount = 0;
            int errorCount = 0;
            String addTime = "";
            boolean isHeart = false;
            String heartTime = "";
            for (CidRelation r : vidList) {
                Long id = cidDataService.selectEmuTreeMisId(r.getCid(), r.getVid(), r.getRoadType());
                //String plantName="";
                //plantName=powerstationinfoService.selectCidPowerstationinfoById(r.getPowerStationId()).getEnergyName();
                if (id != null) {
                    CidData child = cidDataService.selectCidDataById(id);
                    CidRelation addRelation = new CidRelation();
                    addRelation.setCid(child.getVid());
                    addRelation.setCidType("MIs");

                    if (child.getRoadType() != null) {
                        System.out.println("add???---" + child.getRoadType());
                        addRelation.setRoadType(child.getRoadType().substring(1, 2));
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

                    if (beginTime.substring(0, 10).equals(endTime.substring(0, 10))) {
                        if (!(jian > 1000 * 60 * 30) && jian > 0) {

                            status = "OnLine";
                            onlineStatus = "OnLine";
                            onlineCount++;
                        } else {
                            status = "OffLine";
                            onlineStatus = "OffLine";
                            offlineCount++;
                        }
                    } else {
                        status = "OffLine";
                        onlineStatus = "OffLine";
                        offlineCount++;
                    }
                    if (child.getStatus().equals("1")) {
                        hasError = true;
                        onlineStatus = "Error";
                        errorCount++;
                        status = "Error";


                    }

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
                        List<CidRelation> childEnergy = relationService.selectCidRelationList(childQueryRelation);
                        CidRelation childRelation = childEnergy.get(0);
                        currentEnergy = (currentEnergy + Float.parseFloat(childRelation.getCurrentEnergy()));
                        addRelation.setCurrentEnergy(childRelation.getCurrentEnergy());
                        addRelation.setCurrentPower(child.getPower());


                    } else {
                        currentPower = (currentPower + 0f);
                        currentEnergy = (currentEnergy + 0f);
                        addRelation.setCurrentEnergy("0");
                        addRelation.setCurrentPower("0");
                    }
                    addRelation.setHasError(hasError);
                    addRelation.setStatus(status);
                    addRelation.setVid(r.getCid());
                    addRelation.setUpdateTime(r.getUpdateTime());
                    addRelation.setSoftVersion(r.getSoftVersion());
                    addRelation.setSoftDeputyVersion(r.getSoftDeputyVersion());
                    addRelation.setPowerStationName(r.getPowerStationName());

                    if (status.equals("OffLine")) {
                        addRelation.setCurrentPower("0");
                        addRelation.setLastError("----");
                        addRelation.setLastErrorCode("----");
                    } else {
                        currentPower = (currentPower + Float.parseFloat(child.getPower()));
                    }
                    addRelation.setLastDate(endTime);
                    addRelation.setSoftVersion(child.getSoftVersion());
                    addRelation.setSoftDeputyVersion(child.getSoftDeputyVersion());

                    childList.add(addRelation);
                    relation.setLastDate(endTime);

                    if (offlineCount == childList.size() && !cidLastHeart.equals("")) {
                        isHeart = true;
                        Date heartDate = format.parse(cidLastHeart);
                        long cidBeginMillisecond = heartDate.getTime();
                        long cidEndMillisecond = date1.getTime();
                        long cidJian = cidEndMillisecond - cidBeginMillisecond;
                        //beginTime.substring(0,10).equals(endTime.substring(0,10))
                        if (beginTime.substring(0, 10).equals(cidLastHeart.substring(0, 10))) {
                            if (!(cidJian > 1000 * 60 * 30) && cidJian > 0) {
                                onlineStatus = "OnLine";
                                onlineCount++;
                            } else {
                                onlineStatus = "OffLine";
                            }
                        } else {
                            onlineStatus = "OffLine";
                        }
                        relation.setCurrentPower("0");
                        relation.setStatus(onlineStatus);
                        relation.setLastDate(cidLastHeart);
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
                    addRelation.setPowerStationName(r.getPowerStationName());
                    addRelation.setUpdateTime(r.getUpdateTime());
                    addRelation.setSoftVersion(r.getSoftVersion());
                    addRelation.setSoftDeputyVersion(r.getSoftDeputyVersion());

                    childList.add(addRelation);

                    offlineCount++;
                }

            }

            if (!isHeart) {
                if (onlineCount > 0 && errorCount == 0) {
                    System.out.println("Addtime:" + addTime);
                    relation.setStatus("OnLine");
                    relation.setLastDate(addTime);
                } else if (onlineCount > 0 && errorCount > 0) {
                    relation.setStatus("Error");
                    relation.setLastDate(addTime);
                } else if (offlineCount == childList.size()) {
                    relation.setStatus("OffLine");
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
        AjaxResult ajaxResult = AjaxResult.success();
        ajaxResult.put("cidList", returnList);
        return ajaxResult;
    }

    /**
     * 新版api
     *
     * @return
     * @throws ParseException
     */
    @GetMapping("/getCidListForEquipNew")
    public AjaxResult getCidListForEquipNew() throws ParseException {

        LoginUser loginUser = getLoginUser();
        List<CidRelation> returnMisList = new ArrayList<>();
        List returnEMUList = new ArrayList();
        int misNormalCount = 0;

        List<CidRelation> returnList = new ArrayList<>();
        for (Object obj : relationService.selectEmuList(loginUser.getDeptId(), null, null)) {
            String s = JSON.toJSONString(obj);
            CidRelation relation = JSON.parseObject(s, CidRelation.class);
            CidRelation queryRelation = new CidRelation();
            queryRelation.setDelFlag("0");
            queryRelation.setBindType("0");
            queryRelation.setCid(relation.getCid());
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            List<CidRelation> vidList = relationService.selectCidRelationList(queryRelation);
            List<CidRelation> childList = new ArrayList<>();
            Float currentPower = 0f;
            Float currentEnergy = 0f;

            boolean hasError = false;
            String onlineStatus = "OnLine";
            List<CidLoginHeart> heartList = heartService.selectLastUpdateTime(relation.getCid());

            String cidLastHeart = "";

            if (heartList.size() > 0) {
                cidLastHeart = format.format(heartList.get(0).getCreateDate());
            }
            int offlineCount = 0;
            int onlineCount = 0;
            int errorCount = 0;
            String addTime = "";
            boolean isHeart = false;
            String heartTime = "";
            for (CidRelation r : vidList) {
                Long id = cidDataService.selectEmuTreeMisId(r.getCid(), r.getVid(), r.getRoadType());
                //String plantName="";
                //plantName=powerstationinfoService.selectCidPowerstationinfoById(r.getPowerStationId()).getEnergyName();
                if (id != null) {
                    CidData child = cidDataService.selectCidDataById(id);
                    CidRelation addRelation = new CidRelation();
                    addRelation.setCid(child.getVid());
                    addRelation.setCidType("MIs");

                    if (child.getRoadType() != null) {
                        addRelation.setRoadType(child.getRoadType().substring(1, 2));
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

                    if (beginTime.substring(0, 10).equals(endTime.substring(0, 10))) {
                        if (!(jian > 1000 * 60 * 30) && jian > 0) {

                            status = "OnLine";
                            onlineStatus = "OnLine";
                            onlineCount++;
                            misNormalCount = misNormalCount + 1;
                        } else {
                            status = "OffLine";
                            onlineStatus = "OffLine";
                            offlineCount++;
                        }
                    } else {
                        status = "OffLine";
                        onlineStatus = "OffLine";
                        offlineCount++;
                    }
                    if (child.getStatus().equals("1")) {
                        hasError = true;
                        onlineStatus = "Error";
                        errorCount++;
                        status = "Error";

                    }

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
                        List<CidRelation> childEnergy = relationService.selectCidRelationList(childQueryRelation);
                        CidRelation childRelation = childEnergy.get(0);
                        currentEnergy = (currentEnergy + Float.parseFloat(childRelation.getCurrentEnergy()));
                        addRelation.setCurrentEnergy(childRelation.getCurrentEnergy());
                        addRelation.setCurrentPower(child.getPower());
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
                        addRelation.setCurrentPower("0");
                        addRelation.setLastError("----");
                        addRelation.setLastErrorCode("----");
                    } else {
                        currentPower = (currentPower + Float.parseFloat(child.getPower()));
                    }
                    addRelation.setLastDate(endTime);
                    addRelation.setSoftVersion(child.getSoftVersion());
                    addRelation.setSoftDeputyVersion(child.getSoftDeputyVersion());
                    childList.add(addRelation);
                    returnMisList.add(addRelation);

                    //心跳和发电时间比较 并且判断 是否为error
                    relation.setLastDate(endTime);

                    if (offlineCount == childList.size() && !cidLastHeart.equals("")) {
                        isHeart = true;
                        Date heartDate = format.parse(cidLastHeart);
                        long cidBeginMillisecond = heartDate.getTime();
                        long cidEndMillisecond = date1.getTime();
                        long cidJian = cidEndMillisecond - cidBeginMillisecond;
                        //beginTime.substring(0,10).equals(endTime.substring(0,10))
                        if (beginTime.substring(0, 10).equals(cidLastHeart.substring(0, 10))) {
                            if (!(cidJian > 1000 * 60 * 30) && cidJian > 0) {
                                onlineStatus = "OnLine";
                                onlineCount++;
                            } else {
                                onlineStatus = "OffLine";
                            }
                        } else {
                            onlineStatus = "OffLine";
                        }
                        relation.setCurrentPower("0");
                        relation.setStatus(onlineStatus);
                        relation.setLastDate(cidLastHeart);
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
                    childList.add(addRelation);
                    offlineCount++;
                }

            }

            if (!isHeart) {
                if (onlineCount > 0 && errorCount == 0) {
                    relation.setStatus("OnLine");
                    relation.setLastDate(addTime);
                } else if (onlineCount > 0 && errorCount > 0) {
                    relation.setStatus("Error");
                    relation.setLastDate(addTime);
                } else if (offlineCount == childList.size()) {
                    relation.setStatus("OffLine");
                    relation.setLastDate(addTime);
                }
            }


            if (onlineStatus.equals("OffLine")) {
                relation.setLastErrorCode("----");
                relation.setLastError("----");
            }
            relation.setHasError(hasError);
            relation.setCidType("EMU");
            relation.setCurrentEnergy(String.valueOf(currentEnergy));
            relation.setCurrentPower(String.valueOf(currentPower));

            String plantName = "";
            plantName = powerstationinfoService.selectCidPowerstationinfoById(relation.getPowerStationId()).getEnergyName();
            relation.setPowerStationName(plantName);
            returnList.add(relation);
            returnEMUList.add(relation);

        }

        Map data = new HashMap();
        data.put("mis", returnMisList);
        data.put("misNormalCount", misNormalCount);
        data.put("misCount", returnMisList != null ? 0 : returnMisList.size());
        AjaxResult ajaxResult = AjaxResult.success();
        ajaxResult.put("data", data);

        return ajaxResult;
    }

    @Log(title = "网关、微逆关系", businessType = BusinessType.DELETE)
    @GetMapping("/removeByInfo")
    public AjaxResult removeByInfo(String cid, String vid, Long powerStationId) {

        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();

        CidDayData updateDayDate = new CidDayData();
        updateDayDate.setCid(cid);
        updateDayDate.setVidArr(new String[]{vid});
        relationService.updateCidRelationByCammand(cid, "1");
        dayDataService.updateBatchForRemoveAndDel(updateDayDate);


        AjaxResult ajaxResult = AjaxResult.success(relationService.deleteCidRelationByInfo(cid,
                vid, null, powerStationId, user.getUserId().toString()));

        Map updatedDeviceMap = redisCache.getCacheMap("updatedDeviceMap");
        if (updatedDeviceMap == null) {
            updatedDeviceMap = new HashMap();
        }
        updatedDeviceMap.put(cid, 1);
        redisCache.setCacheMap("updatedDeviceMap", updatedDeviceMap);

        return ajaxResult;
    }


    @GetMapping("/selectMisDetailByDateType")
    public AjaxResult selectMisDetailByDateType(String cid, String vid, String loop, String searchDateStart, String searchDateEnd, String searchType, String dataType) {

        if (searchType.equals("d")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date();
            SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");

            if (searchDateStart == null) {
                searchDateStart = sdfDay.format(date);
                searchDateEnd = sdfDay.format(date);
            }
            String startMonth = searchDateStart.substring(5, 7);
            String endMonth = searchDateEnd.substring(5, 7);
            List<String> searchDayList = new ArrayList<>();
            if (startMonth.equals(endMonth)) {
                String startDay = searchDateStart.substring(searchDateStart.length() - 2, searchDateStart.length());
                String endDay = searchDateEnd.substring(searchDateEnd.length() - 2, searchDateEnd.length());
                int dayZone = Integer.parseInt(endDay) - Integer.parseInt(startDay);
                String searchDateZone = searchDateStart.substring(0, searchDateStart.length() - 2);

                //System.out.println("searchDateZone:"+searchDateZone);
                for (int i = 0; i <= dayZone; i++) {
                    searchDateZone = searchDateStart.substring(0, searchDateStart.length() - 2);
                    String addDate = "";
                    if (Integer.parseInt(startDay) + i < 10) {
                        addDate = "0" + (Integer.parseInt(startDay) + i);
                    } else {
                        addDate = String.valueOf((Integer.parseInt(startDay) + i));
                    }
                    searchDateZone = searchDateZone + addDate;
                    searchDayList.add(searchDateZone);
                }
            } else {
                String startDay = searchDateStart.substring(searchDateStart.length() - 2, searchDateStart.length());
                String endDay = searchDateEnd.substring(searchDateEnd.length() - 2, searchDateEnd.length());
                String year = searchDateStart.substring(0, 4);
                int startMontDay = CidDataController.getDays(Integer.parseInt(year), Integer.parseInt(startMonth));
                String searchDateZone = searchDateStart.substring(0, searchDateStart.length() - 2);
                String searchDateEndZone = searchDateEnd.substring(0, searchDateEnd.length() - 2);
                int startDay_1 = Integer.parseInt(startDay);
                for (int i = 0; i <= (startMontDay - startDay_1); i++) {
                    searchDateZone = searchDateStart.substring(0, searchDateStart.length() - 2);
                    String addDate = "";
                    if (Integer.parseInt(startDay) + i < 10) {
                        addDate = "0" + (Integer.parseInt(startDay) + i);
                    } else {
                        addDate = String.valueOf((Integer.parseInt(startDay) + i));
                    }
                    searchDateZone = searchDateZone + addDate;
                    searchDayList.add(searchDateZone);
                }
                for (int j = 1; j <= Integer.parseInt(endDay); j++) {

                    if (j < 10) {
                        searchDateEndZone = year + "-" + endMonth + "-0" + (j);
                    } else {
                        searchDateEndZone = year + "-" + endMonth + "-" + (j);
                    }
                    searchDayList.add(searchDateEndZone);
                }
            }
            List<CidData> returnList = new ArrayList<>();
            for (String dateZone : searchDayList) {
                List<Map> resultList = cidDataService.selectMisDetailByDateTypeForDayNewForMinute(null,cid, vid, loop, dateZone);
                TreeMap<String, String> dataMapPower = new TreeMap<>();
                TreeMap<String, String> dataMapVolt = new TreeMap<>();
                TreeMap<String, String> dataMapTemp = new TreeMap<>();
                TreeMap<String, String> dataMapFreq = new TreeMap<>();
                TreeMap<String, String> dataMapGridVolt = new TreeMap<>();
                for (Object obj : resultList) {
                    String s = JSON.toJSONString(obj);
                    //System.out.println(s);
                    CidData d = JSON.parseObject(s, CidData.class);
                    String dataPower = "";
                    String dataVolt = "";
                    String dataTemp = "";
                    String dataFreq = "";
                    String dataGridVolt = "";
                    dataPower = d.getPower();
                    dataVolt = d.getVolt();
                    dataTemp = d.getTemp();
                    dataFreq = d.getGridFreq();
                    dataGridVolt = d.getGridVolt();
                    String fmtDate = sdf.format(d.getCreateDate());
                    dataMapPower.put(fmtDate, dataPower);
                    dataMapVolt.put(fmtDate, dataVolt);
                    dataMapTemp.put(fmtDate, dataTemp);
                    dataMapFreq.put(fmtDate, dataFreq);
                    dataMapGridVolt.put(fmtDate, dataGridVolt);
                }

                TreeMap<String, String> rmapPower = CidDataController.subTreeMapNotAdd(dataMapPower, dateZone);
                TreeMap<String, String> rmapVolt = CidDataController.subTreeMapNotAdd(dataMapVolt, dateZone);
                TreeMap<String, String> rmapTemp = CidDataController.subTreeMapNotAdd(dataMapTemp, dateZone);
                TreeMap<String, String> rmapFreq = CidDataController.subTreeMapNotAdd(dataMapFreq, dateZone);
                TreeMap<String, String> rmapGridVolt = CidDataController.subTreeMapNotAdd(dataMapGridVolt, dateZone);
                for (String s : rmapPower.keySet()) {
                    CidData data = new CidData();
                    data.setDate(s);
                    data.setPower(rmapPower.get(s));
                    data.setVolt(rmapVolt.get(s));
                    data.setTemp(rmapTemp.get(s));
                    data.setGridFreq(rmapFreq.get(s));
                    data.setGridVolt(rmapGridVolt.get(s));
                    returnList.add(data);
                }
            }

            AjaxResult ajaxResult = AjaxResult.success(returnList);
            return ajaxResult;
        } else {
            List<CidDayData> resultListDay = cidDataService.selectMisDetailByDateType(cid, vid, loop, searchDateStart, searchDateEnd, searchType);
            AjaxResult ajaxResult = AjaxResult.success(resultListDay);
            return ajaxResult;
        }

    }

    @GetMapping("/selectMisEnergyPowerByDate")
    public AjaxResult selectMisEnergyPowerByDate(String cid, String vid, String loop, String searchDate, String searchType) {

        if (searchType.equals("d")) {
            Map paramMap = new HashMap();
            paramMap.put("cid", cid);
            paramMap.put("vid", vid);
            paramMap.put("createDate", searchDate);
            List<Map> cidDataHourMapList = cidDataHourMapper.analyseByOneDay(paramMap);

            TreeMap<String, Map> heMap = new TreeMap<String, Map>();
            for (int i = 0; i < 24; i++) {

                Map map =new HashMap();
                map.put("energy",0);
                if (i < 10) {
                    map.put("date","0" + i);
                } else {
                    map.put("date",String.valueOf(i));;
                }
                heMap.put(map.get("date").toString(), map);
            }

            for (Map itemMap : cidDataHourMapList) {
                heMap.put(itemMap.get("date").toString(),itemMap);
            }

            List<Map> returnList =new ArrayList<>();
            for (String s:heMap.keySet()){
                returnList.add(heMap.get(s));
            }

            return AjaxResult.success(returnList);
        } else {

            if (searchType.equals("m")) {
                String searchStartDate = null;
                String searchEndDate = null;

                if (searchDate.length() <= 7) {
                    searchStartDate = searchDate + "-01";
                    searchEndDate = searchDate + "-31";
                } else {
                    searchStartDate = searchDate.substring(0, searchDate.indexOf("-") + 3) + "-01";
                    searchEndDate = searchDate.substring(0, searchDate.indexOf("-") + 3) + "-31";
                }

                Map paramMap = new HashMap();
                paramMap.put("cid", cid);
                paramMap.put("vid", vid);
                paramMap.put("startDate", searchStartDate);
                paramMap.put("endDate", searchEndDate);

                List<Map> cidDataDaysList = cidDataDayMapper.selectByDay(paramMap);
                List<Map> returnList=new ArrayList<>();
                for (Map map:cidDataDaysList){
                    Map tmpMap =new HashMap();
                    String date =map.get("date").toString();
                    tmpMap.put("createDate",date.substring(date.indexOf("-")+1,date.length()));
                    tmpMap.put("energy",map.get("energy"));
                    tmpMap.put("cid", cid);
                    tmpMap.put("vid", vid);
                    returnList.add(tmpMap);
                }

                String mothStr= searchDate.substring(searchDate.indexOf("-")+1, searchDate.lastIndexOf("-"));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());

                int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                TreeMap<String,Map> treeMap=new TreeMap();
                for (int i=1;i<=daysOfMonth;i++){
                    Map map=new HashMap();
                    map.put("energy",0);
                    map.put("cid", cid);
                    map.put("vid", vid);
                    String date=mothStr+"-"+i;
                    if (i<10){
                        date=mothStr+"-"+"0"+i;
                    }
                    map.put("createDate",date);
                    treeMap.put(date,map);
                }

                for (Map itemMap:returnList){
                    treeMap.put(itemMap.get("createDate").toString(),itemMap);
                }
                List<Map> returnNewList=new ArrayList<>();
                for (String s :treeMap.keySet()){
                    returnNewList.add(treeMap.get(s));
                }
                return AjaxResult.success(returnNewList);
            }    if (searchType.equals("w")) {
                //w
                SimpleDateFormat sdf=new  SimpleDateFormat("yyyy-MM-dd");
                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(sdf.parse(searchDate));
                } catch (ParseException e) {
                    //throw new RuntimeException(e);
                    cal.setTime(new Date());
                    e.printStackTrace();

                }

                // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
                int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
                if (1 == dayWeek) {
                    cal.add(Calendar.DAY_OF_WEEK, -1);
                }

                cal.setFirstDayOfWeek(Calendar.SUNDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
                int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天

                cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
                Date weekBeginDate =cal.getTime();
                String weekBegin = sdf.format(weekBeginDate);
                cal.add(Calendar.DATE, 1);
                String monday = sdf.format(cal.getTime());
                cal.add(Calendar.DATE, 1);
                String tuesday = sdf.format(cal.getTime());
                cal.add(Calendar.DATE, 1);
                String wednesday = sdf.format(cal.getTime());
                cal.add(Calendar.DATE, 1);
                String thursday = sdf.format(cal.getTime());
                cal.add(Calendar.DATE, 1);
                String friday = sdf.format(cal.getTime());

                cal.add(Calendar.DATE, 1);
                String weekTailer = sdf.format(cal.getTime());



                Map paramMap = new HashMap();
                paramMap.put("cid", cid);
                paramMap.put("vid", vid);
                paramMap.put("startDate", weekBegin);
                paramMap.put("endDate", sdf.format(cal.getTime()));

                List<Map> cidDataDaysList = cidDataDayMapper.selectByDay(paramMap);
                List<Map> returnList=new ArrayList<>();
                for (Map itemMap:cidDataDaysList){
                    Map tmpMap =new HashMap();
                    String date =itemMap.get("date").toString();
                    tmpMap.put("createDate",date.substring(date.indexOf("-")+1,date.length()));
                    tmpMap.put("energy",itemMap.get("energy"));
                    tmpMap.put("cid", cid);
                    tmpMap.put("vid", vid);
                    returnList.add(tmpMap);
                }

                Map map=new HashMap();
                map.put("energy",0);
                map.put("cid",cid);
                map.put("vid",vid);

                TreeMap<String,Map> treeMap = new TreeMap();
                map.put("createDate",weekBegin.substring(weekBegin.indexOf("-")+1,weekBegin.length()));
                treeMap.put(map.get("createDate").toString(),new HashMap(map));

                map.put("createDate",monday.substring(monday.indexOf("-")+1,monday.length()));
                treeMap.put(map.get("createDate").toString(),new HashMap(map));

                map.put("createDate",tuesday.substring(tuesday.indexOf("-")+1,tuesday.length()));
                treeMap.put(map.get("createDate").toString(),new HashMap(map));

                map.put("createDate",wednesday.substring(wednesday.indexOf("-")+1,wednesday.length()));
                treeMap.put(map.get("createDate").toString(),new HashMap(map));

                map.put("createDate",thursday.substring(thursday.indexOf("-")+1,thursday.length()));
                treeMap.put(map.get("createDate").toString(),new HashMap(map));

                map.put("createDate",friday.substring(friday.indexOf("-")+1,friday.length()));
                treeMap.put(map.get("createDate").toString(),new HashMap(map));

                map.put("createDate",weekTailer.substring(weekTailer.indexOf("-")+1,weekTailer.length()));
                treeMap.put(map.get("createDate").toString(),new HashMap(map));

                for (Map itemMap:returnList){
                    treeMap.put(itemMap.get("createDate").toString(),itemMap);
                }

                List<Map> returnNewList=new ArrayList<>();
                for (String s :treeMap.keySet()){
                    returnNewList.add(treeMap.get(s));
                }
                return AjaxResult.success(returnNewList);
            }
        }

        return  AjaxResult.error("illegal access.");
    }


    @GetMapping("/selectCidOrVidDetail")
    public AjaxResult selectCidOrVidDetail(String cid, String vid, String loop) {

        AjaxResult ajaxResult = AjaxResult.success();
        CidRelation relation = new CidRelation();
        List<CidData> lastList = new ArrayList<>();
        if (cid != null && cid != "") {
            relation = relationService.getCidOrVidDetail(cid, null, null);
            CidData queryData = new CidData();
            queryData.setCid(cid);
            lastList = cidDataService.selectLastCidList(queryData);
            if (lastList != null && lastList.size() > 0) {
                CidData lastData = lastList.get(0);
                relation.setSoftVersion(lastData.getSoftVersion());
                relation.setSoftDeputyVersion(lastData.getSoftDeputyVersion());
            }
        } else {
            if (loop != null) {
                if (vid.substring(0, 1).equals("2")) {
                    loop = "1" + loop;
                } else {
                    loop = "2" + loop;
                }
            }
            relation = relationService.getCidOrVidDetail(null, vid, loop);
            CidData queryData = new CidData();
            queryData.setCid(cid);
            queryData.setVid(vid);
            if (loop != null) {
                queryData.setRoadType(loop);
            }
            lastList = cidDataService.selectLastCidList(queryData);
            if (lastList != null && lastList.size() > 0) {
                CidData lastData = lastList.get(0);
                relation.setSoftVersion(lastData.getSoftVersion());
                relation.setSoftDeputyVersion(lastData.getSoftDeputyVersion());
            }
        }
        ajaxResult.put("cidDetail", relation);
        return ajaxResult;
    }

    @GetMapping("/selectCidChildInfo")
    public AjaxResult selectCidChildInfo(String cid) {
        AjaxResult ajaxResult = AjaxResult.success();
        ajaxResult.put("vidList", relationService.getCidChildDetail(cid));
        return ajaxResult;
    }

    @GetMapping("/addPowerStation")
    public AjaxResult addPowerStation(String plantName, String capacity, String type, String timeZone) {
        LoginUser loginUser = getLoginUser();

        CidPowerstationinfo powerstationinfo = new CidPowerstationinfo();
        powerstationinfo.setEnergyName(plantName);
        powerstationinfo.setEnergyDeptId(loginUser.getDeptId());
        powerstationinfo.setEnergyCapacity(Long.parseLong(capacity));
        powerstationinfo.setEnergyType(type);
        powerstationinfo.setEnergyTimeZone(timeZone);
        powerstationinfo.setDelFlag("0");

        SysDept dept = deptService.selectDeptById(loginUser.getDeptId());
        powerstationinfo.setEnergyDeptName(dept.getDeptName());
        int i = powerstationinfoService.insertCidPowerstationinfo(powerstationinfo);

        AjaxResult ajaxResult = AjaxResult.success();
        if (i > 0) {
            CidPowerstationinfo cidPowerstationinfo = powerstationinfoService.selectCidPowerstationinfoByStationName(powerstationinfo.getEnergyName());
            ajaxResult.put("code", 200);
            ajaxResult.put("data", cidPowerstationinfo.getId());
        } else {
            ajaxResult.put("code", HttpStatus.ERROR);
            ajaxResult.put("msg", "Operate failed.");
        }

        return ajaxResult;
    }

    @GetMapping("/editPowerStation")
    public AjaxResult editPowerStation(Long id, String plantName, String capacity, String type, String timeZone) {
        LoginUser loginUser = getLoginUser();
        CidPowerstationinfo valPowerStation = powerstationinfoService.selectCidPowerstationinfoById(id);
        AjaxResult ajaxResult = AjaxResult.success();
        if (valPowerStation == null) {
            ajaxResult.put("code", 100);
        } else {
            valPowerStation.setEnergyName(plantName);
            valPowerStation.setEnergyType(type);
            valPowerStation.setEnergyCapacity(Long.parseLong(capacity));
            valPowerStation.setEnergyTimeZone(timeZone);
            powerstationinfoService.updateCidPowerstationinfo(valPowerStation);
            ajaxResult.put("code", 200);
        }
        return ajaxResult;
    }

    public TreeMap<String, String> threeHourPowerPlus(TreeMap<String, String> dataMap) {

        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        for (int i = 0; i < 24; i++) {
            if (i % 3 == 0) {
                if (i < 10) {
                    treeMap.put("0" + i + ":00", "0");
                } else {
                    treeMap.put(i + ":00", "0");
                }
            }
        }

        for (String ss : dataMap.keySet()) {
            int time = Integer.parseInt(ss.substring(0, 2));
            if (time >= 0 && time < 3) {
                float add = Float.parseFloat(treeMap.get("00:00")) + Float.parseFloat(dataMap.get(ss)) / 3;
                treeMap.put("00:00", String.valueOf(add));
            } else if (time >= 3 && time < 6) {
                float add = Float.parseFloat(treeMap.get("03:00")) + Float.parseFloat(dataMap.get(ss)) / 3;
                treeMap.put("03:00", String.valueOf(add));
            } else if (time >= 6 && time < 9) {
                float add = Float.parseFloat(treeMap.get("06:00")) + Float.parseFloat(dataMap.get(ss)) / 3;
                treeMap.put("06:00", String.valueOf(add));
            } else if (time >= 9 && time < 12) {
                float add = Float.parseFloat(treeMap.get("09:00")) + Float.parseFloat(dataMap.get(ss)) / 3;
                treeMap.put("09:00", String.valueOf(add));
            } else if (time >= 12 && time < 15) {
                float add = Float.parseFloat(treeMap.get("12:00")) + Float.parseFloat(dataMap.get(ss)) / 3;
                treeMap.put("12:00", String.valueOf(add));
            } else if (time >= 15 && time < 18) {
                float add = Float.parseFloat(treeMap.get("15:00")) + Float.parseFloat(dataMap.get(ss)) / 3;
                treeMap.put("15:00", String.valueOf(add));
            } else if (time >= 18 && time < 21) {
                float add = Float.parseFloat(treeMap.get("18:00")) + Float.parseFloat(dataMap.get(ss)) / 3;
                treeMap.put("18:00", String.valueOf(add));
            } else if (time >= 21 && time < 24) {
                float add = Float.parseFloat(treeMap.get("21:00")) + Float.parseFloat(dataMap.get(ss)) / 3;
                treeMap.put("21:00", String.valueOf(add));
            }
        }

        return treeMap;
    }

    public TreeMap<String, String> threeHourPowerPlusNotAdd(TreeMap<String, String> dataMap) {

        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        for (int i = 0; i < 24; i++) {
            if (i % 3 == 0) {
                if (i < 10) {
                    treeMap.put("0" + i + ":00", "0");
                } else {
                    treeMap.put(i + ":00", "0");
                }
            }
        }

        for (String ss : dataMap.keySet()) {
            int time = Integer.parseInt(ss.substring(0, 2));
            if (time >= 0 && time < 3) {
                float add = Float.parseFloat(dataMap.get(ss));
                treeMap.put("00:00", String.valueOf(add));
            } else if (time >= 3 && time < 6) {
                float add = Float.parseFloat(dataMap.get(ss));
                treeMap.put("03:00", String.valueOf(add));
            } else if (time >= 6 && time < 9) {
                float add = Float.parseFloat(dataMap.get(ss));
                treeMap.put("06:00", String.valueOf(add));
            } else if (time >= 9 && time < 12) {
                float add = Float.parseFloat(dataMap.get(ss));
                treeMap.put("09:00", String.valueOf(add));
            } else if (time >= 12 && time < 15) {
                float add = Float.parseFloat(dataMap.get(ss));
                treeMap.put("12:00", String.valueOf(add));
            } else if (time >= 15 && time < 18) {
                float add = Float.parseFloat(dataMap.get(ss));
                treeMap.put("15:00", String.valueOf(add));
            } else if (time >= 18 && time < 21) {
                float add = Float.parseFloat(dataMap.get(ss));
                treeMap.put("18:00", String.valueOf(add));
            } else if (time >= 21 && time < 24) {
                float add = Float.parseFloat(dataMap.get(ss));
                treeMap.put("21:00", String.valueOf(add));
            }
        }

        return treeMap;
    }

    /**
     * 根据日期获取 星期
     *
     * @param datetime
     * @return
     */
    public static String dateToWeek(String datetime) {

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        Date date;
        try {
            date = f.parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //一周的第几天
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    private static void getWeekByDate(Date time) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // 设置时间格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }

        cal.setFirstDayOfWeek(Calendar.SUNDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, 6);
}

    public static void main(String[] args) throws ParseException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        getWeekByDate(sdf.parse("2022-12-31"));

        float ff = 0.01f;
        String aa = String.format("%.3f", ff);
        System.out.println(aa);
//        String add = s + "_" + String.format("%.3f", ff);


    }

}
