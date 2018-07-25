package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;
import java.util.Objects;

/** 
 * 两张表结构一模一样：
 * 用户app端菜单权限表 T_SYSTEM_USERMENU_APP
 * 用户WEB端权限表  T_SYSTEM_USERMENU_WEB
 * @author ZhuWen
 * @date 2017-12-28
 */
public class SystemUsermenu {

	private String account; //联合主键1。账号，登录名
	private String orgId; //联合主键2。仓库ID
	private String menuId; //联合主键3。web或app菜单ID
	private String orgName; //仓库名称，冗余字段
	private String menuName; //菜单名称，冗余字段
	private Timestamp createTime;
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名

	@Override
    public boolean equals(Object o) {
        if (o == this){
        	return true;  
        }
        if (!(o instanceof SystemUsermenu)) {  
            return false;  
        }  
        SystemUsermenu thisObj = (SystemUsermenu) o;  
        return Objects.equals(account, thisObj.getAccount()) &&
        		Objects.equals(orgId, thisObj.getOrgId()) &&
        		Objects.equals(menuId, thisObj.getMenuId());  
    }
	
	@Override  
    public int hashCode() {  
        return Objects.hash(account,orgId,menuId);  
    } 
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
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
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	
}
