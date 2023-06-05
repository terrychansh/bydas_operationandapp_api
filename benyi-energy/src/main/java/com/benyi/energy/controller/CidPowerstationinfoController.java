package com.benyi.energy.controller;

import java.awt.datatransfer.FlavorEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.benyi.common.config.RuoYiConfig;
import com.benyi.common.core.domain.entity.SysDept;
import com.benyi.common.core.domain.model.LoginUser;
import com.benyi.common.core.page.PageDomain;
import com.benyi.common.core.page.TableSupport;
import com.benyi.common.utils.DateUtils;
import com.benyi.common.utils.StringUtils;
import com.benyi.common.utils.file.FileUploadUtils;
import com.benyi.common.utils.file.MimeTypeUtils;
import com.benyi.energy.domain.*;
import com.benyi.energy.mapper.CidDataDayMapper;
import com.benyi.energy.mapper.CidDataHourMapper;
import com.benyi.energy.service.*;
import com.benyi.framework.web.domain.server.Sys;
import com.benyi.framework.web.service.TokenService;
import com.benyi.system.service.ISysDeptService;
import com.github.pagehelper.PageInfo;
import org.springframework.http.HttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.benyi.common.annotation.Log;
import com.benyi.common.core.controller.BaseController;
import com.benyi.common.core.domain.AjaxResult;
import com.benyi.common.enums.BusinessType;
import com.benyi.common.utils.poi.ExcelUtil;
import com.benyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 电站列表Controller
 *
 * @author wuqiguang
 * @date 2022-08-01
 */
@RestController
@RequestMapping("/energy/powerstationinfo")
public class CidPowerstationinfoController extends BaseController
{
    @Autowired
    private ICidPowerstationinfoService powerstationinfoService;

    @Autowired
    private ICidRelationService cidRelationService;


    @Autowired
    CidDataHourMapper cidDataHourMapper ;

    @Autowired
    CidDataDayMapper cidDataDayMapper ;


    @Autowired
    private TokenService tokenService;

    @Autowired
    private ICidDataService cidDataService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ICidDayDataService dayDataService;

    @Autowired
    private ICidLoginHeartService heartService;


