package com.benyi.web.controller.system;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.benyi.common.core.controller.BaseController;
import com.benyi.common.core.domain.AjaxResult;
import com.benyi.common.core.domain.entity.SysDept;
import com.benyi.common.core.domain.entity.SysUser;
import com.benyi.common.core.domain.model.LoginUser;
import com.benyi.common.utils.SecurityUtils;
import com.benyi.energy.controller.CidDataController;
import com.benyi.energy.domain.*;
import com.benyi.energy.mapper.CidDataDayMapper;
import com.benyi.energy.mapper.CidDataHourMapper;
import com.benyi.energy.mapper.CidDataMonthMapper;
import com.benyi.energy.service.ICidDataService;
import com.benyi.energy.service.ICidLoginHeartService;
import com.benyi.energy.service.ICidPowerstationinfoService;
import com.benyi.energy.service.ICidRelationService;
import com.benyi.framework.web.domain.server.Sys;
import com.benyi.framework.web.service.TokenService;
import com.benyi.system.service.ISysDeptService;
import com.benyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.benyi.common.config.RuoYiConfig;
import com.benyi.common.utils.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 首页
 *
 * @author wuqiguang
 */
@RestController
public class SysIndexController extends BaseController {
    /**
     * 系统基础配置
     */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    CidDataHourMapper cidDataHourMapper;

    @Autowired
    CidDataDayMapper cidDataDayMapper;

    @Autowired
    CidDataMonthMapper cidDataMonthMapper;

    @Autowired
    private ICidPowerstationinfoService powerstationinfoService;

    @Autowired
    private ICidDataService cidDataService;


    @Autowired
    private ICidRelationService cidRelationService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ICidLoginHeartService heartService;


    /**
     * 访问首页，提示语
     */
    @RequestMapping("/")
    public String index() {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
    }

    @ResponseBody
    @RequestMapping(value = "index/getUserInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public String getUserInfo(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = userService.selectUserByUserName(loginUser.getUsername());
        long role = user.getRoles().get(0).getRoleId();
        return String.valueOf(role);
    }

    @ResponseBody
    @RequestMapping(value = "index/getDeptInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public String getDeptInfo(HttpServletRequest request) {

        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = userService.selectUserById(loginUser.getUserId());

        Long deptId = user.getDeptId();
        SysDept dept = deptService.selectDeptById(deptId);
        return JSON.toJSONString(dept);
    }

    @ResponseBody
    @RequestMapping(value = "index/getChildDeptInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public String getChildDeptInfo(HttpServletRequest request) {

        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = userService.selectUserById(loginUser.getUserId());

        Long deptId = user.getDeptId();
        long childCount = deptService.selectNormalChildrenDeptById(deptId);
        logger.info("===getChildDeptInfo:" + childCount);
        return String.valueOf(childCount);
    }

