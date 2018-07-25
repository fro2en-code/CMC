package com.cdc.cdccmc.controller.web;

import static org.junit.Assert.*;

import org.hamcrest.Matchers;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.cdc.cdccmc.CdccmcApplication;
import com.cdc.cdccmc.domain.container.Container;
import com.cdc.cdccmc.service.DoorEquipmentService;
import com.esotericsoftware.minlog.Log;

import java.util.ArrayList;
import java.util.List;

/** 
 * 门型设备测试
 * @author ZhuWen
 * @date 2018年3月14日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CdccmcApplication.class)
public class CirculateOrderControllerTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CirculateOrderControllerTest.class);
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
	    			MockMvcRequestBuilders.post("/doorEquipment/login")
	                .contentType(MediaType.APPLICATION_JSON_UTF8)
	                .param("username", "zhuwen").param("password", "123456") //隶属仓库：常熟整车CMC
//	                .param("username", "mengxing12").param("password", "123456") //隶属仓库：	 凯史乐（上海）汽车工程技术有限公司昆山分公司
//	                .param("username", "menxing7").param("password", "123456") //隶属仓库：无锡特瑞堡减震器有限公司
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
    	
    	//登陆成功后获取session
    	mockHttpSession = (MockHttpSession) result.getRequest().getSession();
    	
    	//账号切换仓库
    	result = mockMvc.perform(
    			MockMvcRequestBuilders.post("/page/changeCurrentOrg")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("orgId","dc6531dc653c4fea897dcbe84a1be3e3")
                .session(mockHttpSession)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andDo(MockMvcResultHandlers.print())
            .andReturn();
    	//断言账号切换仓库成功
    	assertEquals(jsonObj.getString("status"), "200");
    }
    //测试门型发货接口
    @Test
    public void testPrintCirculateOrder() throws Exception{
    	MvcResult result = mockMvc.perform(
	    			MockMvcRequestBuilders.post("/circulateOrder/printCirculateOrder")
	                .contentType(MediaType.APPLICATION_JSON_UTF8)
	                .param("orderCode","CSCMC201806040008")
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
