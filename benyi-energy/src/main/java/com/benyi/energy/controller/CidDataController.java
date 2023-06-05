package com.benyi.energy.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.benyi.common.core.domain.entity.SysUser;
import com.benyi.common.core.domain.model.LoginUser;
import com.benyi.common.utils.DateUtils;
import com.benyi.energy.domain.*;
import com.benyi.energy.mapper.*;
import com.benyi.energy.service.ICidPowerstationinfoService;
import com.benyi.energy.service.ICidRelationService;
import com.benyi.system.service.ISysUserService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import com.benyi.framework.web.service.TokenService;
import org.springframework.web.bind.annotation.*;
import com.benyi.common.annotation.Log;
import com.benyi.common.core.controller.BaseController;
import com.benyi.common.core.domain.AjaxResult;
import com.benyi.common.enums.BusinessType;
import com.benyi.energy.service.ICidDataService;
import com.benyi.common.utils.poi.ExcelUtil;
import com.benyi.common.core.page.TableDataInfo;

/**
 * 发电信息Controller
 *
 * @author wuqiguang
 * @date 2022-07-31
 */
@RestController
@RequestMapping("/energy/data")
public class CidDataController extends BaseController {

    @Autowired
    private ICidDataService cidDataService;

    @Autowired
    private ICidRelationService cidRelationService;


    @Autowired
    private CidDataMinMapper cidDataMinMapper;

    @Autowired
    private CidDataMonthMapper cidDataMonthMapper;

    @Autowired
    private CidDataYearMapper cidDataYearMapper;

    @Autowired
    private CidDataDayMapper cidDataDayMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ICidPowerstationinfoService powerstationinfoService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private CidEntityMapper cidEntityMapper;