    /**
     * 查询电站列表列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:list')")
    @GetMapping("/listAllPowerStation")
    public AjaxResult listAllPowerStation(CidPowerstationinfo cidPowerstationinfo) throws ParseException {


        LoginUser loginUser = getLoginUser();
        cidPowerstationinfo.setDelFlag("0");
        if (loginUser.getDeptId() != 100) {
            cidPowerstationinfo.setEnergyDeptId(loginUser.getDeptId());
        }
        List<CidPowerstationinfo> list = powerstationinfoService.selectCidPowerstationinfoListForPlantList(cidPowerstationinfo);
        System.out.println("===CidRelationController getCidVidDetail listAllPowerStation size" + list.size() + " cidPowerstationinfo:" + JSON.toJSONString(cidPowerstationinfo));


        return AjaxResult.success(list);
    }



    /**
     * 查询电站列表列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:list')")
    @GetMapping("/list")
    public TableDataInfo list(CidPowerstationinfo cidPowerstationinfo) throws ParseException {


        System.out.println("===CidRelationController getCidVidDetail list pageNum:"+ TableSupport.getPageDomain().getPageNum()  );
        LoginUser loginUser = getLoginUser();
        cidPowerstationinfo.setDelFlag("0");
        if(loginUser.getDeptId()!=100){
            cidPowerstationinfo.setEnergyDeptId(loginUser.getDeptId());
        }
        List<CidPowerstationinfo> listTotal = powerstationinfoService.selectCidPowerstationinfoListForPlantList(cidPowerstationinfo);

        int totalOnline=0;
        int totalOffline=0;
        int totalError=0;
        for (CidPowerstationinfo cidPowerstationinfo1:listTotal){
            System.out.println("===plant:"+ JSON.toJSONString(cidPowerstationinfo1)  );
            if (cidPowerstationinfo1.getEnergyStatus().equals("0")){
                totalOnline=totalOnline+1;
            }else  if (cidPowerstationinfo1.getEnergyStatus().equals("2")){
                totalError=totalError+1;
            }else {
                totalOffline=totalOffline+1;
            }

        }

        startPage();
        List<CidPowerstationinfo> list = powerstationinfoService.selectCidPowerstationinfoListForPlantList(cidPowerstationinfo);

        List<CidPowerstationinfo> listNew =new ArrayList<CidPowerstationinfo>();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Integer untZoneMinute=0;

        for(CidPowerstationinfo plant:list){

            untZoneMinute=DateUtils.getUTCZoneMinute(plant.getEnergyTimeZone());;
            if (untZoneMinute==null){
                System.out.println("===CidRelationController getCidVidDetail untZoneMinute is null ,plantId:"+plant.getId() );
                untZoneMinute=0;
            }

            Date nowUTC = new Date(new Date().getTime() + untZoneMinute * 60 * 1000);
            Map paramMap =new HashMap();
            paramMap.put("plantID",plant.getId());
            paramMap.put("createDate",nowUTC);
            List<Map> cidDataHourMapList = cidDataHourMapper.selectBySomeDay(paramMap);

            TreeMap<String,String> heMap=new TreeMap<String,String>();
            for(int i=0;i<24;i++){
                if(i<10){
                    heMap.put("0"+i,"0");
                }else{
                    heMap.put(String.valueOf(i),"0");
                }
            }

            for(Map map:cidDataHourMapList){
                heMap.put(map.get("date").toString(),map.get("energy").toString());
            }
            plant.setEnergyHourList(heMap);
            listNew.add(plant);
        }


        Map statusMap=new HashMap();
        statusMap.put("offline",totalOffline);
        statusMap.put("online",totalOnline);
        statusMap.put("error",totalError);

        TableDataInfo tableDataInfo =getDataTable(listNew);
        tableDataInfo.setTotal(listTotal.size());
        tableDataInfo.setData(statusMap);

        return tableDataInfo;
    }

    @GetMapping("/totalPlantInfoDetail")
    public AjaxResult totalPlantInfoDetail() throws ParseException {

        LoginUser loginUser = getLoginUser();
        CidPowerstationinfo cidPowerstationinfo=new CidPowerstationinfo();
        cidPowerstationinfo.setDelFlag("0");
        if(loginUser.getDeptId()!=100){
            cidPowerstationinfo.setEnergyDeptId(loginUser.getDeptId());
        }
        List<CidPowerstationinfo> list = powerstationinfoService.selectCidPowerstationinfoList(cidPowerstationinfo);
        int count=list.size();
        int online =0;
        int offline =0;
        int error =0;
        int build =0;
        AjaxResult ajaxResult = AjaxResult.success();
        SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        for(CidPowerstationinfo plant:list){
            if(plant.getLastErrorCode()!=null){
                    if(!plant.getLastErrorCode().equals("0000") && !plant.getLastErrorCode().equals("0200") && !plant.getLastErrorCode().equals("0008") ){
                    error++;
                }
            }
            if(plant.getLastUpdate().equals("0")){
                build++;
            }
            if(!plant.getLastUpdate().equals("0")){
                String beginTime = format.format(new Date());
                String endTime = plant.getLastUpdate();

                Date date1 = format.parse(beginTime);
                Date date2 = format.parse(endTime);

                long beginMillisecond = date1.getTime();
                long endMillisecond = date2.getTime();
                long jian=beginMillisecond-endMillisecond;
                if(beginTime.substring(0,10).equals(endTime.substring(0,10))){
                    if(!(jian>1000*60*30)&&jian>0){
                        online++;
                    }else{
                        offline++;
                    }
                }else{
                    offline++;
                }

            }
        }

        ajaxResult.put("count",count);
        ajaxResult.put("online",online);
        ajaxResult.put("build",build);
        ajaxResult.put("error",error);
        ajaxResult.put("offline",offline);

        return ajaxResult;
    }

//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:detail')")
    @GetMapping("/detail")
    public AjaxResult detail(Long id) throws ParseException {

        CidPowerstationinfo powerStation=powerstationinfoService.selectCidPowerstationinfoById(id);

        if (powerStation.getEnergyStatus().equals("0")){
            powerStation.setEnergyStatus("OnLine");
        }else  if (powerStation.getEnergyStatus().equals("2")){
            powerStation.setEnergyStatus("Alarm");
        }else  if (powerStation.getEnergyStatus().equals("3")){
            powerStation.setEnergyStatus("OffLine");
        }

//        SysDept dept = deptService.selectDeptById(powerStation.getEnergyDeptId());
//        powerStation.setEnergyDeptName(dept.getDeptName());
        return AjaxResult.success(powerStation);
    }

//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:detailEchart')")

    /**
     * power echart
     * @param id
     * @param searchDate
     * @param searchDateType
     * @return
     */
    @GetMapping("/detailEchart")
    public AjaxResult detailEchart(Long id,String searchDate,String searchDateType,String startDate,String endDate){
        if(searchDateType.equals("d")){
            Date date = new Date();
            SimpleDateFormat sdfDay =new SimpleDateFormat("yyyy-MM-dd");
            List<CidData> returnList=new ArrayList<>();
            if(startDate==null) {
                startDate = sdfDay.format(date);
                endDate = sdfDay.format(date);
            }
            String startMonth=startDate.substring(5,7);
            String endMonth=endDate.substring(5,7);
            List<String> searchDayList=new ArrayList<>();

            if(startMonth.equals(endMonth)){
                String startDay=startDate.substring(startDate.length()-2,startDate.length());
                String endDay=endDate.substring(endDate.length()-2,endDate.length());
                int dayZone=Integer.parseInt(endDay)-Integer.parseInt(startDay);
                String searchDateZone=startDate.substring(0,startDate.length()-2);
                System.out.println("startDay:"+startDay);
                System.out.println("endDay:"+endDay);
                System.out.println("dayZone:"+dayZone);
                for(int i=0;i<=dayZone;i++){
                    searchDateZone=startDate.substring(0,startDate.length()-2);
                    String addDate="";
                    if(Integer.parseInt(startDay)+i<10){
                        addDate="0"+(Integer.parseInt(startDay)+i);
                    }else{
                        addDate=String.valueOf((Integer.parseInt(startDay)+i));
                    }
                    searchDateZone=searchDateZone+addDate;
                    searchDayList.add(searchDateZone);
                }
            }else{
                String startDay=startDate.substring(startDate.length()-2,startDate.length());
                String endDay=endDate.substring(endDate.length()-2,endDate.length());
                String year=startDate.substring(0,4);
                int startMontDay=CidDataController.getDays(Integer.parseInt(year),Integer.parseInt(startMonth));
                String searchDateZone=startDate.substring(0,startDate.length()-2);
                String searchDateEndZone=endDate.substring(0,endDate.length()-2);
                int startDay_1=Integer.parseInt(startDay);
                for(int i=0;i<=(startMontDay-startDay_1);i++){
                    System.out.println(year+"-"+startMonth+"-"+(startDay_1+i));
                    searchDateZone=startDate.substring(0,startDate.length()-2);
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

            for(String dateZone:searchDayList){
                System.out.println("dateZone:"+dateZone);
//                List<CidData> resultList=cidDataService.getStationPowerByPidAndDateForDay(id,null,dateZone,searchDateType);
                List<CidData> resultList=cidDataService.getStationPowerByPidAndDateForDayForMinute(id,null,dateZone,searchDateType);
                TreeMap<String,String> dataMap=new TreeMap<>();
                for(Object obj:resultList){
                    String s=JSON.toJSONString(obj);
                    CidData d =JSON.parseObject(s,CidData.class);
                    dataMap.put(d.getDate(),d.getPower());
                }

                TreeMap<String,String> rmap=CidDataController.subTreeMap(dataMap,dateZone);
                for(String s:rmap.keySet()){
                    CidData data =new CidData();
                    data.setDate(s);
                    data.setPower(rmap.get(s));
                    returnList.add(data);
                }
            }

            return AjaxResult.success(returnList);
        }else if(searchDateType.equals("m")){
            List<CidDayData> resultList=cidDataService.getStationPowerByPidAndDate(id,null,searchDate+"-01",searchDateType);
            List<CidDayData> returnResultList= new ArrayList<>();
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
                List<Integer> returnList=CidDataController.reAddMonthList(year,month,list);
                Map<Integer,String> resultMap=new HashMap<>();

                for(int i=0;i<returnList.size();i++){
                    resultMap.put(returnList.get(i),"0");
                }
                for(int i=0;i<dataList.size();i++){
                    String data="";
                    //System.out.println("dataType:"+dataType);
                    data = dataList.get(i).getPower();
                    resultMap.put(Integer.parseInt(dataList.get(i).getDate().split("-")[2]),data);
                }
                for (Integer key : resultMap.keySet()) {
                    CidDayData d=new CidDayData();
                    if(key<10){
                        d.setDate(year+"-"+month+"-0"+key);
                    }else{
                        d.setDate(year+"-"+month+"-"+key);
                    }
                    d.setPower(resultMap.get(key));
                    returnResultList.add(d);
                }
            }else{
                String year=searchDate.split("-")[0];
                String month=searchDate.split("-")[1];
                List<Integer> returnList=CidDataController.reAddMonthList(year,month,null);
                Map<Integer,String> resultMap=new HashMap<>();
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
                    d.setPower(resultMap.get(key));
                    returnResultList.add(d);
                }
            }
            return AjaxResult.success(returnResultList);
        }else if(searchDateType.equals("y")){
            List<CidDayData> resultList=cidDataService.getStationPowerByPidAndDate(id,null,searchDate+"-01-01",searchDateType);
            List<CidDayData> returnResultList= new ArrayList<>();
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
                List<Integer> returnList=CidDataController.reAddYearList(list);
                Map<Integer,String> resultMap=new HashMap<>();
                for(int i=0;i<returnList.size();i++){
                    resultMap.put(returnList.get(i),"0");
                }
                for(int i=0;i<dataList.size();i++){
                    String data="";
                    data = dataList.get(i).getPower();
                    resultMap.put(Integer.parseInt(dataList.get(i).getDate().split("-")[1]),data);
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
            }else{
                List<String> list=new ArrayList<>();
                String year=searchDate.split("-")[0];
                List<Integer> returnList=CidDataController.reAddYearList(list);
                Map<Integer,String> resultMap=new HashMap<>();
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
                    d.setPower(resultMap.get(key));
                    returnResultList.add(d);
                }
            }

            return AjaxResult.success(returnResultList);
        }else{
            List<CidDayData> resultList=cidDataService.getStationPowerByPidAndDate(id,null,searchDate,searchDateType);
            return AjaxResult.success(resultList);
        }
    }

    /**
     * Energy echart
     * @param id
     * @param searchDate
     * @param searchDateType
     * @return
     */
    @GetMapping("/detailEchartEnergy")
    public AjaxResult detailEchartEnergy(Long id,String searchDate,String searchDateType){

        if(searchDateType.equals("d")){

            Map paramMap =new HashMap();
            paramMap.put("plantID",id);
            paramMap.put("createDate",searchDate);
            List<Map> cidDataHourMapList = cidDataHourMapper.analyseByOneDay(paramMap);

            TreeMap<String,Map> heMap=new TreeMap();
            for(int i=0;i<24;i++){
                Map map = new HashMap();
                map.put("energy",0);
                if(i<10){
                    map.put("date","0"+i);
                }else{
                    map.put("date",String.valueOf(i));
                }
                heMap.put(map.get("date").toString(),map);
            }
            for(Map map:cidDataHourMapList){
                heMap.put(map.get("date").toString(),map);
            }

            List<Map> returnList =new ArrayList<>();
            for (String s:heMap.keySet()){
                returnList.add(heMap.get(s));
            }

            return AjaxResult.success(returnList);

        }else if(searchDateType.equals("m")){
            String searchStartDate=null;
            String searchEndDate=null;

            if(searchDate.length()<=7){
                searchStartDate=searchDate+"-01";
                searchEndDate=searchDate+"-31";
            }else {
                searchStartDate=searchDate.substring(0,searchDate.indexOf("-")+3)+"-01";
                searchEndDate=searchDate.substring(0,searchDate.indexOf("-")+3)+"-31";
            }


            List plantIDList=new ArrayList();
            plantIDList.add(id);

            Map paramMap= new HashMap();
            paramMap.put("plantID",plantIDList);
            paramMap.put("startDate",searchStartDate);
            paramMap.put("endDate",searchEndDate);

            List<Map> cidDataDaysList=cidDataDayMapper.selectByDay(paramMap);
            System.out.println("==paramMap:"+paramMap+"  cidDataDaysList:"+cidDataDaysList.size());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());

            int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            TreeMap<String,Map> treeMap=new TreeMap();

            for (int i=1;i<=daysOfMonth;i++){
                Map map = new HashMap();
                map.put("energy",0);
                if(i<10){
                    map.put("date",searchDate+"-0"+i);
                }else{
                    map.put("date",searchDate+"-"+String.valueOf(i));
                }
                treeMap.put(map.get("date").toString(),map);
            }

            for (Map itemMap:cidDataDaysList){
                treeMap.put(itemMap.get("date").toString(),itemMap);
            }
            List<Map> returnNewList=new ArrayList<>();
            for (String s :treeMap.keySet()){
                returnNewList.add(treeMap.get(s));
            }
            return AjaxResult.success(returnNewList);
        }else if(searchDateType.equals("y")){

            if(searchDate.length()<=4){
                searchDate=searchDate+"-01-01";
            }

            List<CidDayData> resultList=cidDataService.getStationPowerByPidAndDate(id,null,searchDate,searchDateType);
            List<CidDayData> returnResultList= new ArrayList<>();
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
                List<Integer> returnList=CidDataController.reAddYearList(list);
                Map<Integer,String> resultMap=new HashMap<>();
                for(int i=0;i<returnList.size();i++){
                    resultMap.put(returnList.get(i),"0");
                }
                for(int i=0;i<dataList.size();i++){
                    String data="";
                    data = dataList.get(i).getEnergy();
                    resultMap.put(Integer.parseInt(dataList.get(i).getDate().split("-")[1]),data);
                }
                for (Integer key : resultMap.keySet()) {
                    CidDayData d=new CidDayData();
                    if(key<10){
                        d.setDate(year+"-0"+key);
                    }else{
                        d.setDate(year+"-"+key);
                    }
                    Float ff=Float.parseFloat(resultMap.get(key));
                    d.setEnergy(String.format("%.2f", ff));
                    returnResultList.add(d);
                }
            }else{
                List<String> list=new ArrayList<>();
                String year=searchDate.split("-")[0];
                List<Integer> returnList=CidDataController.reAddYearList(list);
                Map<Integer,String> resultMap=new HashMap<>();
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
                    Float ff=Float.parseFloat(resultMap.get(key));
                    d.setEnergy(String.format("%.2f", ff));
                    returnResultList.add(d);
                }
            }
            return AjaxResult.success(returnResultList);
        }else{
            List<CidDayData> resultList=cidDataService.getStationPowerByPidAndDate(id,null,searchDate,searchDateType);
            List<CidDayData> dataList=new ArrayList<>();
            for(Object obj:resultList){
                String s=JSON.toJSONString(obj);
                CidDayData d =JSON.parseObject(s,CidDayData.class);
                Float ff=Float.parseFloat(d.getEnergy());
                d.setEnergy(String.format("%.2f", ff));
                dataList.add(d);
            }

            return AjaxResult.success(dataList);
        }
    }

    /**
     * 查询电站列表列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:cidList')")
    @GetMapping("/cidList")
    public TableDataInfo cidList(CidRelation queryRelation)
    {
//        CidRelation queryRelation=new CidRelation();
//        queryRelation.setPowerStationId(Long.parseLong(powerStationId));
        queryRelation.setDelFlag("0");
        List<CidRelation> list= cidRelationService.selectCidRelationList(queryRelation);
        return getDataTable(list);
    }


//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:detail')")
    @GetMapping("/toEdit")
    public AjaxResult toEdit(Long powerstationId){
        AjaxResult ajaxResult=AjaxResult.success(powerstationinfoService.selectCidPowerstationinfoById(powerstationId));
        return ajaxResult;
    }

    /**
     * 导出电站列表列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:export')")
    @Log(title = "电站列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CidPowerstationinfo cidPowerstationinfo)
    {
        List<CidPowerstationinfo> list = powerstationinfoService.selectCidPowerstationinfoList(cidPowerstationinfo);
        ExcelUtil<CidPowerstationinfo> util = new ExcelUtil<CidPowerstationinfo>(CidPowerstationinfo.class);
        util.exportExcel(response, list, "电站列表数据");
    }

    /**
     * 获取电站列表详细信息
     */
