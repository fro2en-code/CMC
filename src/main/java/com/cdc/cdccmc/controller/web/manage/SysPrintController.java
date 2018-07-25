package com.cdc.cdccmc.controller.web.manage;


import java.util.List;

import com.cdc.cdccmc.controller.Paging;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.sys.SysPrint;
import com.cdc.cdccmc.domain.sys.SysUserPrint;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.sys.SysPrintService;

/**
 * 打印机设置
 * @author LiCao
 * @date 2018-05-31
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class SysPrintController {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SysPrintController.class);
    @Autowired
    private SysPrintService sysPrintService;

    /**
     * 打印机名称列表
     * @param sessionUser
     * @return
     */

    @RequestMapping(value = "/sysPrint/listAllPrintName")
    public AjaxBean listAllPrintName(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser){
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        List<SysPrint> printNameList = sysPrintService.listAllPrintName(sessionUser);
        ajaxBean.setList(printNameList);
        return ajaxBean;
    }

    /**
     * 查询打印机-按仓库ID
     *
     * @param sessionUser
     * @return
     */
    @RequestMapping(value = "/sysPrint/queryPrinter")
    public Paging queryPrinter(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String orgId, Paging paging){

        LOG.info("---queryPrinter   orgId="+orgId+"   sessionUser.getFilialeSystemOrgIds="+sessionUser.getFilialeSystemOrgIds());
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        //List<SysPrint> printNameList = sysPrintService.queryPrinter(paging,orgId);
        if(StringUtils.isEmpty(orgId) || StringUtils.isBlank(orgId))
        {
            orgId = sessionUser.getFilialeSystemOrgIds();
        }
        else
        {
            orgId = SysConstants.DYH + orgId + SysConstants.DYH;
        }
        paging = sysPrintService.queryPrinter(paging,orgId);
        //ajaxBean.setList(printNameList);
        return paging;
    }
    /**
     * 新增打印设置
     */
    @RequestMapping(value = "/sysPrint/addPrinter")
    public AjaxBean addPrinter(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SysPrint sysPrint){
        return sysPrintService.addPrinter(sessionUser, sysPrint);
    }
    /**
     * 修改打印设置
     */
    @RequestMapping(value = "/sysPrint/updatePrinter")
    public AjaxBean updatePrinter(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SysPrint sysPrint){
        return sysPrintService.updatePrinter(sessionUser, sysPrint);
    }
    /**
     * 删除打印设置
     */
    @RequestMapping(value = "/sysPrint/deletePrinter")
    public AjaxBean deletePrinter(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SysPrint sysPrint){
        return sysPrintService.deletePrinter(sessionUser, sysPrint);
    }

    /**
     * 新增打印设置
     */
    @RequestMapping(value = "/sysPrint/addPrint")
    public AjaxBean addPrint(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, SysPrint sysPrint,AjaxBean ajaxBean, SysUserPrint sysUserPrint,String printAccount){
        if(StringUtils.isBlank(sysPrint.getPrintName())){
            ajaxBean = sysPrintService.deletePrint(ajaxBean,sessionUser,printAccount);
            return ajaxBean;
        }
        ajaxBean = sysPrintService.addPrint(sessionUser, sysPrint, printAccount);
        return  ajaxBean;
    }
    /**
     * 查询指定帐号，指定仓库下的打印机设置
     */
    @RequestMapping(value = "/sysPrint/queryPrintNameByOrgAndAccount")
    public AjaxBean queryPrintNameByOrgAndAccount(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String printAccount){
        AjaxBean ajaxBean = AjaxBean.SUCCESS();
        SysUserPrint sysUserPrint = sysPrintService.queryPrintNameByOrgAndAccount(printAccount,sessionUser.getCurrentSystemOrg().getOrgId());
        ajaxBean.setBean(sysUserPrint);
        return ajaxBean;
    }



}
