package com.cdc.cdccmc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.cdc.cdccmc.controller.permission.AuthInterceptor;

@SpringBootApplication
@Configuration
@ImportResource(locations={"classpath:spring-bean.xml"})
@EnableAsync
public class CdccmcApplication extends WebMvcConfigurerAdapter{
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CdccmcApplication.class); 
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
		SpringApplication.run(CdccmcApplication.class, args);
	}
}
