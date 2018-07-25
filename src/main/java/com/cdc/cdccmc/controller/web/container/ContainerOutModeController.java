package com.cdc.cdccmc.controller.web.container;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.ContainerService;

/** 
 * 过时器具
 * @author Jerry
 * @date 2018-01-15
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class ContainerOutModeController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContainerOutModeController.class);

	@Autowired
	private ContainerService containerService;



	/**
	 * 添加过时器具
	 *
	 * 1.验证器具列表是否存在
	 * 2.验证器具是否存在维修列表
	 * @param sessionUser 当前登录用户
	 * @param container
	 * @return
	 */
	@RequestMapping("/containerOutMode/addOutmode")
	public AjaxBean addOut(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Container container){
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
        if (StringUtils.isBlank(container.getContractNumber())){ //合同号
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("合同号"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
        if (StringUtils.isBlank(container.getReceiveNumber())){ //领用单号
            ajaxBean.setStatus(StatusCode.STATUS_305);
            ajaxBean.setMsg("领用单号"+StatusCode.STATUS_305_MSG);
            return ajaxBean;
        }
		//判断这个器具是否是过时器具
		ajaxBean = containerService.addOutMode(ajaxBean,sessionUser,container);
		return ajaxBean;
	}
}
