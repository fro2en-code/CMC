package com.cdc.cdccmc.controller.web.manage;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.controller.Paging;
import com.cdc.cdccmc.domain.sys.SystemJob;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.sys.SystemButtonService;
import com.cdc.cdccmc.service.sys.SystemJobService;
import com.cdc.cdccmc.service.sys.SystemMenuWebService;
/**
 * 工种管理
 * @author ZhuWen
 * @date 2018-01-09
 */
@RestController
@SessionAttributes(SysConstants.SESSION_USER)
public class JobController {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JobController.class); 
	
	@Autowired
	private SystemJobService systemJobService;
	@Autowired
	private SystemMenuWebService systemMenuWebService;
	@Autowired
	private SystemButtonService systemButtonService;
	
	/**
	 * 工种管理列表
	 */
	@RequestMapping(value = "/job/pagingJob")
    public Paging pagingJob(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,Paging paging, String selectOrgId,String jobName){
		LOG.info("request URL /log/listJob");
		paging = systemJobService.pagingJobByOrgId(paging,sessionUser,selectOrgId,jobName);
		return paging;
    }
	/**
	 * 获取当前选择仓库的所有工种
	 */
	@RequestMapping(value = "/job/listCurrentOrgJob")
    public AjaxBean listCurrentOrgJob(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser){
		LOG.info("request URL /log/listCurrentOrgJob");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<SystemJob> list = systemJobService.listCurrentOrgJob(sessionUser);
		ajaxBean.setList(list);
		return ajaxBean;
    }
	/**
	 * 获取当前选择仓库下，指定账号的权限工种列表
	 * @param selectAccount 选中的账号，参数不能命名为account，否则优先设置sessionUser里的account
	 */
	@RequestMapping(value = "/job/listCurrentOrgAccountJob")
    public AjaxBean listCurrentOrgAccountJob(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,String selectAccount){
		LOG.info("request URL /log/listCurrentOrgAccountJob");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		List<SystemJob> list = systemJobService.listJobByAccountAndOrg(selectAccount,sessionUser.getCurrentSystemOrg().getOrgId());
		ajaxBean.setList(list);
		return ajaxBean;
    }
	/**
	 * 编辑工种名称
	 */
	@RequestMapping(value = "/job/editJobName")
    public AjaxBean editJobName(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,ModelMap modelmap,SystemJob job){
		LOG.info("request URL /job/editJobName");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(job.getJobName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("工种名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		job.setJobName(job.getJobName().trim());
		SystemJob findJob = systemJobService.findJobByName(job.getJobName());
		if(null != findJob && findJob.getOrgId().equals(sessionUser.getCurrentSystemOrg().getOrgId())
				&& !findJob.getJobId().equals(job.getJobId())){ //如果当前选择公司下已存在该工种名称
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("工种名称["+job.getJobName()+"]已存在仓库["+sessionUser.getCurrentSystemOrg().getOrgName()+"]");
			return ajaxBean;
		}
		ajaxBean = systemJobService.editJobName(ajaxBean,sessionUser,job);
		return ajaxBean;
    }
	/**
	 * 新增工种
	 */
	@RequestMapping(value = "/job/addJob")
    public AjaxBean addJob(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser,SystemJob job){
		LOG.info("request URL /log/addJob");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(job.getJobName())){
			ajaxBean.setStatus(StatusCode.STATUS_305);
			ajaxBean.setMsg("工种名称"+StatusCode.STATUS_305_MSG);
			return ajaxBean;
		}
		job.setJobName(job.getJobName().trim());
		SystemJob findJob = systemJobService.findJobByName(job.getJobName());
		if(null != findJob && findJob.getOrgId().equals(sessionUser.getCurrentSystemOrg().getOrgId())){ //如果当前选择公司下已存在该工种名称
			ajaxBean.setStatus(StatusCode.STATUS_302);
			ajaxBean.setMsg("工种名称["+job.getJobName()+"]已存在仓库["+sessionUser.getCurrentSystemOrg().getOrgName()+"]");
			return ajaxBean;
		}
		ajaxBean = systemJobService.addSystemJob(ajaxBean,sessionUser,job);
		return ajaxBean;
    }
	/**
	 * 保存某个工种，新的web权限菜单和新的权限按钮列表
	 * @param account 用户账号
	 * @return
	 */
	@RequestMapping("/job/saveJobMenuWeb")
	public AjaxBean saveJobMenuWeb(@ModelAttribute(SysConstants.SESSION_USER) SystemUser sessionUser
			,String jobId
			,@RequestParam(value = "menuList[]",required=false) List menuList
			,@RequestParam(value = "buttonList[]",required=false) List buttonList ){
		LOG.info("request URL /job/saveJobMenuWeb");
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		if(StringUtils.isBlank(jobId)){
			ajaxBean.setMsg("请先选择一个需要编辑菜单权限的工种");
			ajaxBean.setStatus(StatusCode.STATUS_201);
			return ajaxBean;
		}
		//加载指定工种
		SystemJob job = systemJobService.findJobById(jobId);
		//更新选中账号的权限菜单
		systemMenuWebService.updateJobMenu(job,menuList,sessionUser);
		//更新选中账号的权限按钮
		systemButtonService.updateJobButton(job,buttonList,sessionUser);
		return AjaxBean.SUCCESS();
	}
}
