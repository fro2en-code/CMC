package com.cdc.cdccmc.controller.handset;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.CdccmcApplication;
import com.cdc.cdccmc.domain.circulate.CirculateDetail;
import com.cdc.cdccmc.service.CirculateOrderService;
import com.cdc.cdccmc.service.CirculateService;

/**
 * 手持入库测试
 * 
 * @author ZhuWen
 * @date 2018年3月14日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CdccmcApplication.class)
public class HandsetInputTest {
	@Autowired
	private WebApplicationContext context;
	private MockMvc mockMvc;
	private MockHttpSession mockHttpSession;
	private CirculateOrderService circulateOrderService;

	@Before
	public void setUp() throws Exception {
		// mvc = MockMvcBuilders.standaloneSetup(new TestController()).build();
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();// 建议使用这种
		initLogin();
	}

	public void initLogin() throws Exception {
		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/handsetLogin/login").contentType(MediaType.APPLICATION_JSON_UTF8)
						.param("username", "linkai").param("password", "123456").accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn()
		// .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
		;
		// 断言登录接口调用成功
		assertEquals(result.getResponse().getStatus(), 200);
		String jsonResultStr = result.getResponse().getContentAsString();
		JSONObject jsonObj = JSONObject.parseObject(jsonResultStr);
		// 断言账号登录成功
		assertEquals(jsonObj.getString("status"), "200");
		mockHttpSession = (MockHttpSession) result.getRequest().getSession();
	}

	/**
	 * 获取包装流转单的收货状态
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGetCirculateOrderStatus() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/handsetInput/getCirculateOrderStatus")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("orderCode", "ZGS201803140008")
				.session(mockHttpSession).accept(MediaType.APPLICATION_JSON))
				// .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
		// .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
		;
	}

	/**
	 * 手持机扫描器具
	 * 
	 * @throws Exception
	 */
	@Test
	public void testScanContainer() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/handsetInput/scanContainer")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("orderCode", "WUHAN201803160004")
				.param("epcIdList[]", "wuhantest-001,wuhantest-002,wuhantest-003").session(mockHttpSession)
				.accept(MediaType.APPLICATION_JSON))
				// .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
		// .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
		;
	}

	/**
	 * 实收入库
	 * 
	 * @throws Exception
	 */
	@Test
	public void testnOrgActualScan() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/handsetInput/inOrgActualScan")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("orderCode", "NANJING201803170027")
				.param("epcIdList[]", "nanjingtest-010,nanjingtest-011").param("differenceRemark", "进行实收入库")
				.session(mockHttpSession).accept(MediaType.APPLICATION_JSON))
				// .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
		// .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
		;
	}

	/**
	 * 实收入库--当已进行过人工收货
	 * 
	 * @throws Exception
	 */
	@Test
	public void testnOrgActualScanByIsManualReceive() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/handsetInput/inOrgActualScan")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("orderCode", "NANJING201803170027")
				.param("epcIdList[]", "nanjingtest-010,nanjingtest-011")
				
				.session(mockHttpSession).accept(MediaType.APPLICATION_JSON))
				// .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
		// .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
		;
	}
	
	/**
	 * 实收入库
	 * 
	 * @throws Exception
	 */
	@Test
	public void testnOrgActualScanByContainerGroup() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/handsetInput/inOrgActualScan")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("orderCode", "NANJING201803220001")
				.param("epcIdList[]", "xiaohua-001,xiaohua-002")
				.param("differenceRemark", "死胖子")
				.session(mockHttpSession).accept(MediaType.APPLICATION_JSON))
				// .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
		// .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
		;
	}
	
	
	
	
	
	

	/**
	 * 照单全收
	 * 
	 * @throws Exception
	 */
	@Test
	public void testnOrgDifferent() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/handsetInput/inOrgAll")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("orderCode", "ZGS201803220002")
				.session(mockHttpSession).accept(MediaType.APPLICATION_JSON))
				// .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
		// .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
		;
	}

	/**
	 * 托盘盖入库
	 * 
	 * @throws Exception
	 */
	@Test
	public void testnOrgTrayCover() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.post("/handsetInput/inOrgTrayCover")
				.contentType(MediaType.APPLICATION_JSON_UTF8).param("orderCode", "ZGS201803150015")
				.param("epcIdList[]", "托盘盖1号,托盘盖2号").session(mockHttpSession).accept(MediaType.APPLICATION_JSON))
				// .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
		// .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
		;
	}

}
