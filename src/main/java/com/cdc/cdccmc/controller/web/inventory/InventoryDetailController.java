
package com.cdc.cdccmc.controller.web.inventory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.InventoryDetail;
import com.cdc.cdccmc.domain.InventoryMain;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateService;
import com.cdc.cdccmc.service.ContainerService;
import com.cdc.cdccmc.service.InventoryDetailService;
import com.cdc.cdccmc.service.InventoryMainService;
import com.cdc.cdccmc.service.report.ContainerLostService;


/**
 * 盘点明细
 * @author shch-pc
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class InventoryDetailController {
	private Logger LOG = Logger.getLogger(InventoryDetailController.class);
	
	@Autowired
	private InventoryDetailService inventoryDetailService;
	@Autowired
	private ContainerLostService containerLostService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private InventoryMainService inventoryMainService;
	
	 /**
     * 盘点单明细（差异）报表
     * 
     * @param sessionUser 
     * @param paging
     * @param inventoryId 盘点编号
     * @return
     */
    @RequestMapping("/inventoryDetail/pagingInventoryDetail")
    public Paging pagingInventoryDetail(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging,String inventoryId, Integer isHaveDifferent) {
        paging = inventoryDetailService.pagingInventoryDetail(paging, inventoryId,sessionUser,isHaveDifferent);
        return paging;
    }

	/**
	 * 盘点单统计表
	 *
	 * @param sessionUser
	 * @param paging
	 * @param inventoryId 盘点编号
	 * @return
	 */
	@RequestMapping("/inventoryDetail/pagingInventoryDetailSum")
	public Paging pagingInventoryDetailSum(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Paging paging,String inventoryId, String differentNumber) {
		paging = inventoryDetailService.pagingInventoryDetailSum(paging, inventoryId,sessionUser,differentNumber);
		return paging;
	}
    /**
     * 器具丢失确认
     * 
     * @param sessionUser
     * @param inventoryDetail
     * @return
     */
    @RequestMapping("/inventoryDetail/confirmLoss")
    public AjaxBean confirmLoss(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String inventoryId, String epcId,String lostRemark){
    	AjaxBean ajaxBean = AjaxBean.SUCCESS();
    	//通过盘点编号查这个对象
    	InventoryMain iMain= inventoryMainService.findInventoryMainById(inventoryId);
    	String iOrgName = iMain.getInventoryOrgName();
    	if(!iOrgName.equals(sessionUser.getCurrentSystemOrg().getOrgName())) {
    		ajaxBean.setStatus(StatusCode.STATUS_353);
			ajaxBean.setMsg(StatusCode.STATUS_353_MSG+"["+ iOrgName +"],请重试！");
			return ajaxBean;
    	}
    	return containerLostService.addContainerLost(sessionUser, ajaxBean,inventoryId,epcId,lostRemark);
    }

	/**
	 * 修改区域
	 * @param sessionUser
	 * @return
	 */
	@RequestMapping("/inventoryDetail/modifyArea")
	public AjaxBean modifyArea(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,InventoryDetail detail){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if (StringUtils.isBlank(detail.getInventoryDetailId())){
			ajaxBean.setMsg("盘点明细主键ID"+StatusCode.STATUS_305_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_305);
			return ajaxBean;
		}
		if (StringUtils.isBlank(detail.getInventoryId())){
			ajaxBean.setMsg("盘点编号"+StatusCode.STATUS_305_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_305);
			return ajaxBean;
		}
		if (StringUtils.isBlank(detail.getEpcId())){
			ajaxBean.setMsg("EPC编号"+StatusCode.STATUS_305_MSG);
			ajaxBean.setStatus(StatusCode.STATUS_305);
			return ajaxBean;
		}
		//通过盘点编号查这个对象
    	InventoryMain iMain= inventoryMainService.findInventoryMainById(detail.getInventoryId());
    	String iOrgName = iMain.getInventoryOrgName();
		if(!iOrgName.equals(sessionUser.getCurrentSystemOrg().getOrgName())) {
			ajaxBean.setStatus(StatusCode.STATUS_353);
			ajaxBean.setMsg(StatusCode.STATUS_353_MSG+"["+ iOrgName +"],请重试！");
			return ajaxBean;
    	}
		InventoryDetail findDetail = inventoryDetailService.queryInventoryDetailByepcId(detail.getEpcId(),detail.getInventoryId());
		ajaxBean = inventoryDetailService.updateCirculateLatestForInventory(sessionUser,findDetail);
		return ajaxBean;
	}


	/**
     * 盘点差异处理：新增入库
     * 
     * @param systemUser
     * @param inventoryId
     * @param epcId
     * @return
     */
    @RequestMapping("/inventoryDetail/add")
    public AjaxBean add(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String inventoryId, String epcId) {
    	AjaxBean ajaxBean = new AjaxBean();
    	//通过盘点编号查这个对象
    	InventoryMain iMain= inventoryMainService.findInventoryMainById(inventoryId);
    	String iOrgName = iMain.getInventoryOrgName();
    	if(!iOrgName.equals(sessionUser.getCurrentSystemOrg().getOrgName())) {
    		ajaxBean.setStatus(StatusCode.STATUS_353);
			ajaxBean.setMsg(StatusCode.STATUS_353_MSG+"["+ iOrgName +"],请重试！");
			return ajaxBean;
    	}
    	//根据inventoryId,epcId获取InventoryDetail对象
    	InventoryDetail iDetail = inventoryDetailService.queryInventoryDetailByepcId(epcId, inventoryId);
    	//根据盘点单号查询盘点单对象
   	 	InventoryMain inventoryMain = inventoryMainService.findInventoryMainById(inventoryId);
    	//根据epc编号查询器具信息
    	 Container container = containerService.findContainerByEpcId(epcId);
 		 //添加流转记录
 		 circulateService.addCirculate(sessionUser, ajaxBean, inventoryId, epcId, container, iDetail, inventoryMain);
    	 return ajaxBean;
    	
    }

	/**
	 * 根据盘点单号查询盘点单对象
	 * @param inventoryId
	 * @return
	 */
	@RequestMapping("/inventoryDetail/queryByInventoryId")
	public AjaxBean queryByInventoryId(String inventoryId){
		AjaxBean ajaxBean = new AjaxBean();
		InventoryMain inventoryMain = inventoryMainService.findInventoryMainById(inventoryId);
		if(inventoryMain == null){
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("单号  ["+ inventoryId +"] 不存在");
		}
		ajaxBean.setBean(inventoryMain);
		return ajaxBean;
	}

	/**
	 * [盘点单详情]页面，提交盘点数量按钮监听事件
	 * @param ajaxBean
	 * @param sessionUser
	 * @param inventoryDetailId 盘点明细ID
	 * @param inventoryNumber 盘点到数量
	 * @return
	 */
	@RequestMapping("/inventoryDetail/submitInventoryNumber")
	public AjaxBean submitInventoryNumber(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String inventoryDetailId,Integer inventoryNumber){
		AjaxBean ajaxBean = new AjaxBean();
		ajaxBean = inventoryDetailService.submitInventoryNumber(ajaxBean,sessionUser,inventoryDetailId,inventoryNumber);
		return ajaxBean;
	}
}
