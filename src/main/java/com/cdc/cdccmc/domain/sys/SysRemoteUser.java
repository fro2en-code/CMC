package com.cdc.cdccmc.domain.sys;


public class SysRemoteUser {
	/**
	 * 鉴权微服务应用的token
	 */
	private String access_token;
	/**
	 * 账号
	 */
	private String account	;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 用户姓名
	 */
	private String real_name;	
	/**
	 * 11位手机号码
	 */
	private String mobile	;
	/**
	 * 生日，格式为yyyy-mm-dd
	 */
	private String birthday	;
	/**
	 * 性别 0未知，1男，2女。默认是0
	 */
	private String gender	;
	/**
	 * 身份证
	 */
	private String id_card_num	;
	/**
	 * 电子邮箱地址
	 */
	private String email	;
	/**
	 * 原有系统用户ID
	 */
	private String old_account_id	;
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
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
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getId_card_num() {
		return id_card_num;
	}
	public void setId_card_num(String id_card_num) {
		this.id_card_num = id_card_num;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOld_account_id() {
		return old_account_id;
	}
	public void setOld_account_id(String old_account_id) {
		this.old_account_id = old_account_id;
	}

	
}