    /**
     * 查询发电信息列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:data:cidlist')")
    @GetMapping("/cidList")
    public TableDataInfo cidList(CidData cidData,HttpServletRequest request) {
        //当前登陆用户 deptId过滤
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user=userService.selectUserById(loginUser.getUserId());
        Long deptId = user.getDeptId();
        String vid=request.getParameter("vid");
        String cid=request.getParameter("cid");
        String queryStationId=request.getParameter("queryStationId");
        String searchDate=request.getParameter("searchDate");
        startPage();
        List<CidData> resultList=cidDataService.getCidList(deptId,vid,cid,queryStationId,searchDate);
        return getDataTable(resultList);
    }

    /**
     * 查询发电信息列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:data:emulist')")
    @GetMapping("/emuList")
    public TableDataInfo emuList(CidData cidData,HttpServletRequest request) {


        long beginTime=System.currentTimeMillis();
        //当前登陆用户 deptId过滤
        LoginUser loginUser = tokenService.getLoginUser(request);
        String vid=request.getParameter("vid");
        String cid=request.getParameter("cid");
        String cidType=request.getParameter("cidType");
        String queryStationId=request.getParameter("queryStationId");
        String searchDate=request.getParameter("searchDate");
        String pageNumStr=request.getParameter("pageNum");
        String pageSizeStr=request.getParameter("pageSize");


        String mError = request.getParameter("mError");
        String mErrorCode=request.getParameter("m1MErrorCode");
        String sErrorCode=request.getParameter("m1SErrorCode");
        String sError=request.getParameter("s1MError");
        startPage();
        if (cidType==null)
            cidType="02";
        List<CidData> resultList=cidDataService.getEmuList(cidType,loginUser.getDeptId(),vid,cid,queryStationId
                ,mError,mErrorCode,sError,sErrorCode
                ,searchDate);

        logger.info("====CidDataController resultList :"+JSON.toJSONString(resultList));
        TableDataInfo tt= getDataTable(resultList);
        logger.info("====CidDataController emuList cost:"+(System.currentTimeMillis()-beginTime));
        return tt;
    }



    @GetMapping("/chartMap")
    public AjaxResult getCidDataWithChart(String createDate,String searchType,String startDate,String endDate,Long plantID) {

        logger.info("===CidDataController getCidDataWithChart plantID:"+JSON.toJSONString(plantID));

        // 获取当前的用户名称
        LoginUser loginUser = getLoginUser();
        Long deptId = loginUser.getDeptId();
        CidPowerstationinfo queryPowerstation = new CidPowerstationinfo();

        //增加判断，如果dept=100 查询所有
        queryPowerstation.setEnergyDeptId(loginUser.getDeptId());
        queryPowerstation.setId(plantID);
        List<CidPowerstationinfo> plantList =null;

        plantList=powerstationinfoService.selectCidPowerstationinfoList(queryPowerstation);
        if (plantList.size()==0){
            return AjaxResult.success("no plant");
        }

        String dateType = "";
        List<CidDayData> resultListDay = null;
        AjaxResult ajaxResult= null;
        if (null != searchType) {
            dateType = searchType;
        } else {
            dateType = "m";
        }
        String searchDate="";
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        if(createDate==null){
            searchDate=sdf.format(date);
        }else{
            searchDate=createDate;
        }

        if (dateType.equals("d")) {//日

            SimpleDateFormat sdfDay =new SimpleDateFormat("yyyy-MM-dd");
            List<CidData> returnList=new ArrayList<>();
            if(startDate==null) {
                startDate = sdfDay.format(date);
                endDate = sdfDay.format(date);
            }

            Map paramMap=new HashMap();
            List<Long> plantIDList=new ArrayList<Long>();
            for (int i=0;i<plantList.size();i++){
                plantIDList.add(plantList.get(i).getId());
            }

            paramMap.put("plantID",plantIDList);
            paramMap.put("startDate",startDate);
            paramMap.put("endDate",endDate);


            TreeMap<String,Map> treeMap=new TreeMap();
            for (int i=0;i<24;i++){
                String key=startDate+" ";
                if (i<10){
                    key=key+"0"+i;
                }else {
                    key=key+String.valueOf(i);
                }

                //00
                Map itemMap00 = new HashMap();
                itemMap00.put("date",key+":00");
                itemMap00.put("power",0);
                treeMap.put(key+":00",itemMap00);
                //05
                Map itemMap05 = new HashMap();
                itemMap05.put("power",0);
                itemMap05.put("date",key+":05");
                treeMap.put(key+":05",itemMap05);
                //10
                Map itemMap10 = new HashMap();
                itemMap10.put("power",0);
                itemMap10.put("date",key+":10");
                treeMap.put(key+":10",itemMap10);
                //15
                Map itemMap15 = new HashMap();
                itemMap15.put("power",0);
                itemMap15.put("date",key+":15");
                treeMap.put(key+":15",itemMap15);
                //20
                Map itemMap20 = new HashMap();
                itemMap20.put("power",0);
                itemMap20.put("date",key+":20");
                treeMap.put(key+":20",itemMap20);

                //25
                Map itemMap25 = new HashMap();
                itemMap25.put("power",0);
                itemMap25.put("date",key+":25");
                treeMap.put(key+":25",itemMap25);

                //30
                Map itemMap30 = new HashMap();
                itemMap30.put("power",0);
                itemMap30.put("date",key+":30");
                treeMap.put(key+":30",itemMap30);

                //35
                Map itemMap35 = new HashMap();
                itemMap35.put("power",0);
                itemMap35.put("date",key+":35");
                treeMap.put(key+":35",itemMap35);

                //40
                Map itemMap40 = new HashMap();
                itemMap40.put("power",0);
                itemMap40.put("date",key+":40");
                treeMap.put(key+":40",itemMap40);

                //45
                Map itemMap45 = new HashMap();
                itemMap45.put("power",0);
                itemMap45.put("date",key+":45");
                treeMap.put(key+":45",itemMap45);

                //50
                Map itemMap50 = new HashMap();
                itemMap50.put("power",0);
                itemMap50.put("date",key+":50");
                treeMap.put(key+":50",itemMap50);

                //55
                Map itemMap55 = new HashMap();
                itemMap55.put("power",0);
                itemMap55.put("date",key+":55");
                treeMap.put(key+":55",itemMap55);
            }

            List<Map> cidDataMinList = cidDataMinMapper.selectByDay(paramMap);
            for (Map map: cidDataMinList){
                logger.info("cidDataMinMapper:"+JSON.toJSONString(map));
                String dateTMP=map.get("date").toString();
                treeMap.put(dateTMP,map);
            }

            List<Map> resultList=new ArrayList<>();
            for (String s :treeMap.keySet()){
                resultList.add(treeMap.get(s));
            }
            ajaxResult=AjaxResult.success(resultList);

        } else if (dateType.equals("w")) {//周

            SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd");
            List plantIDList=new ArrayList();
            plantIDList.add(plantID);

            Map paramMap = new HashMap();
            paramMap.put("startDate",simpleDateFormat.format(DateUtils.getWeekStartTime()));
            paramMap.put("endDate",simpleDateFormat.format(DateUtils.getWeekEndTime()));
            paramMap.put("plantID",plantIDList);

            List<Map> cidDayDataList = cidDataDayMapper.selectByDay(paramMap);
            ajaxResult=AjaxResult.success(cidDayDataList);


        } else if (dateType.equals("m")) {//月

            resultListDay = cidDataService.getMonthPowerList(deptId, searchDate+"-01");
            List<String> list=new ArrayList<>();
            String year="";
            String month="";
            List<CidDayData> dataList=new ArrayList<>();
            for(Object obj:resultListDay){
                String s=JSON.toJSONString(obj);
                CidDayData d =JSON.parseObject(s,CidDayData.class);
                dataList.add(d);
                year=d.getDate().split("-")[0];
                month=d.getDate().split("-")[1];
                list.add(d.getDate().split("-")[2]);
            }
            if(year==""){
                year = searchDate.split("-")[0];
            }
            if(month==""){
                month = searchDate.split("-")[1];
            }
            List<Integer> returnList=reAddMonthList(year,month,list);
            Map<Integer,String> resultMap=new HashMap<>();
            List<CidDayData> returnResultList= new ArrayList<>();
            for(int i=0;i<returnList.size();i++){
                resultMap.put(returnList.get(i),"0");
            }
            for(int i=0;i<dataList.size();i++){
                resultMap.put(Integer.parseInt(dataList.get(i).getDate().split("-")[2]),dataList.get(i).getPower());
            }
            for (Integer key : resultMap.keySet()) {
                CidDayData d=new CidDayData();
                d.setDate(year+"-"+month+"-"+key);
                d.setPower(resultMap.get(key));
                returnResultList.add(d);
            }
            ajaxResult=AjaxResult.success(returnResultList);
        } else if (dateType.equals("y")) {//年
            resultListDay = cidDataService.getYearPowerList(deptId, searchDate+"-01-01");
            List<String> list=new ArrayList<>();
            String year="";
            List<CidDayData> dataList=new ArrayList<>();
            for(Object obj:resultListDay){
                String s=JSON.toJSONString(obj);
                CidDayData d =JSON.parseObject(s,CidDayData.class);
                dataList.add(d);
                year=d.getDate().split("-")[0];
                list.add(d.getDate().split("-")[1]);
            }
            if(year==""){
                year = searchDate;
            }
            List<Integer> returnList=reAddYearList(list);
            Map<Integer,String> resultMap=new HashMap<>();
            List<CidDayData> returnResultList= new ArrayList<>();
            for(int i=0;i<returnList.size();i++){
                resultMap.put(returnList.get(i),"0");
            }
            for(int i=0;i<dataList.size();i++){
                resultMap.put(Integer.parseInt(dataList.get(i).getDate().split("-")[1]),dataList.get(i).getPower());
            }
            for (Integer key : resultMap.keySet()) {
                CidDayData d=new CidDayData();
                if(key<10){
                    d.setDate(year+"-0"+key);
                }else{
                    d.setDate(year+"-"+key);
                }
                d.setPower(resultMap.get(key));
                returnResultList.add(d);
            }
            ajaxResult=AjaxResult.success(returnResultList);
        }else if (dateType.equals("a")) {//总
            resultListDay = cidDataService.getAllPowerList(deptId);
            ajaxResult=AjaxResult.success(resultListDay);
        }

        return ajaxResult;
    }


    @GetMapping("/getPlantEnergyPowerChart")
    public AjaxResult getCidEnergyByPowerStationId(String searchDate,Long powerStationId){
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        List<CidData> resultList=cidDataService.selectEnergyByDate(user.getDeptId(),searchDate);
        List<CidData> returnList= new ArrayList<>();

        for(Object obj:cidDataService.selectEnergyByDate(user.getDeptId(),searchDate)){
            String s=JSON.toJSONString(obj);
        }

        AjaxResult ajaxResult=AjaxResult.success(resultList);
        return ajaxResult;
    }


//    @PreAuthorize("@ss.hasPermi('energy:data:detail')")
    @GetMapping("/getCidEmuEchartsDetail")
    public AjaxResult getCidEmuEchartsDetail(String id,String searchDate,String roadType,String idType,String searchType){
        List<CidData> resultList=new ArrayList<CidData>();
        List<CidDayData> resultListDay=new ArrayList<CidDayData>();
        AjaxResult ajaxResult=null;
        if(idType==null|| idType.equals("cid")){
            //查询网关
            if(searchType==null||searchType.equals("d")){
                //日
                resultList=cidDataService.getCidDayDetail(id,searchDate);
                ajaxResult=AjaxResult.success(resultList);
            }else if(searchType.equals("w")){
                //周
                resultListDay=cidDataService.getCidWeekDetail(id,searchDate);
                ajaxResult=AjaxResult.success(resultListDay);
            }else if(searchType.equals("m")){
                //月
                resultListDay=cidDataService.getCidMonthDetail(id,searchDate);
                ajaxResult=AjaxResult.success(resultListDay);
            }else if(searchType.equals("y")){
                //年
                resultListDay=cidDataService.getCidYearDetail(id,searchDate);
                ajaxResult=AjaxResult.success(resultListDay);
            }else if(searchType.equals("a")){
                //总
                resultListDay=cidDataService.getCidAllDetail(id);
                ajaxResult=AjaxResult.success(resultListDay);
            }
        }else{
            //查询微逆
            //查询网关
            if(searchType==null||searchType.equals("d")){
                //日
                resultList=cidDataService.getVidsDayDetail(id,roadType,searchDate);
                ajaxResult=AjaxResult.success(resultList);
            }else if(searchType.equals("w")){
                //周
                resultListDay=cidDataService.getVidsWeekDetail(id,roadType,searchDate);
                ajaxResult=AjaxResult.success(resultListDay);
            }else if(searchType.equals("m")){
                //月
                resultListDay=cidDataService.getVidsMonthDetail(id,roadType,searchDate);
                ajaxResult=AjaxResult.success(resultListDay);
            }else if(searchType.equals("y")){
                //年
                resultListDay=cidDataService.getVidsYearDetail(id,roadType,searchDate);
                ajaxResult=AjaxResult.success(resultListDay);
            }else if(searchType.equals("a")){
                //总
                resultListDay=cidDataService.getVidsAllDetail(id,roadType);
                ajaxResult=AjaxResult.success(resultListDay);
            }
        }
        return ajaxResult;
    }

    @GetMapping("/misDetailByCidVidAndDate")
    public TableDataInfo selectDetailByCidVidAndDate(String plantID,String cid,String vid,String loop,String searchTableDateStart,String searchTableDateEnd,String searchType,Integer pageNum,Integer pageSize){


        if (vid!=null){
            String vidHead = vid.substring(0, 1);
            if (vidHead.toLowerCase().equals("c")) {
                vidHead = "4";
            }else if(vidHead.toLowerCase().equals("a")) {
                vidHead = "1";
            }else if(vidHead.toLowerCase().equals("b")) {
                vidHead = "2";
            }
            vid=vidHead+vid.substring( 1,vid.length());
        }

        if(searchType.equals("d")){

            PageHelper.startPage(pageNum,pageSize);
            List<CidData> resultList=cidDataService.selectDetailByCidVidAndDateForDay(plantID,cid,vid,loop,searchTableDateStart,searchTableDateEnd,searchType);

            return getDataTable(resultList);
        }else{
            PageHelper.startPage(pageNum,pageSize);
            List<CidDayData> resultList=cidDataService.selectDetailByCidVidAndDate(cid,vid,loop,searchTableDateStart,searchTableDateEnd,searchType);

            return getDataTable(resultList);
        }
    }

    /**
     * cid，vid detail页面 energy 图表数据
     * @param cid
     * @param vid
     * @param roadType
     * @param searchDate
     * @param searchType
     * @return
     */
    @GetMapping("/selectCidVidEnergyDetailByDate")
    public AjaxResult selectCidVidEnergyDetailByDate(String plantID, String cid,String vid,String roadType,String searchDate,String searchType){


        if (vid!=null){
            String vidHead = vid.substring(0, 1);
            if (vidHead.toLowerCase().equals("c")) {
                vidHead = "4";
            }else if(vidHead.toLowerCase().equals("a")) {
                vidHead = "1";
            }else if(vidHead.toLowerCase().equals("b")) {
                vidHead = "2";
            }
            vid=vidHead+ vid.substring( 1,vid.length());
        }

        if(searchType.equals("d")){
            TreeMap<String,String> firstDataMap=new TreeMap<>();
            TreeMap<String,String> lastDataMap=new TreeMap<>();

            List<CidData> resultList=cidDataService.getStationEnergyDayByCidVidAndDate(plantID,cid,vid,roadType,searchDate);
//            List<CidData> resultList =new ArrayList<>();
//            List<MIDataMdb> miDataMdbList = miDataMdbService.selectMIDataFromMdb();
            TreeMap<String,String> dataMap=new TreeMap<>();
            for(Object obj:resultList){
                String s=JSON.toJSONString(obj);
                CidData d =JSON.parseObject(s,CidData.class);
                dataMap.put(d.getDate(),d.getEnergy());
                //System.out.println(d.getDate());
            }
            TreeMap<String,String> rmapFirst=CidDataController.subTreeMapEnergyFirst(dataMap);
            TreeMap<String,String> rmapLast=CidDataController.subTreeMapEnergyLast(dataMap);
            for(String first:rmapFirst.keySet()){
                if(firstDataMap.get(first)==null){
                    firstDataMap.put(first,rmapFirst.get(first));
                }else{
                    Float firstEnergy = Float.parseFloat(firstDataMap.get(first))+Float.parseFloat(rmapFirst.get(first));
                    firstDataMap.put(first,String.valueOf(firstEnergy));
                }
            }

            for(String last:rmapLast.keySet()){
                if(lastDataMap.get(last)==null){
                    lastDataMap.put(last,rmapLast.get(last));
                }else{
                    //lastDataMap.put(last,rmapLast.get(last));
                    Float lastEnergy = Float.parseFloat(lastDataMap.get(last))+Float.parseFloat(rmapLast.get(last));
                    lastDataMap.put(last,String.valueOf(lastEnergy));
                }
            }

            List<CidData> returnList=new ArrayList<>();
            for(String s:firstDataMap.keySet()){
//                System.out.println("time:"+s);
                //System.out.println("last:"+Float.parseFloat(lastDataMap.get(s)));
                //System.out.println("first:"+Float.parseFloat(firstDataMap.get(s)));
                CidData data =new CidData();
                data.setDate(s);
                Float ff= Float.parseFloat(lastDataMap.get(s))-Float.parseFloat(firstDataMap.get(s));
                data.setEnergy(String.valueOf(ff));//"%.3f",
                returnList.add(data);
            }
            return AjaxResult.success(returnList);
        }else if (searchType.equals("m")){

            String searchStartDate,searchEndDate;
            if (searchDate.length() <= 7) {
                searchStartDate = searchDate + "-01";
                searchEndDate = searchDate + "-31";
            } else {
                searchStartDate = searchDate.substring(0, searchDate.indexOf("-") + 3) + "-01";
                searchEndDate = searchDate.substring(0, searchDate.indexOf("-") + 3) + "-31";
            }

//            List<CidDayData> resultList= dayDataService.selectCidVidEnergyByDateAndType(cid,vid,roadType,searchDate,searchType);
                    //cidDataService.getStationPowerByPidAndDate(id,null,searchDate,searchDateType);



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

            String mothStr= searchDate.substring(searchDate.indexOf("-")+1,searchDate.length());
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
                map.put("date",date);
                treeMap.put(date,map);
            }

            for (Map itemMap:returnList){
                itemMap.put("date",itemMap.get("createDate").toString());
                itemMap.remove("createDate");
                treeMap.put(itemMap.get("date").toString(),itemMap);
            }
            List<Map> returnNewList=new ArrayList<>();
            for (String s :treeMap.keySet()){
                returnNewList.add(treeMap.get(s));
            }
            return AjaxResult.success(returnNewList);


        }else if(searchType.equals("y")){


            Map paramMap = new HashMap();
            paramMap.put("startDate",searchDate+"-01");
            paramMap.put("endDate",searchDate+"-12");
            paramMap.put("cid", cid);
            paramMap.put("vid", vid);

            List<Map> cidDataMonthList= cidDataMonthMapper.selectByYear(paramMap);
            TreeMap<String,Map> treeMap=new TreeMap();

            for (int i=1;i<=12;i++){
                Map map=new HashMap();
                map.put("energy",0);
                map.put("cid", cid);
                map.put("vid", vid);
                String date=searchDate+"-"+i;
                if (i<10){
                    date=searchDate+"-"+"0"+i;
                }
                map.put("date",date);
                treeMap.put(date,map);
            }

            for (Map itemMap:cidDataMonthList){
                treeMap.put(itemMap.get("date").toString(),itemMap);
            }

            List<Map> returnNewList=new ArrayList<>();
            for (String s :treeMap.keySet()){
                returnNewList.add(treeMap.get(s));
            }
            return AjaxResult.success(returnNewList);

        }else if(searchType.equals("a")){

            Map paramMap = new HashMap();
            paramMap.put("cid", cid);
            paramMap.put("vid", vid);

            List<Map> returnList = cidDataYearMapper.sumEnergyByCidAndVid(paramMap);
            return AjaxResult.success(returnList);
        }

