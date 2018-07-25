package com.cdc.cdccmc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.AjaxBean;
import com.cdc.cdccmc.domain.InventoryHistory;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.domain.print.PritCirculateOrder;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

/** 
 * 
 * @author ZhuWen
 * @date 2017年12月29日
 */
public class MainTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MainTest.class);
	@Test
	public void 测试流转单主单收货() {
		List<Integer> countList = new ArrayList<Integer>();
		countList.add(0);
		countList.add(1);
		String isReceive = null;
		//如果receive_number = 1的数量与这个流转单所有epc编号个数相符，则认为：2 已全部收货
		if((1==countList.size() && countList.get(0).intValue() != SysConstants.INTEGER_0) || (countList.get(0).intValue() == countList.get(1).intValue())) {
			isReceive = "2";
		}else {
			//如果第一位是0，则是：未收货
			if(countList.get(0).intValue() == SysConstants.INTEGER_0) {
				isReceive = "0"; //未收货的话对主单表 t_circulate_order什么都不做
			}else {
				isReceive = "1"; //已部分收货
			}
		}
		LOG.info("countList.get(0)="+countList.get(0));
		LOG.info("countList.get(1)="+countList.get(1));
		LOG.info("isReceive="+isReceive);
	}
	@Test
	public void 测试数字转换() {
		LOG.info("s="+Integer.valueOf("005"));
	}
	
	@Test
	public void 测试BigDecimal() {
		BigDecimal s = (new BigDecimal(11).subtract(new BigDecimal(10)));
		int c = s.compareTo(BigDecimal.ZERO);
		boolean boo = c > 0;
		LOG.info("s="+s + "; c="+c+"; boo="+boo);
	}
	@Test
	public void 队列方法测试() throws InterruptedException, IOException {
		ConcurrentLinkedQueue<Integer> list1 = new ConcurrentLinkedQueue<Integer>();
		list1.add(1);
		list1.add(10);
		list1.add(20);
		list1.add(30);
		list1.add(50);
		List<Integer> containerEpcIdList = new ArrayList<Integer>();
		CollectionUtils.addAll(containerEpcIdList, list1);
		LOG.info("containerEpcIdList=="+JSONObject.toJSONString(containerEpcIdList));
	}
	@Test
	public void 队列拷贝测试() throws InterruptedException, IOException {
		ConcurrentLinkedQueue<Integer> list1 = new ConcurrentLinkedQueue<Integer>();
		list1.add(1);
		list1.add(10);
		list1.add(20);
		list1.add(30);
		list1.add(50);
		ConcurrentLinkedQueue<Integer> list2 = new ConcurrentLinkedQueue<Integer>();
		CollectionUtils.addAll(list2, list1);
		list1.remove(20);
		list1.remove(30);
		LOG.info("list1=="+JSONObject.toJSONString(list1));
		LOG.info("list2=="+JSONObject.toJSONString(list2));
	}
	@Test
	public void 队列删除元素测试() throws InterruptedException, IOException {
		ConcurrentLinkedQueue<Integer> epcList = new ConcurrentLinkedQueue<Integer>();
		epcList.add(1);
		epcList.add(10);
		epcList.add(20);
		epcList.add(50);
		ConcurrentLinkedQueue<Integer> list2 = new ConcurrentLinkedQueue<Integer>();
		for (int i = 0; i < 60; i++) {
			list2.add(i);
		}
		for (Integer i2 : list2) {
			for (Integer e : epcList) {
				if(i2.equals(e)) {
					list2.remove(i2);
				}
			}
		}
		LOG.info(JSONObject.toJSONString(list2));
	}
	@Test
	public void 队列删除元素测试2() throws InterruptedException, IOException {
		ConcurrentLinkedQueue<Integer> list1 = new ConcurrentLinkedQueue<Integer>();
		list1.add(1);
		list1.add(3);
		list1.add(7);
		ConcurrentLinkedQueue<Integer> list2 = new ConcurrentLinkedQueue<Integer>();
		for (int i = 0; i < 10; i++) {
			list2.add(i);
		}
		for (Integer e : list1) {
			if(list2.contains(e)) {
				list2.remove(e);
			}
		}
		LOG.info(JSONObject.toJSONString(list2));
	}
	
	@Test
	public void 测试InventoryHistory类的equals和hashCode方法() throws InterruptedException, IOException {
		InventoryHistory h1 = new InventoryHistory();
		InventoryHistory h2 = new InventoryHistory();
		InventoryHistory h3 = new InventoryHistory();
		h1.setOrgId("h1");
		h1.setContainerCode("212109");
		h1.setInventoryLatestId(new BigInteger("100"));
		h2.setOrgId("h1");
		h2.setContainerCode("212109");
		h2.setInventoryLatestId(new BigInteger("20"));
		h3.setOrgId("h1");
		h3.setContainerCode("212109");
		h3.setInventoryLatestId(new BigInteger("30"));
		assertEquals(h1, h2);
		assertEquals(h2, h1);
		assertEquals(h2, h3);
		assertEquals(h2, h1);
		assertEquals(h1, h3);
		assertEquals(h3, h1);
//		LOG.info("h1==="+JSONObject.toJSONString(h1));
		h1.setInventoryLatestId(new BigInteger("333"));
//		LOG.info("h1==="+JSONObject.toJSONString(h1));
		assertEquals(h2, h1);
		assertEquals(h1, h3);
		

		InventoryHistory h4 = new InventoryHistory();
		h4.setOrgId("h4");
		h4.setContainerCode("212109");
		h4.setInventoryLatestId(new BigInteger("401"));
		
		My t = new My();
		t.run(h2);
		t.run(h3);
		t.run(h4);
		
	    Runnable r1 = new Runnable() {
			@Override
			public void run() {
				try {
					LOG.info("线程1启动");
					t.run(h1);
					LOG.info("线程1结束");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r1).start();
		
	    Runnable r2 = new Runnable() {
			@Override
			public void run() {
				try {
					LOG.info("线程2启动");
					t.run(h2);
					LOG.info("线程2结束");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r2).start();

	    Runnable r3 = new Runnable() {
			@Override
			public void run() {
				try {
					LOG.info("线程3启动");
					t.run(h3);
					LOG.info("线程3结束");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r3).start();

	    Runnable r4 = new Runnable() {
			@Override
			public void run() {
				try {
					LOG.info("线程4启动");
					t.run(h4);
					LOG.info("线程4结束");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		new Thread(r4).start();
		
		while(true) {
			
		}
	}
	
	private class My {
		public void run(InventoryHistory ih) throws IOException {
			LOG.info("run。run。run。run。");
			synchronized (ih) {
				LOG.info("【::】"+JSONObject.toJSONString(ih));
				for (int i = 0; i < 30000; i++) {
					File file = new File("d:/temp/a" + ih.getOrgId()+i + ".txt");
					file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					fos.write((ih.getOrgId() + "--" + ih.getContainerCode() + i).getBytes());
					fos.close();
				}
			}
		}
	}

	@Test
	public void test() {
		boolean isHaveWhere = false;
		LOG.info(isHaveWhere?" and ":" where ");
	}
	
	@Test
	public void 总页数计算(){
		Integer total = 10; 
		Integer pageSize = 3;
		
		BigDecimal b1 = new BigDecimal(total);
		BigDecimal b2 = new BigDecimal(pageSize);
		int scale = 0; // 表示表示需要精确到小数点以后几位。 
		String result = b1.divide(b2, scale, BigDecimal.ROUND_UP).toString();
		LOG.info("result=="+result);
	}
	@Test
	public void 字符串截取测试() {
		String a = "2,3,";
		LOG.info(a.substring(0, a.length()-1));
	}
	@Test
	public void null字符串拼接() {
		String a = null;
		LOG.info(":::" + (a == null));
	}
	@Test
	public void 生成insert语句() {
		String insert = "";
		for (int i = 1; i <= 44; i++) {
			insert += "INSERT INTO t_system_usermenu_web (account, menu_id, create_time) VALUES ('zhuwen', '"+i+"', sysdate());";
		}
		LOG.info(insert);
	}

	@Test
	public void 测试空集合判断() {
		List list = null;
		assertTrue(CollectionUtils.isEmpty(list));
	}

	@Test
	public void 密码正则匹配() {
        String mainRegex = "[0-9a-zA-Z_]{6,15}";
        assertTrue("223AdGGG".matches(mainRegex));
        assertTrue(!"哦25543Ad".matches(mainRegex));
	}

	@Test
	public void 数字正则匹配(){
		String regex = "[0-9]{1,5}";
		System.out.println("0".matches(regex));
	}

	@Test
	public void 器具代码正则匹配(){
		System.out.println("中文-".matches(SysConstants.REGEX_CONTAINER_CODE));
	}

	@Test
	public void 器具代码类型正则匹配(){
		System.out.println("ABC01中文".matches(SysConstants.REGEX_CONTAINER_CODE_TYPE));
	}

	@Test
	public void jsonTest(){
		PritCirculateOrder domain = new PritCirculateOrder();
		domain.setTitle("包装流转单");
		domain.setBarCode("CMCCG2017083007");
		domain.setSendLocation("CMC");
		domain.setReceiveByShopFlag("SH027");
		domain.setTransactionType("出库");
		domain.setCmcReceiveFlag("YES");
		domain.setCmcReceiveCode("Code2");
		domain.setDescription("描述信息");
		domain.setCarNo("苏A1278888");
		domain.setTransportCompany("顺丰");
		domain.setConfirm("李书军");
		domain.setContact("15810665042");
		domain.setSenderConfirm("sender");
		domain.setSenderConfirmDate("2017-09-09");
		domain.setReceiverConfirm("LISHUJUN");
		domain.setReceiverConfirmDate("2017-10-11");
		domain.setBillNumber("A123433");
		List<Map<String,Object>> list = new ArrayList<>();
		Integer i =0;
		while (i<21){
			CirculateDetail o = new CirculateDetail();
			o.setContainerCode("包装代码"+i);
			o.setContainerName("包装名称"+i);
			o.setContainerSpecification("规格x规格"+i);
			o.setPlanNumber(i+1);
			o.setSendNumber(i-1);
			o.setRemark("备注"+i);

			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id",String.valueOf(i));
			map.put("code",o.getContainerCode());
			map.put("name",o.getContainerName());
			map.put("size",o.getContainerSpecification());
			map.put("planCount",o.getPlanNumber());
			map.put("sendCount",o.getSendNumber());
			map.put("remark",o.getRemark());
			list.add(map);
			i++;
		}
		String s = JSON.toJSONString(domain);
		PritCirculateOrder orderDto = JSON.parseObject(s,PritCirculateOrder.class);
		System.out.println(orderDto.getDetails().size());
	}


	public static List makeDetailList(){
		List<Map<String, Object>> detail = new ArrayList<Map<String, Object>>();
		int code =1000;
		for(int i=0; i<10; i++){
			code++;
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("id",String.valueOf(i));
			map.put("code",String.valueOf(code));
			map.put("name",String.valueOf(code));
			map.put("size","10");
			map.put("planCount","20");
			map.put("sendCount","30");
			map.put("receiveCount","40");
			map.put("remark","remake");
			detail.add(map);
		}

		return detail;
	}

	@Test
	public void testString(){
		String a = "topic.order.tests";
		System.out.println(a.split("\\.").length);
	}

	@Test
	public void 数字补位测试(){
		Integer serialNumber = 1378;
		int length = 4;
		String serialNumberStr = serialNumber.toString();
		int forLength = length - serialNumberStr.length();
		for (int i = 0; i < forLength; i++) {
			if(serialNumberStr.length() > length){
				break;
			}
			serialNumberStr = "0" + serialNumberStr;
		}
		LOG.info(serialNumberStr);
	}

	@Test
	public void 当前时间加一秒(){
		Timestamp time1 = new Timestamp(new Date().getTime() + 10001); 
		Timestamp time2 = new Timestamp(new Date().getTime()); 
		System.out.println(time1);
		System.out.println(time2);
	}
	
	@Test
	public void 正则匹配维修时长() {
		String hourRegex = "[1-9]{1}[0-9]{0,5}";
		String s = "1111222";
		LOG.info(s.matches(hourRegex)+"");
	}
	
	@Test
	public void 正则匹配器具代码() {
		String s = "NBW272313---";
		LOG.info(s.matches(SysConstants.REGEX_CONTAINER_CODE)+"");
	}
	
	@Test
	public void 除法() {
		BigDecimal a = new BigDecimal(SysConstants.MAX_FILE_UPLOAD_SIZE);
		BigDecimal b = new BigDecimal(1024 * 1024);
		LOG.info(a.divide(b).toString());
	}
	
	@Test
	public void ConcurrentLinkedQueue测试poll() {
		 ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
		 queue.add("11");
		 queue.add("22");
		 queue.add("33");
		 String p = queue.poll();
		 LOG.info(p);
		 LOG.info(JSONObject.toJSONString(queue));
	}
	
	@Test
	public void ConcurrentLinkedQueue测试peek() {
		 ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
		 queue.add("11");
		 queue.add("22");
		 queue.add("33");
		 for (int i = 0; i < queue.size(); i++) {
			 String p = queue.peek(); 
			 LOG.info(p);
			 LOG.info(JSONObject.toJSONString(queue));
		}
	}
	@Test
	public void ConcurrentLinkedQueue测试iterator() {
		 ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
		 queue.add("11");
		 queue.add("22");
		 queue.add("33");
		 Iterator<String> it = queue.iterator();
		 while (it.hasNext()) {
			 String str = it.next();
			 LOG.info(str);
			 LOG.info(JSONObject.toJSONString(queue));
		}
	}
	
	@Test
	public void 身份证号码测试() {
			String REGEX_ID_CARD_NUM = "(\\d{15})|(\\d{17}[0-9Xx]{1})";
			String 陈婷婷 = "11111111111111151x";
			System.out.println(陈婷婷.matches(REGEX_ID_CARD_NUM));
			assertTrue(陈婷婷.matches(REGEX_ID_CARD_NUM));
	}
	
	@Test
	public void isNotBlank测试() {
		assertTrue(!StringUtils.isNotBlank(null));
		assertTrue(!StringUtils.isNotEmpty(null));
	}
	
	@Test
	public void AjaxBean封装成Json() {
		AjaxBean ajaxBean = AjaxBean.SUCCESS();
		LOG.info(JSONObject.toJSONString(ajaxBean));
	}
	@Test
	public void sqlToString() {
		String sql = "INSERT INTO t_maintain (maintain_id, epc_id, maintain_apply_bad_reason, print_code, container_code, container_type_id, container_type_name, maintain_state,maintain_level, maintain_apply_time, maintain_apply_account, maintain_apply_real_name, maintain_apply_org_id, maintain_apply_org_name,maintain_apply_area_id,maintain_apply_area_name) "
				+ " VALUES (:maintainId, :epcId, :maintainApplyBadReason,:printCode, :containerCode, "
				+ ":containerTypeId, :containerTypeName, :maintainState,  :maintainLevel, sysdate(), "
				+ ":maintainApplyAccount, :maintainApplyRealName, :maintainApplyOrgId,:maintainApplyOrgName,"
				+ ":maintainApplyAreaId,:maintainApplyAreaName)";
		LOG.info(sql);
	}
	
}
