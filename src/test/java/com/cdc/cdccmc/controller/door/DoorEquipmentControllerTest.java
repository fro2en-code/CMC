package com.cdc.cdccmc.controller.door;

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
public class DoorEquipmentControllerTest {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DoorEquipmentControllerTest.class);
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
//	                .param("username", "menxing1").param("password", "123456") //隶属仓库：常熟整车CMC
//	                .param("username", "menxing6").param("password", "123456") //隶属仓库：无锡特瑞堡减震器有限公司
//	                .param("username", "menxing7").param("password", "123456") //隶属仓库：无锡特瑞堡减震器有限公司
//	                .param("username", "menxing8").param("password", "123456") //隶属仓库：凯史乐（上海）汽车工程技术有限公司昆山分公司
	                .param("username", "menxing9").param("password", "123456") //隶属仓库：无锡吉兴汽车零部件有限公司
//	                .param("username", "knk").param("password", "123456") //康奈可汽车电子（无锡）有限公司
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
    public void testSendEpc() throws Exception{
    	MvcResult result = mockMvc.perform(
	    			MockMvcRequestBuilders.post("/doorEquipment/sendEpc")
	                .contentType(MediaType.APPLICATION_JSON_UTF8)
//	                .param("epcIdList[]","CFPQQQQ-0001002","CFPQQQQ-0001003") //不存在的器具
//	                .param("epcIdList[]","CFP001BU-0006252") //组成托的托盘
//	                .param("epcIdList[]","CFP001BU-0000101") //没有组成托的托盘
//	                .param("epcIdList[]","CFK006BU-0013984")  //没有托盘 
	                .param("epcIdList[]","CFP001BU-0000002","CFK006BU-0013995","CFK006BU-0013994")
//	                .param("epcIdList[]","CFK006BU-0013996","CFK006BU-0013997","CFK006BU-0013998","CFK006BU-0013999")
//	                .param("epcIdList[]", "CFR330GY-0000030","CFR330GY-0000031","CFR330GY-0000032","CFR330GY-0000033","CFR330GY-0000034","CFR330GY-0000035")
//	                .param("epcIdList[]", "CFR330GY-0000002","CFR330GY-0000005","CFR330GY-0000006")
//	                .param("epcIdList[]","CFK002BU-0006610","CFK002BU-0006604","CFK002BU-0006611","CFK002BU-0006606","CFK002BU-0006603","CFK002BU-0006617") //EPC卡在上海办公室
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
    @Test
    public void testReceiveEpc() throws Exception{
    	MvcResult result = mockMvc.perform(
	    			MockMvcRequestBuilders.post("/doorEquipment/receiveEpc")
	                .contentType(MediaType.APPLICATION_JSON_UTF8)
//	                .param("epcIdList[]","CFK006BU-0013993","CFK006BU-0013996","CFK006BU-0013997") 
	                .param("epcIdList[]","CFR226GY-0230100","CFR226GY-0230101","CFR226GY-0230102","CFR226GY-0230103"
	                		,"CFR226GY-0230104","CFR226GY-0230105","CFR226GY-0230106","CFR226GY-0230107","CFR226GY-0230108") 
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

    //未关联到包装流转单
	@Test
	public void testdontLiuzhuan() throws Exception{
		MvcResult result = mockMvc.perform(

				MockMvcRequestBuilders.post("/doorEquipment/receiveEpc")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
	                .param("epcIdList[]","CFK00APP-0007717")
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

	//只收托盘
	@Test
	public void testzhishouTuopan() throws Exception{
		MvcResult result = mockMvc.perform(

				MockMvcRequestBuilders.post("/doorEquipment/receiveEpc")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.param("epcIdList[]","CFK00APP-0007703")
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

	//流转单以收货
	@Test
	public void testliuzhuandanyishouhuo() throws Exception{
		MvcResult result = mockMvc.perform(

				MockMvcRequestBuilders.post("/doorEquipment/receiveEpc")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.param("epcIdList[]","CFK00APP-0007703","CFK00APP-0007702","CFK00APP-0007701")
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

	//单器具未关联流传单
	@Test
	public void testdanqijuweiguanlianliuzhuandan() throws Exception{
		MvcResult result = mockMvc.perform(

				MockMvcRequestBuilders.post("/doorEquipment/receiveEpc")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.param("epcIdList[]","CFK00APP-0007704")
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

	//单器具关联到流转单未收货
	@Test
	public void testdanqijuyiguanlianliuzhuandan() throws Exception{
		MvcResult result = mockMvc.perform(

				MockMvcRequestBuilders.post("/doorEquipment/receiveEpc")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.param("epcIdList[]","CFK00APP-0007723")
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