        return null;
    }

    @GetMapping("/selectMisDetailByDateType")
    public AjaxResult selectMisDetailByDateType(Long plantID,String cid,String vid,String loop,String searchDateStart
                ,String searchDateEnd,String searchType,String dataType){

        logger.info("plantID:"+plantID);
        logger.info("loop:"+loop);

        if (plantID==null){
            Map map =new HashMap();
            map.put("cid",cid);
            List<Map> cidEntityList = cidEntityMapper.selectCidList(map);
            if (cidEntityList.size()>0){
                plantID=Long.parseLong(cidEntityList.get(0).get("powerStationId").toString());
            }
        }

        AjaxResult ajaxResult=null;
        if (vid!=null){
            String vidHead = vid.substring(0, 1);
            if (vidHead.toLowerCase().equals("c")) {
                vidHead = "4";
            }else if(vidHead.toLowerCase().equals("a")) {
                vidHead = "1";
            }else if(vidHead.toLowerCase().equals("b")) {
                vidHead = "2";
            }
            vid=vidHead+vid.substring( 1,vid.length());
        }

        if(searchType.equals("d")){

            SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date();
            SimpleDateFormat sdfDay =new SimpleDateFormat("yyyy-MM-dd");

            if(searchDateStart==null) {
                searchDateStart = sdfDay.format(date);
                searchDateEnd = sdfDay.format(date);
            }
            String startMonth=searchDateStart.substring(5,7);
            String endMonth=searchDateEnd.substring(5,7);
            List<String> searchDayList=new ArrayList<>();
            if(startMonth.equals(endMonth)){
                String startDay=searchDateStart.substring(searchDateStart.length()-2,searchDateStart.length());
                String endDay=searchDateEnd.substring(searchDateEnd.length()-2,searchDateEnd.length());
                int dayZone=Integer.parseInt(endDay)-Integer.parseInt(startDay);
                String searchDateZone=searchDateStart.substring(0,searchDateStart.length()-2);

                //System.out.println("searchDateZone:"+searchDateZone);
                for(int i=0;i<=dayZone;i++){
                    searchDateZone=searchDateStart.substring(0,searchDateStart.length()-2);
                    String addDate="";
                    if(Integer.parseInt(startDay)+i<10){
                        addDate="0"+(Integer.parseInt(startDay)+i);
                    }else{
                        addDate=String.valueOf((Integer.parseInt(startDay)+i));
                    }
                    searchDateZone=searchDateZone+addDate;
                    searchDayList.add(searchDateZone);
                }
//            }
            }else{
                String startDay=searchDateStart.substring(searchDateStart.length()-2,searchDateStart.length());
                String endDay=searchDateEnd.substring(searchDateEnd.length()-2,searchDateEnd.length());
                String year=searchDateStart.substring(0,4);
                int startMontDay=getDays(Integer.parseInt(year),Integer.parseInt(startMonth));
                String searchDateZone=searchDateStart.substring(0,searchDateStart.length()-2);
                String searchDateEndZone=searchDateEnd.substring(0,searchDateEnd.length()-2);
                int startDay_1=Integer.parseInt(startDay);
                for(int i=0;i<=(startMontDay-startDay_1);i++){
                    System.out.println(year+"-"+startMonth+"-"+(startDay_1+i));
                    searchDateZone=searchDateStart.substring(0,searchDateStart.length()-2);
                    String addDate="";
                    if(Integer.parseInt(startDay)+i<10){
                        addDate="0"+(Integer.parseInt(startDay)+i);
                    }else{
                        addDate=String.valueOf((Integer.parseInt(startDay)+i));
                    }
                    searchDateZone=searchDateZone+addDate;
                    searchDayList.add(searchDateZone);
                }
                for(int j=1;j<=Integer.parseInt(endDay);j++){

                    if(j<10){
                        searchDateEndZone=year+"-"+endMonth+"-0"+(j);
                        System.out.println(year+"-"+endMonth+"-0"+(j));
                    }else{
                        searchDateEndZone=year+"-"+endMonth+"-"+(j);
                        System.out.println(year+"-"+endMonth+"-"+(j));
                    }
                    searchDayList.add(searchDateEndZone);
                }
            }
            List<CidData> returnList = new ArrayList<>();
            for(String dateZone:searchDayList){

                List<Map> resultList=null;
                resultList=cidDataService.selectMisDetailByDateTypeForDayNewForMinute(plantID,cid,vid,loop,dateZone);

                TreeMap<String,String> dataMapPower=new TreeMap<>();
                TreeMap<String,String> dataMapVolt=new TreeMap<>();
                TreeMap<String,String> dataMapTemp=new TreeMap<>();
                TreeMap<String,String> dataMapFreq=new TreeMap<>();
                TreeMap<String,String> dataMapGridVolt=new TreeMap<>();
                for(Object obj:resultList){

                    String s=JSON.toJSONString(obj);
                    CidData d =JSON.parseObject(s,CidData.class);
                    String dataPower="";
                    String dataVolt="";
                    String dataTemp="";
                    String dataFreq="";
                    String dataGridVolt="";
                    dataPower = d.getPower();
                    dataVolt = d.getVolt();
                    dataTemp = d.getTemp();
                    dataFreq = d.getGridFreq();
                    dataGridVolt = d.getGridVolt();
                    String fmtDate=sdf.format(d.getCreateDate());
                    dataMapPower.put(fmtDate,dataPower);
                    dataMapVolt.put(fmtDate,dataVolt);
                    dataMapTemp.put(fmtDate,dataTemp);
                    dataMapFreq.put(fmtDate,dataFreq);
                    dataMapGridVolt.put(fmtDate,dataGridVolt);
                }

                TreeMap<String,String> rmapPower= CidDataController.subTreeMapNotAdd(dataMapPower,dateZone);
                TreeMap<String,String> rmapVolt= CidDataController.subTreeMapNotAdd(dataMapVolt,dateZone);
                TreeMap<String,String> rmapTemp= CidDataController.subTreeMapNotAdd(dataMapTemp,dateZone);
                TreeMap<String,String> rmapFreq= CidDataController.subTreeMapNotAdd(dataMapFreq,dateZone);
                TreeMap<String,String> rmapGridVolt= CidDataController.subTreeMapNotAdd(dataMapGridVolt,dateZone);
                for(String s:rmapPower.keySet()){
                    CidData data =new CidData();
                    data.setDate(s);
                    data.setPower(rmapPower.get(s));
                    data.setVolt(rmapVolt.get(s));
                    data.setTemp(rmapTemp.get(s));
                    data.setGridFreq(rmapFreq.get(s));
                    data.setGridVolt(rmapGridVolt.get(s));
                    returnList.add(data);
                }
            }
            ajaxResult=AjaxResult.success(returnList);
        }else if(searchType.equals("m")){
            List<CidDayData> resultList=cidDataService.selectMisDetailByDateType(cid,vid,loop,searchDateStart,searchDateEnd,searchType);
            if(resultList.size()>0){
                List<String> list=new ArrayList<>();
                String year="";
                String month="";
                List<CidDayData> dataList=new ArrayList<>();
                for(Object obj:resultList){
                    String s=JSON.toJSONString(obj);
                    CidDayData d =JSON.parseObject(s,CidDayData.class);
                    dataList.add(d);
                    year=d.getDate().split("-")[0];
                    month=d.getDate().split("-")[1];
                    list.add(d.getDate().split("-")[2]);
                }
                List<Integer> returnList=reAddMonthList(year,month,list);
                Map<Integer,String> resultMap=new HashMap<>();
                List<CidDayData> returnResultList= new ArrayList<>();
                for(int i=0;i<returnList.size();i++){
                    resultMap.put(returnList.get(i),"0");
                }
                for(int i=0;i<dataList.size();i++){
                    String data="";
                    //System.out.println("dataType:"+dataType);
                    if(dataType.equals("power")){
                        data = dataList.get(i).getPower();
                    }else if(dataType.equals("energy")){
                        data = dataList.get(i).getEnergy();
                    }else if(dataType.equals("volt")){
                        data = dataList.get(i).getVolt();
                    }else if(dataType.equals("temp")){
                        data = dataList.get(i).getTemp();
                    }else if(dataType.equals("gridFreq")){
                        data = dataList.get(i).getGridFreq();
                    }else if(dataType.equals("gridVolt")){
                        data = dataList.get(i).getGridVolt();
                    }
                    resultMap.put(Integer.parseInt(dataList.get(i).getDate().split("-")[2]),data);
                }
                for (Integer key : resultMap.keySet()) {
                    CidDayData d=new CidDayData();
                    if(key<10){
                        d.setDate(year+"-"+month+"-0"+key);
                    }else{
                        d.setDate(year+"-"+month+"-"+key);
                    }
                    if(dataType.equals("power")){
                        d.setPower(resultMap.get(key));
                    }else if(dataType.equals("energy")){
                        d.setEnergy(resultMap.get(key));
                    }else if(dataType.equals("volt")){
                        d.setVolt(resultMap.get(key));
                    }else if(dataType.equals("temp")){
                        d.setTemp(resultMap.get(key));
                    }else if(dataType.equals("gridFreq")){
                        d.setGridFreq(resultMap.get(key));
                    }else if(dataType.equals("gridVolt")){
                        d.setGridVolt(resultMap.get(key));
                    }
                    returnResultList.add(d);
                }
                ajaxResult=AjaxResult.success(returnResultList);
            }else{
                String year=searchDateStart.split("-")[0];
                String month=searchDateStart.split("-")[1];
                List<Integer> returnList=reAddMonthList(year,month,null);
                Map<Integer,String> resultMap=new HashMap<>();
                List<CidDayData> returnResultList= new ArrayList<>();
                for(int i=0;i<returnList.size();i++){
                    resultMap.put(returnList.get(i),"0");
                }
                for (Integer key : resultMap.keySet()) {
                    CidDayData d=new CidDayData();
                    if(key<10){
                        d.setDate(year+"-"+month+"-0"+key);
                    }else{
                        d.setDate(year+"-"+month+"-"+key);
                    }
                    if(dataType.equals("power")){
                        d.setPower(resultMap.get(key));
                    }else if(dataType.equals("energy")){
                        d.setEnergy(resultMap.get(key));
                    }else if(dataType.equals("volt")){
                        d.setVolt(resultMap.get(key));
                    }else if(dataType.equals("temp")){
                        d.setTemp(resultMap.get(key));
                    }else if(dataType.equals("gridFreq")){
                        d.setGridFreq(resultMap.get(key));
                    }else if(dataType.equals("gridVolt")){
                        d.setGridVolt(resultMap.get(key));
                    }
                    returnResultList.add(d);
                }
                ajaxResult=AjaxResult.success(returnResultList);
            }
        }else if(searchType.equals("y")){
            System.out.println("searchDateStart:"+searchDateStart);
            List<CidDayData> resultList=cidDataService.selectMisDetailByDateType(cid,vid,loop,searchDateStart,searchDateEnd,searchType);
            if(resultList.size()>0){
                List<String> list=new ArrayList<>();
                String year="";
                List<CidDayData> dataList=new ArrayList<>();
                for(Object obj:resultList){
                    String s=JSON.toJSONString(obj);
                    CidDayData d =JSON.parseObject(s,CidDayData.class);
                    dataList.add(d);
                    year=d.getDate().split("-")[0];
                    list.add(d.getDate().split("-")[1]);
                }
                List<Integer> returnList=reAddYearList(list);
                Map<Integer,String> resultMap=new HashMap<>();
                List<CidDayData> returnResultList= new ArrayList<>();
                for(int i=0;i<returnList.size();i++){
                    resultMap.put(returnList.get(i),"0");
                }
                for(int i=0;i<dataList.size();i++){
                    String data="";
                    if(dataType.equals("power")){
                        data = dataList.get(i).getPower();
                    }else if(dataType.equals("energy")){
                        data = dataList.get(i).getEnergy();
                    }else if(dataType.equals("volt")){
                        data = dataList.get(i).getVolt();
                    }else if(dataType.equals("temp")){
                        data = dataList.get(i).getTemp();
                    }else if(dataType.equals("gridFreq")){
                        data = dataList.get(i).getGridFreq();
                    }else if(dataType.equals("gridVolt")){
                        data = dataList.get(i).getGridVolt();
                    }
                    resultMap.put(Integer.parseInt(dataList.get(i).getDate().split("-")[1]),data);
                }
                for (Integer key : resultMap.keySet()) {
                    CidDayData d=new CidDayData();
                    if(key<10){
                        d.setDate(year+"-0"+key);
                    }else{
                        d.setDate(year+"-"+key);
                    }
                    if(dataType.equals("power")){
                        d.setPower(resultMap.get(key));
                    }else if(dataType.equals("energy")){
                        d.setEnergy(resultMap.get(key));
                    }else if(dataType.equals("volt")){
                        d.setVolt(resultMap.get(key));
                    }else if(dataType.equals("temp")){
                        d.setTemp(resultMap.get(key));
                    }else if(dataType.equals("gridFreq")){
                        d.setGridFreq(resultMap.get(key));
                    }else if(dataType.equals("gridVolt")){
                        d.setGridVolt(resultMap.get(key));
                    }
                    returnResultList.add(d);
                }
                ajaxResult = AjaxResult.success(returnResultList);
            }else{
                List<String> list=new ArrayList<>();
                String year=searchDateStart.split("-")[0];
                List<Integer> returnList=reAddYearList(list);
                Map<Integer,String> resultMap=new HashMap<>();
                List<CidDayData> returnResultList= new ArrayList<>();
                for(int i=0;i<returnList.size();i++){
                    resultMap.put(returnList.get(i),"0");
                }
                for (Integer key : resultMap.keySet()) {
                    CidDayData d=new CidDayData();
                    if(key<10){
                        d.setDate(year+"-0"+key);
                    }else{
                        d.setDate(year+"-"+key);
                    }
                    if(dataType.equals("power")){
                        d.setPower(resultMap.get(key));
                    }else if(dataType.equals("energy")){
                        d.setEnergy(resultMap.get(key));
                    }else if(dataType.equals("volt")){
                        d.setVolt(resultMap.get(key));
                    }else if(dataType.equals("temp")){
                        d.setTemp(resultMap.get(key));
                    }else if(dataType.equals("gridFreq")){
                        d.setGridFreq(resultMap.get(key));
                    }else if(dataType.equals("gridVolt")){
                        d.setGridVolt(resultMap.get(key));
                    }
                    returnResultList.add(d);
                }
                ajaxResult = AjaxResult.success(returnResultList);
            }
        }

        return ajaxResult;
    }


    @GetMapping("/misDetailLoop")
    public AjaxResult selectMisDetailLoop(String cid,String vid){
        List<String> resultList=cidDataService.selectMisDetailLoop(cid,vid);
        AjaxResult ajaxResult=AjaxResult.success(resultList);
        return ajaxResult;
    }

    /**
     * 导出发电信息列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:data:export')")
    @Log(title = "发电信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CidData cidData) {
        List<CidData> list = cidDataService.selectCidDataList(cidData);
        ExcelUtil<CidData> util = new ExcelUtil<CidData>(CidData.class);
        util.exportExcel(response, list, "发电信息数据");
    }

    /**
     * 获取发电信息详细信息
     */
