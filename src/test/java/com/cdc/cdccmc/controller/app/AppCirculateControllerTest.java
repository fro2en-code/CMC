package com.cdc.cdccmc.controller.app;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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
/** 
 * app测试
 * @author ZhuWen
 * @date 2018年5月20日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CdccmcApplication.class)
public class AppCirculateControllerTest {
	@Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession;
    @Autowired
	JdbcTemplate jdbcTemplate;
    @Before
    public void setUp() throws Exception {
 //       mvc = MockMvcBuilders.standaloneSetup(new TestController()).build();
    	mockMvc = MockMvcBuilders.webAppContextSetup(context).build();//建议使用这种
    	initLogin();
    }
    
    public void initLogin() throws Exception {
    	//1 终究
		//2 嘉欣
		//3 长春
    	MvcResult result = mockMvc.perform(
	    			MockMvcRequestBuilders.post("/appLogin/login")
	                .contentType(MediaType.APPLICATION_JSON_UTF8)
	                .param("userName", "zhuwen").param("pwd", "123456")
	                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                //.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
                ;
    	//断言登录接口调用成功
    	assertEquals(result.getResponse().getStatus(), 200);
    	String jsonResultStr = result.getResponse().getContentAsString();
    	JSONObject jsonObj = JSONObject.parseObject(jsonResultStr);
    	//断言账号登录成功
    	assertEquals(jsonObj.getString("status"), "200");
    	mockHttpSession = (MockHttpSession) result.getRequest().getSession();
    }
  //测试门型发货接口
    @Test
    public void testDoorScanAdd() throws Exception{
    	MvcResult result = mockMvc.perform(
	    			MockMvcRequestBuilders.post("/appCirculate/doorScanAdd")
	                .contentType(MediaType.APPLICATION_JSON_UTF8)
	                .param("doorAccount","menxing1") //账号
	                .param("cirulateOrderCode","CSCMC201805280007") //单号
	                .param("groupId","null") //是否是组托，0不是（epc_id不为空），1是（group_id不为空）。
	                .param("scanTime","2018-05-28 23:12:25") //
	                .session(mockHttpSession)
	                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    	//断言登录接口调用成功
    	assertEquals(result.getResponse().getStatus(), 200);
    	String jsonResultStr = result.getResponse().getContentAsString();
    	JSONObject jsonObj = JSONObject.parseObject(jsonResultStr);
    	//断言
    	assertEquals(jsonObj.getString("status"), "200");
    }
}
