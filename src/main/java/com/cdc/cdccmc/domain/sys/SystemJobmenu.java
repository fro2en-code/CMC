package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;
import java.util.Objects;

/** 
 * 工种与web菜单的中间表 t_system_jobmenu
 * @author ZhuWen
 * @date 2018-03-16
 */
public class SystemJobmenu {

	private String orgId;//联合主键1。仓库ID
	private String jobId;//联合主键2. 工种ID
	private String menuId; //联合主键3. web菜单ID
	private String orgName;//组织名称
	private String jobName; //工种名称
	private String menuName; //菜单名称
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	
	@Override
    public boolean equals(Object o) {
        if (o == this){
        	return true;  
        }
        if (!(o instanceof SystemJobmenu)) {  
            return false;  
        }  
        SystemJobmenu thisObj = (SystemJobmenu) o;  
        return Objects.equals(orgId, thisObj.getOrgId()) &&  
               Objects.equals(jobId, thisObj.getJobId()) &&  
               Objects.equals(menuId, thisObj.getMenuId());  
    }
	
	@Override  
    public int hashCode() {  
        return Objects.hash(orgId, jobId, menuId);  
    } 
	
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public String getCreateAccount() {
		return createAccount;
	}
	public void setCreateAccount(String createAccount) {
		this.createAccount = createAccount;
	}
	public String getCreateRealName() {
		return createRealName;
	}
	public void setCreateRealName(String createRealName) {
		this.createRealName = createRealName;
	}
	
}