//    @PreAuthorize("@ss.hasPermi('energy:data:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(cidDataService.selectCidDataById(id));
    }

    /**
     * 新增发电信息
     */
//    @PreAuthorize("@ss.hasPermi('energy:data:add')")
    @Log(title = "发电信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CidData cidData) {
        return toAjax(cidDataService.insertCidData(cidData));
    }

    /**
     * 修改发电信息
     */
//    @PreAuthorize("@ss.hasPermi('energy:data:edit')")
    @Log(title = "发电信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CidData cidData) {
        return toAjax(cidDataService.updateCidData(cidData));
    }

    /**
     * 删除发电信息
     */
//    @PreAuthorize("@ss.hasPermi('energy:data:remove')")
    @Log(title = "发电信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(cidDataService.deleteCidDataByIds(ids));
    }


    @GetMapping("/getPlantCurrentPower")
    public AjaxResult getPlantCurrentPowerByIds(Long powerStationId){
        List<Long> idsList=new ArrayList<Long>();
        List<Long> idsMinList=new ArrayList<Long>();
        float currentPower=0f;
        float sumMaxEnergy=0f;
        float sumMinEnergy=0f;
        for(Object obj:cidDataService.selectPlantCurrentPowerId(powerStationId)){
            String s =JSON.toJSONString(obj);
            CidData cidData=JSON.parseObject(s,CidData.class);
            idsList.add(cidData.getId());
        }

        for(Object obj:cidDataService.selectPlantMinId(powerStationId)){
            String s =JSON.toJSONString(obj);
            CidData cidData=JSON.parseObject(s,CidData.class);
            idsMinList.add(cidData.getId());
        }

        Long[] ids=new Long[idsList.size()];
        for(int i=0;i<idsList.size();i++){
            ids[i]=idsList.get(i);
        }
        Long[] idsMin=new Long[idsMinList.size()];
        for(int i=0;i<idsMinList.size();i++){
            idsMin[i]=idsMinList.get(i);
        }

        CidPowerstationinfo plant = new CidPowerstationinfo();
        if(ids.length>0){
            for(Object obj:cidDataService.selectPlantCurrentPowerByIds(ids)){
                String s =JSON.toJSONString(obj);
                CidData cidData=JSON.parseObject(s,CidData.class);
                currentPower+=Float.parseFloat(cidData.getPower());
                sumMaxEnergy+=Float.parseFloat(cidData.getEnergy());
            }
            plant.setEnergyStatus("0");
        }else{
            plant.setEnergyStatus("3");
        }
        if(idsMin.length>0){
            for(Object obj:cidDataService.selectPlantCurrentPowerByIds(idsMin)){
                String s =JSON.toJSONString(obj);
                CidData cidData=JSON.parseObject(s,CidData.class);
                sumMinEnergy+=Float.parseFloat(cidData.getEnergy());
            }
        }

        plant.setId(powerStationId);
        plant.setEnergyTotalDay(String.valueOf(sumMaxEnergy-sumMinEnergy));
        plant.setEnergyCurrentPower(String.valueOf(currentPower));

        AjaxResult ajaxResult=AjaxResult.success();
        ajaxResult.put("currentPower",currentPower);
        ajaxResult.put("todayEnergy",String.valueOf(sumMaxEnergy-sumMinEnergy));
        return ajaxResult;
    }


    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String searchDate="2022-09-28 17:22:22";
        String beginTime = format.format(new Date());

        Date date1 = format.parse(beginTime);
        Date date2 = format.parse(searchDate);
        System.out.println(date1.after(date2));

        String searchDateStart="2022-09-28";
        String searchDateEnd="2022-10-02";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-8);
        String dddd = sdf.format(calendar.getTime());
        System.out.println(dddd);

