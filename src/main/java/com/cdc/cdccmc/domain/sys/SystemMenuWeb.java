package com.cdc.cdccmc.domain.sys;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** 
 * web菜单表 T_SYSTEM_MENU_WEB
 * @author ZhuWen
 * @date 2017-12-28
 */
public class SystemMenuWeb {
	private String menuId; //web菜单ID
	private String menuName; //菜单名称
	private Integer menuOrder; //排序
	private String menuUri; //菜单uri
	private Integer menuLevel; //菜单层级
	private String parentMenuId; //父菜单ID
	private Integer isActive; //菜单状态。0启用，1禁用
	private Timestamp createTime;
	private String createAccount; //创建账号
	private String createRealName;//创建账号的姓名
	private Timestamp modifyTime; //修改时间
	private String modifyAccount; //修改账号
	private String modifyRealName;//修改账号的姓名

	@Override
    public boolean equals(Object o) {
        if (o == this){
        	return true;  
        }
        if (!(o instanceof SystemMenuWeb)) {  
            return false;  
        }  
        SystemJobmenu thisObj = (SystemJobmenu) o;  
        return Objects.equals(menuId, thisObj.getMenuId());  
    }
	
	@Override  
    public int hashCode() {  
        return Objects.hash(menuId);  
    } 
	
	/**
	 * 存储二级菜单列表
	 */
	private List<SystemMenuWeb> childMenu = new ArrayList<SystemMenuWeb>();
	
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public Integer getMenuOrder() {
		return menuOrder;
	}
	public void setMenuOrder(Integer menuOrder) {
		this.menuOrder = menuOrder;
	}
	public String getMenuUri() {
		return menuUri;
	}
	public void setMenuUri(String menuUri) {
		this.menuUri = menuUri;
	}
	public Integer getMenuLevel() {
		return menuLevel;
	}
	public void setMenuLevel(Integer menuLevel) {
		this.menuLevel = menuLevel;
	}
	public String getParentMenuId() {
		return parentMenuId;
	}
	public void setParentMenuId(String parentMenuId) {
		this.parentMenuId = parentMenuId;
	}
	public Integer getIsActive() {
		return isActive;
	}
	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
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
	public String getModifyRealName() {
		return modifyRealName;
	}
	public void setModifyRealName(String modifyRealName) {
		this.modifyRealName = modifyRealName;
	}
	public List<SystemMenuWeb> getChildMenu() {
		return childMenu;
	}
	public void setChildMenu(List<SystemMenuWeb> childMenu) {
		this.childMenu = childMenu;
	}
}
