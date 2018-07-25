<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="com.cdc.cdccmc.common.util.SysConstants"%>
<%@ page import="com.cdc.cdccmc.domain.sys.SystemUser"%>
<%@ page import="com.cdc.cdccmc.domain.sys.SystemOrg"%>
<style>
.currentOrgClass{
	cursor: pointer;
}
.memberOrgList{
	position: absolute;top: 35px;right:43%;
	z-index:10;line-height:10px;background-color: #fff;
}
.memberOrgList table{
	border: 1px #009688 solid;
}
.memberOrgList table tr td{
	padding: 10px 20px 10px 20px;text-align: center;border:  1px #009688 solid;font-size: 10px;
}
.userInfoClass{
	padding-left: 10px;padding-right: 20px;cursor: pointer;
}
.userInfoList{
	position: absolute;right:10%;top: 35px;z-index:10;line-height:10px;background-color: #fff;
}
.userInfoList table{
	border: 1px #009688 solid;
}
.userInfoList table tr td{
	padding: 10px 20px 10px 20px;text-align: center;border:  1px #009688 solid;font-size: 10px;
}
</style>

<%------------------------------------------------ 
        					top bar 内容开始 
        ------------------------------------------------%>
<%  SystemUser sessionUser = (SystemUser)session.getAttribute(SysConstants.SESSION_USER); %>
<div class="layui-row" style="background-color:#393D49;color:#fff; height: 35px;">
	<div class="layui-col-md3" >
		<span style="margin-left:15px;font-size: 22px; ">CMC周转箱管理信息系统</span>
	</div>
	<div class="layui-col-md4" style="padding-top: 10px;padding-right:10px;text-align: right;">
		<span class="currentOrgClass">
				<%=sessionUser.getCurrentSystemOrg().getOrgName() %>  
				<!-- SH027-伟巴斯特车顶供暖系统（上海）有限公司 -->
				<!-- 凯史乐（上海）汽车工程技术有限公司昆山分公司 -->
				<i class="layui-icon" style="font-size: 15px; color: #FFF;">&#xe61a;</i> 
		</span>
	</div>	
	<div class="layui-col-md4" style="padding-top: 10px;text-align: right;">
		欢迎：
		<span class="userInfoClass">
				<%=sessionUser.getRealName() %>  
				<%--  最长的名字：上海纳铁福传动系统有限公司（吉凯恩集团）    康桥工厂 --%>
				<%--  超级管理员 --%>
				 <!-- 上海纳铁福传动系统有限公司（吉凯恩集团）    康桥工厂 -->
				<i class="layui-icon" style="font-size: 15px;font-weight:900; color: #FFF;">&#xe671;</i> 
		</span>
	</div>
	<div class="layui-col-md1" style="padding-top: 5px;text-align: center;">
		<img alt="下载用户使用操作手册 " class="downloadHelpDoc" src="<%=request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ request.getContextPath()%>/static/img/login/help.png" style="width: 30px;height: 30px;cursor: pointer;">
	</div>
</div>

<!-- 隶属仓库选择弹出框 -->
<div class="memberOrgList">
	<table>
			<% List<SystemOrg> filialeSystemOrgList = sessionUser.getMemberOfOrgList();
		       for(SystemOrg filiale : filialeSystemOrgList){  %>
					<tr><td><a href="<%=request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ request.getContextPath()%>/page/changeCurrentOrg?orgId=<%=filiale.getOrgId() %>"><%=filiale.getOrgName() %></a></td></tr>
			<% } %>
	</table>
	<button class="layui-btn layui-btn-fluid layui-btn-sm closeMemberOrgList" style="height: 29px;">关闭</button>
</div>
<!-- 修改密码、退出登录弹出框 -->
<div class="userInfoList">
	<table>
			<tr><td><a href="<%=request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ request.getContextPath()%>/logout" >退出登录</a></td></tr>
			<tr><td><a href="#" class="changePasswordCss">修改密码</a></td></tr>
	</table>
	<button class="layui-btn layui-btn-fluid layui-btn-sm closeUserInfoList" style="height: 29px;">关闭</button>