//        String day_1=searchDateStart.substring(searchDateStart.length()-2,searchDateStart.length());
//        String day_2=searchDateEnd.substring(searchDateEnd.length()-2,searchDateEnd.length());
//        String startMonth=searchDateStart.substring(5,7);
//        String endMonth=searchDateEnd.substring(5,7);
//
//        if(startMonth.equals(endMonth)){
//            int dateZone=Integer.parseInt(day_2)-Integer.parseInt(day_1);
//            for(int i=1;i<=dateZone;i++){
//                String addDay=String.valueOf((Integer.parseInt(day_1)+i));
//                if(addDay.length()<2){
//                    addDay="0"+addDay;
//                }
//                String searchDate=searchDateStart.substring(0,searchDateStart.length()-2)+addDay;
//                System.out.println(searchDate);
//            }
//        }else{
//            String year=searchDateStart.substring(0,4);
//            int startMontDay=getDays(Integer.parseInt(year),Integer.parseInt(startMonth));
//            int startDay_1=Integer.parseInt(day_1);
//            for(int i=0;i<=(startMontDay-startDay_1);i++){
//                System.out.println(year+"-"+startMonth+"-"+(startDay_1+i));
//            }
//            for(int j=1;j<=Integer.parseInt(day_2);j++){
//                if(j<10){
//                    System.out.println(year+"-"+endMonth+"-0"+(j));
//                }else{
//                    System.out.println(year+"-"+endMonth+"-"+(j));
//                }
//
//            }
//            System.out.println(startMontDay-startDay_1);
//            System.out.println(startMontDay);
//        }
//        TreeMap<String,String> treeMap = new TreeMap<>();
//        for(int i =0;i<20;i++){
//            treeMap.put("i"+i,"r"+Math.random());
//        }
//        for(String s:treeMap.keySet()){
//            System.out.println(s+"_"+treeMap.get(s));
//        }
//        System.out.println("firstKey:"+treeMap.firstKey()+"_"+treeMap.firstEntry());
//        System.out.println("firstKey:"+treeMap.lastKey()+"_"+treeMap.lastEntry());
        float aa= 0.001236f;
        System.out.println(String.format("%.3f", aa));

        //System.out.println(searchDateEnd.substring(searchDateEnd.length()-2, searchDateEnd.length()));

