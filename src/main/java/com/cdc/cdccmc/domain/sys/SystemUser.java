package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;
import java.util.List;

/** 
 * 用户表 T_SYSTEM_USER
 * @author ZhuWen
 * @date 2018-01-02
 */
public class SystemUser  {
	private String account; //账号，登录名
	private String password; //密码
	private String realName; //用户姓名
	private String idCardNum; //身份证号码
	private Timestamp lastLoginTime; //最后登录时间
	private String defaultOrgId;  // 用户默认选择仓库ID
	private Integer isActive; //0正常，1禁用
	private Integer isDoor; //是否是门型设备账号。0否，1是
	private Integer isDelete; //0正常，1删除
	private Timestamp createTime; //创建时间
	private String createAccount; //创建账号
	private String createRealName; //创建账号的真实姓名
	private Timestamp modifyTime; //修改时间
	private String modifyAccount; //修改账号
	private String modifyRealName; //修改账号的真实姓名
	/**
	 * 是否是门型设备账号。0否，1是
	 * @return
	 */
	public Integer getIsDoor() {
		return isDoor;
	}
	/**
	 * 是否是门型设备账号。0否，1是
	 * @param isDoor
	 */
	public void setIsDoor(Integer isDoor) {
		this.isDoor = isDoor;
	}

	/**
	 * 存储session登录用户，所有隶属公司，比如A用户同时隶属仓库1和仓库2
	 * 例如：list[0]='123',list[1]='456' 其中123,456都是A用户的隶属公司
	 */
	private List<SystemOrg> memberOfOrgList;
	/**
	 * 存储session登录用户当前所选仓库
	 */
	private SystemOrg currentSystemOrg;
	/**
	 * 存储session登录用户，所选公司的所有子公司列表，包含当前所选公司
	 * 例如：list[0]='123',list[1]='456' 其中123是当前所选公司
	 */
	private List<SystemOrg> filialeSystemOrgList;
	/**
	 * 存储session登录用户，所选公司的所有子公司ID，包含当前所选公司
	 * 例如： '123','456','789' 其中123是当前所选公司
	 */
	private String filialeSystemOrgIds;
	
