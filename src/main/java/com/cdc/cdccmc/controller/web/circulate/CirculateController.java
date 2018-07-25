package com.cdc.cdccmc.controller.web.circulate;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
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
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateService;
import com.cdc.cdccmc.service.LogService;


/**
 * 器具流转记录
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class CirculateController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CirculateController.class); 
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private LogService logService;
    
	/**
	 * 器具最新流转状态列表
	 * @param paging
	 * @param systemUser
	 * @param circulate
	 * @param code
	 * @return
	 */
    @RequestMapping(value = "/circulate/pagingCirculateLatest")
	public Paging pagingCirculateLatest(Paging paging,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Circulate circulate, String code,String orgId) {
		paging = circulateService.pagingCirculateLatest(paging, circulate, code,orgId);
		return paging;
	}
    
    /**
	 * 器具流转历史查询
	 * @param paging
	 * @param systemUser
	 * @param circulate
	 * @param startDate
	 * @param endDate
	 * @return
	 */
    @RequestMapping(value = "/circulate/pagingCirculateHistory")
   	public Paging pagingCirculateHistory(Paging paging,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Circulate circulate, String startDate, String endDate) {
    	paging = circulateService.pagingCirculateHistory(paging, sessionUser, circulate, startDate, endDate);
   		return paging;
   	}


	/**
	 * 最新流转状态导出
	 * 把查询出的结果写到excel中,导出给用户
	 * @param sessionUser
	 * @param circulate
	 * @param response
	 */
	@RequestMapping("/circulate/expertToExcelLast")
	public void expertToExcel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Circulate circulate, HttpServletResponse response, String code, String orgId){
		// 建立工作簿和电子表格对象
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("sheet1");
		//创建标题行
		XSSFRow row0=sheet.createRow(0);
		row0.createCell(0).setCellValue("包装流转单号");
		row0.createCell(1).setCellValue("创建人与创建时间");
		row0.createCell(2).setCellValue("EPC编号");
		row0.createCell(3).setCellValue("器具代码");
		row0.createCell(4).setCellValue("器具类型");
		row0.createCell(5).setCellValue("流转状态");
		row0.createCell(6).setCellValue("操作仓库");
		row0.createCell(7).setCellValue("来源仓库");
		row0.createCell(8).setCellValue("目标仓库");
		row0.createCell(9).setCellValue("区域");
		//写入内容
		int rowNumber = 1;

		//因为考虑到符合条件的数量很多，所以暂时屏蔽一次查询出所有的方法，改为分页查询，一次查询5000条
//		List<Container> outmodes = containerService.queryAllContainerList(container, startDate,endDate);
		//开始计算分页数据
		Paging paging = new Paging();
		paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
		//分页查询，一次查询2000条，统一装入一个list对象里面
		int pageNum = 1;
		boolean whileFlag = true;
		while(whileFlag){
			paging.setCurrentPage(pageNum++);
			paging = this.pagingCirculateLatest(paging,sessionUser, circulate, code,orgId);

			List<Circulate> list = (List<Circulate>) paging.getData();
			if(CollectionUtils.isEmpty(list)){
				break;
			}
			for (Circulate o : list) {
				if (rowNumber > SysConstants.MAX_EXPORT_ROWS) {
					whileFlag = false;
					break;
				}
				XSSFRow row = sheet.createRow(rowNumber);
				row.createCell(0).setCellValue(o.getOrderCode());
				row.createCell(1).setCellValue(o.getCreateRealName() + " " + DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getCreateTime()));
				row.createCell(2).setCellValue(o.getEpcId());
				row.createCell(3).setCellValue(o.getContainerCode());
				row.createCell(4).setCellValue(o.getContainerTypeName());
				row.createCell(5).setCellValue(o.getCirculateStateName());
				row.createCell(6).setCellValue(o.getOrgName());
				row.createCell(7).setCellValue(o.getFromOrgName());
				row.createCell(8).setCellValue(o.getTargetOrgName());
				row.createCell(9).setCellValue(o.getAreaName());
				rowNumber++;
			}
				//如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
				if (list.size()<SysConstants.MAX_QUERY_ROWS){
					break;
				}
			}
			ExcelUtil.exportExcel(response,wb,"【器具最新流转状态】"+DateUtil.today_yyyyMMddHHmmssZH());
			try {
				if(null != wb){
					wb.close();
				}
			} catch (IOException e) {
				LOG.error(e.getMessage()+StatusCode.STATUS_408_MSG,e);
				logService.addLogError(sessionUser, e, e.getMessage()+StatusCode.STATUS_408_MSG, null);
			}

	}


	/**
	 * 历史流转状态导出
	 * 把查询出的结果写到excel中,导出给用户
	 * @param sessionUser
	 * @param circulate
	 * @param response
	 */
	@RequestMapping("/circulate/expertToExcelHistory")
	public void expertToExcelHistory(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Circulate circulate, HttpServletResponse response, String startDate, String endDate){
		// 建立工作簿和电子表格对象
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("sheet1");
		//创建标题行
		XSSFRow row0=sheet.createRow(0);
		row0.createCell(0).setCellValue("包装流转单号");
		row0.createCell(1).setCellValue("创建人与创建时间");
		row0.createCell(2).setCellValue("EPC编号");
		row0.createCell(3).setCellValue("器具代码");
		row0.createCell(4).setCellValue("器具类型");
		row0.createCell(5).setCellValue("流转状态");
		row0.createCell(6).setCellValue("操作仓库");
		row0.createCell(7).setCellValue("来源仓库");
		row0.createCell(8).setCellValue("目标仓库");
		row0.createCell(9).setCellValue("区域");
		//写入内容
		int rowNumber = 1;

		//因为考虑到符合条件的数量很多，所以暂时屏蔽一次查询出所有的方法，改为分页查询，一次查询5000条
//		List<Container> outmodes = containerService.queryAllContainerList(container, startDate,endDate);
		//开始计算分页数据
		Paging paging = new Paging();
		paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
		//分页查询，一次查询2000条，统一装入一个list对象里面
		int pageNum = 1;
		boolean whileFlag = true;
		while(whileFlag){
			paging.setCurrentPage(pageNum++);
			paging = this.pagingCirculateHistory(paging,sessionUser, circulate, startDate, endDate);

			List<Circulate> list = (List<Circulate>) paging.getData();
			if(CollectionUtils.isEmpty(list)){
				break;
			}
			for (Circulate o : list) {
				if (rowNumber > SysConstants.MAX_EXPORT_ROWS) {
					whileFlag = false;
					break;
				}
				XSSFRow row = sheet.createRow(rowNumber);
				row.createCell(0).setCellValue(o.getOrderCode());
				row.createCell(1).setCellValue(o.getCreateRealName() + " " + DateUtil.format("yyyy-MM-dd HH:mm:ss", o.getCreateTime()));
				row.createCell(2).setCellValue(o.getEpcId());
				row.createCell(3).setCellValue(o.getContainerCode());
				row.createCell(4).setCellValue(o.getContainerTypeName());
				row.createCell(5).setCellValue(o.getCirculateStateName());
				row.createCell(6).setCellValue(o.getOrgName());
				row.createCell(7).setCellValue(o.getFromOrgName());
				row.createCell(8).setCellValue(o.getTargetOrgName());
				row.createCell(9).setCellValue(o.getAreaName());
				rowNumber++;
			}
			//如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
			if (list.size()<SysConstants.MAX_QUERY_ROWS){
				break;
			}
		}
		ExcelUtil.exportExcel(response,wb,"【器具流转历史】"+DateUtil.today_yyyyMMddHHmmssZH());
		try {
			if(null != wb){
				wb.close();
			}
		} catch (IOException e) {
			LOG.error(e.getMessage()+StatusCode.STATUS_408_MSG,e);
			logService.addLogError(sessionUser, e, e.getMessage()+StatusCode.STATUS_408_MSG, null);
		}

	}

}
