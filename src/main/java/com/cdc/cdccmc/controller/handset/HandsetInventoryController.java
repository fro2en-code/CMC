package com.cdc.cdccmc.controller.handset;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.CdccmcApplication;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.Area;
import com.cdc.cdccmc.domain.InventoryMain;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.InventoryDetailService;
import com.cdc.cdccmc.service.InventoryMainService;
import com.cdc.cdccmc.service.basic.AreaService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 手持机端--器具盘点
 *
 * @author
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class HandsetInventoryController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HandsetInventoryController.class); 
    @Autowired
    private AreaService areaService;
    @Autowired
    private InventoryMainService inventoryMainService;
    @Autowired
    private InventoryDetailService inventoryDetailService;

    /**
     * 查询当前仓库是否有正在进行中的盘点任务,如果有则返回当前仓库库区信息以及盘点单信息
     *
     * @param sessionUser
     * @return
     */
    @RequestMapping("/handsetInventory/getCurrentInventoryMain")
    public AjaxBean getCurrentInventoryMain(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser) {
        AjaxBean ajaxBean = new AjaxBean();
        SystemOrg inventoryOrg = sessionUser.getCurrentSystemOrg();
        // 查询盘点仓库下状态为“盘点中”盘点单
        InventoryMain inventoryMain = inventoryMainService.listNotFinishInventoryMain(inventoryOrg);
        if (null == inventoryMain) { // 如果当前仓库下不存在“盘点中”盘点单，则不能进行盘点操作
            ajaxBean.setStatus(StatusCode.STATUS_320);
            ajaxBean.setMsg("[" + inventoryOrg.getOrgName() + "]" + StatusCode.STATUS_320_MSG);
            return ajaxBean;
        }
        // 获取库区
        List<Area> listArea = areaService.listAllArea();
        ajaxBean.setStatus(StatusCode.STATUS_200);
        ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
        ajaxBean.setList(listArea);
        ajaxBean.setBean(inventoryMain);
        return ajaxBean;
    }

    /**
     * 进行盘点操作
     *
     * @param sessionUser
     * @param inventoryId
     * @param areaId        盘点区域
     * @param epcIdList
     * @return
     */
    @RequestMapping("/handsetInventory/submitInventory")
    public AjaxBean submitInventory(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
                                    String inventoryId, String areaId, @RequestParam(value = "epcIdList[]") List<String> epcIdList) {
    	LOG.info("请求URL：/handsetInventory/submitInventory，inventoryId="+inventoryId + ",areaId="+areaId+",epcIdList="+JSONObject.toJSONString(epcIdList));
        AjaxBean ajaxBean = new AjaxBean();
        if (StringUtils.isBlank(inventoryId)) { // 如果当前仓库下不存在“盘点中”盘点单，则不能进行盘点操作
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("盘点单号" + StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        InventoryMain main = inventoryMainService.findInventoryMainById(inventoryId);
        //如果盘点完毕，则提交失败
        if (main.getInventoryState() == SysConstants.INTEGER_1) {
            ajaxBean.setStatus(StatusCode.STATUS_355);
            ajaxBean.setMsg("[" + inventoryId + "]" + StatusCode.STATUS_355_MSG);
            return ajaxBean;
        }
        try {
            inventoryDetailService.addInventoryDetailFromHandset(sessionUser, inventoryId, areaId, epcIdList);
        } catch (Exception e) {
        	LOG.error("发生异常：", e);
            ajaxBean.setStatus(StatusCode.STATUS_201);
            ajaxBean.setMsg(e.getMessage());
            return ajaxBean;
        }
        ajaxBean.setStatus(StatusCode.STATUS_200);
        ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
        return ajaxBean;
    }

}
