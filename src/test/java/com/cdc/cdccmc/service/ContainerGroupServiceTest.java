package com.cdc.cdccmc.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cdc.cdccmc.common.util.StatusCode;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.common.util.UUIDUtil;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.domain.sys.SystemUser;
import com.cdc.cdccmc.service.basic.ContainerCodeService;

/** 
 * @author Clm
 * @date 2018-4-24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ContainerGroupServiceTest {

	@Autowired
	private ContainerGroupService containerGroupService;
	@Autowired
	private ContainerService containerService;
	
	@Test
    public void testAddOneContainerGroup() throws Exception {
	//定义一个集合接收器具
	List<Container> containerList = new ArrayList<Container>();
	// 生成组托识别号
	String groupId = UUIDUtil.creatUUID();
	//组托EPC器具关键字
	String groupType = "单独成托";
	//new一个sessionUser对象并且赋值
	SystemUser sessionUser =new SystemUser();
	sessionUser.setAccount("Account_clm");
	sessionUser.setRealName("Name_CLM");
	// 创建器具列表中存在的epcId
	String epcId = "CFK00APP-0007702";
	// 获取器具信息
	Container con = containerService.getContainerByEpcId(epcId);
	//将器具信息添加到集合中
	containerList.add(con);
	//组托EPC编号
	String groupEpcId = epcId;
	// 创建组托
	containerGroupService.createContainerGroupFromApp(groupId, groupEpcId, groupType, sessionUser, containerList);
	}
	
	@Test
    public void testAddAllContainerGroup() throws Exception {
	//定义一个集合接收epcId
	List<String> epcIdList = new ArrayList<String>(); 
	epcIdList.add("CFK00APP-0007704");
	epcIdList.add("CFK00APP-0007705");	
	epcIdList.add("CFK00APP-0007706");//这个是托盘	
	String groupEpcId = "";// 组托EPC编号
	String groupType = "";// 组托EPC器具关键字
	//定义一个集合接收器具
	List<Container> containerList = new ArrayList<Container>();
	// 生成组托识别号
	String groupId = UUIDUtil.creatUUID();
	//new一个sessionUser对象并且赋值
	SystemUser sessionUser =new SystemUser();
	sessionUser.setAccount("Account_all_clm");
	sessionUser.setRealName("Name_all_CLM");
	for (String epcId : epcIdList) {
		// 获取器具信息
		Container con = containerService.getContainerByEpcId(epcId);
		if (con.getContainerTypeId().equals(SysConstants.STRING_1)) {
			// 将托盘的epcId设置为组托epcId
			groupEpcId = con.getEpcId();
		}
		groupType = "托盘";
		// 将器具信息存储在集合中
		containerList.add(con);
	}	
	// 创建组托
	containerGroupService.createContainerGroupFromApp(groupId, groupEpcId, groupType, sessionUser, containerList);
	}
}
