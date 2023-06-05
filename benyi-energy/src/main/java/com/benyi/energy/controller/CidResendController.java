package com.benyi.energy.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.benyi.common.core.domain.model.LoginUser;
import com.github.pagehelper.PageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.benyi.common.annotation.Log;
import com.benyi.common.core.controller.BaseController;
import com.benyi.common.core.domain.AjaxResult;
import com.benyi.common.enums.BusinessType;
import com.benyi.energy.domain.CidResend;
import com.benyi.energy.service.ICidResendService;
import com.benyi.common.utils.poi.ExcelUtil;
import com.benyi.common.core.page.TableDataInfo;

/**
 * 协议补发Controller
 * 
 * @author wuqiguang
 * @date 2022-09-02
 */
@RestController
@RequestMapping("/energy/resend")
public class CidResendController extends BaseController
{
    @Autowired
    private ICidResendService cidResendService;

    /**
     * 查询协议补发列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:resend:list')")
    @GetMapping("/list")
    public TableDataInfo list(String cid,Long powerStationId,Integer pageNum,Integer pageSize)
    {
        LoginUser loginUser = getLoginUser();
        PageHelper.startPage(pageNum,pageSize);
        List<CidResend> list = cidResendService.selectResendByDeptId(loginUser.getDeptId(),cid,null,powerStationId);
        return getDataTable(list);
    }

    @GetMapping("/valHasResend")
    public AjaxResult valHasResend(){
        LoginUser loginUser = getLoginUser();
        List<CidResend> resendList =cidResendService.selectResendValByDeptId(loginUser.getDeptId());
        int i =0;
        for(Object obj:resendList){
            i++;
        }
        System.out.println("resend--------------"+i);
        AjaxResult ajaxResult = AjaxResult.success();
        if(i>0){
            ajaxResult.put("resend",'1');
        }else{
            ajaxResult.put("resend",'0');
        }
        return ajaxResult;
    }

    @GetMapping("/comfirmResend")
    public AjaxResult comfirmResend(){
        LoginUser loginUser = getLoginUser();
        AjaxResult ajaxResult = AjaxResult.success(cidResendService.updateCidResendConfirm(loginUser.getDeptId()));
        return ajaxResult;
    }

    /**
     * 导出协议补发列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:resend:export')")
    @Log(title = "协议补发", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CidResend cidResend)
    {
        List<CidResend> list = cidResendService.selectCidResendList(cidResend);
        ExcelUtil<CidResend> util = new ExcelUtil<CidResend>(CidResend.class);
        util.exportExcel(response, list, "协议补发数据");
    }

    /**
     * 获取协议补发详细信息
     */
//    @PreAuthorize("@ss.hasPermi('energy:resend:query')")
    @GetMapping(value = "/{resendId}")
    public AjaxResult getInfo(@PathVariable("resendId") Long resendId)
    {
        return AjaxResult.success(cidResendService.selectCidResendByResendId(resendId));
    }

    /**
     * 新增协议补发
     */
//    @PreAuthorize("@ss.hasPermi('energy:resend:add')")
    @Log(title = "协议补发", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CidResend cidResend)
    {
        return toAjax(cidResendService.insertCidResend(cidResend));
    }

    /**
     * 修改协议补发
     */
//    @PreAuthorize("@ss.hasPermi('energy:resend:edit')")
    @Log(title = "协议补发", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CidResend cidResend)
    {
        return toAjax(cidResendService.updateCidResend(cidResend));
    }

    /**
     * 删除协议补发
     */
//    @PreAuthorize("@ss.hasPermi('energy:resend:remove')")
    @Log(title = "协议补发", businessType = BusinessType.DELETE)
	@DeleteMapping("/{resendIds}")
    public AjaxResult remove(@PathVariable Long[] resendIds)
    {
        return toAjax(cidResendService.deleteCidResendByResendIds(resendIds));
    }
}