//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(powerstationinfoService.selectCidPowerstationinfoById(id));
    }

    /**
     * 新增电站列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:add')")
    @Log(title = "电站列表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CidPowerstationinfo cidPowerstationinfo,HttpServletRequest request)
    {
        // 获取当前的用户名称
        LoginUser loginUser = tokenService.getLoginUser(request);
        Long userId = loginUser.getUserId();
        Long deptId = loginUser.getDeptId();
        SysDept dept = deptService.selectDeptById(deptId);
        cidPowerstationinfo.setEnergyDeptId(deptId);
        cidPowerstationinfo.setEnergyDeptName(dept.getDeptName());
        //厂家
        if(deptId==100){
            cidPowerstationinfo.setEnergyInstall(dept.getDeptName());
        }else{
            cidPowerstationinfo.setEnergyInstall(dept.getParentName());
        }
        cidPowerstationinfo.setEnergySettingsAntiReflux("0");
        cidPowerstationinfo.setEnergySettingsRoleAllowLayout("0");
        cidPowerstationinfo.setEnergySettingsRolePrice(0l);
        cidPowerstationinfo.setEnergySettingsThreePhaseEquilibrium("0");
        cidPowerstationinfo.setEnergyTotal("0");
        cidPowerstationinfo.setEnergyTotalMonth("0");
        cidPowerstationinfo.setEnergyTotalYear("0");
        cidPowerstationinfo.setEnergyTotalDay("0");
        cidPowerstationinfo.setDelFlag("0");
        cidPowerstationinfo.setEnergyStatus("3");

        powerstationinfoService.insertCidPowerstationinfo(cidPowerstationinfo);
        AjaxResult ajaxResult=AjaxResult.success();
        if(cidPowerstationinfo.getId()>0){
            ajaxResult.put("msg","success");
            ajaxResult.put("code",200);
            ajaxResult.put("powerStationId",cidPowerstationinfo.getId());
        }else{
            ajaxResult.put("msg","error");
            ajaxResult.put("code",100);
        }

        return ajaxResult;
    }

    /**
     * 修改电站列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:edit')")
    @Log(title = "电站列表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CidPowerstationinfo cidPowerstationinfo)
    {
        logger.info("toEdit.....start,cidPowerstationinfo:"+cidPowerstationinfo);
       int result= powerstationinfoService.updateCidPowerstationinfo(cidPowerstationinfo);
        logger.info("toEdit.....result:"+result);

        return toAjax(result);
    }

    /**
     * 删除电站列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:powerstationinfo:remove')")
    @Log(title = "电站列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(powerstationinfoService.deleteCidPowerstationinfoByIds(ids));
    }

    /**
     * 头像上传
     */
    @Log(title = "电站图片", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file,Long powerStationId) throws Exception
    {
        if (!file.isEmpty())
        {
            String avatar = FileUploadUtils.upload(RuoYiConfig.getPowerStationPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            if ( powerstationinfoService.updatePowerImage(powerStationId,avatar) )
            {
                AjaxResult ajax = AjaxResult.success();
                ajax.put("energyImageUrl", avatar);
                ajax.put("code",200);
                return ajax;
            }
        }
        return AjaxResult.error("上传图片异常，请联系管理员");
    }

    @Log(title = "转移电站", businessType = BusinessType.UPDATE)
    @PostMapping("/changePlantDept")
    public AjaxResult changePlantDept(Long plantId,Long deptId){

        CidPowerstationinfo plant = new CidPowerstationinfo();
        plant.setId(plantId);
        plant.setEnergyDeptId(deptId);

        CidRelation queryRelation = new CidRelation();
        queryRelation.setPowerStationId(plantId);

        List<CidRelation> relationList = cidRelationService.selectCidRelationList(queryRelation);
        for(CidRelation relation :relationList){
            CidDayData updateDayDate = new CidDayData();
            updateDayDate.setCid(relation.getCid());
            updateDayDate.setVid(relation.getVid());
            updateDayDate.setRoadType(relation.getRoadType());
            dayDataService.updateForRemoveAndDel(updateDayDate);
        }
        int result = powerstationinfoService.updateCidPowerstationinfo(plant);
        return AjaxResult.success(result);
    }




    public static void main(String[] args) throws ParseException {
        SimpleDateFormat sdf =new SimpleDateFormat("YYYY-mm-dd hh:MM:ss");
        String s="2022-10-27 20:38:17";
        Date d=new Date(1640522297000l);
        Date d2=new Date(1666873739000l);
        System.out.println(sdf.format(d));
        System.out.println(sdf.format(d2));
        Date d3=sdf.parse(s);
        Date d4=new Date(1640520617000l);
        System.out.println(d3.getTime());
        System.out.println(sdf.format(d3));
        System.out.println(sdf.format(d4));

    }
}
