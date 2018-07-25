package com.cdc.cdccmc.controller.web.car;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cdc.cdccmc.domain.dto.CarDto;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.Car;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.basic.CarService;
import com.cdc.cdccmc.service.basic.ShipperService;

import sun.rmi.runtime.Log;

/** 
 * 车辆管理
 * @author Jerry
 * @date 2017-1-3
 */ 
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class CarController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CarController.class);
	@Autowired
	private CarService carService;
	@Autowired
	private LogService logService;
	@Autowired
	private ShipperService shipperService;

	@Value("${upload.file.xlsx.path}")
	private String uploadPath;
	
	//车辆列表
	@RequestMapping("/car/pagingCar")
	public Paging pagingCar(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, CarDto carDto, Paging paging){
		return carService.pagingCar(paging,carDto);
	}

	/**
	 * 添加车辆
	 * @param sessionUser
	 * @param car
	 * @return
	 */
	@RequestMapping("/car/addCar")
	public AjaxBean addCar(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Car car,String licenseValidDateString){
	    AjaxBean ajaxBean = new AjaxBean();
	    if (StringUtils.isBlank(car.getCarNo())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("车牌号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if (StringUtils.isNotBlank(licenseValidDateString)){
			try {
				car.setLicenseValidDate(DateUtil.parseToDate(licenseValidDateString,DateUtil.yyyy_MM_dd));
			} catch (ParseException e) {
				LOG.error("添加车辆时,驾驶证有效日期转换错误",e);
				logService.addLogError(sessionUser, e, "添加车辆时,驾驶证有效日期["+licenseValidDateString+"]转换错误", null);
			}
		}

	    Car findCar = carService.findCarByCarNo(car.getCarNo());
	    if(null != findCar){
	    	ajaxBean.setStatus(StatusCode.STATUS_302);
	    	ajaxBean.setMsg("当前车牌号["+findCar.getCarNo()+"]"+StatusCode.STATUS_302_MSG);
            return ajaxBean;
        }
        ajaxBean = carService.addCar(sessionUser,ajaxBean,car);
		return ajaxBean;
	}

	/**
	 * 更新车辆信息
	 * @param sessionUser
	 * @param car
	 * @param ajaxBean
	 * @return
	 */
	@RequestMapping("/car/updateCar")
	public AjaxBean updateCar(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Car car,AjaxBean ajaxBean,String licenseValidDateString){
		if (StringUtils.isBlank(car.getCarNo())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("车牌号"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if (StringUtils.isNotBlank(licenseValidDateString)){
			try {
				car.setLicenseValidDate(DateUtil.parseToDate(licenseValidDateString,DateUtil.yyyy_MM_dd));
			} catch (ParseException e) {
				LOG.error("更新车辆时,驾驶证有效日期转换错误",e);
				logService.addLogError(sessionUser, e, "更新车辆时,驾驶证有效日期["+licenseValidDateString+"]转换错误", null);
			}
		}
		/*此段校验代码先保留
		 Car findCar = carService.findCarByCarNo(car.getCarNo());
	    if(null != findCar){
	    	ajaxBean.setStatus(StatusCode.STATUS_302);
	    	ajaxBean.setMsg("当前车牌号["+findCar.getCarNo()+"]"+StatusCode.STATUS_302_MSG);
            return ajaxBean;
        }*/
		ajaxBean = carService.updateCar(sessionUser,ajaxBean,car);
		return ajaxBean;
	}

	/**
	 * 车辆删除
	 * @param carNo
	 * @return
	 */
	@RequestMapping("/car/delCarByNo")
	public AjaxBean delCar(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,@RequestParam("carNo") String carNo){
		AjaxBean ajaxBean = carService.delCar(sessionUser,carNo);
		return ajaxBean;
	}

	/**
	 * 车辆模版下载
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "/car/carExcelDownload", method = {RequestMethod.POST, RequestMethod.GET})
	public void downLoadFile(HttpServletRequest req, HttpServletResponse resp) {
		String fileDownName ="BatchInsert-Car.xlsx";
		ExcelUtil.downLoadExcel(req,resp,fileDownName);
	}

	/**
	 * excel 上传,入库
	 * @param multipartFile
	 * @param sessionUser
	 * @return
	 */
	@RequestMapping("/car/batchUploadCar")
	public AjaxBean batchUploadCar(@RequestParam("file")MultipartFile multipartFile,@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,AjaxBean ajaxBean){
		if(!multipartFile.getOriginalFilename().endsWith(SysConstants.XLSX)){
			ajaxBean.setStatus(StatusCode.STATUS_405);
			ajaxBean.setMsg(StatusCode.STATUS_405_MSG);
		    return ajaxBean;
		}
		long fileSize = multipartFile.getSize();
		if(fileSize > SysConstants.MAX_FILE_UPLOAD_SIZE){ //如果文件大小超出上限
			new BigDecimal(SysConstants.MAX_FILE_UPLOAD_SIZE);
			ajaxBean.setStatus(StatusCode.STATUS_409);
			ajaxBean.setMsg(StatusCode.STATUS_409_MSG);
			return ajaxBean;
		}
		String[] endWith = multipartFile.getOriginalFilename().split("\\.");
		File file = new File(uploadPath +"/"+ DateUtil.format(DateUtil.yyyyMMddHHmmss,new Date())+"[BatchInsert-Car]["+sessionUser.getAccount()+"]."+endWith[endWith.length-1]);
		File fileParent = file.getParentFile();
		if (!fileParent.exists()){
			fileParent.mkdirs();
		}
		
		try {
			multipartFile.transferTo(file); //上传到服务器
		} catch (IllegalStateException | IOException e) {
			LOG.error(e.getMessage(),e);
			logService.addLogError(sessionUser, e, e.getMessage(), null);
			ajaxBean.setStatus(StatusCode.STATUS_400);
			ajaxBean.setMsg(StatusCode.STATUS_400_MSG);
			return ajaxBean;
		}
		ajaxBean = carService.batchUploadCar(sessionUser,ajaxBean,file);
		return ajaxBean;
	}
}
