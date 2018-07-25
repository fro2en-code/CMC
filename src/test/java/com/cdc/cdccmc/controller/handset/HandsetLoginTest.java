package com.cdc.cdccmc.controller.handset;

import static org.junit.Assert.*;
import javax.validation.constraints.AssertTrue;

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
import com.cdc.cdccmc.controller.web.LoginController;
import com.esotericsoftware.minlog.Log;

/**
 * 手持登陆测试
 * 
 * @author ZhuWen
 * @date 2018年3月14日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CdccmcApplication.class)
public class HandsetLoginTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(HandsetLoginTest.class); 

	@Autowired
	private WebApplicationContext context;
	private MockMvc mockMvc;
	private MockHttpSession mockHttpSession;

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
	 * 手持机切换仓库
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSetCurrentOrg() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.post("/handsetLogin/setCurrentOrg").contentType(MediaType.APPLICATION_JSON_UTF8)
						.param("orgId", "3e6bf4d607d9444483b36e2821687f8b")
						.session(mockHttpSession).accept(MediaType.APPLICATION_JSON))
				// .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
		// .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
		;
	}
	
	
	/**
	 * 手持机退出
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoginout() throws Exception {
		//退出登录
		MvcResult logoutResult = mockMvc.perform(
				MockMvcRequestBuilders.post("/handsetLogin/logout").contentType(MediaType.APPLICATION_JSON_UTF8)
						.session(mockHttpSession).accept(MediaType.APPLICATION_JSON))
				// .andExpect(MockMvcResultMatchers.status().isOk())
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		String jsonLogoutResult = logoutResult.getResponse().getContentAsString();
		JSONObject jsonLogoutObj = JSONObject.parseObject(jsonLogoutResult);
		assertTrue(200 == jsonLogoutObj.getInteger("status"));
//		LOG.info("jsonLogoutResult=="+jsonLogoutResult);
		
		//校验session
		MvcResult validResult = mockMvc.perform(
				MockMvcRequestBuilders.post("/validSessionLogin").contentType(MediaType.APPLICATION_JSON_UTF8)
						.session(mockHttpSession).accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andReturn();
		String jsonValidResult = validResult.getResponse().getContentAsString();
		JSONObject jsonValidObj = JSONObject.parseObject(jsonValidResult);
		assertTrue(100 == jsonValidObj.getInteger("status"));
//		LOG.info("jsonValidResult=="+jsonValidResult);
	}

}
