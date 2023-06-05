package com.benyi.energy.controller;

import com.alibaba.fastjson2.JSON;
import com.benyi.common.annotation.Log;
import com.benyi.common.core.controller.BaseController;
import com.benyi.common.core.domain.AjaxResult;
import com.benyi.common.core.domain.model.LoginUser;
import com.benyi.energy.domain.*;
import com.benyi.energy.service.*;
import com.benyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/energy/lof")
public class CidLofTvController extends BaseController {

    @Autowired
    private ICidPowerstationinfoService powerstationinfoService;

    @Autowired
    private ICidRelationService relationService;

    @Autowired
    private ICidDataService cidDataService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ICidDayDataService dayService;

    @Autowired
    private ICidLoginHeartService heartService;

    @GetMapping("/plantInfoCount")
    public AjaxResult plantInfoCount() throws ParseException {

        SimpleDateFormat sdf =new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        CidPowerstationinfo query=new CidPowerstationinfo();
        query.setEnergyDeptId(getLoginUser().getDeptId());
        List<CidPowerstationinfo> powerstationinfoList=powerstationinfoService.selectCidPowerstationinfoList(query);

        float countEnergy = 0;
        float monthEnergy = 0;
        float currentPower = 0;
        float todayEnergy = 0;
        float yearEnergy = 0;
        int buildCount = 0;
        int offline = 0;
        int online = 0;
        int inactive = 0;
        int error = 0;
        int count = powerstationinfoList.size();
        int build = 0;
        int cut = 0;
        float countCapacity = 0;


        for (CidPowerstationinfo power:powerstationinfoList) {

            SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            if(power.getLastUpdate()!=null&&!power.getLastUpdate().equals("0")){
                String beginTime = format.format(new Date());
                String endTime = power.getLastUpdate();

                Date date1 = format.parse(beginTime);
                Date date2 = format.parse(endTime);

                long beginMillisecond = date1.getTime();
                long endMillisecond = date2.getTime();
                long jian=beginMillisecond-endMillisecond;
                if(beginTime.substring(0,10).equals(endTime.substring(0,10))){
                    if(!(jian>1000*60*30)&&jian>0){
                        System.out.println("power.getLastErrorCode():"+power.getLastErrorCode());
                        if(power.getLastUpdate()==null){
                            offline++;
                        }else {
                            if (power.getLastErrorCode().equals("0000")||power.getLastErrorCode().equals("0200")) {
                                online++;
                            } else {
                                //online++;
                                error++;
                            }
                        }
                    }else{
                        CidRelation queryRelation = new CidRelation();
                        queryRelation.setDelFlag("0");
                        queryRelation.setBindType("0");
                        queryRelation.setIsConfirm("0");
                        queryRelation.setPowerStationId(power.getId());
                        List<CidRelation> relationList=relationService.selectCidRelationList(queryRelation);
                        boolean isCut=false;
                        System.out.println("relationList.size():"+relationList.size());
                        for(CidRelation re:relationList){
                            System.out.println("re.id:"+re.getId());
                            Long maxId=heartService.selectMaxLoginHeartByCid(re.getCid());
                            System.out.println("maxId:"+maxId);
                            if(maxId !=null){
                                CidLoginHeart lh=heartService.selectCidLoginHeartById(maxId);
                                Date dateNowHeart = new Date();
                                String heartBeginDate= sdf.format(dateNowHeart) ;
                                System.out.println("beginTime:"+heartBeginDate);
                                System.out.println("plant:"+power.getId()+",Cid:"+lh.getCid()+",LastTime:"+sdf.format(lh.getCreateDate()));
                                String ymd=sdf.format(lh.getCreateDate()).substring(0,10);
                                String beginTimeYmd=heartBeginDate.substring(0,10);
                                System.out.println("ymd:"+ymd);
                                System.out.println("beginTimeYmd:"+beginTimeYmd);
                                if(ymd.equals(beginTimeYmd)){
                                    long beginHeartMillisecond = dateNowHeart.getTime();
                                    long endHeartMillisecond = lh.getCreateDate().getTime();
                                    long jianHeart=beginHeartMillisecond-endHeartMillisecond;
                                    if(!(jianHeart>1000*60*30)&&jianHeart>0){
                                        //power.setEnergyStatus("5");
                                        System.out.println("有心跳了");
                                        //powerStation.setEnergyStatus("PowerCut");
                                        //break;
                                        cut++;
                                    }
                                }else{
                                    offline++;
                                    System.out.println("没心跳了");
                                }
                            }
                        }


                    }
                }else{
                    offline++;
                }
            }else{
//                if(!power.getEnergyStatus().equals("2")){
//                    offline++;
//                }else{
                    build++;
//                }
                //offline++;
            }


            countEnergy=countEnergy+Float.parseFloat(power.getEnergyTotal());
            System.out.println("energyTotal:"+power.getEnergyTotal());
            if(power.getEnergyStatus().equals("2")||power.getEnergyStatus()=="2"){
                buildCount++;
            }
            System.out.println("power.getEnergyTotalDay()===================="+power.getEnergyTotalDay());
            todayEnergy = todayEnergy + Float.parseFloat(power.getEnergyTotalDay());
            currentPower = currentPower + Float.parseFloat(power.getEnergyCurrentPower());
            monthEnergy = monthEnergy + Float.parseFloat(power.getEnergyTotalMonth());
            countCapacity = countCapacity + power.getEnergyCapacity();
            yearEnergy = yearEnergy + Float.parseFloat(power.getEnergyTotalYear());
        }
        //String sumEnergy=cidDataService.getSumEnergyByLoginUser(deptId);
//        String sumTodayEnergy= cidDataService.getSumTodayEnergyByLoginUser(deptId);

        AjaxResult ajax = AjaxResult.success();
        ajax.put("countEnergy",String.valueOf(countEnergy));
        ajax.put("todayEnergy",todayEnergy);
        ajax.put("monthEnergy",monthEnergy);
        ajax.put("currentPower",currentPower);
        ajax.put("yearEnergy",yearEnergy);
        ajax.put("buildCount",buildCount);
        ajax.put("offline",offline);
        ajax.put("online",online);
        ajax.put("inactive",inactive);
        ajax.put("error",error);
        ajax.put("count",count);
        ajax.put("cut",cut);
        ajax.put("countCapacity",countCapacity);
        ajax.put("build",build);

        //其他数据为null
        return ajax;
    }

