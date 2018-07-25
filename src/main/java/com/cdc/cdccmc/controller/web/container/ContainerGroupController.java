package com.cdc.cdccmc.controller.web.container;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.cdc.cdccmc.common.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.container.ContainerGroup;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.ContainerGroupService;
import com.cdc.cdccmc.service.LogService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
 * 器具组托
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class ContainerGroupController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
			.getLogger(ContainerGroupController.class);
	
	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private LogService logService;

	/**
	 * 列出当前所选仓库的包含所有子公司的所有组托，支持分页
	 * @param sessionUser 当前登录用户
	 * @return
	 */
	@RequestMapping("/containerGroup/pagingAllGroup")
	public Paging pagingAllGroup(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Paging paging
			,String epcId,String groupState){
		LOG.info("request URL /containerGroup/pagingAllGroup");
		paging = containerGroupService.pagingAllGroup(paging,sessionUser,epcId,groupState);
		return paging;
	}

	/**
	 * 把查询出的结果写到excel中,导出给用户
	 *
	 * @param systemUser
	 * @param startDate
	 * @param endDate
	 * @param response
	 */
	@RequestMapping("/containerGroup/expertToExcel")
	public void expertToExcel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser systemUser,String epcId,String groupState , HttpServletResponse response){
		// 建立工作簿和电子表格对象
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("sheet1");
		XSSFRow row0=sheet.createRow(0);
		row0.createCell(0).setCellValue("组托EPC编号");
		row0.createCell(1).setCellValue("EPC编号");
		row0.createCell(2).setCellValue("器具代码");
		row0.createCell(3).setCellValue("器具类型");
		row0.createCell(4).setCellValue("器具名称");
		row0.createCell(5).setCellValue("组托状态");
		row0.createCell(6).setCellValue("创建人与创建时间");
		row0.createCell(7).setCellValue("修改人和修改时间");
		row0.createCell(8).setCellValue("组托识别号");
		int rowNumber = 1;


		Paging paging = new Paging();
		paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
		//分页查询，一次查询2000条，统一装入一个list对象里面
		int pageNum = 1;
		boolean whileFlag = true;
		while(whileFlag) {
			paging.setCurrentPage(pageNum++);
			paging = this.pagingAllGroup(systemUser, paging,epcId, groupState);

			List<ContainerGroup> list = (List<ContainerGroup>) paging.getData();
			if (CollectionUtils.isEmpty(list)) {
				break;
			}
			for (ContainerGroup o : list) {
				if (rowNumber > SysConstants.MAX_EXPORT_ROWS) {
					whileFlag = false;
					break;
				}
				XSSFRow row = sheet.createRow(rowNumber);
				row.createCell(0).setCellValue(o.getGroupEpcId());
				row.createCell(1).setCellValue(o.getEpcId());
				row.createCell(2).setCellValue(o.getContainerCode());
				row.createCell(3).setCellValue(o.getContainerTypeName());
				row.createCell(4).setCellValue(o.getContainerName());
				row.createCell(8).setCellValue(o.getGroupId());
				if(o.getGroupState()==1) {
					row.createCell(5).setCellValue("已解托");
				}else if(o.getGroupState()==0){
					row.createCell(5).setCellValue("已组托");
				}else {
					row.createCell(5).setCellValue("");
				}
				row.createCell(6).setCellValue(o.getCreateRealName() + " " + DateUtil.format(DateUtil.yyyy_MM_dd_HH_mm_ss, o.getCreateTime()));
				if(o.getModifyRealName()==null || o.getModifyRealName()=="") {
					row.createCell(7).setCellValue("");
				}else {
					row.createCell(7).setCellValue(o.getModifyRealName() + " " + DateUtil.format(DateUtil.yyyy_MM_dd_HH_mm_ss, o.getModifyTime()));
				}
				rowNumber++;
			}
			//如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
			if (list.size() < SysConstants.MAX_QUERY_ROWS) {
				break;
			}
		}
		ExcelUtil.exportExcel(response, wb,"【组托和解脱】"+DateUtil.today_yyyyMMddHHmmssZH());
		try {
            if(null != wb){
                wb.close();
            }
        } catch (IOException e) {
        	LOG.error(e.getMessage()+ StatusCode.STATUS_408_MSG,e);
        	logService.addLogError(systemUser, e, e.getMessage()+ StatusCode.STATUS_408_MSG, null);
        }
	}

	/**
	 * 对指定器具进行整托解托
	 * @param sessionUser 当前登录用户
	 * @param groupIdList 指定器具的组托识别号
	 * @return
	 */
	@RequestMapping("/containerGroup/relieveGroup")
	public AjaxBean relieveGroup(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser
			,@RequestParam(value = "epcIdList[]",required=false) List<String> epcIdList){
		LOG.info("request URL /containerGroup/relieveGroup");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(CollectionUtils.isEmpty(epcIdList)){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("至少选中一个器具进行整托解托！");
			return ajaxBean;
		}
		try {
			ajaxBean = containerGroupService.relieveGroup(ajaxBean,sessionUser,epcIdList);
		} catch (CmcException e) {
			LOG.error(e.getMessage(),e);
			ajaxBean.setStatus(Integer.valueOf(e.getErrorCode()));
			ajaxBean.setMsg(e.getMessage());
			return ajaxBean;
		}
		return ajaxBean;
	}
}
