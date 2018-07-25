package com.cdc.cdccmc.controller.web.car;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.CarShipper;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.ShipperService;

/** 
 * 承运商管理
 * @author Jerry
 * @date 2017-1-3
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class ShipperController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ShipperController.class);
	@Autowired
	private ShipperService shipperService;
			
	//页面列表
	@RequestMapping("/shipper/pagingCarShipper")
	public Paging pagingCarShipper(@ModelAttribute(SysConstants.SESSION_USER) SystemUser systemUser,CarShipper carShipper,Paging paging){
		paging = shipperService.pagingCarShipper(paging,carShipper);
		return paging;
	}

	/**
	 * 添加承运商
	 * @param sessionUser
	 * @param carShipper
	 * @return
	 */
	@RequestMapping("/shipper/addCarShipper")
	public AjaxBean addCarShipper(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,CarShipper carShipper){
		AjaxBean ajaxBean = new AjaxBean();
		if(StringUtils.isBlank(carShipper.getShipperName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("承运商名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		CarShipper findShipper = shipperService.listByShipperName(carShipper.getShipperName());
		if (null != findShipper){ 
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("承运商名称["+carShipper.getShipperName()+"]"+StatusCode.STATUS_302_MSG);
			return ajaxBean;
		}
		if(StringUtils.isNotEmpty(carShipper.getShipperContactNumber().trim())) {
			if(!(carShipper.getShipperContactNumber().matches(SysConstants.REGEX_SHIPPER_CONTAINER_NUMBER))){
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("联系电话只能是数字,空格和'-'的组合且长度为1到20位");
				return ajaxBean;
			}
		}
		
		//如果承运商名称不存在，则可以新增
		ajaxBean = shipperService.addCarShipper(sessionUser,carShipper);
		return ajaxBean;
	}

	/**
	 * 更新承运商
	 * @param sessionUser
	 * @param carShipper
	 * @param ajaxBean
	 * @return
	 */
	@RequestMapping("/shipper/updateCarShipper")
	public AjaxBean updateCarShipper(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,CarShipper carShipper,AjaxBean ajaxBean){
		if (StringUtils.isBlank(carShipper.getShipperName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("承运商名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		/*暂且保留
		 * List<CarShipper> shipperList = shipperService.listByShipperName(carShipper.getShipperName());
		if(CollectionUtils.isNotEmpty(shipperList) && !shipperList.get(0).getShipperId().equals(carShipper.getShipperId())){ //如果承运商名称已存在
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("承运商名称["+carShipper.getShipperName()+"]"+StatusCode.STATUS_302_MSG);
			return ajaxBean;
		}*/
		if(StringUtils.isNotEmpty(carShipper.getShipperContactNumber().trim())) {
			if(!(carShipper.getShipperContactNumber().matches(SysConstants.REGEX_SHIPPER_CONTAINER_NUMBER))){
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("联系电话只能是数字,空格和'-'的组合且长度为1到20位");
				return ajaxBean;
			}
		}
		ajaxBean = shipperService.updateCarShipper(sessionUser,carShipper);
		return ajaxBean;
	}

	/**
	 * 删除承运商
	 * @param shipperId
	 * @return
	 */
	@RequestMapping("/shipper/delCarShipper")
	public AjaxBean delCarShipper(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, String shipperId){
		AjaxBean ajaxBean = new AjaxBean();
		if (StringUtils.isBlank(shipperId)){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("承运商Id"+StatusCode.STATUS_305_MSG);
		}else {
			ajaxBean = shipperService.delCarShipper(sessionUser,shipperId);
		}
		return ajaxBean;
	}

	/**
	 * 根据条件搜索承运商
	 * @param sessionUser
	 * @return
	 */
	@RequestMapping("/shipper/listAllShipper")
	public AjaxBean listAllShipper(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<CarShipper> list = shipperService.listAllShipper();
		ajaxBean.setList(list);
		return ajaxBean;
	}
}
