package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;
import java.util.Objects;

/** 
 * 用户工种对应表 T_SYSTEM_USERJOB(这是中间表)
 * @author ZhuWen
 * @date 2018年1月2日
 */
public class SystemUserjob {
	private String account; //'联合主键1. 账号，登录名'
	private String orgId; // 联合主键2。仓库ID
	private String jobId;//联合主键3. 工种ID
	private String jobName; //工种名称
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名

	@Override
    public boolean equals(Object o) {
        if (o == this){
        	return true;  
        }
        if (!(o instanceof SystemUserjob)) {  
            return false;  
        }  
        SystemUserjob thisObj = (SystemUserjob) o;  
        return Objects.equals(account, thisObj.getAccount()) &&
        		Objects.equals(orgId, thisObj.getOrgId()) &&
        		Objects.equals(jobId, thisObj.getJobId());  
    }
	
	@Override  
    public int hashCode() {  
        return Objects.hash(account,orgId,jobId);  
    } 
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
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
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
}
