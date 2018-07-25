package com.cdc.cdccmc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.controller.door.DoorEquipmentController;
import com.cdc.cdccmc.controller.permission.AuthInterceptor;
import com.cdc.cdccmc.domain.sys.SystemOrg;
import com.cdc.cdccmc.domain.sys.SystemUser;

@SpringBootApplication
@Configuration
@ImportResource(locations={"classpath:spring-bean.xml"})
@EnableAsync
public class CdccmcApplicationTests2 extends WebMvcConfigurerAdapter{
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CdccmcApplicationTests2.class); 
	@Bean
    InternalResourceViewResolver internalResourceViewResolver () {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/pages");
        viewResolver.setSuffix(".jsp"); 
        return viewResolver;
    }
	  
	@Override
	public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
		registry.addInterceptor(new AuthInterceptor()).addPathPatterns("/**"); //controller层拦截器

	}
  
	/**
	 * 静态文件路径添加
	 * @param registry
	 */
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
        		.addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/static/");
        registry.addResourceHandler("/templates/**")
				.addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX+"/templates/");
        super.addResourceHandlers(registry);
    }
//	@Override
//	public void configureDefaultServletHandling(
//			DefaultServletHandlerConfigurer configurer) {
//		configurer.enable();
//	}

	/**
	 * 增加错误文件的定义
	 * @return
	 */
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
	 
	   return (container -> {
	        ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401");
	        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
	        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500");
	 
	        container.addErrorPages(error401Page, error404Page, error500Page);
	   });
	}

	public static void main(String[] args) {
//		SpringApplication.run(CdccmcApplication.class, args);
		//测试门型异步收货
		ConfigurableApplicationContext cac = SpringApplication.run(CdccmcApplication.class, args);
		testAsync(cac);
	}
	
	private static void testAsync(ConfigurableApplicationContext cac) {
		//获得门型登录bean
		Object beanObj = cac.getBean("doorEquipmentController");
		LOG.info("beanObj = " + beanObj);
		assertNotNull(beanObj);
		DoorEquipmentController bean = (DoorEquipmentController) beanObj;
	
		//组织第一个参数：session
		SystemUser user = new SystemUser();
		user.setAccount("menxing7");
		user.setRealName("门型7号（无锡特瑞堡）");
		SystemOrg currentSystemOrg = new SystemOrg();
		currentSystemOrg.setOrgId("4671e92901e24e7e915e6d9c232aea5d");
		currentSystemOrg.setOrgName("无锡特瑞堡减震器有限公司");
		user.setCurrentSystemOrg(currentSystemOrg );
		MockHttpSession session = new MockHttpSession();
		session.setAttribute(SysConstants.SESSION_USER,user);
		//组织第二个参数：epcIdList
		List<String> epcIdList = new ArrayList<String>();
		epcIdList.add("epc");
		//开始门型收货
		bean.receiveEpc(session, epcIdList );
	}
}
