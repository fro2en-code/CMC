package com.cdc.cdccmc.controller.handset;

import static org.junit.Assert.assertEquals;

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

/** 
 * 手持盘点测试
 * @author ZhuWen
 * @date 2018年3月14日
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CdccmcApplication.class)
public class HandsetInventoryTest {
	@Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    private MockHttpSession mockHttpSession; 
    
    @Before
    public void setUp() throws Exception {
 //       mvc = MockMvcBuilders.standaloneSetup(new TestController()).build();
    	mockMvc = MockMvcBuilders.webAppContextSetup(context).build();//建议使用这种
    	initLogin();
    }
    public void initLogin() throws Exception {
    	MvcResult result = mockMvc.perform(
	    			MockMvcRequestBuilders.post("/handsetLogin/login")
	                .contentType(MediaType.APPLICATION_JSON_UTF8)
	                .param("username", "licao").param("password", "1234")
	                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                //.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
                ;
    	mockHttpSession = (MockHttpSession) result.getRequest().getSession();
    	
    	//断言登录接口调用成功
    	assertEquals(result.getResponse().getStatus(), 200);
    	String jsonResultStr = result.getResponse().getContentAsString();
    	JSONObject jsonObj = JSONObject.parseObject(jsonResultStr);
    	//断言账号登录成功
    	assertEquals(jsonObj.getString("status"), "200");
    	mockHttpSession = (MockHttpSession) result.getRequest().getSession();
    }
    
    
    
    
    /**
	 * 查询当前仓库是否有正在进行中的盘点任务,如果有则返回当前仓库库区信息以及盘点单信息
	 * 
	 * @param sessionUser
	 * @return
	 */
    @Test
    public void testgetCurrentInventoryMain() throws Exception{
    	mockMvc.perform(
	    			MockMvcRequestBuilders.post("/handsetInventory/getCurrentInventoryMain")
	                .contentType(MediaType.APPLICATION_JSON_UTF8)
	                .param("ajaxBean","")
	                .session(mockHttpSession)  
	                .accept(MediaType.APPLICATION_JSON)
                )
                //.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                //.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
                ;
    }
    
    /**
	 * 进行盘点操作
	 * 
	 * @param sessionUser
	 * @param inventoryMain
	 * @param area
	 * @param epcIdList
	 * @return
	 */
    @Test
    public void testsubmitInventory() throws Exception{
    	mockMvc.perform(
	    			MockMvcRequestBuilders.post("/handsetInventory/submitInventory")
	                .contentType(MediaType.APPLICATION_JSON_UTF8)
	                .param("inventoryId","WUHAN-PD-201803170002").param("areaId", "10").param("epcIdList[]", "zzzz,cccc")
	                .session(mockHttpSession)  
	                .accept(MediaType.APPLICATION_JSON)
                )
                //.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                //.andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("SUCCESS")))
                ;
    }
    
}