//        String time="2022-09-20 10:23";
//        System.out.println(time.substring(time.length()-2,time.length()));
//        System.out.println(time.substring(time.length()-5,time.length()-3));
//
//
//        SimpleDateFormat sdf =new SimpleDateFormat("HH:mm");
//        Date dateNow = new Date();
//        String stringDate = sdf.format(dateNow);
//
//        String hour = time.substring(5,7);
//        String minute = time.substring(5,7);
//        System.out.println(hour.equals(minute));

    }

    public static List<Integer> reAddMonthList(String year,String month,List<String> list){

        List<Integer> sortList=new ArrayList<>();
        int currentMonthDayCounts=getDays(Integer.parseInt(year),Integer.parseInt(month));
        if(list!=null){
            for(int i=1;i<=currentMonthDayCounts;i++){
                if(list.contains(String.valueOf(i))){
                    continue;
                }else{
                    list.add(String.valueOf(i));
                }
            }
        }else{
            list = new ArrayList<>();
            for(int i=1;i<=currentMonthDayCounts;i++){
                list.add(String.valueOf(i));
            }
        }

        for(String s:list){
            sortList.add(Integer.parseInt(s));
        }
        Collections.sort(sortList);
        return sortList;
    }

    public static List<Integer> reAddYearList(List<String> list){

        List<Integer> sortList=new ArrayList<>();
        for(int i=1;i<=12;i++){
            if(list.contains(String.valueOf(i))){
                continue;
            }else{
                list.add(String.valueOf(i));
            }
        }
        for(String s:list){
            sortList.add(Integer.parseInt(s));
        }
        Collections.sort(sortList);
        return sortList;
    }

    public static List<String> reAddHourList(Map<String,String>  hmMap){

        List<String> returnList=new ArrayList<>();

        for(int i=0;i<24;i++){
            boolean hasMinuteTime = true;
            int minuteCount=0;
            for(int j=0;j<60;j++){

                String hour ="";
                String minute="";
                if(i<10){
                    hour="0"+i;
                }else{
                    hour=String.valueOf(i);
                }
                if(j<10){
                    minute="0"+j;
                }else{
                    minute=String.valueOf(j);
                }
                //System.out.println(hour+":"+minute+"====>"+hmMap.containsKey(hour+":"+minute));
                if(hmMap.containsKey(hour+":"+minute)){
                    minuteCount++;
                    break;
                }
            }
            if(minuteCount==0){
                if(i<10){
                    returnList.add("0"+i+":00");
                }else{
                    returnList.add(i+":00");
                }
            }
        }
        for(String s:returnList){
            System.out.println(s);
        }
        return returnList;
    }

    /**
     * 计算当前月有多少天
     *
     * @return
     */
    public static int getDays(int year, int month) {
        int days = 0;
        if (month != 2) {
            switch (month) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    days = 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    days = 30;

            }
        } else {
            // 闰年
            if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
                days = 29;
            else
                days = 28;
        }
        System.out.println("当月有" + days + "天！");
        return days;
    }


    /**
     * 初始化 00:00  --- 23:55 的treemap
     * @return
     */
    public static TreeMap<String,String> initHourTreeMap(){
        //初始化hashmap
        TreeMap<String,String> hmMap=new TreeMap<String,String>();
        for(int i=0;i<=23;i++){
            for(int j=0;j<60;j++){

                if(i<10&&j%5==0){
                    if(j<10){
                        hmMap.put("0"+i+":0"+j,"0");
                    }else{
                        hmMap.put("0"+i+":"+j,"0");
                    }
                }else if(i>=10&&j%5==0){
                    if(j<10){
                        hmMap.put(i+":0"+j,"0");
                    }else{
                        hmMap.put(i+":"+j,"0");
                    }
                }
            }
        }
//        for(String s:hmMap.keySet()){
//            System.out.println(s);
//        }
        return hmMap;
    }

    /**
     * 初始化 00:00  --- 23:55 的treemap
     * @return
     */
    public static TreeMap<String,String> initHourTreeMap(String searchDate){
        //初始化hashmap
        TreeMap<String,String> hmMap=new TreeMap<String,String>();
        for(int i=0;i<=23;i++){
            for(int j=0;j<60;j++){

                if(i<10&&j%5==0){
                    if(j<10){
                        hmMap.put(searchDate+" 0"+i+":0"+j,"-1");
                    }else{
                        hmMap.put(searchDate+" 0"+i+":"+j,"-1");
                    }
                }else if(i>=10&&j%5==0){
                    if(j<10){
                        hmMap.put(searchDate+" "+i+":0"+j,"-1");
                    }else{
                        hmMap.put(searchDate+" "+i+":"+j,"-1");
                    }
                }
            }
        }
//        for(String s:hmMap.keySet()){
//            System.out.println(s);
//        }
        return hmMap;
    }

    /**
     * 初始化 00:00  --- 23:55 的treemap
     * @return
     */
    public static TreeMap<String,String> initHourTreeEnergyMap(){
        //初始化hashmap
        TreeMap<String,String> hmMap=new TreeMap<String,String>();
        for(int i=0;i<=23;i++){
            if(i<10){
                hmMap.put("0"+i,"0");
            }else{
                hmMap.put(String.valueOf(i),"0");
            }
        }
//        for(String s:hmMap.keySet()){
//            System.out.println(s);
//        }
        return hmMap;
    }

    /**
     * 初始化 00:00  --- 23:55 的treemap
     * @return
     */
    public static TreeMap<String,String> initHourTreeEnergyMap(String searchDate){
        //初始化hashmap
        TreeMap<String,String> hmMap=new TreeMap<String,String>();
        for(int i=0;i<=23;i++){
            if(i<10){
                hmMap.put(searchDate+" 0"+i,"0");
            }else{
                hmMap.put(searchDate+" "+i,"0");
            }
        }
        for (String s:hmMap.keySet()
             ) {
            System.out.println(s+"____"+hmMap.get(s));
        }
        return hmMap;
    }

    public static TreeMap<String,String> subTreeMap(TreeMap<String,String> dataMap) {
        TreeMap<String, String> initMap = initHourTreeMap();
        for (String s : dataMap.keySet()) {
            String hour = s.split(":")[0];
            String minute = s.split(":")[1];
            String minute_1 = minute.substring(0, 1);
            String minute_2 = minute.substring(1, 2);
            String addMinute = "";

            if (hour.equals("00")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 5) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("01")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("02")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("03")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("04")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("05")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                System.out.println(hour+":"+addMinute);
                System.out.println(initMap.get(hour + ":" + addMinute));
                System.out.println(dataMap.get(s));
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("06")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("07")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("08")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("09")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("10")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                System.out.println("hour"+hour+":"+addMinute+"=>"+initMap.get(hour + ":" + addMinute));
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("11")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("12")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("13")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("14")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("15")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("16")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("17")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("18")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("19")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("20")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("21")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("22")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("23")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                initMap.put(hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            }
            //System.out.println(s);
        }
        return initMap;
    }

    public static TreeMap<String,String> subTreeMapEnergy(TreeMap<String,String> dataMap) {
        TreeMap<String, String> initMap = initHourTreeEnergyMap();
        for (String s : dataMap.keySet()) {
            String hour = s.split(":")[0];
            String minute = s.split(":")[1];

            if (hour.equals("00")) {
                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("01")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("02")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("03")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("04")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("05")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("06")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("07")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("08")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("09")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("10")) {
                //System.out.println("dataMap.get(s):"+dataMap.get(s));
                //System.out.println("hour"+hour+":"+addMinute+"=>"+initMap.get(hour + ":" + addMinute));
                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("11")) {
                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("12")) {
                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("13")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("14")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("15")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("16")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("17")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("18")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("19")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("20")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("21")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("22")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("23")) {

                initMap.put(hour , String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            }
            //System.out.println(s);
        }
        return initMap;
    }

    /**
     * energy计算，first
     * @param dataMap
     * @return
     */
    public static TreeMap<String,String> subTreeMapEnergyFirst(TreeMap<String,String> dataMap) {
        TreeMap<String, String> initMap = initHourTreeEnergyMap();
        for (String s : dataMap.keySet()) {
            String hour = s.split(":")[0];
            String minute = s.split(":")[1];

            if (hour.equals("00")) {
                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }

            } else if (hour.equals("01")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("02")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("03")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("04")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("05")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("06")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("07")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("08")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("09")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("10")) {

                //System.out.println("dataMap.get(s):"+dataMap.get(s));
                //System.out.println("hour"+hour+":"+addMinute+"=>"+initMap.get(hour + ":" + addMinute));
                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("11")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("12")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("13")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("14")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("15")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("16")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("17")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("18")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("19")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("20")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("21")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("22")) {

                if(initMap.get(hour).equals("0")){
                    initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
                }
            } else if (hour.equals("23")) {

                initMap.put(hour , String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            }
            //System.out.println(s);
        }
        return initMap;
    }

    /**
     * energy计算 last
     * @param dataMap
     * @return
     */
    public static TreeMap<String,String> subTreeMapEnergyLast(TreeMap<String,String> dataMap) {
        TreeMap<String, String> initMap = initHourTreeEnergyMap();
        for (String s : dataMap.keySet()) {
            String hour = s.split(":")[0];
            String minute = s.split(":")[1];

            if (hour.equals("00")) {
                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("01")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("02")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("03")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("04")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("05")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("06")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("07")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("08")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("09")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("10")) {

                //System.out.println("dataMap.get(s):"+dataMap.get(s));
                //System.out.println("hour"+hour+":"+addMinute+"=>"+initMap.get(hour + ":" + addMinute));
                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("11")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("12")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("13")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("14")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("15")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("16")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("17")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("18")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("19")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("20")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("21")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("22")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("23")) {

                initMap.put(hour , String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            }
            //System.out.println(s);
        }
        return initMap;
    }

    public static TreeMap<String,String> subTreeMapEnergy(TreeMap<String,String> dataMap,String searchDate) {
        TreeMap<String, String> initMap = initHourTreeEnergyMap(searchDate);
        for (String s : dataMap.keySet()) {
            String hour = s.split(":")[0];
            String minute = s.split(":")[1];
            String minute_1 = minute.substring(0, 1);
            String minute_2 = minute.substring(1, 2);
            String addMinute = "";

            if (hour.equals("00")) {
                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("01")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("02")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("03")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("04")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("05")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("06")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("07")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("08")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("09")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("10")) {

                //System.out.println("dataMap.get(s):"+dataMap.get(s));
                //System.out.println("hour"+hour+":"+addMinute+"=>"+initMap.get(hour + ":" + addMinute));
                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("11")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("12")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("13")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("14")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("15")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("16")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("17")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("18")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("19")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("20")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("21")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("22")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("23")) {

                initMap.put(searchDate+" "+hour , String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            }
            //System.out.println(s);
        }
        return initMap;
    }

    public static TreeMap<String,String> subTreeMap(TreeMap<String,String> dataMap,String searchDate) {
//        System.out.println("searchDate==========>"+searchDate);

        TreeMap<String, String> initMap = initHourTreeMap(searchDate);
//        for (String s : dataMap.keySet()) {
//            System.out.println("TreeMap====>s"+s);
        //有数据的小时和分钟
        String firstHour = "";
        String firstMinute = "";
        String lastHour = "";
        String lastMinute = "";

        if(dataMap!=null&&dataMap.size()>0){
            firstHour = dataMap.firstKey().split(":")[0];
            firstMinute = dataMap.firstKey().split(":")[1];
            lastHour = dataMap.lastKey().split(":")[0];
            lastMinute = dataMap.lastKey().split(":")[1];
        }
        for (String s : initMap.keySet()) {
            String hour = s.substring(s.length()-5,s.length()-3);
            String minute = s.substring(s.length()-2,s.length());
            String minute_1 = minute.substring(0, 1);
            String minute_2 = minute.substring(1, 2);
//            System.out.println("minute:"+minute+","+"minute_1:"+minute_1+":"+minute_2);





//            String firstData = dataMap.firstEntry().getValue();

            String addMinute = "";
            if (hour.equals("00")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }

                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }

//                System.out.println("dataMap.get(s):"+dataMap.get(s));

            } else if (hour.equals("01")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("02")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("03")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("04")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("05")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("06")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    try{
                        initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                    }catch (Exception ex){
                        System.out.println("===CidDataController.subTreeMap key: "+(searchDate+" "+hour + ":" + addMinute)+"  ,value:"+(String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute)))));
                        ex.printStackTrace();
                    }
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("07")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("08")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));

                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("09")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("10")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("11")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("12")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("13")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("14")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("15")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("16")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("17")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("18")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("19")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("20")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("21")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("22")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("23")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
