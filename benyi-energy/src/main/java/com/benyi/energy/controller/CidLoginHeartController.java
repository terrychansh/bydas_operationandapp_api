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
import com.benyi.energy.domain.CidLoginHeart;
import com.benyi.energy.service.ICidLoginHeartService;
import com.benyi.common.utils.poi.ExcelUtil;
import com.benyi.common.core.page.TableDataInfo;

/**
 * 登陆、心跳入库Controller
 *
 * @author wuqiguang
 * @date 2022-09-09
 */
@RestController
@RequestMapping("/energy/heart")
public class CidLoginHeartController extends BaseController
{
    @Autowired
    private ICidLoginHeartService cidLoginHeartService;

    /**
     * 查询心跳入库列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:heart:list')")
    @GetMapping("/list")
    public TableDataInfo heartList(String cid,Long powerStationId,String heartLoginType,String searchDate,Integer pageSize,Integer pageNum)
    {
        LoginUser loginUser = getLoginUser();
        //cidLoginHeart.setHeartLoginType("1");
        PageHelper.startPage(pageNum,pageSize);
        List<CidLoginHeart> list = cidLoginHeartService.selectCidLoginHeartListByQuery(cid,heartLoginType,searchDate, loginUser.getDeptId(),powerStationId);
        return getDataTable(list);
    }

    /**
     * 查询登陆入库列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:heart:list')")
    @GetMapping("/loginList")
    public TableDataInfo loginList(CidLoginHeart cidLoginHeart)
    {
        cidLoginHeart.setHeartLoginType("0");
        startPage();
        List<CidLoginHeart> list = cidLoginHeartService.selectCidLoginHeartList(cidLoginHeart);
        return getDataTable(list);
    }

    /**
     * 导出登陆、心跳入库列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:heart:export')")
    @Log(title = "登陆、心跳入库", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CidLoginHeart cidLoginHeart)
    {
        List<CidLoginHeart> list = cidLoginHeartService.selectCidLoginHeartList(cidLoginHeart);
        ExcelUtil<CidLoginHeart> util = new ExcelUtil<CidLoginHeart>(CidLoginHeart.class);
        util.exportExcel(response, list, "登陆、心跳入库数据");
    }

    /**
     * 获取登陆、心跳入库详细信息
     */
//    @PreAuthorize("@ss.hasPermi('energy:heart:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(cidLoginHeartService.selectCidLoginHeartById(id));
    }

    /**
     * 新增登陆、心跳入库
     */
//    @PreAuthorize("@ss.hasPermi('energy:heart:add')")
    @Log(title = "登陆、心跳入库", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CidLoginHeart cidLoginHeart)
    {
        return toAjax(cidLoginHeartService.insertCidLoginHeart(cidLoginHeart));
    }

    /**
     * 修改登陆、心跳入库
     */
//    @PreAuthorize("@ss.hasPermi('energy:heart:edit')")
    @Log(title = "登陆、心跳入库", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CidLoginHeart cidLoginHeart)
    {
        return toAjax(cidLoginHeartService.updateCidLoginHeart(cidLoginHeart));
    }

    /**
     * 删除登陆、心跳入库
     */
//    @PreAuthorize("@ss.hasPermi('energy:heart:remove')")
    @Log(title = "登陆、心跳入库", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(cidLoginHeartService.deleteCidLoginHeartByIds(ids));
    }
}
