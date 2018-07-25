package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;
import java.util.Objects;

/** 
 * 
 * @author ZhuWen
 * @date 2018年3月16日
 */
public class SystemJobbutton {

	private String orgId; // 联合主键1。仓库ID
	private String jobId; //联合主键2. 工种ID
	private String buttonId; //联合主键3. 按钮ID
	private String orgName; // 组织名称
	private String buttonName; //按钮名称
	private String jobName; //工种名称
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名

	@Override
    public boolean equals(Object o) {
        if (o == this){
        	return true;  
        }
        if (!(o instanceof SystemJobbutton)) {  
            return false;  
        }  
        SystemJobbutton thisObj = (SystemJobbutton) o;  
        return Objects.equals(orgId, thisObj.getOrgId()) &&
        		Objects.equals(jobId, thisObj.getJobId()) &&
        		Objects.equals(buttonId, thisObj.getButtonId());  
    }
	
	@Override  
    public int hashCode() {  
        return Objects.hash(orgId,jobId,buttonId);  
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
	public String getButtonId() {
		return buttonId;
	}
	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getButtonName() {
		return buttonName;
	}
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
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