	private List<SystemJob> systemJobList; //用户对应的工种列表
	private List<SystemOrg> systemOrgList; //用户对应的机构列表
	private List<SystemMenuWeb> systemMenuWebList; //用户对应的web端菜单权限列表
	private List<SystemButton> systemButtonList; //用户对应的web端按钮权限列表
	private List<SystemMenuApp> systemMenuAppList; //用户对应的app端菜单权限列表
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getIdCardNum() {
		return idCardNum;
	}
	public void setIdCardNum(String idCardNum) {
		this.idCardNum = idCardNum;
	}
	public Timestamp getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(Timestamp lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public Integer getIsActive() {
		return isActive;
	}
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}
	public Integer getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
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
	public Timestamp getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getModifyAccount() {
		return modifyAccount;
	}
	public void setModifyAccount(String modifyAccount) {
		this.modifyAccount = modifyAccount;
	}
	/**
	 * 用户对应的工种列表
	 * @return
	 */
	public List<SystemJob> getSystemJobList() {
		return systemJobList;
	}
	/**
	 * 用户对应的工种列表
	 * @param systemJobList
	 */
	public void setSystemJobList(List<SystemJob> systemJobList) {
		this.systemJobList = systemJobList;
	}
	/**
	 * 用户对应的机构列表
	 * @return
	 */
	public List<SystemOrg> getSystemOrgList() {
		return systemOrgList;
	}
	/**
	 * 用户对应的机构列表
	 * @param systemOrgList
	 */
	public void setSystemOrgList(List<SystemOrg> systemOrgList) {
		this.systemOrgList = systemOrgList;
	}
	public List<SystemMenuWeb> getSystemMenuWebList() {
		return systemMenuWebList;
	}
	public void setSystemMenuWebList(List<SystemMenuWeb> systemMenuWebList) {
		this.systemMenuWebList = systemMenuWebList;
	}
	public List<SystemButton> getSystemButtonList() {
		return systemButtonList;
	}
	public void setSystemButtonList(List<SystemButton> systemButtonList) {
		this.systemButtonList = systemButtonList;
	}
	public List<SystemMenuApp> getSystemMenuAppList() {
		return systemMenuAppList;
	}
	public void setSystemMenuAppList(List<SystemMenuApp> systemMenuAppList) {
		this.systemMenuAppList = systemMenuAppList;
	}
	/**
	 * 存储session登录用户，所选公司的所有子公司列表，包含当前所选公司
	 * 例如：list[0]='123',list[1]='456' 其中123是当前所选公司
	 */
	public List<SystemOrg> getFilialeSystemOrgList() {
		return filialeSystemOrgList;
	}
	/**
	 * 存储session登录用户，所选公司的所有子公司列表，包含当前所选公司
	 * 例如：list[0]='123',list[1]='456' 其中123是当前所选公司
	 */
	public void setFilialeSystemOrgList(List<SystemOrg> filialeSystemOrgList) {
		this.filialeSystemOrgList = filialeSystemOrgList;
	}
	/**
	 * 存储session登录用户，所选公司的所有子公司ID，包含当前所选公司
	 * 例如： '123','456','789' 其中123是当前所选公司
	 */
	public String getFilialeSystemOrgIds() {
		return filialeSystemOrgIds;
	}
	/**
	 * 存储session登录用户，所选公司的所有子公司ID，包含当前所选公司
	 * 例如： '123','456','789' 其中123是当前所选公司
	 */
	public void setFilialeSystemOrgIds(String filialeSystemOrgIds) {
		this.filialeSystemOrgIds = filialeSystemOrgIds;
	}
	/**
	 * 存储session登录用户当前所选仓库
	 */
	public SystemOrg getCurrentSystemOrg() {
		return currentSystemOrg;
	}
	/**
	 * 存储session登录用户当前所选仓库
	 */
	public void setCurrentSystemOrg(SystemOrg currentSystemOrg) {
		this.currentSystemOrg = currentSystemOrg;
	}
	public List<SystemOrg> getMemberOfOrgList() {
		return memberOfOrgList;
	}
	public void setMemberOfOrgList(List<SystemOrg> memberOfOrgList) {
		this.memberOfOrgList = memberOfOrgList;
	}
	public String getDefaultOrgId() {
		return defaultOrgId;
	}
	public void setDefaultOrgId(String defaultOrgId) {
		this.defaultOrgId = defaultOrgId;
	}
	public String getCreateRealName() {
		return createRealName;
	}
	public void setCreateRealName(String createRealName) {
		this.createRealName = createRealName;
	}
	public String getModifyRealName() {
		return modifyRealName;
	}
	public void setModifyRealName(String modifyRealName) {
		this.modifyRealName = modifyRealName;
	}

	@Override
	public String toString() {
		return "SystemUser{" +
				"account='" + account + '\'' +
				", password='" + password + '\'' +
				", realName='" + realName + '\'' +
				", idCardNum='" + idCardNum + '\'' +
				", lastLoginTime=" + lastLoginTime +
				", defaultOrgId='" + defaultOrgId + '\'' +
				", isActive=" + isActive +
				", isDelete=" + isDelete +
				", createTime=" + createTime +
				", createAccount='" + createAccount + '\'' +
				", createRealName='" + createRealName + '\'' +
				", modifyTime=" + modifyTime +
				", modifyAccount='" + modifyAccount + '\'' +
				", modifyRealName='" + modifyRealName + '\'' +
				", memberOfOrgList=" + memberOfOrgList +
				", currentSystemOrg=" + currentSystemOrg +
				", filialeSystemOrgList=" + filialeSystemOrgList +
				", filialeSystemOrgIds='" + filialeSystemOrgIds + '\'' +
				", systemJobList=" + systemJobList +
				", systemOrgList=" + systemOrgList +
				", systemMenuWebList=" + systemMenuWebList +
				", systemButtonList=" + systemButtonList +
				", systemMenuAppList=" + systemMenuAppList +
				'}';
	}
}
