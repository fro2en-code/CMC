
package com.cdc.cdccmc.controller.app;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.service.sys.SystemOrgService;

/**
 * app端组织机构
 * 
 * @author Administrator
 *
 */

@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class AppOrgController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AppContainerController.class);

	@Autowired
	private SystemOrgService systemOrgService;

	@RequestMapping("/appOrgController/listOrg")
	public AjaxBean listOrg() {
		AjaxBean ajaxBean = new AjaxBean();
		List<SystemOrg> list = systemOrgService.listAllOrg();
		if(CollectionUtils.isEmpty(list)){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg(StatusCode.STATUS_201_MSG);
			return ajaxBean;
		}
		ajaxBean.setList(list);
		ajaxBean.setStatus(StatusCode.STATUS_200);
		ajaxBean.setMsg(StatusCode.STATUS_200_MSG);
		return ajaxBean;
	}
}