</div>
<script type="text/javascript">
//隶属仓库选择
$(".memberOrgList").hide();
$(".closeMemberOrgList").click(function(){
	$(".memberOrgList").slideUp("500");
});
$(".currentOrgClass").click(function(){
	$(".memberOrgList").slideDown("500");
});
//修改密码、退出登录
$(".userInfoList").hide();
$(".closeUserInfoList").click(function(){
	$(".userInfoList").slideUp("500");
});
$(".userInfoClass").click(function(){
	$(".userInfoList").slideDown("500");
});
//
$(".downloadHelpDoc").click(function(){
	window.open(basePath + '/downloadHelpDoc');
});
</script>

<!-- “修改密码”弹出框 -->
<script type="text/html" id="updatePasswordPanel">
    <form class="layui-form layerForm " id="updatePasswordPanelForm" action="">
        <div class="layui-form-item" >
            <label class="layui-form-label" style="width:125px;">登录名<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="text" name="accountName" value="<%=sessionUser.getAccount()%>" autocomplete="off" class="layui-input layui-disabled" disabled="disabled">
            </div>
        </div>
		<div class="layui-form-item" >
            <label class="layui-form-label" style="width:125px;">旧密码<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="password" name="oldPassword" autocomplete="off" class="layui-input" placeholder="此项必填">
            </div>
        </div>
        <div class="layui-form-item" >
            <label class="layui-form-label" style="width:125px;">新密码<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="password" name="newPassword" autocomplete="off" class="layui-input" placeholder="此项必填">
            </div>
        </div>
        <div class="layui-form-item" >
            <label class="layui-form-label" style="width:125px;">确认新密码<span class="myRedColor">*</span></label>
            <div class="layui-input-inline">
                <input type="password" name="confirmPassword" autocomplete="off" class="layui-input" placeholder="此项必填">
            </div>
        </div>
        <div class="layui-form-item" >
            <label class="layui-form-label" style="width:125px;"></label>
            <div class="layui-input-inline">
                <div style="color:red;">新密码必须为字母、数字、下划线的任意组合，长度为6到15位</div>
            </div>
        </div>
    </form>
</script>
<script language="javascript" type="text/javascript">  
$(document).ready(function(){  
	$("select[name='currentSystemOrg']").change(function(){  
		/* alert($(this).children('option:selected').val());    */
		window.location.href = basePath+"/page/changeCurrentOrg?orgId=" + $(this).children('option:selected').val();
	})  
	layui.use(['laydate', 'laypage', 'layer', 'table', 'carousel', 'upload', 'element','form','layedit'], function(){
        var table = layui.table;
        var layer = layui.layer;
        var form = layui.form;
        var upload = layui.upload;
        var laydate = layui.laydate;
        var layerEditName;
        layui.$('.changePasswordCss').on('click', function(){
            layerEditName = layer.open({
                type: 1 //Page层类型
                ,area: ['500px', '500px']
                ,title: '修改当前用户登录密码'
                ,shade: 0.6 //遮罩透明度
                ,maxmin: true //允许全屏最小化
                ,anim: -1 //0-6的动画形式，-1不开启
                ,content: $("#updatePasswordPanel").html() //'<div style="padding:50px;">这是一个非常普通的页面层，传入了自定义的html</div>'
                ,cancel: function(){
                    //alert("关闭啦");
                }
                ,success: function (layero, index) {  //弹出框渲染成功后，设置初始化值
                	//设置弹出框form表单d值
              	  $("#updatePasswordPanelForm input[name='accountName']").val();
                    form.render();
                    
                  //关闭弹出框：修改密码、退出登录
                  $(".userInfoList").hide();
                }
                ,btn: ['保存']
                ,btn1: function(index){
                    var param = {};
                    param["selectAccount"] = $("#updatePasswordPanelForm input[name='accountName']").val(); //登录名
                    param["oldPassword"] = $("#updatePasswordPanelForm input[name='oldPassword']").val(); //旧密码
                    param["newPassword"] = $("#updatePasswordPanelForm input[name='newPassword']").val(); //新密码
                    param["confirmPassword"] = $("#updatePasswordPanelForm input[name='confirmPassword']").val(); //新密码
                    $.ajax({
                        url: basePath + "/user/queryUserAccount",
                        type:"POST",
                        dataType: "json",
                        data: param,
                        success: function(result, textStatus, jqXHR){
                            layer.msg(result.msg);
                            if(result.status == 200){
                                layer.closeAll('page');   //成功后，关闭所有弹出框
                                tableReload.reload(); //重新加载table数据
                            }
                        }
                    });
                }
            });
        }); 
	}); 
})  

</script>       
<%------------------------------------------------ 
        					top bar 内容结束 
        ------------------------------------------------%>