//                initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
                if(dataMap.get(hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            }
            //System.out.println(s);
        }
        return initMap;
    }

    public static TreeMap<String,String> subTreeMapNotAdd(TreeMap<String,String> dataMap,String searchDate) {
        TreeMap<String, String> initMap = initHourTreeMap(searchDate);
        //有数据的小时和分钟
        String firstHour = "";
        String firstMinute = "";
        String lastHour = "";
        String lastMinute = "";
        String firstData="";
        String lastData="";

        if(dataMap!=null&&dataMap.size()>0){
            firstData = dataMap.firstKey();
            lastData = dataMap.lastKey();
            firstHour = firstData.substring(firstData.length()-5,firstData.length()-3); //dataMap.firstKey().split(":")[0];
            firstMinute = firstData.substring(firstData.length()-2,firstData.length());//dataMap.firstKey().split(":")[1];
            lastHour = lastData.substring(lastData.length()-5,lastData.length()-3); // dataMap.lastKey().split(":")[0];
            lastMinute = lastData.substring(lastData.length()-2,lastData.length()); // dataMap.lastKey().split(":")[1];
//            System.out.println("not add first key:"+dataMap.firstKey());
//            System.out.println("not add last key:"+dataMap.lastKey());
//            System.out.println("not add first entry:"+dataMap.firstEntry());
//            System.out.println("not add last entry:"+dataMap.lastEntry());
        }

        for (String s : initMap.keySet()) {
//            System.out.println("====subTreeMapNotAdd initMap s:"+s +"  value:"+initMap.get(s));
            String hour = s.substring(s.length()-5,s.length()-3);
            String minute = s.substring(s.length()-2,s.length());
            String minute_1 = minute.substring(0, 1);
            String minute_2 = minute.substring(1, 2);
//            System.out.println("minute:"+minute+","+"minute_1:"+minute_1+":"+minute_2);
            String addMinute = "";



            if (hour.equals("00")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("01")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("02")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("03")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("04")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("05")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("06")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("07")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("08")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("09")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("10")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("11")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("12")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("13")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("14")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("15")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("16")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("17")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("18")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("19")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("20")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("21")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("22")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("23")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            }
            //System.out.println(s);
        }
        return initMap;
    }

    public static TreeMap<String,String> subTreeMapNotAddForIndex(TreeMap<String,String> dataMap,String searchDate) {
        TreeMap<String, String> initMap = initHourTreeMap(searchDate);
        //有数据的小时和分钟
        String firstHour = "";
        String firstMinute = "";
        String lastHour = "";
        String lastMinute = "";
        String firstData="";
        String lastData="";

        if(dataMap!=null&&dataMap.size()>0){
            firstData = dataMap.firstKey();
            lastData = dataMap.lastKey();
            firstHour = firstData.substring(firstData.length()-5,firstData.length()-3); //dataMap.firstKey().split(":")[0];
            firstMinute = firstData.substring(firstData.length()-2,firstData.length());//dataMap.firstKey().split(":")[1];
            lastHour = lastData.substring(lastData.length()-5,lastData.length()-3); // dataMap.lastKey().split(":")[0];
            lastMinute = lastData.substring(lastData.length()-2,lastData.length()); // dataMap.lastKey().split(":")[1];
            System.out.println("not add first key:"+dataMap.firstKey());
            System.out.println("not add last key:"+dataMap.lastKey());
            System.out.println("not add first entry:"+dataMap.firstEntry());
            System.out.println("not add last entry:"+dataMap.lastEntry());
        }

        for (String s : initMap.keySet()) {
//            System.out.println("TreeMap====>s"+s);
            String hour = s.substring(s.length()-5,s.length()-3);
            String minute = s.substring(s.length()-2,s.length());
            String minute_1 = minute.substring(0, 1);
            String minute_2 = minute.substring(1, 2);
//            System.out.println("minute:"+minute+","+"minute_1:"+minute_1+":"+minute_2);
            String addMinute = "";



            if (hour.equals("00")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("01")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("02")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("03")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("04")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("05")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
            } else if (hour.equals("06")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("07")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("08")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("09")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("10")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("11")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("12")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("13")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("14")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("15")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("16")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("17")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("18")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("19")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("20")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("21")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("22")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("23")) {
                if (Integer.parseInt(minute_2) >= 0 && Integer.parseInt(minute_2) <= 4) {
                    addMinute = minute_1 + "0";
                } else {
                    addMinute = minute_1 + "5";
                }
                if(dataMap.get(searchDate+" "+hour + ":" + addMinute)!=null){
                    initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(dataMap.get(searchDate+" "+hour + ":" + addMinute))));
                }
                //先判断时间
                if(dataMap!=null&&dataMap.size()>0){
                    if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                        //大于等于起始小时，小于等于结束小时
                        if(Integer.parseInt(hour)>=Integer.parseInt(firstHour)&&Integer.parseInt(hour)<=Integer.parseInt(lastHour)){

                            if(Integer.parseInt(hour)==Integer.parseInt(firstHour)&&Integer.parseInt(addMinute)>=Integer.parseInt(firstMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)==Integer.parseInt(lastHour)&&Integer.parseInt(addMinute)<=Integer.parseInt(lastMinute)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }else if(Integer.parseInt(hour)>Integer.parseInt(firstHour)&&Integer.parseInt(hour)<Integer.parseInt(lastHour)){

                                if(initMap.get(searchDate+" "+hour + ":" + addMinute).equals("0")){
                                    System.out.println("key:"+initMap.get(searchDate+" "+hour + ":" + addMinute));
                                    initMap.put(searchDate+" "+hour + ":" + addMinute,"0.00001");
                                }
                            }

                        }
                    }
                }