    @GetMapping("/equptInfo")
    public AjaxResult equptInfo(Long deptId) throws ParseException {

        CidPowerstationinfo query=new CidPowerstationinfo();
        //增加判断，如果dept=100 查询所有
        if(deptId!=100){
            query.setEnergyDeptId(deptId);
        }

        int online=0;
        int offline=0;
        int inactive=0;
        int error=0;
        List<CidRelation> relationList=relationService.getEquptStatuCount(deptId);
        for(int i=0;i<relationList.size();i++){
            Map<String,Object> map= (Map<String, Object>) relationList.get(i);
//            if(map.get("status").equals("0")){
//                online++;
//            }else if(map.get("status").equals("1")){
//                inactive++;
//            }else if(map.get("status").equals("3")){
//                offline++;
//            }
            if(map.get("lastErrorCode")!=null){
                if(!map.get("lastErrorCode").equals("0000")&&!map.get("lastErrorCode").equals("0200")){
                    error++;
                }
            }
            if(map.get("lastUpdate").equals("0")){
                offline++;
            }

            SimpleDateFormat format=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
            if(map.get("lastUpdate")!=null&&!map.get("lastUpdate").equals("0")){
                String beginTime = format.format(new Date());
                String endTime = (String) map.get("lastUpdate");

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

        AjaxResult ajax = AjaxResult.success();
        ajax.put("count",relationList.size());
        ajax.put("online",online);
        ajax.put("offline",offline);
        ajax.put("error",error);
        //其他数据为null
        return ajax;
    }

    @GetMapping("/psInfo")
    public AjaxResult psInfo(){

        CidPowerstationinfo query=new CidPowerstationinfo();

        query.setEnergyDeptId(getLoginUser().getDeptId());

        List<CidPowerstationinfo> resultList=powerstationinfoService.selectCidPowerstationinfoList(query);
        for(CidPowerstationinfo plant:resultList){
            plant.setEnergyDeptName(deptService.selectDeptById(plant.getEnergyDeptId()).getDeptName());
        }
        AjaxResult ajaxResult=AjaxResult.success(resultList);
//        if(resultList.size()>0){
//            ajaxResult.put("plant",resultList.get(0));
//        }

        return ajaxResult;
    }

    @GetMapping("/dayPower")
    public AjaxResult dayPower(){
        LoginUser user =getLoginUser();

        SimpleDateFormat sdfHm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR,-8);
        String searchDate = sdf.format(calendar.getTime());
        String now= sdfHm.format(new Date());
        List<CidData> resultList = cidDataService.selectLofPower(searchDate,user.getDeptId());
        TreeMap<String,String> dataMap=new TreeMap<>();
        TreeMap<String,String> resultMap=new TreeMap<>();
        if(resultList.size()>0){
            for(Object obj:resultList){
                String s = JSON.toJSONString(obj);
                CidData data=JSON.parseObject(s,CidData.class);
                dataMap.put(sdfHm.format(data.getCreateDate()),data.getPower());
            }
            resultMap=eightHour(now,dataMap);
        }else{
            resultMap=eightHour(now,null);
        }
        System.out.println(resultMap.size());
        List<CidData> returnList= new ArrayList<>();
        for(String s:resultMap.keySet()){
            CidData data = new CidData();
            data.setDate(s);
            data.setPower(resultMap.get(s));
            returnList.add(data);
        }

        System.out.println("now:"+now);

        AjaxResult ajaxResult=AjaxResult.success(returnList);
        return ajaxResult;
    }

    @GetMapping("/daysEnergy")
    public AjaxResult daysEnergy(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = new Date();

        List<CidDayData> resultList = dayService.selectLofEnergy(getLoginUser().getDeptId());
        AjaxResult ajaxResult=AjaxResult.success(resultList);
        return ajaxResult;
    }

    @GetMapping("/countPower")
    public AjaxResult countPower(Long deptId){


        AjaxResult ajaxResult=AjaxResult.success();
        return ajaxResult;
    }

    public static TreeMap<String,String> eightHour(String time, TreeMap<String, String> dataMap){
        TreeMap<String,String> hmMap= new TreeMap<>();
        String date=time.substring(0,10);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(!time.equals("")){
            int hour = Integer.parseInt(time.substring(time.length()-5,time.length()-3));
            int minute = Integer.parseInt(time.substring(time.length()-2,time.length()));
            int endHour = hour - 8;
            if(endHour<0){
                endHour = 24+endHour;
                int addCount = 0;
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE,-1);
                String date_1 = sdf.format(calendar.getTime());
                for(int i = endHour;i<24;i++){
                    addCount ++;
                    hmMap.put(date_1.substring(0,10)+" "+i+":00","0");
                    hmMap.put(date_1.substring(0,10)+" "+i+":20","0");
                    hmMap.put(date_1.substring(0,10)+" "+i+":40","0");

                }
                for(int j = 0 ;j<8-addCount;j++){
                    System.out.println("j:"+j+",hour:"+hour+",j==hour:"+(j==hour));
                    if(j!=hour){
                        hmMap.put(date+" "+"0"+j+":00","0");
                        hmMap.put(date+" "+"0"+j+":20","0");
                        hmMap.put(date+" "+"0"+j+":40","0");
                    }else{
                        if(minute<20){
                            hmMap.put(date+" "+"0"+j+":00","0");
                        }else if(minute>20&&minute<40){
                            hmMap.put(date+" "+"0"+j+":00","0");
                            hmMap.put(date+" "+"0"+j+":20","0");
                        }else{
                            hmMap.put(date+" "+"0"+j+":00","0");
                            hmMap.put(date+" "+"0"+j+":20","0");
                            hmMap.put(date+" "+"0"+j+":40","0");
                        }
                    }

                }
                //System.out.println(addCount);


            }else{
                if(endHour+1+8>24){
                    System.out.println("endHour:"+endHour);
                }

                for(int i = endHour+1;i<=hour;i++){
                    System.out.println("i:"+i+",hour:"+hour+",i==hour:"+(i==hour));
                    if(i<10){
                        if(i==hour&&minute<20){
                            hmMap.put(date+" "+"0"+i+":00",null);
                        }else if(i==hour&&minute>=20&&minute<40){
                            hmMap.put(date+" "+"0"+i+":00",null);
                            hmMap.put(date+" "+"0"+i+":20",null);
                        }else if(i==hour&&minute>=40&&minute<60){
                            hmMap.put(date+" "+"0"+i+":00",null);
                            hmMap.put(date+" "+"0"+i+":20",null);
                            hmMap.put(date+" "+"0"+i+":40",null);
                        }else if(i!=hour){
                            hmMap.put(date+" "+"0"+i+":00",null);
                            hmMap.put(date+" "+"0"+i+":20",null);
                            hmMap.put(date+" "+"0"+i+":40",null);
                        }
                    }else{
                        if(i==hour&&minute<20){
                            hmMap.put(date+" "+i+":00",null);
                        }else if(i==hour&&minute>=20&&minute<40){
                            hmMap.put(date+" "+i+":00",null);
                            hmMap.put(date+" "+i+":20",null);
                        }else if(i==hour&&minute>=40&&minute<60){
                            hmMap.put(date+" "+i+":00",null);
                            hmMap.put(date+" "+i+":20",null);
                            hmMap.put(date+" "+i+":40",null);
                        }else if(i!=hour){
                            hmMap.put(date+" "+i+":00",null);
                            hmMap.put(date+" "+i+":20",null);
                            hmMap.put(date+" "+i+":40",null);
                        }
                    }

                }
            }
            if(dataMap!=null){
                for(String s: hmMap.keySet()){
                    System.out.println("time:"+s+", value:"+hmMap.get(s));
                    if(dataMap.containsKey(s)){
                        hmMap.put(s,dataMap.get(s));
                    }
                }
            }

            //System.out.println(endHour);
        }
        return hmMap;
    }

