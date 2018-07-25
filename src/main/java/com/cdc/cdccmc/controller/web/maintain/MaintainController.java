package com.cdc.cdccmc.controller.web.maintain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.enums.CirculateState;
import com.cdc.cdccmc.common.enums.MaintainState;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Maintain;
import com.cdc.cdccmc.domain.circulate.Circulate;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.CirculateService;
import com.cdc.cdccmc.service.ContainerService;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.MaintainService;

/**
 * 维修
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class MaintainController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MaintainController.class);

	@Autowired
	private MaintainService maintainService;
	@Autowired
	private ContainerService containerService;
	@Autowired
	private CirculateService circulateService;
	@Autowired
	private LogService logService;

	@Value("${upload.file.xlsx.path}")
	private String excelPath;


	/**
	 * 器具维修列表查询
	 * @param systemUser
	 * @param paging
	 * @param maintain
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value = "/maintain/pagingMaintain")
	public Paging pagingMaintain(@ModelAttribute(SysConstants.SESSION_USER) SystemUser systemUser, Paging paging, Maintain maintain, String startDate, String endDate, String outMode){
		paging = maintainService.pagingMaintain(paging, systemUser,maintain,startDate,endDate,outMode);
		return paging;
	}

	/**
	 * 把查询出的结果写到excel中,导出给用户
	 * @param sessionUser
	 * @param maintain
	 * @param response
	 * @param startDate
	 * @param endDate
	 */
	@RequestMapping("/maintain/expertToExcel")
	public void expertToExcel(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Maintain maintain, HttpServletResponse response, String startDate , String endDate, String outMode){
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("sheet1");
		//创建标题行
		XSSFRow row0=sheet.createRow(0);
		row0.createCell(0).setCellValue("EPC编号");
		row0.createCell(1).setCellValue("印刷编号");
		row0.createCell(2).setCellValue("器具类型");
		row0.createCell(3).setCellValue("维修状态");
		row0.createCell(4).setCellValue("维修级别");
		row0.createCell(5).setCellValue("报修不良原因");
		row0.createCell(6).setCellValue("操作人与维修完成时间");
		row0.createCell(7).setCellValue("维修完成措施与不良原因");
		row0.createCell(8).setCellValue("出库维修公司");
		row0.createCell(9).setCellValue("出库维修包装流转单号");
		row0.createCell(10).setCellValue("报修人与报修时间");
		row0.createCell(11).setCellValue("维修鉴定人与鉴定时间");

		//写入内容
		int rowNumber = 1;


		Paging paging = new Paging();
		paging.setPageSize(SysConstants.MAX_QUERY_ROWS);
		//分页查询，一次查询2000条，统一装入一个list对象里面
		int pageNum = 1;
		boolean whileFlag = true;
		while(whileFlag){
			paging.setCurrentPage(pageNum++);
			paging = this.pagingMaintain(sessionUser,paging, maintain, startDate, endDate, outMode);

			List<Maintain> list = (List<Maintain>) paging.getData();
			if(CollectionUtils.isEmpty(list)){
				break;
			}
			for (Maintain o : list) {
				if (rowNumber > SysConstants.MAX_EXPORT_ROWS){
					whileFlag = false;
					break;
				}
				XSSFRow row = sheet.createRow(rowNumber);
				row.createCell(0).setCellValue(o.getEpcId());
				row.createCell(1).setCellValue(o.getPrintCode());
				row.createCell(2).setCellValue(o.getContainerTypeName());
				row.createCell(3).setCellValue(MaintainState.getTypeName(o.getMaintainState()));
//				if ("1".equals(o.getMaintainState()) || "2".equals(o.getMaintainState())){
//					row.createCell(3).setCellValue("维修中");
//				}else{
//					row.createCell(3).setCellValue("维修完成");
//				}
//				switch (o.getMaintainState()){
//					case "1":
//						row.createCell(4).setCellValue("在库维修 ");
//						break;
//					case "2":
//						row.createCell(4).setCellValue("出库维修");
//						break;
//					case "3":
//						row.createCell(4).setCellValue("待报废");
//						break;
//					case "4":
//						row.createCell(4).setCellValue("已报废");
//						break;
//					case "5":
//						row.createCell(4).setCellValue("维修完毕");
//						break;
//
//				}
				row.createCell(4).setCellValue(o.getMaintainLevel());
				row.createCell(5).setCellValue(o.getMaintainApplyBadReason());
				if (o.getMaintainState().equals("3")){
					if(o.getMaintainFinishTime()!=null){
						row.createCell(6).setCellValue(o.getMaintainFinishRealName()+" "+DateUtil.format(DateUtil.yyyy_MM_dd_HH_mm_ss,o.getMaintainFinishTime()));
					}
					if(o.getMaintainFinishSolution() != null){
						if(o.getMaintainFinishBadReason() !=null){
							row.createCell(7).setCellValue("措施："+o.getMaintainFinishSolution()+" "+"不良原因："+o.getMaintainFinishBadReason());
						}
						else{
							row.createCell(7).setCellValue("措施："+o.getMaintainFinishSolution());
						}
					}
					else{
						if(o.getMaintainFinishBadReason() !=null){
							row.createCell(7).setCellValue("不良原因："+o.getMaintainFinishBadReason());
						}
					}
				}
				row.createCell(8).setCellValue(o.getMaintainOrgName());
				if(o.getMaintainState().equals("2")){
					row.createCell(9).setCellValue(o.getOrderCode());
				}
				row.createCell(10).setCellValue(o.getMaintainApplyRealName()+" "+ DateUtil.format(DateUtil.yyyy_MM_dd_HH_mm_ss,o.getMaintainApplyTime()));
				if(o.getMaintainCheckRealName() != null){
					row.createCell(11).setCellValue(o.getMaintainCheckRealName()+" "+ DateUtil.format(DateUtil.yyyy_MM_dd_HH_mm_ss,o.getMaintainCheckTime()));
				}
				rowNumber++;
			}
			//如果查询结果小于一整页，说明已经查询完毕。直接跳出循环
			if (list.size()<SysConstants.MAX_QUERY_ROWS){
				break;
			}
		}

		ExcelUtil.exportExcel(response,wb,"【器具维修】"+DateUtil.today_yyyyMMddHHmmssZH());
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
	 * 维修完成
	 * @param sessionUser
	 * @param maintain
	 * @return
	 */
	@RequestMapping("/maintain/maintainFinsh")
	public AjaxBean maintainFinsh(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Maintain maintain){
		AjaxBean ajaxBean = new AjaxBean();
		if (maintain.getVersion()==null){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("版本号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if (StringUtils.isBlank(maintain.getMaintainId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("器具Id"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		ajaxBean = maintainService.maintainFinsh(sessionUser,maintain);
		return ajaxBean;
	}

	/**
	 * 同意报废
	 * @param sessionUser
	 * @param maintain
	 * @return
	 */
	@RequestMapping("/maintain/maintainScrap")
	public AjaxBean maintainScrap(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Maintain maintain){
		AjaxBean ajaxBean = new AjaxBean();
		if (maintain.getVersion()==null){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("版本号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if (StringUtils.isBlank(maintain.getMaintainId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("维修Id"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		ajaxBean = maintainService.maintainScrap(sessionUser,maintain);
		return ajaxBean;
	}

	/**
	 * 获取维修状态
	 * @return
	 */
	@RequestMapping("/maintain/getAllMaintainStatus")
	public AjaxBean getAllMaintainStatus(){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		for (MaintainState s: MaintainState.values()) {
			Map<String,String> map = new HashMap<String,String>();
			map.put("code",s.getCode());
			map.put("value",s.getState());
			list.add(map);
		}
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setList(list);
		return ajaxBean;
	}
    
    /**
     * 器具报修
     * @return
     */
    @RequestMapping(value = "/maintain/addMaintain")
    public AjaxBean addMaintain(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, Maintain maintain){
		AjaxBean ajaxBean = new AjaxBean();
		//检测epcId是否存在,不存在返回
		Container container = containerService.findContainerByEpcId(maintain.getEpcId());
		if (container == null){
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("EPC编号["+maintain.getEpcId()+"]"+StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		//检测是否在本仓库
		Circulate circulate = circulateService.queryCirculateLatestByEpcId(maintain.getEpcId(), sessionUser.getCurrentSystemOrg().getOrgId());
		if(circulate == null) {
			ajaxBean.setStatus(StatusCode.STATUS_312);
			ajaxBean.setMsg("EPC编号["+maintain.getEpcId()+"]"+StatusCode.STATUS_312_MSG);
			return ajaxBean;
		}
		//检测是否在库状态
		if(!circulate.getCirculateState().equals(CirculateState.ON_ORG.getCode())) {
			ajaxBean.setStatus(StatusCode.STATUS_310);
			ajaxBean.setMsg("该器具在当前仓库的流转状态不是在库，不能进行报修！");
			return ajaxBean;
		}
		maintain.setContainerCode(container.getContainerCode());
		maintain.setContainerTypeId(container.getContainerTypeId());
		maintain.setContainerTypeName(container.getContainerTypeName());
		maintain.setPrintCode(container.getPrintCode());
        //根据ecpId 查询出最后一条该器具的维修记录
        Maintain mCheck = maintainService.queryLastMaintainByEcpId(maintain.getEpcId(),sessionUser.getCurrentSystemOrg().getOrgId());
		if (mCheck != null && !mCheck.getMaintainState().equals(MaintainState.FINISH.getCode())){
			switch (mCheck.getMaintainState()){  // 维修状态。1在库维修 2出库维修 3 待报废 4已报废 5维修完毕
				case "1":
					ajaxBean.setStatus(StatusCode.STATUS_326);
					ajaxBean.setMsg("EPC编号["+maintain.getEpcId()+"]"+StatusCode.STATUS_326_MSG);
					return ajaxBean;
				case "2":
					ajaxBean.setStatus(StatusCode.STATUS_327);
					ajaxBean.setMsg("EPC编号["+maintain.getEpcId()+"]"+StatusCode.STATUS_327_MSG);
					return ajaxBean;
			}
		}
		ajaxBean = maintainService.repairMaintain(maintain,sessionUser);
		return ajaxBean;
	}
    
    /**
	 * 维修鉴定
	 */
	@RequestMapping("/maintain/maintainAppraisal")
	public AjaxBean maintainAppraisal(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Maintain maintain){
		AjaxBean ajaxBean = new AjaxBean();
		//检测epcId是否存在,不存在返回
		Container container = containerService.findContainerByEpcId(maintain.getEpcId());
		if (container == null){
			ajaxBean.setStatus(StatusCode.STATUS_311);
			ajaxBean.setMsg("EPC编号["+maintain.getEpcId()+"]"+StatusCode.STATUS_311_MSG);
			return ajaxBean;
		}
		//查询器具最新流转记录
		Circulate circulate = circulateService.getCirculateLatestByEpcId(maintain.getEpcId());
		if(circulate == null) {
			ajaxBean.setStatus(StatusCode.STATUS_300);
			ajaxBean.setMsg("EPC编号["+maintain.getEpcId()+"]"+StatusCode.STATUS_300_MSG);
			return ajaxBean;
		}
		ajaxBean = maintainService.maintainAppraisal(ajaxBean,sessionUser,maintain);
		return ajaxBean;
	}
	
	/**
	 * 维修完成
	 */
	@RequestMapping("/maintain/maintainFinish")
	public AjaxBean maintainFinish(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Maintain maintain){
		AjaxBean ajaxBean = new AjaxBean();
		if (StringUtils.isBlank(maintain.getMaintainId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("器具Id"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		ajaxBean = maintainService.maintainFinish(sessionUser,maintain);
		return ajaxBean;
	}
}
