package com.cdc.cdccmc.controller.permission;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;

/** 
 * 
 * @author ZhuWen
 * @date 2018年3月13日
 */
public class RestErrorEndpoint 
//implements ErrorController
{
	private static final String PATH = "/error";


   /* @Autowired
    private ErrorAttributes errorAttributes;

//    @RequestMapping(value = PATH)
    @RequestMapping(value = PATH,  produces = {MediaType.APPLICATION_JSON_VALUE})
    StatefulBody error(HttpServletRequest request, HttpServletResponse response) {
        if(!EnvironmentUtils.isProduction()) {
            return buildBody(request,true);
        }else{
            return buildBody(request,false);
        }
    }
    
	private StatefulBody buildBody(HttpServletRequest request,Boolean includeStackTrace){
        Map<String,Object> errorAttributes = getErrorAttributes(request, includeStackTrace);
        Integer status=(Integer)errorAttributes.get("status");
        String path=(String)errorAttributes.get("path");
        String messageFound=(String)errorAttributes.get("message");
        String message="";
        String trace ="";
        if(!StringUtils.isEmpty(path)){
            message=String.format("Requested path %s with result %s",path,messageFound);
        }
        if(includeStackTrace) {
             trace = (String) errorAttributes.get("trace");
             if(!StringUtils.isEmpty(trace)) {
                 message += String.format(" and trace %s", trace);
             }
        }
        return FailureResponseBody.builder().code(0).status(status).message(message).build();
    }
	@Override
	public String getErrorPath() {
		return PATH;
	}
	private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, includeStackTrace);
    }*/
}
