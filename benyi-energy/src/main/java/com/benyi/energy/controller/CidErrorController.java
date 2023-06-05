package com.benyi.energy.controller;


import com.benyi.common.core.controller.BaseController;
import com.benyi.common.core.domain.model.LoginUser;
import com.benyi.common.core.page.TableDataInfo;
import com.benyi.energy.domain.CidData;
import com.benyi.energy.domain.CidRelation;
import com.benyi.energy.service.ICidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/energy/error")
public class CidErrorController extends BaseController {

    @Autowired
    private ICidDataService cidDataService;

//    @PreAuthorize("@ss.hasPermi('energy:error:index')")
    @GetMapping("/list")
    public TableDataInfo list(String cid,String vid,String searchDate,String mError,String mErrorCn,String mErrorCode,String sError,String sErrorCn,String sErrorCode,Long plantId)
    {
        LoginUser loginUser=getLoginUser();
        startPage();
        List<CidData> list = cidDataService.selectCidDataErrorList(loginUser.getDeptId(),cid,vid,searchDate,mError,mErrorCn,mErrorCode,sError,sErrorCn,sErrorCode,plantId);
        return getDataTable(list);
    }

}
