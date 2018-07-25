package com.cdc.cdccmc.domain;

import java.sql.Timestamp;

/**
 * 索赔明细表 t_claim_detail
 * @author Jerry
 * @date 2018/1/24 10:20
 */
public class ClaimDetail {
    private String claimDetailId;//索赔主键ID
    private String claimType;//索赔来源，1：流转单差异索赔，2：盘点丢失索赔
    private String orderCode; //包装流转单单号
    private String inventoryId;//盘点编号，参考表T_INVENTORY_MAIN
    private String epcId; //EPC编号
    private String containerCode; //器具代码
    private String containerName; //器具名称
    private String containerTypeId; //器具类型ID
    private String containerTypeName; //器具类型名称
	private Integer differenceNumber; //差异数量
    private String remark; //备注
    private Timestamp createTime; //创建时间
    private String createAccount; //创建账号
    private String createRealName; //创建账号的姓名
    private String createOrgId;//创建仓库ID
    private String createOrgName;//创建仓库名称

    
    public String getCreateOrgName() {
		return createOrgName;
	}

	public void setCreateOrgName(String createOrgName) {
		this.createOrgName = createOrgName;
	}

	public String getCreateOrgId() {
        return createOrgId;
    }

    public void setCreateOrgId(String createOrgId) {
        this.createOrgId = createOrgId;
    }

    public String getClaimDetailId() {
		return claimDetailId;
	}

	public void setClaimDetailId(String claimDetailId) {
		this.claimDetailId = claimDetailId;
	}

	public String getClaimType() {
        return claimType;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getEpcId() {
        return epcId;
    }

    public void setEpcId(String epcId) {
        this.epcId = epcId;
    }

    public String getContainerCode() {
        return containerCode;
    }

    public void setContainerCode(String containerCode) {
        this.containerCode = containerCode;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getContainerTypeId() {
        return containerTypeId;
    }

    public void setContainerTypeId(String containerTypeId) {
        this.containerTypeId = containerTypeId;
    }

    public String getContainerTypeName() {
        return containerTypeName;
    }

    public void setContainerTypeName(String containerTypeName) {
        this.containerTypeName = containerTypeName;
    }

    public Integer getDifferenceNumber() {
        return differenceNumber;
    }

    public void setDifferenceNumber(Integer differenceNumber) {
        this.differenceNumber = differenceNumber;
    }

    public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