//                System.out.println("dataMap.get(s):"+dataMap.get(s));
                //initMap.put(searchDate+" "+hour + ":" + addMinute, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour + ":" + addMinute)) + Float.parseFloat(dataMap.get(s))));
            }
            //System.out.println(s);
        }
        return initMap;
    }


    public static TreeMap<String,String> subTreeMapForAppNotAdd(TreeMap<String,String> dataMap) {
        TreeMap<String, String> initMap = initHourTreeEnergyMap();
        for (String s : dataMap.keySet()) {
//            System.out.println("TreeMap====>s"+s);
            String hour = s.substring(s.length()-5,s.length()-3);
            String minute = s.substring(s.length()-2,s.length());
            String minute_1 = minute.substring(0, 1);
            String minute_2 = minute.substring(1, 2);
//            System.out.println("minute:"+minute+","+"minute_1:"+minute_1+":"+minute_2);
            String addMinute = "";
            if (hour.equals("00")) {
                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("01")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("02")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("03")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("04")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("05")) {
                System.out.println(s);
                System.out.println(dataMap.get(s));
                System.out.println("hour:"+Float.parseFloat(initMap.get(hour)));
                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("06")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("07")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("08")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("09")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("10")) {

                //System.out.println("dataMap.get(s):"+dataMap.get(s));
                //System.out.println("hour"+hour+":"+addMinute+"=>"+initMap.get(hour + ":" + addMinute));
                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("11")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("12")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("13")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("14")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("15")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("16")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("17")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("18")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("19")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("20")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("21")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("22")) {

                initMap.put(hour, String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("23")) {

                initMap.put(hour , String.valueOf(Float.parseFloat(initMap.get(hour)) + Float.parseFloat(dataMap.get(s))));
            }
            //System.out.println(s);
        }
        return initMap;
    }

    public static TreeMap<String,String> subTreeMapForAppNotAdd(TreeMap<String,String> dataMap,String searchDate) {
        TreeMap<String, String> initMap = initHourTreeEnergyMap(searchDate);
        for (String s : dataMap.keySet()) {
//            System.out.println("TreeMap====>s"+s);
            String hour = s.substring(s.length()-5,s.length()-3);
            String minute = s.substring(s.length()-2,s.length());
            String minute_1 = minute.substring(0, 1);
            String minute_2 = minute.substring(1, 2);
//            System.out.println("minute:"+minute+","+"minute_1:"+minute_1+":"+minute_2);
            String addMinute = "";
            if (hour.equals("00")) {
                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("01")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("02")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("03")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("04")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("05")) {
                System.out.println(initMap.get(hour));
                System.out.println(s);
                System.out.println(dataMap.get(s));
                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("06")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("07")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("08")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("09")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("10")) {

                //System.out.println("dataMap.get(s):"+dataMap.get(s));
                //System.out.println("hour"+hour+":"+addMinute+"=>"+initMap.get(hour + ":" + addMinute));
                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("11")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("12")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("13")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("14")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("15")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("16")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("17")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("18")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("19")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("20")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("21")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("22")) {

                initMap.put(searchDate+" "+hour, String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            } else if (hour.equals("23")) {

                initMap.put(searchDate+" "+hour , String.valueOf(Float.parseFloat(initMap.get(searchDate+" "+hour)) + Float.parseFloat(dataMap.get(s))));
            }
            //System.out.println(s);
        }
        return initMap;
    }

}

