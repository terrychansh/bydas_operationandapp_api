package com.benyi.energy.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.benyi.common.core.domain.entity.SysDept;
import com.benyi.common.core.domain.entity.SysUser;
import com.benyi.common.core.domain.model.LoginUser;
import com.benyi.framework.web.domain.server.Sys;
import com.benyi.framework.web.service.TokenService;
import com.benyi.system.service.ISysDeptService;
import com.benyi.system.service.ISysUserService;
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
import com.benyi.energy.domain.CidWorkOrder;
import com.benyi.energy.service.ICidWorkOrderService;
import com.benyi.common.utils.poi.ExcelUtil;
import com.benyi.common.core.page.TableDataInfo;

/**
 * 工单Controller
 * 
 * @author wuqiguang
 * @date 2022-08-11
 */
@RestController
@RequestMapping("/energy/order")
public class CidWorkOrderController extends BaseController
{
    @Autowired
    private ICidWorkOrderService cidWorkOrderService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询工单列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(CidWorkOrder cidWorkOrder)
    {
        startPage();
        //当前登陆用户 deptId过滤
        LoginUser loginUser = getLoginUser();
        //SysUser user=userService.selectUserById(loginUser.getUserId());
        Long deptId = loginUser.getDeptId();
        SysDept dept=deptService.selectDeptById(deptId);
//        if(deptId==100){
//            cidWorkOrder.setWorkOrderSentTo(100l);
//        }else{
            cidWorkOrder.setCreateDeptId(deptId);
//        }
        List<CidWorkOrder> list = cidWorkOrderService.selectCidWorkOrderList(cidWorkOrder);
        for (CidWorkOrder order:list) {
            order.setSentDeptName(deptService.selectDeptById(order.getWorkOrderSentTo()).getDeptName());
        }
        return getDataTable(list);
    }

    /**
     * 查询工单列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:order:replayList')")
    @GetMapping("/replayList")
    public TableDataInfo replayList(HttpServletRequest request,CidWorkOrder cidWorkOrder)
    {
        startPage();
        //当前登陆用户 deptId过滤
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user=userService.selectUserById(loginUser.getUserId());
        Long deptId = user.getDeptId();
        if(deptId!=100){//厂家可以看所有工单
            cidWorkOrder.setWorkOrderSentTo(deptId);
        }
        List<CidWorkOrder> list = cidWorkOrderService.selectCidWorkOrderList(cidWorkOrder);
        for (CidWorkOrder order:list) {
            order.setCreateDeptName(deptService.selectDeptById(order.getCreateDeptId()).getDeptName());
        }
        return getDataTable(list);
    }

//    @PreAuthorize("@ss.hasPermi('energy:order:deptTree')")
    @GetMapping("/deptTree")
    public AjaxResult getDeptTree(){
        LoginUser loginUser = getLoginUser();
        Long deptId = loginUser.getDeptId();
        SysDept queryDept=new SysDept();
        queryDept.setDeptType("3");
        List<SysDept> deptList=deptService.selectDeptList(queryDept);
        return AjaxResult.success(deptList);
    }

    /**
     * 导出工单列表
     */
//    @PreAuthorize("@ss.hasPermi('energy:order:export')")
    @Log(title = "工单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CidWorkOrder cidWorkOrder)
    {
        List<CidWorkOrder> list = cidWorkOrderService.selectCidWorkOrderList(cidWorkOrder);
        ExcelUtil<CidWorkOrder> util = new ExcelUtil<CidWorkOrder>(CidWorkOrder.class);
        util.exportExcel(response, list, "工单数据");
    }

    /**
     * 获取工单详细信息
     */
//    @PreAuthorize("@ss.hasPermi('energy:order:query')")
    @GetMapping(value = "/{workOrderId}")
    public AjaxResult getInfo(@PathVariable("workOrderId") Long workOrderId)
    {
        return AjaxResult.success(cidWorkOrderService.selectCidWorkOrderByWorkOrderId(workOrderId));
    }

    /**
     * 新增工单
     */
//    @PreAuthorize("@ss.hasPermi('energy:order:add')")
    @Log(title = "工单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CidWorkOrder cidWorkOrder,HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        SysUser user=userService.selectUserById(loginUser.getUserId());
        Long deptId = user.getDeptId();
        SysDept dept=deptService.selectDeptById(deptId);
        //设置创建工单机构ID
        cidWorkOrder.setCreateBy(String.valueOf(deptId));
        cidWorkOrder.setCreateDeptId(deptId);
        //设置接收工单机构ID
//        if(deptId==100){
//            cidWorkOrder.setWorkOrderSentTo(deptId);
//        }else{
//            cidWorkOrder.setWorkOrderSentTo(dept.getParentId());
//        }

        return toAjax(cidWorkOrderService.insertCidWorkOrder(cidWorkOrder));
    }

    /**
     * 修改工单
     */
//    @PreAuthorize("@ss.hasPermi('energy:order:edit')")
    @Log(title = "工单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CidWorkOrder cidWorkOrder)
    {
        return toAjax(cidWorkOrderService.updateCidWorkOrder(cidWorkOrder));
    }

    /**
     * 删除工单
     */
//    @PreAuthorize("@ss.hasPermi('energy:order:remove')")
    @Log(title = "工单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{workOrderIds}")
    public AjaxResult remove(@PathVariable Long[] workOrderIds)
    {
        return toAjax(cidWorkOrderService.deleteCidWorkOrderByWorkOrderIds(workOrderIds));
    }
}
