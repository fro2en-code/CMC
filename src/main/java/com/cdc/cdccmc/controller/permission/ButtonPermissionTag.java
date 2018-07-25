package com.cdc.cdccmc.controller.permission;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.cdc.cdccmc.common.util.SysConstants;
import com.cdc.cdccmc.domain.sys.SystemButton;
import com.cdc.cdccmc.domain.sys.SystemUser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 自定义权限标签，可以控制用户权限至按钮级别
 * @Author: ZhuWen
 * @Time: 2018-01-25
 */
public class ButtonPermissionTag extends SimpleTagSupport {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ButtonPermissionTag.class); 
    //标签属性，作为按钮的唯一标识
    private String buttonId;
    @Override
    public void doTag() throws JspException, IOException {
    	//获取session里的登录用户
        PageContext page = (PageContext) this.getJspContext();
        HttpSession session = page.getSession();
        Object obj = session.getAttribute(SysConstants.SESSION_USER);
        if(obj == null){  //如果没有登录用户，则什么按钮权限都没有
        	LOG.info("请求按钮权限buttonId["+buttonId+"]，用户未登录，没有任何按钮权限，");
            return;
        }
        SystemUser sessionUser = (SystemUser)obj;
        //获取登录用户的权限按钮
        List<SystemButton> buttonList = sessionUser.getSystemButtonList();
        boolean findButtonPermission = false;
        for(SystemButton button : buttonList){
        	if(button.getButtonId().equals(buttonId)){  //如果登录用户的权限按钮里有当前请求的按钮
        		findButtonPermission = true;
        		getJspBody().invoke(null); //则渲染该按钮
        	}
        }
        if(findButtonPermission){
        	LOG.info("account["+sessionUser.getAccount()+"]姓名["+sessionUser.getRealName()+"]拥有该按钮buttonId["+buttonId+"]权限，渲染成功！");
        }else{
        	LOG.info("account["+sessionUser.getAccount()+"]姓名["+sessionUser.getRealName()+"]没有该按钮buttonId["+buttonId+"]权限，渲染失败！");
        }

    }
	public String getButtonId() {
		return buttonId;
	}
	public void setButtonId(String buttonId) {
		this.buttonId = buttonId;
	}
}