    @GetMapping("index/getCreateCidInfo")
    public AjaxResult getCreateCidInfo() {

        List<CidRelation> resultList = cidRelationService.selectCreateCidCount(getLoginUser().getDeptId());

        List<CidRelation> resultVidList = cidRelationService.selectCreateVidCount(getLoginUser().getDeptId());
        logger.info("===resultVidList:" + resultVidList.size());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(new Date());
        TreeMap<String, String> treeMap = new TreeMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 12; i++) {
            LocalDate localDate = today.minusMonths(i);
            String ss = localDate.toString().substring(0, 7).replace("-", "");
            treeMap.put(localDate.toString().substring(0, 7), "0");
        }
        TreeMap<String, String> cidMap = new TreeMap<>();
        //TreeMap<String,String> vidMap=new TreeMap<>();
        for (Object obj : resultList) {
            String s = JSON.toJSONString(obj);
            CidRelation relation = JSON.parseObject(s, CidRelation.class);
            String date = relation.getCidType();
            int add = 0;
            if (cidMap.get(date) != null) {
                int count = Integer.parseInt(cidMap.get(date).split("_")[0]);
                add = count + 1;
            } else {
                add = 1;
            }
            cidMap.put(date, String.valueOf(add) + "_0");
        }
        for (Object obj : resultVidList) {
            String s = JSON.toJSONString(obj);
            CidRelation relation = JSON.parseObject(s, CidRelation.class);
            String date = relation.getCidType();
            int add = 0;
            if (cidMap.get(date) != null) {
                int countCid = Integer.parseInt(cidMap.get(date).split("_")[0]);
                int countVid = Integer.parseInt(cidMap.get(date).split("_")[1]);
                add = countVid + 1;
                String addInput = countCid + "_" + add;
                cidMap.put(date, addInput);
            } else {
                cidMap.put(date, "0_1");
            }
        }
        for (String s : treeMap.keySet()) {
            if (cidMap.get(s) != null) {
                treeMap.put(s, cidMap.get(s));
            }
            System.out.println("date:" + s + ",cid_vid:" + cidMap.get(s));
        }
        List<CidRelation> retrunList = new ArrayList<>();
        for (String s : treeMap.keySet()) {
            CidRelation relation = new CidRelation();
            relation.setStatus(s);
            relation.setCidType(treeMap.get(s));
            retrunList.add(relation);
        }

