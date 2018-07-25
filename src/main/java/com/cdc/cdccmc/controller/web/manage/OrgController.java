package com.cdc.cdccmc.controller.web.manage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.cdc.cdccmc.common.enums.OrgType;
import com.cdc.cdccmc.common.util.DateUtil;
import com.cdc.cdccmc.common.util.ExcelUtil;
import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.LogService;
import com.cdc.cdccmc.service.sys.SystemOrgService;

/**
 * 组织机构管理
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class OrgController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(OrgController.class); 
	@Autowired
	private SystemOrgService systemOrgService;
	@Autowired
	private LogService logService;
    @Value("${upload.file.xlsx.path}")
    private String uploadPath;

	/**
	 * 当前选中仓库的所有子公司列表，包括自己
	 */
	@RequestMapping(value = "/org/filialeSystemOrgList")
    public AjaxBean filialeSystemOrgList(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser){
		LOG.info("request URL /org/filialeSystemOrgList");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setList(sessionUser.getFilialeSystemOrgList());
		return ajaxBean;
    }

	/**
	 * 当前选中仓库的所有子公司列表，包括自己
	 */
	@RequestMapping(value = "/org/listAllOrg")
    public AjaxBean listAllOrg(){
		LOG.info("request URL /org/listAllOrg");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<SystemOrg> orgList = systemOrgService.listAllOrg();
		ajaxBean.setList(orgList);
		return ajaxBean;
    }

	/**
	 * 当前选中仓库的所有子公司列表，包括自己
	 */
	@RequestMapping(value = "/org/listAllOrgType")
    public AjaxBean listAllOrgType(){
		LOG.info("request URL /org/listAllOrg");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		ajaxBean.setList(OrgType.listAll());
		return ajaxBean;
    }
	/**
	 * 获取当前选择仓库的所有工种
	 * @param sessionUser 当前登录用户
	 * @param paging 分页支持
	 * @param orgName 机构名称，支持模糊搜索
	 * @return
	 */
	@RequestMapping(value = "/org/pagingSystemOrg")
    public Paging pagingSystemOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Paging paging,String orgName){
		LOG.info("request URL /org/pagingSystemOrg");
		paging = systemOrgService.pagingSystemOrg(sessionUser,paging,orgName);
		return paging;
    }
	/**
	 * 新增一个机构
	 */
	@RequestMapping(value = "/org/addSystemOrg")
    public AjaxBean addSystemOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,SystemOrg systemOrg){
		LOG.info("request URL /org/addSystemOrg");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(systemOrg.getParentOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("上级组织"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(systemOrg.getOrgName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("组织名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		systemOrg.setOrgName(systemOrg.getOrgName().trim());
		SystemOrg findOrg = systemOrgService.findByOrgName(systemOrg.getOrgName());
		if(null != findOrg){
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("组织名称["+systemOrg.getOrgName()+"]"+StatusCode.STATUS_302_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(systemOrg.getOrgCode())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("组织代码"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		systemOrg.setOrgCode(systemOrg.getOrgCode().trim());
		SystemOrg findOrgCode = systemOrgService.findByOrgCode(systemOrg.getOrgCode());
		if(null != findOrgCode){
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("组织代码["+findOrgCode.getOrgCode()+"]已在组织名称为["+findOrgCode.getOrgName()+"]中占用！");
			return ajaxBean;
		}
//		if(!systemOrg.getOrgCode().matches(SysConstants.REGEX_ORG_CODE)){
//			ajaxBean.setStatus(StatusCode.STATUS_201);
//			ajaxBean.setMsg("组织代码必须是大写字母，且长度为2到7位大写字母或数字！");
//			return ajaxBean;
//		}
		if(StringUtils.isBlank(systemOrg.getOrgTypeId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("组织类型"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isNotBlank(systemOrg.getContactPhone())) {
			if(!(systemOrg.getContactPhone().matches(SysConstants.REGEX_SHIPPER_CONTAINER_NUMBER))){
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("联系电话只能是数字,空格和'-'的组合且长度为1到20位");
				return ajaxBean;
			}
		}
		ajaxBean = systemOrgService.addSystemOrg(sessionUser,ajaxBean,systemOrg);
		if(StatusCode.STATUS_200 == ajaxBean.getStatus()){ //如果新增成功，则重新加载当前登录用户的可见仓库列表，以免造成新增的仓库看不见的情况
			// 设置systemUser对象的两个字段值：filialeSystemOrgList、filialeSystemOrgIds
			sessionUser = systemOrgService.loadFilialeOrg(sessionUser);
		}
		return ajaxBean;
    }
	/**
	 * 编辑一个机构
	 */
	@RequestMapping(value = "/org/editSystemOrg")
    public AjaxBean editSystemOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,SystemOrg systemOrg){
		LOG.info("request URL /org/editSystemOrg");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(systemOrg.getParentOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("上级组织"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(systemOrg.getOrgId())){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("组织ID不能为空！");
			return ajaxBean;
		}
		if(StringUtils.isBlank(systemOrg.getOrgName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("组织名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		systemOrg.setOrgName(systemOrg.getOrgName().trim());
		SystemOrg findOrg = systemOrgService.findByOrgName(systemOrg.getOrgName());
		if(null != findOrg && !systemOrg.getOrgId().equals(findOrg.getOrgId())){ //如果别的组织也存在这个名称，则不允许
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("组织名称["+systemOrg.getOrgName()+"]"+StatusCode.STATUS_302_MSG);
			return ajaxBean;
		}
		if(StringUtils.isBlank(systemOrg.getOrgCode())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("组织代码"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		systemOrg.setOrgCode(systemOrg.getOrgCode().trim());
		SystemOrg findOrgCode = systemOrgService.findByOrgCode(systemOrg.getOrgCode());
		if(null != findOrgCode && !systemOrg.getOrgId().equals(findOrgCode.getOrgId())){  //如果别的组织也存在这个代码，则不允许
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("组织代码["+findOrgCode.getOrgCode()+"]已在组织名称为["+findOrgCode.getOrgName()+"]中占用！");
			return ajaxBean;
		}
		if(!(systemOrg.getOrgCode().matches(SysConstants.REGEX_ORG_CODE))){
			ajaxBean.setStatus(StatusCode.STATUS_201);
			ajaxBean.setMsg("组织代码必须是大写字母，且长度为2到7位大写字母或数字！");
			return ajaxBean;
		}
		if(StringUtils.isBlank(systemOrg.getOrgTypeId())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("组织类型"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		if(StringUtils.isNotBlank(systemOrg.getContactPhone())) {
			if(!(systemOrg.getContactPhone().matches(SysConstants.REGEX_SHIPPER_CONTAINER_NUMBER))){
				ajaxBean.setStatus(StatusCode.STATUS_201);
				ajaxBean.setMsg("联系电话只能是数字,空格和'-'的组合且长度为1到20位");
				return ajaxBean;
			}
		}
		ajaxBean = systemOrgService.editSystemOrg(sessionUser,ajaxBean,systemOrg);
		return ajaxBean;
    }
	/**
	 * 启用或禁用指定机构
	 * @param sessionUser 当前登录用户
	 * @param systemOrg 指定机构
	 * @return
	 */
	@RequestMapping("/org/changeActiveOrg")
	public AjaxBean changeActiveOrg(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,SystemOrg systemOrg){
		LOG.info("request URL /org/changeActiveOrg");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		//加载用户权限菜单
		ajaxBean = systemOrgService.changeActiveOrg(ajaxBean,sessionUser,systemOrg);
		return ajaxBean;
	}

	/**
	 * 组织模版下载
	 * @param req
	 * @param resp
	 */
	@RequestMapping(value = "/org/orgExcelDownload", method = {RequestMethod.POST, RequestMethod.GET})
	public void downLoadFile(HttpServletRequest req, HttpServletResponse resp) {
		String fileDownName ="BatchInsert-Org.xlsx";
		ExcelUtil.downLoadExcel(req,resp,fileDownName);
	}

    /**
     * excel 上传,入库
     * @param multipartFile
     * @param sessionUser
     * @return
     */
    @RequestMapping("/org/batchUpload")
    public AjaxBean batchUpload(@RequestParam("file")MultipartFile multipartFile, @ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser, AjaxBean ajaxBean){
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
        File file = new File(uploadPath +"/"+ DateUtil.format(DateUtil.yyyyMMddHHmmss,new Date())+"[BatchInsert-Account]["+sessionUser.getAccount()+"]."+endWith[endWith.length-1]);
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
        ajaxBean = systemOrgService.batchUpload(sessionUser,file);
        //如果新增成功，则实时刷新当前用户可见的仓库列表
        if(ajaxBean.getStatus() == StatusCode.STATUS_200){
        	// 设置systemUser对象的两个字段值：filialeSystemOrgList、filialeSystemOrgIds
    		sessionUser = systemOrgService.loadFilialeOrg(sessionUser);
        }
        return ajaxBean;
    }

	/**
	 * 获取上级组织机构代码
	 * 	默认读取所有的（排除自己）
	 * @return
	 */
	@RequestMapping("/org/findParent")
    public AjaxBean findParent(){
    	AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<SystemOrg> orgs = systemOrgService.listAllOrg();
		ajaxBean.setList(orgs);
		return ajaxBean;
	}
}