    public static void main(String[] args){
        String time="2022-10-13 02:24";
        String date=time.substring(0,10);
        TreeMap<String,String> hmMap= new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if(!time.equals("")){
            int hour = Integer.parseInt(time.substring(time.length()-5,time.length()-3));
            int minute = Integer.parseInt(time.substring(time.length()-2,time.length()));
            int endHour = hour - 8;
            if(endHour<0){
                endHour = 24+endHour;
                int addCount = 0;
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE,-1);
                String date_1 = sdf.format(calendar.getTime());
                System.out.println(date_1);
                for(int i = endHour;i<24;i++){
                    addCount ++;
                    hmMap.put(date_1.substring(0,10)+" "+i+":00","0");
                    hmMap.put(date_1.substring(0,10)+" "+i+":20","0");
                    hmMap.put(date_1.substring(0,10)+" "+i+":40","0");
                }
                for(int j = 0 ;j<8-addCount;j++){
                    if(j!=hour){
                        hmMap.put(date+" "+"0"+j+":00","0");
                        hmMap.put(date+" "+"0"+j+":20","0");
                        hmMap.put(date+" "+"0"+j+":40","0");
                    }else{
                        if(minute<20){
                            hmMap.put(date+" "+"0"+j+":00","0");
                        }else if(minute>20&&minute<40){
                            hmMap.put(date+" "+"0"+j+":00","0");
                            hmMap.put(date+" "+"0"+j+":20","0");
                        }else{
                            hmMap.put(date+" "+"0"+j+":00","0");
                            hmMap.put(date+" "+"0"+j+":20","0");
                            hmMap.put(date+" "+"0"+j+":40","0");
                        }
                    }

                }
                //System.out.println(addCount);


            }else{
                if(endHour+1+8>24){
                    System.out.println("endHour:"+endHour);
                }
                for(int i = endHour+1;i<=hour;i++){
                    if(i<10){
                        hmMap.put(date+" "+"0"+i+":00",null);
                        hmMap.put(date+" "+"0"+i+":20",null);
                        hmMap.put(date+" "+"0"+i+":40",null);
                    }else{
                        hmMap.put(date+" "+i+":00",null);
                        hmMap.put(date+" "+i+":20",null);
                        hmMap.put(date+" "+i+":40",null);
                    }

                }
            }

            for(String s: hmMap.keySet()){
                System.out.println("time:"+s+", value:"+hmMap.get(s));
            }
            //System.out.println(endHour);

        }
    }
}