        AjaxResult ajaxResult = AjaxResult.success(retrunList);
        return ajaxResult;
    }

    @GetMapping("index/valPowerStationCount")
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
     * 获取最近12个月，经常用于统计图表的X轴
     */
    public static void getLast12Months() {

        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // 月份最大为11 最小为0 所以设置初始月份时加1
        // 需要获取到当前月份所以设置初始月份时需要加2
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 2);
        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
            // 需要判断月份是否为0  如果0则需要转换成12
            dateList.add(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) == 0 ? 12 : calendar.get(Calendar.MONTH)));
        }
        for (String s : dateList) {
            System.out.println(s);
        }
    }

    @ResponseBody
    @RequestMapping(value = "index/getCreatePowerStationInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public AjaxResult getCreatePowerStationInfo(HttpServletRequest request) {

        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = userService.selectUserById(loginUser.getUserId());
        Long deptId = user.getDeptId();

        // CidPowerstationinfo id为创建数量
        List<CidPowerstationinfo> resultList = powerstationinfoService.selectCidPowerstationinfoListByGroupCreateTime(deptId);
        AjaxResult ajaxResult = AjaxResult.success(resultList);
        return ajaxResult;
    }

    @ResponseBody
    @RequestMapping(value = "index/getPsInfo", method = {RequestMethod.GET, RequestMethod.POST})
    public String getPowerInfo(HttpServletRequest request) {

        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = userService.selectUserById(loginUser.getUserId());

        Long deptId = user.getDeptId();
        CidPowerstationinfo query = new CidPowerstationinfo();
        //增加判断，如果dept=100 查询所有
        if (deptId != 100) {
            query.setEnergyDeptId(deptId);
        }
        List<CidPowerstationinfo> powerstationinfoList = powerstationinfoService.selectCidPowerstationinfoList(query);
        //装机容量
        Long energyCapacity = 0l;

        for (CidPowerstationinfo ps : powerstationinfoList) {
            energyCapacity += ps.getEnergyCapacity() == null ? 0 : ps.getEnergyCapacity();
        }
        return energyCapacity.toString();
    }

    @GetMapping("index/getCidEmuCount")
    public AjaxResult getCidEmuCount() {

        LoginUser loginUser = getLoginUser();
        int cidCount = cidRelationService.selectCidCount(loginUser.getDeptId()).size();
        int vidCount = cidRelationService.selectVidCount(loginUser.getDeptId()).size();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("cidCount", cidCount);
        ajax.put("vidCount", vidCount);

        System.out.println(ajax);
        //其他数据为null
        return ajax;
    }

    @ResponseBody
    @RequestMapping(value = "inedx/getCreatePowerStationCount", method = {RequestMethod.GET, RequestMethod.POST})
    public AjaxResult getCreatePowerstationCount(HttpServletRequest request) {

        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user = userService.selectUserById(loginUser.getUserId());

        Long deptId = user.getDeptId();
        CidPowerstationinfo query = new CidPowerstationinfo();
        //增加判断，如果dept=100 查询所有
        if (deptId != 100) {
            query.setEnergyDeptId(deptId);
        }
        List<CidPowerstationinfo> powerstationinfoList = powerstationinfoService.selectCidPowerstationinfoList(query);

        return AjaxResult.success(powerstationinfoList);
    }

    @GetMapping("index/getPowerDetail")
    public AjaxResult getPowerDetail() throws ParseException {

        Long deptId = getLoginUser().getDeptId();
        CidPowerstationinfo query = new CidPowerstationinfo();
        query.setEnergyDeptId(deptId);
        List<CidPowerstationinfo> powerstationinfoList = powerstationinfoService.selectCidPowerstationinfoList(query);

        float countEnergy = 0;
        float monthEnergy = 0;
        float currentPower = 0;
        float todayEnergy = 0;
        int buildCount = 0;
        int offline = 0;
        int online = 0;
        int inactive = 0;
        int error = 0;
        int cut = 0;


        for (CidPowerstationinfo power : powerstationinfoList) {

            if (power.getEnergyStatus().equals("0")) {
                online = online + 1;
            } else if (power.getEnergyStatus().equals("0") || power.getEnergyStatus().equals("1")) {
                error = error + 1;
            } else if (power.getEnergyStatus().equals("0") || power.getEnergyStatus().equals("1")) {
                offline = offline + 1;
            }


            countEnergy = countEnergy + Float.parseFloat(power.getEnergyTotal());
            todayEnergy = todayEnergy + Float.parseFloat(power.getEnergyTotalDay() != null ? power.getEnergyTotalDay() : "0");
            monthEnergy = monthEnergy + Float.parseFloat(power.getEnergyTotalMonth());
            currentPower = currentPower + Float.parseFloat(power.getEnergyCurrentPower());
        }

        AjaxResult ajax = AjaxResult.success();
        if (monthEnergy < todayEnergy)
            monthEnergy = todayEnergy;
        ajax.put("countEnergy", String.valueOf(countEnergy));
        ajax.put("todayEnergy", todayEnergy);
        ajax.put("monthEnergy", monthEnergy);
        ajax.put("currentPower", currentPower);
        ajax.put("buildCount", buildCount);
        ajax.put("offline", offline);
        ajax.put("online", online);
        ajax.put("inactive", inactive);
        ajax.put("error", error);
        ajax.put("cut", cut);
        System.out.println("===SysIndexController getPowerDetail  ajax:" + JSON.toJSONString(ajax));

        //其他数据为null
        return ajax;
    }

    @GetMapping("index/getEnergyByDate")
    public AjaxResult getEnergyByDate(String searchDate, String searchType) {

        LoginUser loginUser = getLoginUser();
        long deptId = loginUser.getDeptId();
        AjaxResult ajaxResult = null;

        TreeMap<String, String> returnMap = new TreeMap<>();
        CidPowerstationinfo queryPlant = new CidPowerstationinfo();
        queryPlant.setDelFlag("0");
        queryPlant.setEnergyDeptId(deptId);
        List<CidPowerstationinfo> plantList = powerstationinfoService.selectCidPowerstationinfoList(queryPlant);

        if (plantList.size() == 0)
            return AjaxResult.error("no plant");

        if (searchType.equals("d")) {//日

            Map paramMap = new HashMap();

            List<Long> plantIDList = new ArrayList<Long>();
            for (int i = 0; i < plantList.size(); i++) {
                plantIDList.add(plantList.get(i).getId());
            }

            paramMap.put("plantID", plantIDList);
            paramMap.put("startDate", searchDate + " 00:00:00");
            paramMap.put("endDate", searchDate + " 23:00:00");
            logger.info("===CidDataController getCidDataWithChart-day paramMap:" + JSON.toJSONString(paramMap));

            List<Map> cidDataMinList = cidDataHourMapper.selectByDay(paramMap);

            TreeMap<String, Map> heMap = new TreeMap<String, Map>();
            for (int i = 0; i < 24; i++) {

                Map map = new HashMap();
                map.put("energy", 0);
                if (i < 10) {
                    map.put("date", "0" + i);
                } else {
                    map.put("date", String.valueOf(i));
                    ;
                }
                heMap.put(map.get("date").toString(), new HashMap(map));
            }

            for (Map itemMap : cidDataMinList) {
                heMap.put(itemMap.get("date").toString(), itemMap);
            }

            List<Map> returnNewList = new ArrayList<>();
            for (String s : heMap.keySet()) {
                returnNewList.add(heMap.get(s));
            }

            ajaxResult = AjaxResult.success(returnNewList);

        } else if (searchType.equals("m")) {

            Map paramMap = new HashMap();
            List<Long> plantIDList = new ArrayList<Long>();
            for (int i = 0; i < plantList.size(); i++) {
                plantIDList.add(plantList.get(i).getId());
            }

            paramMap.put("plantID", plantIDList);
            paramMap.put("startDate", searchDate + "-00");
            paramMap.put("endDate", searchDate + "-31");

            List<Map> cidDataDayList = cidDataDayMapper.selectByDay(paramMap);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            TreeMap<String, Map> treeMap = new TreeMap();

            for (int i = 1; i <= daysOfMonth; i++) {
                Map map = new HashMap();
                map.put("energy", 0);
                String date = searchDate + "-" + i;
                if (i < 10) {
                    date = searchDate + "-" + "0" + i;
                }
                map.put("date", date);
                treeMap.put(date, map);
            }

            for (Map itemMap : cidDataDayList) {
                treeMap.put(itemMap.get("date").toString(), itemMap);
            }

            List<Map> returnNewList = new ArrayList<>();
            for (String s : treeMap.keySet()) {
                returnNewList.add(treeMap.get(s));
            }

            ajaxResult = AjaxResult.success(returnNewList);

        } else if (searchType.equals("y")) {

            Map paramMap = new HashMap();

            List<Long> plantIDList = new ArrayList<Long>();
            for (int i = 0; i < plantList.size(); i++) {
                plantIDList.add(plantList.get(i).getId());
            }


            paramMap.put("plantID", plantIDList);
            paramMap.put("startDate", searchDate + "-00");
            paramMap.put("endDate", searchDate + "-13");

            List<Map> cidDataDayList = cidDataMonthMapper.selectByYear(paramMap);


            TreeMap<String, Map> treeMap = new TreeMap();

            for (int i = 1; i <= 12; i++) {
                Map map = new HashMap();
                map.put("energy", 0);
                String date = searchDate + "-" + i;
                if (i < 10) {
                    date = searchDate + "-" + "0" + i;
                }
                map.put("date", date);
                treeMap.put(date, map);
            }

            for (Map itemMap : cidDataDayList) {
                treeMap.put(itemMap.get("date").toString(), itemMap);
            }

            List<Map> returnNewList = new ArrayList<>();
            for (String s : treeMap.keySet()) {
                returnNewList.add(treeMap.get(s));
            }

            ajaxResult = AjaxResult.success(returnNewList);
        }

        return ajaxResult;
    }


    @GetMapping("index/getDeviceStatus")
    public AjaxResult getDeviceStatus() throws ParseException {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        int online = 0;
        int offline = 0;
        int inactive = 0;
        int error = 0;
        List<CidRelation> relationList = new ArrayList<>();
        System.out.println("getDeviceStatus()========>start");
        for (Object obj : cidRelationService.getEquptStatuCount(user.getDeptId())) {
            String s = JSON.toJSONString(obj);
            CidRelation relation = JSON.parseObject(s, CidRelation.class);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if (!relation.getLastUpdate().equals("0")) {
                String beginTime = format.format(new Date());
                String endTime = relation.getLastUpdate();

                Date date1 = format.parse(beginTime);
                Date date2 = format.parse(endTime);

                long beginMillisecond = date1.getTime();
                long endMillisecond = date2.getTime();
                long jian = beginMillisecond - endMillisecond;
                if (beginTime.substring(0, 10).equals(endTime.substring(0, 10))) {
                    if (!(jian > 1000 * 60 * 30) && jian > 0) {

                        if (relation.getLastUpdate().equals("0")) {
                            offline++;
                        } else {
                            if (relation.getLastErrorCode().equals("0000")) {
                                System.out.println("cid:" + relation.getCid() + ",vid:" + relation.getVid() + ",loop:" + relation.getRoadType() +
                                        ",date:" + relation.getLastUpdate());
                                System.out.println("relation.getLastErrorCode():" + relation.getLastErrorCode() + ",jian:" + jian);
                                online++;
                            } else if (relation.getLastErrorCode().equals("0200")) {
                                online++;
                            } else {
                                online++;
                                error++;
                            }
                        }
                    } else {
                        if (relation.getLastErrorCode() != null) {
                            if (!relation.getLastErrorCode().equals("0000") && !relation.getLastErrorCode().equals("0200")) {
                                offline++;
                                error++;
                            }
                        } else {
                            offline++;
                        }
                    }
                } else {
                    if (relation.getLastErrorCode() != null) {
                        if (!relation.getLastErrorCode().equals("0000") && !relation.getLastErrorCode().equals("0200")) {
                            offline++;
                            error++;
                        }
                    } else {
                        offline++;
                    }
                }

            } else {
                offline++;
            }
        }
        System.out.println("getDeviceStatus()========>end");
        AjaxResult ajax = AjaxResult.success();
        //ajax.put("count",relationList.size());
        ajax.put("online", online);
        ajax.put("offline", offline);
        ajax.put("inactive", inactive);
        ajax.put("error", error);
        //其他数据为null
        return ajax;
    }

    @GetMapping("index/getIsConfirm")
    public AjaxResult getIsConfirmByDept() {
        LoginUser loginUser = getLoginUser();
        List<CidRelation> resultList = cidRelationService.getIsConfirmByDept(loginUser.getDeptId());

        AjaxResult ajax = AjaxResult.success();
        if (resultList.size() > 0) {

            ajax.put("isConfirm", "1");
            ajax.put("resultList", resultList);
        } else {
            ajax.put("isConfirm", "0");
        }
        return ajax;
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


    public static void main(String[] args) {
//        getLast12Months();
//        String[] aa= {"90999121","90999121","90999122"};
//        List<String> vidList=Arrays.stream(aa).distinct().collect(Collectors.toList());
//        for (String s: vidList) {
//            System.out.println(s);
//        }
//        String result="{'cidCount':'"+aa.length+"','vidCount':'"+vidList.size()+"'}";
//        System.out.println(result);
//        String aaa="3263.123";
//        System.out.println(Float.parseFloat(aaa));

//        String year="2022";
//        TreeMap<String,String> treeMap=new TreeMap<>();
//        for(int i =1 ;i<=12;i++){
//            if(i<10){
//                treeMap.put(year+"-0"+i,"0");
//            }else{
//                treeMap.put(year+"-"+i,"0");
//            }
//        }
//        for (String s:treeMap.keySet()){
//            System.out.println(s);
//        }

        TreeMap<String, String> treeMap = new TreeMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 12; i++) {
            LocalDate localDate = today.minusMonths(i);
            String ss = localDate.toString().substring(0, 7).replace("-", "");
            treeMap.put(localDate.toString().substring(0, 7), "0");
        }
        for (String s : treeMap.keySet()) {
            System.out.println("s:" + s);
        }


    }

}
