package com.cdc.cdccmc.controller.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.InventoryDetail;
import com.cdc.cdccmc.domain.InventoryMain;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.InventoryDetailService;
import com.cdc.cdccmc.service.InventoryMainService;

/**
 * 盘点接口
 * 
 * @author shch
 *
 */

@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class AppInventoryController {
	@Autowired
	private InventoryMainService inventoryMainService;
	@Autowired
	private InventoryDetailService inventoryDetailService ;
	
	/**
	 * 增加盘点单明细
	 * @param sessionUser
	 * @param areaId 盘点区域编码
	 * @param epcIdList 盘点的epcId列表
	 * @param inventory 盘点单好
	 * @return
	 *//*
	@RequestMapping("/appInventory/addInventoryDetail")
	public AjaxBean addInventoryDetail(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,
			String areaId,String areaName,@RequestParam(value = "epcIds[]",required=false) List<String> epcIdList,String inventoryId) {
		AjaxBean ajaxBean = new AjaxBean();
		//1、完成盘点  -- 在盘点单下面产生明细
		for(String epcId : epcIdList){
			InventoryDetail inventoryDetail = new InventoryDetail();
			inventoryDetail.setEpcId(epcId);//epc编号
			inventoryDetail.setAreaId(areaId);//区域代码
			inventoryDetail.setAreaName(areaName);//区域名称
			inventoryDetail.setInventoryId(inventoryId);//盘点单号
			
			//1、盘点单明细中，已经有相同盘点单号和相同epc编号的数据，此时更新已有数据
//			if(inventoryDetailService.listInventoryDetail(inventoryDetail).size()>0){
//				ajaxBean = inventoryDetailService.updateInventoryDetail(sessionUser, inventoryDetail);
//			}else{//2、其他情况，新增盘点单明细
//				ajaxBean = inventoryDetailService.addInventoryDetail(sessionUser, inventoryDetail);
//			}
		}
		return ajaxBean;
	}*/
	
	
}
